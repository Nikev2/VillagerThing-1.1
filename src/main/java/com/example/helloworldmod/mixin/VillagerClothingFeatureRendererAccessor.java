package com.example.helloworldmod.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(VillagerClothingFeatureRenderer.class)
public interface VillagerClothingFeatureRendererAccessor {

    @Accessor("LEVEL_TO_ID")
    static Int2ObjectMap<Identifier> getLevelToId() {
        throw new AssertionError();
    }

    @Invoker("findTexture")
    Identifier findTexture(String keyType, Identifier keyId);
}
