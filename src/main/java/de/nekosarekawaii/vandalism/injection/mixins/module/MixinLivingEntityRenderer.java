package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.render.TrueSightModule;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {

    protected MixinLivingEntityRenderer(final EntityRendererFactory.Context ignored) {
        super(ignored);
    }

    @Inject(method = "getRenderLayer", at = @At("HEAD"), cancellable = true)
    private void vandalism$trueSightForceTranslucentCullOnInvisibleEntities(final T entity, final boolean showBody, final boolean translucent, final boolean showOutline, final CallbackInfoReturnable<RenderLayer> cir) {
        final TrueSightModule trueSightModule = Vandalism.getInstance().getModuleManager().getTrueSightModule();
        if (trueSightModule.isActive() && !showBody && !translucent && !showOutline) {
            cir.setReturnValue(RenderLayer.getItemEntityTranslucentCull(this.getTexture(entity)));
        }
    }

}