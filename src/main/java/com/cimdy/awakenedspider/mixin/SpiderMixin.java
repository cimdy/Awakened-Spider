package com.cimdy.awakenedspider.mixin;

import com.cimdy.awakenedspider.attach.AttachRegister;
import com.cimdy.awakenedspider.block.BlockRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

import static com.cimdy.awakenedspider.event.SpiderEvent.getRiderSpider;

@Mixin(Spider.class)
public abstract class SpiderMixin extends Monster implements TraceableEntity{
    @Shadow protected abstract void playStepSound(BlockPos pPos, BlockState pBlock);

    @Shadow public abstract Vec3 getVehicleAttachmentPoint(Entity pEntity);

    protected SpiderMixin(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Unique
    private static final EntityDataAccessor<Integer> LAY_TIME = SynchedEntityData.defineId(SpiderMixin.class, EntityDataSerializers.INT);
    @Unique
    private static final EntityDataAccessor<Integer> HEAL_TIME = SynchedEntityData.defineId(SpiderMixin.class, EntityDataSerializers.INT);
    @Unique
    private final int awakenedSpider_NeoForge_1_20_6$health = (int) (100 * (this.getHealth() + this.getAbsorptionAmount()) / this.getMaxHealth());

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder, CallbackInfo ci) {
        pBuilder.define(HEAL_TIME, 0);
        pBuilder.define(LAY_TIME, (int) (60 + (Math.random() * 60 + 1)) * 20);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        if(this.getData(AttachRegister.IS_HIDING)){
            return super.getAmbientSound();
        }
        return super.getHurtSound(pDamageSource);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    protected void tick(CallbackInfo ci){
        int shear_cobweb = this.getData(AttachRegister.SHEAR_COBWEB);//蛛网生成倒计时
        if(shear_cobweb > 0){
            shear_cobweb = shear_cobweb - 1;
            this.setData(AttachRegister.SHEAR_COBWEB, shear_cobweb);
        }

        int lay_time = this.getEntityData().get(LAY_TIME);
        if (lay_time > 0) lay_time -= 1;
        if(!this.level().isClientSide){
            ServerLevel serverLevel = (ServerLevel) this.level();
            int random = serverLevel.random.nextInt(100) + 1;
            boolean is_hiding = this.getData(AttachRegister.IS_HIDING);
            if(lay_time == 0 && serverLevel.getBlockEntity(this.getOnPos()) == null
                    && this.getLightLevelDependentMagicValue() <= 0.5F && random == 1 && !is_hiding){//蜘蛛在低亮度 且可以孵蛋CD满足时  每tick有5%概率生成蜘蛛蛋
                this.level().playSound(
                        null, this.getOnPos(), SoundEvents.TURTLE_LAY_EGG,
                        SoundSource.BLOCKS, 0.3F, 0.9F + serverLevel.random.nextFloat() * 0.2F);//播放一个声音
                BlockPos blockPos = new BlockPos(this.getOnPos().getX(),this.getOnPos().getY() + 1,this.getOnPos().getZ());
                BlockEntity blockEntity = null;
                if(this.getType() == EntityType.SPIDER){
                    serverLevel.setBlock(blockPos, BlockRegister.SPIDER_EGG.get().defaultBlockState(), 3);
                    blockEntity = BlockRegister.SPIDER_EGG.get().newBlockEntity(blockPos, BlockRegister.SPIDER_EGG.get().defaultBlockState());
                }else if(this.getType() == EntityType.CAVE_SPIDER){
                    serverLevel.setBlock(blockPos, BlockRegister.CAVE_SPIDER_EGG.get().defaultBlockState(), 3);
                    blockEntity = BlockRegister.CAVE_SPIDER_EGG.get().newBlockEntity(blockPos, BlockRegister.CAVE_SPIDER_EGG.get().defaultBlockState());
                }
                if (blockEntity != null) {
                    serverLevel.setBlockEntity(blockEntity);
                    //没有被骑乘
                    if(getRiderSpider(this) == null){
                        //实际上是让它消失 之后再生成
                        blockEntity.setData(AttachRegister.HAVING_SPIDER,true);//蜘蛛会立即隐匿到蜘蛛蛋附近
                        this.discard();
                    }
                }
                lay_time = (int) (60 + (Math.random() * 60 + 1) * 20);
            }
            //当最大生命小于20% 且12格内没有玩家 隐匿 无视亮度和是否骑乘
            if(awakenedSpider_NeoForge_1_20_6$health < 20 && level().getNearestPlayer(this,12) == null && !is_hiding){
                boolean hiding = true;
                //判断脚下是否有足够方块
                double a = Math.max(this.getBoundingBox().getXsize(), this.getBoundingBox().getZsize());
                double b = this.getBoundingBox().getYsize() * 2 + 1;
                for(int x = 0; x < a; x++){
                    for(int y = 0; y < b; y++){
                        for(int z = 0; z < a; z++){
                            BlockPos blockPos = new BlockPos(this.getBlockX() - 1 + x, this.getBlockY() - 3 + y, this.getBlockZ() - 1 + z);
                            if(this.level().getBlockState(blockPos).isEmpty()){
                                hiding = false;
                            }
                        }
                    }
                }

                if(hiding){
                    BlockPos blockPos = new BlockPos(this.getBlockX(), this.getBlockY() - 2, this.getBlockZ());
                    this.moveTo(blockPos,this.getYRot(),this.getXRot());
                    is_hiding = true;
                }
            }
            //蜘蛛在低亮度 每tick有1%概率藏匿在脚下方块 且没有被骑乘
            if(this.getLightLevelDependentMagicValue() <= 0.5F && random == 1 && !is_hiding && getRiderSpider(this) == null){
                boolean hiding = true;
                //判断脚下是否有足够方块
                double a = Math.max(this.getBoundingBox().getXsize(), this.getBoundingBox().getZsize());
                double b = this.getBoundingBox().getYsize();
                for(int x = 0; x < a; x++){
                    for(int y = 0; y < b; y++){
                        for(int z = 0; z < a; z++){
                            BlockPos blockPos = new BlockPos(this.getBlockX() - 1 + x, this.getBlockY() - 3 + y, this.getBlockZ() - 1 + z);
                            if(this.level().getBlockState(blockPos).isEmpty()){
                                hiding = false;
                            }
                        }
                    }
                }
                if(hiding){
                    BlockPos blockPos = new BlockPos(this.getBlockX(), this.getBlockY() - 2, this.getBlockZ());
                    this.moveTo(blockPos,this.getYRot(),this.getXRot());
                    is_hiding = true;
                }
            }

            Player player = level().getNearestPlayer(this,7);
            if( player != null && is_hiding){//如果蜘蛛处于隐匿状态且附近7格有玩家 退出隐匿状态 且把玩家作为攻击目标
                BlockPos blockPos = new BlockPos(this.getBlockX(), this.getBlockY() + 2, this.getBlockZ());
                this.moveTo(blockPos,this.getYRot(),this.getXRot());
                this.setTarget(player);
                is_hiding = false;
            }

            if(is_hiding){
                int heal_time = this.getEntityData().get(HEAL_TIME);
                heal_time += 1;
                //如果处于隐匿状态每秒恢复 1 血
                if(heal_time == 20) this.heal(1);
                this.entityData.set(HEAL_TIME,heal_time);
                //判断血量和亮度
                boolean hiding = !(this.getLightLevelDependentMagicValue() >= 0.5F) || awakenedSpider_NeoForge_1_20_6$health <= 80;
                //判断是否将要生蛋
                if(hiding && lay_time == 0) hiding = false;
                //判断脚下是否有足够方块
                if(hiding){
                    double a = Math.max(this.getBoundingBox().getXsize(), this.getBoundingBox().getZsize());
                    double b = this.getBoundingBox().getYsize();
                    for(int x = 0; x < a; x++){
                        for(int y = 0; y < b; y++){
                            for(int z = 0; z < a; z++){
                                BlockPos blockPos = new BlockPos(this.getBlockX() -1 + x, this.getBlockY() - 1 + y, this.getBlockZ() -1 + z);
                                if(this.level().getBlockState(blockPos).isEmpty()) hiding = false;
                            }
                        }
                    }
                }

                //如果结束隐匿
                if(!hiding){
                    BlockPos blockPos = new BlockPos(this.getBlockX(), this.getBlockY() + 2, this.getBlockZ());
                    this.moveTo(blockPos,this.getYRot(),this.getXRot());
                    is_hiding = false;
                }
            }
            this.setData(AttachRegister.IS_HIDING, is_hiding);
        }
        this.getEntityData().set(LAY_TIME, lay_time);
    }


    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {//蜘蛛右键交互剪蛛网
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (!this.level().isClientSide) {
            if(this.getData(AttachRegister.SHEAR_COBWEB) > 0 && itemstack.getItem() == Items.SHEARS){
                pPlayer.sendSystemMessage(Component.translatable("message.awakened_spider.your_behavior_makes_the_spider_angry"));
                this.setData(AttachRegister.SHEAR_COBWEB, this.getData(AttachRegister.SHEAR_COBWEB) + 300);
                int time = this.getData(AttachRegister.SHEAR_COBWEB) / 20;
                int min = time / 60;
                int s = time % 60;
                pPlayer.sendSystemMessage(Component.translatable("message.awakened_spider.spiders_still_need_to")
                        .append(String.valueOf(min)).append(Component.translatable("message.awakened_spider.min"))
                        .append(String.valueOf(s)).append(Component.translatable("message.awakened_spider.s")));
                return InteractionResult.CONSUME_PARTIAL;
            }else if(this.getData(AttachRegister.SHEAR_COBWEB) == 0 && itemstack.getItem() == Items.SHEARS){//剪蛛网
                this.awakenedSpider_NeoForge_1_20_6$shear(SoundSource.PLAYERS);
                this.gameEvent(GameEvent.SHEAR, pPlayer);
                itemstack.hurtAndBreak(1, pPlayer, getSlotForHand(pHand));
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Unique
    public void awakenedSpider_NeoForge_1_20_6$shear(SoundSource pCategory) {//像剪羊毛一样剪掉蜘蛛网
        this.level().playSound(null, this, SoundEvents.SHEEP_SHEAR, pCategory, 1.0F, 1.0F);
        this.setData(AttachRegister.SHEAR_COBWEB, 1500);
        int i = 1 + this.random.nextInt(3);
        for(int j = 0; j < i; ++j) {

            ItemEntity itemEntity = this.getType() == EntityType.CAVE_SPIDER ? this.spawnAtLocation(Items.SPIDER_EYE) : this.spawnAtLocation(Items.COBWEB);

            if (itemEntity != null) {
                itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().add(
                        (this.random.nextFloat() - this.random.nextFloat()) * 0.1F,
                        this.random.nextFloat() * 0.05F,
                        (this.random.nextFloat() - this.random.nextFloat()) * 0.1F));
            }

        }
    }

    @Inject(method = "finalizeSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerLevelAccessor;getDifficulty()Lnet/minecraft/world/Difficulty;",shift = At.Shift.BEFORE))
    private void finalizeSpawnEffect(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pSpawnType, SpawnGroupData pSpawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir){
        RandomSource randomsource = pLevel.getRandom();
        ((Spider.SpiderEffectsGroupData)pSpawnGroupData).setRandomEffect(randomsource);//蜘蛛生成后必定带有药水效果
    }

    @Inject(method = "finalizeSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I",shift = At.Shift.BEFORE))
    private void finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pSpawnType, SpawnGroupData pSpawnGroupData, CallbackInfoReturnable<SpawnGroupData> cir){
        RandomSource randomsource = pLevel.getRandom();
        int random = randomsource.nextInt(100) + 1;
        if (random <= 50) {//其中50%为骷髅骑士
            Skeleton skeleton = EntityType.SKELETON.create(this.level());
            if (skeleton != null) {
                skeleton.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                skeleton.finalizeSpawn(pLevel, pDifficulty, pSpawnType, null);
                skeleton.setData(AttachRegister.RIDER, this.stringUUID);
                skeleton.startRiding(this);
            }
        }else if(random <= 70){//20%为掠夺者骑士
            Pillager pillager = EntityType.PILLAGER.create(this.level());
            if (pillager != null) {
                pillager.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                pillager.finalizeSpawn(pLevel, pDifficulty, pSpawnType, null);
                pillager.setData(AttachRegister.RIDER, this.stringUUID);
                pillager.startRiding(this);
            }
        }else if(random <= 80){//10%为女巫骑士
            Witch witch = EntityType.WITCH.create(this.level());
            if (witch != null) {
                witch.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                witch.finalizeSpawn(pLevel, pDifficulty, pSpawnType, null);
                witch.setData(AttachRegister.RIDER, this.stringUUID);
                witch.startRiding(this);
            }
        } else if(random <= 90){//10%为唤魔者骑士
            Evoker evoker = EntityType.EVOKER.create(this.level());
            if (evoker != null) {
                evoker.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                evoker.finalizeSpawn(pLevel, pDifficulty, pSpawnType, null);
                evoker.setData(AttachRegister.RIDER, this.stringUUID);
                evoker.startRiding(this);
            }
        }else if(random <= 100){//10%为幻术师骑士
            Illusioner illusioner = EntityType.ILLUSIONER.create(this.level());
            if (illusioner != null) {
                illusioner.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                illusioner.finalizeSpawn(pLevel, pDifficulty, pSpawnType, null);
                illusioner.setData(AttachRegister.RIDER, this.stringUUID);
                illusioner.startRiding(this);
            }
        }
    }

