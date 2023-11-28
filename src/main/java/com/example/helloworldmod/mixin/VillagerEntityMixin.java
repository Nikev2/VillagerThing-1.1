package com.example.helloworldmod.mixin;

import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(VillagerEntity.class)
public class VillagerEntityMixin {

    private boolean isParentNitwit(VillagerEntity parentEntity){
        return parentEntity.getVillagerData().getProfession() == VillagerProfession.NITWIT;
    }

    private double increaseNitwitificationProbability(VillagerEntity parentEntity)
    {
        if(isParentNitwit(parentEntity))
            return 0.4;
        return 0;
    }

    private void makeNitwit(VillagerEntity villagerEntity, double probability){
        double random = Math.random();

        if(random <= probability)
            villagerEntity.setVillagerData(villagerEntity.getVillagerData().withProfession(VillagerProfession.NITWIT));
    }

    @Inject(method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/VillagerEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/VillagerEntity;initialize(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/LocalDifficulty;Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/entity/EntityData;Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/entity/EntityData;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    void createChild(ServerWorld serverWorld, PassiveEntity passiveEntity, CallbackInfoReturnable<VillagerEntity> cir, VillagerType villagerType, double d, VillagerEntity villagerEntity){
        if(passiveEntity instanceof VillagerEntity) {

            double nitwitProbability = 0.1;

            nitwitProbability += increaseNitwitificationProbability((VillagerEntity)(Object)this);
            nitwitProbability += increaseNitwitificationProbability((VillagerEntity)passiveEntity);

            makeNitwit(villagerEntity, nitwitProbability);
        }
    }
}
