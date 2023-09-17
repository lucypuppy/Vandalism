package de.foxglovedevelopment.foxglove.injection.mixins.minecraft;

import de.foxglovedevelopment.foxglove.Foxglove;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BuiltinModelItemRenderer.class)
public abstract class MixinBuiltinModelItemRenderer {

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/ShieldEntityModel;getLayer(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/RenderLayer;",
                    ordinal = 0
            )
    )
    private RenderLayer redirectRender(final ShieldEntityModel instance, final Identifier identifier) {
        final float value = Foxglove.getInstance().getConfigManager().getMainConfig().shieldAlpha.getValue();
        if (value < 1.0f) return RenderLayer.getEntityTranslucent(identifier);
        return instance.getLayer(identifier);
    }

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V"
            ),
            index = 7
    )
    private float modifyRender(final float value) {
        return Foxglove.getInstance().getConfigManager().getMainConfig().shieldAlpha.getValue();
    }

}
