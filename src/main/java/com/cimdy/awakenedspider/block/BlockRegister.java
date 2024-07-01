package com.cimdy.awakenedspider.block;

import com.cimdy.awakenedspider.AwakenedSpider;
import com.cimdy.awakenedspider.block.custom.CaveSpiderEgg;
import com.cimdy.awakenedspider.block.custom.SpiderEgg;
import com.cimdy.awakenedspider.item.ItemRegister;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegister {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AwakenedSpider.MODID);


    public static final DeferredBlock<SpiderEgg> SPIDER_EGG  = BLOCKS.registerBlock("spider_egg",
            SpiderEgg::new, Block.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .pushReaction(PushReaction.DESTROY));

    public static final DeferredBlock<CaveSpiderEgg> CAVE_SPIDER_EGG  = BLOCKS.registerBlock("cave_spider_egg",
            CaveSpiderEgg::new, Block.Properties.of()
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .pushReaction(PushReaction.DESTROY));

    public static final DeferredItem<BlockItem> SPIDER_EGG_ITEM = ItemRegister.ITEMS.registerSimpleBlockItem("spider_egg", SPIDER_EGG);

    public static final DeferredItem<BlockItem> CAVE_SPIDER_EGG_ITEM = ItemRegister.ITEMS.registerSimpleBlockItem("cave_spider_egg", CAVE_SPIDER_EGG);

}
