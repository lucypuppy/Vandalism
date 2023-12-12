package de.vandalismdevelopment.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.base.event.game.ScreenListener;
import de.vandalismdevelopment.vandalism.base.event.game.TickGameListener;
import de.vandalismdevelopment.vandalism.base.event.network.WorldListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(value = MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow public abstract void setScreen(@Nullable Screen screen);

    @Unique
    private boolean vandalism$selfCall = false;

    @Inject(method = "tick", at = @At(value = "HEAD"))
    private void vandalism$callTickEvent(final CallbackInfo ci) {
        DietrichEvents2.global().postInternal(TickGameListener.TickGameEvent.ID, new TickGameListener.TickGameEvent());
    }

    @Inject(method = "setScreen", at = @At(value = "HEAD"), cancellable = true)
    private void vandalism$callOpenScreenEvent(Screen screen, final CallbackInfo ci) {
        if (vandalism$selfCall) {
            vandalism$selfCall = false;
            return;
        }
        final var openScreenEvent = new ScreenListener.ScreenEvent(screen);
        DietrichEvents2.global().postInternal(ScreenListener.ScreenEvent.ID, openScreenEvent);
        if (openScreenEvent.isCancelled()) {
            ci.cancel();
        }
        if (!Objects.equals(screen, openScreenEvent.screen)) {
            this.vandalism$selfCall = true;
            this.setScreen(openScreenEvent.screen);
        }
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