package me.nekosarekawaii.foxglove.mixin.net.minecraft.client.render.entity;

import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.render.TrueSightModule;
import net.minecraft.client.render.entity.IllusionerEntityRenderer;
import net.minecraft.entity.mob.IllusionerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(IllusionerEntityRenderer.class)
public abstract class MixinIllusionerEntityRenderer {

    @Redirect(method = "render(Lnet/minecraft/entity/mob/IllusionerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/IllusionerEntity;isInvisible()Z"))
    private boolean injectRenderInvisible(final IllusionerEntity instance) {
        final TrueSightModule trueSightModule = Foxglove.getInstance().getModuleRegistry().getTrueSightModule();
        if (trueSightModule.isEnabled() && trueSightModule.illusionerEntity.getValue()) {
            return false;
        }
        return instance.isInvisible();
    }

}
