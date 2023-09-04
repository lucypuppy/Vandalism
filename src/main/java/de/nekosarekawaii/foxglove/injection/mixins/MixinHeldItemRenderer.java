package de.nekosarekawaii.foxglove.injection.mixins;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.config.impl.MainConfig;
import de.nekosarekawaii.foxglove.util.MinecraftWrapper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.raphimc.vialoader.util.VersionEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HeldItemRenderer.class, priority = 1001)
public abstract class MixinHeldItemRenderer implements MinecraftWrapper {

    @Inject(method = "renderFirstPersonItem",
            slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getUseAction()Lnet/minecraft/util/UseAction;")),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    private void injectRenderFirstPersonItem(final AbstractClientPlayerEntity player, final float tickDelta, final float pitch, final Hand hand, final float swingProgress, final ItemStack item, final float equipProgress, final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final int light, final CallbackInfo ci) {
        final MainConfig mainConfig = Foxglove.getInstance().getConfigManager().getMainConfig();
        final float itemSize = mainConfig.blockItemSize.getValue();
        if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8) && player().isBlocking() && mainConfig.blockHitAnimation.getValue()) {

            final float swing = MathHelper.sin(MathHelper.sqrt(swingProgress / 33.0f) * (float) Math.PI);

            matrices.translate(0, -0.1, 0.2);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F));

            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(swing * -80.0F));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(swing * -45.0F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(swing * -20.0F));

            matrices.scale(itemSize, itemSize, itemSize);
        }
    }

}

