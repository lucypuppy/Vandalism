package me.nekosarekawaii.foxglove.injection.mixins;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.event.ScreenListener;
import me.nekosarekawaii.foxglove.event.TickListener;
import me.nekosarekawaii.foxglove.event.WorldListener;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.misc.FastUseModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;onResolutionChanged()V"))
    private void injectInit(final CallbackInfo callbackInfo) {
        Foxglove.getInstance().start();
    }

    @Inject(method = "getWindowTitle", at = @At("RETURN"), cancellable = true)
    private void injectWindowTitle(final CallbackInfoReturnable<String> callbackInfo) {
        callbackInfo.setReturnValue(Foxglove.getInstance().getWindowTitle());
    }

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void injectTick(final CallbackInfo ci) {
        DietrichEvents2.global().postInternal(TickListener.TickEvent.ID, new TickListener.TickEvent());
    }

    @Inject(method = "setScreen", at = @At(value = "HEAD"), cancellable = true)
    private void injectSetScreen(Screen screen, final CallbackInfo ci) {
        final ScreenListener.OpenScreenEvent openScreenEvent = new ScreenListener.OpenScreenEvent(screen);
        DietrichEvents2.global().postInternal(ScreenListener.OpenScreenEvent.ID, openScreenEvent);
        if (openScreenEvent.isCancelled()) ci.cancel();
        else screen = openScreenEvent.screen;
    }

    @Inject(method = "setWorld", at = @At("HEAD"))
    private void injectSetWorld(final ClientWorld world, final CallbackInfo ci) {
        DietrichEvents2.global().postInternal(WorldListener.WorldLoadEvent.ID, new WorldListener.WorldLoadEvent());
    }

    @ModifyConstant(method = "doItemUse", constant = @Constant(intValue = 4))
    private int doItemUse(final int value) {
        final FastUseModule fastUseModule = Foxglove.getInstance().getModuleRegistry().getFastUseModule();
        if (fastUseModule.isEnabled()) return fastUseModule.itemUseCooldown.getValue();
        return value;
    }

}
