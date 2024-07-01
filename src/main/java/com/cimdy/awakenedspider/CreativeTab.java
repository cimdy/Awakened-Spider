package com.cimdy.awakenedspider;

import com.cimdy.awakenedspider.block.BlockRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AwakenedSpider.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AWAKENED_SPIDER = CREATIVE_MODE_TABS.register("awakened_spider", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.awakened_spider")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() ->  BlockRegister.SPIDER_EGG_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(BlockRegister.SPIDER_EGG_ITEM.get());
                output.accept(BlockRegister.CAVE_SPIDER_EGG_ITEM.get());
            }).build());

    static void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(BlockRegister.SPIDER_EGG_ITEM);
    }



}
