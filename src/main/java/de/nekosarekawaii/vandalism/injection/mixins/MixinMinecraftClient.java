package de.nekosarekawaii.vandalism.injection.mixins;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.MinecraftBoostrapListener;
import de.nekosarekawaii.vandalism.base.event.game.ShutdownProcessListener;
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
    private boolean vandalism$loadingDisplayed = false;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;onResolutionChanged()V"))
    private void callMinecraftBootstrapListener(final CallbackInfo ci) {
        DietrichEvents2.global().postInternal(
                MinecraftBoostrapListener.MinecraftBootstrapEvent.ID,
                new MinecraftBoostrapListener.MinecraftBootstrapEvent((MinecraftClient) (Object) this)
        );
    }

    @Inject(method = "close", at = @At(value = "HEAD"))
    private void callShutdownProcessListener(final CallbackInfo ci) {
        DietrichEvents2.global().postInternal(
                ShutdownProcessListener.ShutdownProcessEvent.ID,
                new ShutdownProcessListener.ShutdownProcessEvent()
        );
    }

    @Inject(method = "onFinishedLoading", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;collectLoadTimes(Lnet/minecraft/client/MinecraftClient$LoadingContext;)V", shift = At.Shift.AFTER))
    private void displayLoadingTime(final CallbackInfo ci) {
        if (!this.vandalism$loadingDisplayed) {
            this.vandalism$loadingDisplayed = true;
            Vandalism.getInstance().getLogger().info("");
            Vandalism.getInstance().getLogger().info("Minecraft loading took ~" + ManagementFactory.getRuntimeMXBean().getUptime() + "ms.");
            Vandalism.getInstance().getLogger().info("");
        }
    }

    @Inject(method = "updateWindowTitle", at = @At("HEAD"), cancellable = true)
    private void forceCancelWindowTitleUpdates(final CallbackInfo ci) {
        ci.cancel();
    }

}