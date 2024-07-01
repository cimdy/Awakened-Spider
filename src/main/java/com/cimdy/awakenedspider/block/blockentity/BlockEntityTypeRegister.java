package com.cimdy.awakenedspider.block.blockentity;

import com.cimdy.awakenedspider.AwakenedSpider;
import com.cimdy.awakenedspider.block.BlockRegister;
import com.cimdy.awakenedspider.block.blockentity.custom.CaveSpiderEggBlockEntity;
import com.cimdy.awakenedspider.block.blockentity.custom.SpiderEggBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockEntityTypeRegister {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AwakenedSpider.MODID);

    public static final Supplier<BlockEntityType<SpiderEggBlockEntity>> SPIDER_EGG = BLOCK_ENTITIES.register("spider_egg",
            ()->BlockEntityType.Builder.of(SpiderEggBlockEntity::new, BlockRegister.SPIDER_EGG.get()).build(null));

    public static final Supplier<BlockEntityType<CaveSpiderEggBlockEntity>> CAVE_SPIDER_EGG = BLOCK_ENTITIES.register("cave_spider_egg",
            ()->BlockEntityType.Builder.of(CaveSpiderEggBlockEntity::new, BlockRegister.CAVE_SPIDER_EGG.get()).build(null));
}
