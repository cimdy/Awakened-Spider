package com.cimdy.awakenedspider.attach;

import com.cimdy.awakenedspider.AwakenedSpider;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AttachRegister {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, AwakenedSpider.MODID);

    public static final Supplier<AttachmentType<Integer>> SHEAR_COBWEB = ATTACHMENT_TYPES.register(
            "shear_cobweb", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build());

    public static final Supplier<AttachmentType<Boolean>> HAVING_SPIDER = ATTACHMENT_TYPES.register(
            "having_spider", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build());

    public static final Supplier<AttachmentType<Boolean>> IS_HIDING = ATTACHMENT_TYPES.register(
            "is_hiding", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build());

    public static final Supplier<AttachmentType<String>> RIDER = ATTACHMENT_TYPES.register(
            "rider", () -> AttachmentType.builder(() -> "").serialize(Codec.STRING).build());

}
