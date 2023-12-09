package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.ScreenListener;
import de.vandalismdevelopment.vandalism.base.event.TickListener;
import de.vandalismdevelopment.vandalism.base.event.WorldListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void vandalism$callTickEvent(final CallbackInfo ci) {
        DietrichEvents2.global().postInternal(TickListener.TickEvent.ID, new TickListener.TickEvent());
    }

    @Inject(method = "setScreen", at = @At(value = "HEAD"), cancellable = true)
    private void vandalism$callOpenScreenEvent(Screen screen, final CallbackInfo ci) {
        final ScreenListener.ScreenEvent openScreenEvent = new ScreenListener.ScreenEvent(screen);
        DietrichEvents2.global().postInternal(ScreenListener.ScreenEvent.ID, openScreenEvent);
        if (openScreenEvent.isCancelled()) ci.cancel();
        else screen = openScreenEvent.screen;
    }

    @Inject(method = "setWorld", at = @At("HEAD"))
    private void vandalism$callPreWorldLoadEvent(final ClientWorld world, final CallbackInfo ci) {
        DietrichEvents2.global().postInternal(WorldListener.WorldLoadEvent.ID, new WorldListener.WorldLoadEvent(WorldListener.WorldEventState.PRE));
    }

    @Inject(method = "setWorld", at = @At("RETURN"))
    private void vandalism$callPostWorldLoadEvent(final ClientWorld world, final CallbackInfo ci) {
        DietrichEvents2.global().postInternal(WorldListener.WorldLoadEvent.ID, new WorldListener.WorldLoadEvent(WorldListener.WorldEventState.POST));
    }

    @Inject(method = "onResolutionChanged", at = @At("RETURN"))
    public void vandalism$callScreenEvent(final CallbackInfo ci) {
        DietrichEvents2.global().postInternal(ScreenListener.ScreenEvent.ID, new ScreenListener.ScreenEvent());
    }

}