package com.cimdy.awakenedspider.event;

import com.cimdy.awakenedspider.attach.AttachRegister;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class SpiderEvent {
    @SubscribeEvent
    public static void EntityJoinLevelEvent(EntityJoinLevelEvent event){
        if(event.getEntity() instanceof LivingEntity rider && rider.hasData(AttachRegister.RIDER)){
            LivingEntity living = getVehicleSpider(rider);
            if(living != null){
                if(living instanceof CaveSpider caveSpider){
                    setRandomEffect(caveSpider);

                    rider.getAttribute(Attributes.SCALE).setBaseValue(0.75);
                }
                Collection<MobEffectInstance> mobEffectInstances = living.getActiveEffects();
                for(MobEffectInstance mobEffectInstance : mobEffectInstances){
                    rider.addEffect(mobEffectInstance);
                }
            }
        }
    }

    public static boolean isRider(LivingEntity spider, LivingEntity rider) {
        return spider.getStringUUID().equals(rider.getData(AttachRegister.RIDER));
    }

    public static LivingEntity getVehicleSpider(LivingEntity rider){
        AABB aabb = new AABB(rider.getX() - 2, rider.getY() - 2, rider.getZ() - 2,rider.getX() + 2, rider.getY() + 2, rider.getZ() + 2);
        List<Entity> entityList = rider.level().getEntities(rider, aabb);
        for(Entity entity : entityList){
            if(entity instanceof Spider spider && isRider(spider, rider)){
                return spider;
            }
        }
        return null;
    }

    public static LivingEntity getRiderSpider(LivingEntity spider){
        AABB aabb = new AABB(spider.getX() - 2, spider.getY(), spider.getZ() - 2,spider.getX() + 2, spider.getY() + 3, spider.getZ() + 2);
        List<Entity> entityList = spider.level().getEntities(spider, aabb);
        for(Entity entity : entityList){
            if(entity instanceof LivingEntity rider && isRider(spider, rider)){
                return rider;
            }
        }
        return null;
    }


    public static void setRandomEffect(CaveSpider caveSpider){
        int i = new Random().nextInt(8);
        Holder<MobEffect> effect = switch (i) {
            case 0 -> MobEffects.MOVEMENT_SPEED;
            case 1 -> MobEffects.REGENERATION;
            case 2 -> MobEffects.DAMAGE_BOOST;
            case 3 -> MobEffects.DAMAGE_RESISTANCE;
            case 4 -> MobEffects.FIRE_RESISTANCE;
            case 5 -> MobEffects.INVISIBILITY;
            case 6 -> MobEffects.JUMP;
            default -> MobEffects.HEALTH_BOOST;
        };
        caveSpider.addEffect(new MobEffectInstance(effect, -1));
    }


}
