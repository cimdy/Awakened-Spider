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
                int MOVEMENT_SLOWDOWN = !event.getEntity().hasEffect(MobEffects.MOVEMENT_SLOWDOWN) ?
                        0 : event.getEntity().getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() + 1;
                event.getEntity().forceAddEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,200, MOVEMENT_SLOWDOWN), event.getEntity());
            }
        }
        if(event.getSource().getEntity() instanceof LivingEntity){
            if(event.getSource().getEntity().getType() == EntityType.CAVE_SPIDER){
                int POISON = !event.getEntity().hasEffect(MobEffects.POISON) ?
                        0 : event.getEntity().getEffect(MobEffects.POISON).getAmplifier() + 1;
                event.getEntity().forceAddEffect(new MobEffectInstance(MobEffects.POISON,200, POISON), event.getEntity());
            }
        }

    }
}
