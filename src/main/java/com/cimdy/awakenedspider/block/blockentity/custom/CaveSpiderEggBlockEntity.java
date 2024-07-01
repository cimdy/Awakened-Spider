package com.cimdy.awakenedspider.block.blockentity.custom;

import com.cimdy.awakenedspider.attach.AttachRegister;
import com.cimdy.awakenedspider.block.blockentity.BlockEntityTypeRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CaveSpiderEggBlockEntity extends BlockEntity {
    private int hatch_time;
    private int max_hatch_time;
    public CaveSpiderEggBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityTypeRegister.SPIDER_EGG.get(), pPos, pBlockState);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.putInt("hatch_time",hatch_time);
        pTag.putInt("max_hatch_time",max_hatch_time);
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        hatch_time = pTag.getInt("hatch_time");
        max_hatch_time = pTag.getInt("max_hatch_time");
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, CaveSpiderEggBlockEntity pBlockEntity) {
        ServerLevel serverLevel = (ServerLevel) pLevel;
        RandomSource randomsource = serverLevel.random;
        if (pBlockEntity.max_hatch_time == 0) pBlockEntity.max_hatch_time = (60 + randomsource.nextInt(60)) * 20;//孵化时间为 1200tick - 3600tick

        if(pBlockEntity.hatch_time >= pBlockEntity.max_hatch_time){//蜘蛛生成
            for(int a = pBlockEntity.getData(AttachRegister.HAVING_SPIDER) ? 2 : 1; a > 0; a--){
                Spider spider = new Spider(EntityType.CAVE_SPIDER,pLevel);
                BlockPos blockPos = new BlockPos(pPos.getX(), pPos.getY() + 1, pPos.getZ());
                spider.moveTo(blockPos,0,0);
                spider.finalizeSpawn(serverLevel, pLevel.getCurrentDifficultyAt(pPos), MobSpawnType.MOB_SUMMONED, null);
                serverLevel.addFreshEntityWithPassengers(spider);
            }
            //蜘蛛蛋移除
            pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(),3);
            pBlockEntity.clearRemoved();
        }

        pBlockEntity.hatch_time += 1;//计时器

        Player player = pLevel.getNearestPlayer(pPos.getX(),pPos.getY(),pPos.getZ(),5,true);//如果蜘蛛蛋附近有玩家 且蜘蛛蛋是蜘蛛产出 且有蜘蛛隐匿
        if(pBlockEntity.getData(AttachRegister.HAVING_SPIDER) && player !=null){//生成一只蜘蛛
            Spider spider = new Spider(EntityType.CAVE_SPIDER,pLevel);
            BlockPos blockPos = new BlockPos(pPos.getX() + randomsource.nextInt(3), pPos.getY(), pPos.getZ() + randomsource.nextInt(3));
            spider.moveTo(blockPos,0,0);
            spider.finalizeSpawn(serverLevel, pLevel.getCurrentDifficultyAt(pPos), MobSpawnType.MOB_SUMMONED, null);
            spider.setTarget(player);//蜘蛛攻击玩家
            serverLevel.addFreshEntityWithPassengers(spider);
            pBlockEntity.setData(AttachRegister.HAVING_SPIDER,false);
        }
    }
}
