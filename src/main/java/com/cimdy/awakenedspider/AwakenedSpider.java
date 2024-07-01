package com.cimdy.awakenedspider;

import com.cimdy.awakenedspider.attach.AttachRegister;
import com.cimdy.awakenedspider.block.BlockRegister;
import com.cimdy.awakenedspider.block.blockentity.BlockEntityTypeRegister;
import com.cimdy.awakenedspider.event.HurtEvent;
import com.cimdy.awakenedspider.event.SpiderEvent;
import com.cimdy.awakenedspider.item.ItemRegister;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(AwakenedSpider.MODID)
public class AwakenedSpider
{
    public static final String MODID = "awakened_spider";

    public AwakenedSpider(IEventBus modEventBus, ModContainer modContainer)
    {

        CreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
        AttachRegister.ATTACHMENT_TYPES.register(modEventBus);
        BlockRegister.BLOCKS.register(modEventBus);
        ItemRegister.ITEMS.register(modEventBus);
        BlockEntityTypeRegister.BLOCK_ENTITIES.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::serverSetup);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.addListener(HurtEvent::LivingHurtEvent);
        NeoForge.EVENT_BUS.addListener(HurtEvent::LivingAttackEvent);
        NeoForge.EVENT_BUS.addListener(SpiderEvent::EntityJoinLevelEvent);

    }


    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    private void serverSetup(final FMLDedicatedServerSetupEvent event) {

    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }
}
