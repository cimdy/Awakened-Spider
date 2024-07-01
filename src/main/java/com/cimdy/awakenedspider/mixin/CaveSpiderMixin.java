package com.cimdy.awakenedspider.mixin;

import com.cimdy.awakenedspider.attach.AttachRegister;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CaveSpider.class)
public class CaveSpiderMixin extends Spider {

    public CaveSpiderMixin(EntityType<? extends Spider> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "finalizeSpawn", at = @At("TAIL"))
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

}
