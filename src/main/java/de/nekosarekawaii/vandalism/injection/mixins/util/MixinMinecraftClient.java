package de.nekosarekawaii.vandalism.injection.mixins.util;

import de.nekosarekawaii.vandalism.util.render.RenderUtil;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Inject(method = "render", at = @At("HEAD"))
    public void vandalism$onDrawFrame(final boolean tick, final CallbackInfo ci) {
        RenderUtil.drawFrame();
    }

}