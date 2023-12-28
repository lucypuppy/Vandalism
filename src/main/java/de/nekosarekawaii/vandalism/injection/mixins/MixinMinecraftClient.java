package de.nekosarekawaii.vandalism.injection.mixins;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.MinecraftBoostrapListener;
import de.nekosarekawaii.vandalism.base.event.game.ShutdownProcessListener;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.management.ManagementFactory;

@Mixin(value = MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Unique
    private boolean vandalism$loadingDisplayed = false;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;onResolutionChanged()V"))
    private void callMinecraftBootstrapListener(final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().postInternal(
                MinecraftBoostrapListener.MinecraftBootstrapEvent.ID,
                new MinecraftBoostrapListener.MinecraftBootstrapEvent((MinecraftClient) (Object) this)
        );
    }

    @Inject(method = "close", at = @At(value = "HEAD"))
    private void callShutdownProcessListener(final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().postInternal(
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

    @Inject(method = "doAttack", at = @At("HEAD"))
    public void doAttack(CallbackInfoReturnable<Boolean> cir) {
        Vandalism.getInstance().getHudManager().infoHUDElement.leftClick.click();
    }

    @Inject(method = "doItemUse", at = @At("HEAD"))
    public void doItemUse(CallbackInfo ci) {
        Vandalism.getInstance().getHudManager().infoHUDElement.rightClick.click();
    }

}