    @Redirect(method = "finalizeSpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I"))
    private int finalizeSpawnRemove(RandomSource instance, int i){//原版骷髅骑士运算不再生效 相当于被删除
        return -1;
    }

    @Mixin(targets = "net.minecraft.world.entity.monster.Spider$SpiderEffectsGroupData")
    public static class SpiderEffectsGroupData {
        @Shadow @Nullable public Holder<MobEffect> effect;

        @Inject(method = "setRandomEffect", at = @At(value = "TAIL"))
        public void setRandomEffect(RandomSource pRandom, CallbackInfo ci){
            int i = pRandom.nextInt(7);
            if(i == 0){
                this.effect = MobEffects.MOVEMENT_SPEED;
            }else if(i == 1){
                this.effect = MobEffects.REGENERATION;
            }else if(i == 2){
                this.effect = MobEffects.DAMAGE_BOOST;
            }else if(i == 3){
                this.effect = MobEffects.DAMAGE_RESISTANCE;
            }else if(i == 4){
                this.effect = MobEffects.FIRE_RESISTANCE;
            }else if(i == 5){
                this.effect = MobEffects.INVISIBILITY;
            }else if(i == 6){
                this.effect = MobEffects.JUMP;
            }else if(i == 7){
                this.effect = MobEffects.HEALTH_BOOST;
            }
        }
    }

    @Mixin(targets = "net.minecraft.world.entity.monster.Spider$SpiderTargetGoal")
    public static class SpiderTargetGoalMixin<T extends LivingEntity> extends NearestAttackableTargetGoal<T>  {
        public SpiderTargetGoalMixin(Mob pMob, Class<T> pTargetType, boolean pMustSee) {
            super(pMob, pTargetType, pMustSee);
        }

        @Inject(method = "canUse", at = @At(value = "TAIL"), cancellable = true)
        public void canUse(CallbackInfoReturnable<Boolean> cir){
            if(this.mob.getData(AttachRegister.SHEAR_COBWEB) > 600){//蜘蛛在光照足够的情况下如果结网时间超出一定值一样会攻击玩家
                cir.setReturnValue(super.canUse());
            }
        }
    }

    @Mixin(targets = "net.minecraft.world.entity.monster.Spider$SpiderAttackGoal")
    public static class SpiderAttackGoalMixin extends MeleeAttackGoal {
        public SpiderAttackGoalMixin(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
            super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        }

        @Inject(method = "canContinueToUse", at = @At(value = "RETURN"), cancellable = true)
        public void canContinueToUse(CallbackInfoReturnable<Boolean> cir){
            cir.setReturnValue(super.canContinueToUse());//蜘蛛的攻击攻击之后继续攻击不再进行任何判断直接判断继续攻击
        }
    }

}
