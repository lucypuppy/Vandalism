package de.foxglovedevelopment.foxglove.injection.mixins.minecraft;

import de.foxglovedevelopment.foxglove.Foxglove;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LightmapTextureManager.class)
public abstract class MixinLightmapTextureManager {

    @ModifyArgs(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"))
    private void update(final Args args) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().fullBright.getValue())
            args.set(2, 0xFFFFFFFF);
    }

}
