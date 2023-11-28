package com.example.helloworldmod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
public class CreeperSpawnHandler {
    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((Entity entity, ServerWorld world) -> {
            if (entity instanceof CreeperEntity) {
                CreeperEntity creeper = (CreeperEntity) entity;
                setExplosionRadius(creeper, 0);
            }
        });
    }

    private static void setExplosionRadius(CreeperEntity creeper, int radius) {
        NbtCompound nbtData = creeper.writeNbt(new NbtCompound());
        nbtData.putInt("ExplosionRadius", radius);
        creeper.readNbt(nbtData);
    }
}
