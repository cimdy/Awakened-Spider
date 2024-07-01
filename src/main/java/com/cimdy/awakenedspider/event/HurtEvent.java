package com.cimdy.awakenedspider.event;

import com.cimdy.awakenedspider.attach.AttachRegister;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingAttackEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;

public class HurtEvent {
    @SubscribeEvent
    public static void LivingHurtEvent(LivingHurtEvent event){
        if(event.getEntity().getData(AttachRegister.IS_HIDING)){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void LivingAttackEvent(LivingAttackEvent event){
        if(event.getSource().getEntity() instanceof LivingEntity){
            if(event.getSource().getEntity().getType() == EntityType.SPIDER){
                int MOVEMENT_SLOWDOWN = event.getEntity().getData(AttachRegister.MOVEMENT_SLOWDOWN);
                MOVEMENT_SLOWDOWN += 1;
                event.getEntity().forceAddEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,200, MOVEMENT_SLOWDOWN), event.getEntity());
                event.getEntity().setData(AttachRegister.MOVEMENT_SLOWDOWN,MOVEMENT_SLOWDOWN);
            }
        }
        if(event.getSource().getEntity() instanceof LivingEntity){
            if(event.getSource().getEntity().getType() == EntityType.CAVE_SPIDER){
                int POISON = event.getEntity().getData(AttachRegister.POISON);
                POISON += 1;
                event.getEntity().forceAddEffect(new MobEffectInstance(MobEffects.POISON,200, POISON), event.getEntity());
                event.getEntity().setData(AttachRegister.POISON,POISON);
            }
        }

    }
}
