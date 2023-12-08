package de.vandalismdevelopment.vandalism.injection.mixins.kernel;

import de.vandalismdevelopment.vandalism.Vandalism;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.lang.management.ManagementFactory;

@Mixin(value = MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    @Final
    private Window window;

    @Shadow
    @Final
    public File runDirectory;

    @Unique
    private boolean vandalism_loadingDisplayed = false;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;onResolutionChanged()V"))
    private void vandalism$startMod(final CallbackInfo ci) {
        Vandalism.getInstance().start(this.window, this.runDirectory);
    }

    @Inject(method = "close", at = @At(value = "HEAD"))
    private void vandalism$stopMod(final CallbackInfo ci) {
        Vandalism.getInstance().stop();
    }

    @Inject(method = "onFinishedLoading", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;collectLoadTimes(Lnet/minecraft/client/MinecraftClient$LoadingContext;)V", shift = At.Shift.AFTER))
    private void vandalism$displayLoadingTime(final CallbackInfo ci) {
        if (!this.vandalism_loadingDisplayed) {
            this.vandalism_loadingDisplayed = true;
            Vandalism.getInstance().getLogger().info("");
            Vandalism.getInstance().getLogger().info("Minecraft loading took ~" + ManagementFactory.getRuntimeMXBean().getUptime() + "ms.");
            Vandalism.getInstance().getLogger().info("");
        }
    }

    @Inject(method = "updateWindowTitle", at = @At("HEAD"), cancellable = true)
    private void vandalism$forceCancelWindowTitleUpdates(final CallbackInfo ci) {
        ci.cancel();
    }

}