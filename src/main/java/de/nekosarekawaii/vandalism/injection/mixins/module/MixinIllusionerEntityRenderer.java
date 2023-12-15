package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.render.TrueSightModule;
import net.minecraft.client.render.entity.IllusionerEntityRenderer;
import net.minecraft.entity.mob.IllusionerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(IllusionerEntityRenderer.class)
public abstract class MixinIllusionerEntityRenderer {

    @Redirect(method = "render(Lnet/minecraft/entity/mob/IllusionerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/IllusionerEntity;isInvisible()Z"))
    private boolean hookTrueSight(final IllusionerEntity instance) {
        final TrueSightModule trueSightModule = Vandalism.getInstance().getModuleManager().getTrueSightModule();
        if (trueSightModule.isActive() && trueSightModule.showIllusionerEntity.getValue()) return false;
        return instance.isInvisible();
    }

}
