package com.example.helloworldmod.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerClothingFeatureRenderer;
import net.minecraft.client.render.entity.feature.VillagerResourceMetadata;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHat;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(VillagerClothingFeatureRenderer.class)
public class VillagerClothingFeatureRendererMixin <T extends LivingEntity & VillagerDataContainer, M extends EntityModel<T> & ModelWithHat>{

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/feature/VillagerClothingFeatureRenderer;getContextModel()Lnet/minecraft/client/render/entity/model/EntityModel;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void renderBabyProfession(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci, VillagerData villagerData, VillagerType villagerType, VillagerProfession villagerProfession, VillagerResourceMetadata.HatType hatType, VillagerResourceMetadata.HatType hatType2){

        M entityModel = ((FeatureRenderer<T, M>)(Object)this).getContextModel();
        (entityModel).setHatVisible(hatType2 == VillagerResourceMetadata.HatType.NONE || hatType2 == VillagerResourceMetadata.HatType.PARTIAL && hatType != VillagerResourceMetadata.HatType.FULL);
        Identifier identifier = ((VillagerClothingFeatureRendererAccessor)this).findTexture("type", Registries.VILLAGER_TYPE.getId(villagerType));
        FeatureRendererAccessor.renderModel(entityModel, identifier, matrixStack, vertexConsumerProvider, i, livingEntity, 1.0F, 1.0F, 1.0F);
        (entityModel).setHatVisible(true);

        if(livingEntity.isBaby() && villagerProfession == VillagerProfession.NITWIT)
        {
            Identifier identifier2 = ((VillagerClothingFeatureRendererAccessor)this).findTexture("profession", Registries.VILLAGER_PROFESSION.getId(villagerProfession));
            FeatureRendererAccessor.renderModel(entityModel, identifier2, matrixStack, vertexConsumerProvider, i, livingEntity, 1.0F, 1.0F, 1.0F);

            ci.cancel();
        }

    }
}
