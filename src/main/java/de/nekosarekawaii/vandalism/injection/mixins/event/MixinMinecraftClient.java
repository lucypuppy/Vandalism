/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.florianmichael.dietrichevents2.StateTypes;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.game.*;
import de.nekosarekawaii.vandalism.event.render.ScreenListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(value = MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Unique
    private boolean vandalism$selfInflicted = false;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;onResolutionChanged()V"))
    private void callMinecraftBootstrapListener(final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().callExceptionally(
                MinecraftBoostrapListener.MinecraftBootstrapEvent.ID,
                new MinecraftBoostrapListener.MinecraftBootstrapEvent((MinecraftClient) (Object) this)
        );
    }

    @Inject(method = "close", at = @At(value = "HEAD"))
    private void callShutdownProcessListener(final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().callExceptionally(
                ShutdownProcessListener.ShutdownProcessEvent.ID,
                new ShutdownProcessListener.ShutdownProcessEvent()
        );
    }

    @Inject(method = "setScreen", at = @At(value = "HEAD"), cancellable = true)
    private void callScreenListener(Screen screen, final CallbackInfo ci) {
        if (vandalism$selfInflicted) {
            vandalism$selfInflicted = false;
            return;
        }
        final ScreenListener.ScreenEvent event = new ScreenListener.ScreenEvent(screen);
        Vandalism.getInstance().getEventSystem().callExceptionally(ScreenListener.ScreenEvent.ID, event);
        if (event.isCancelled()) {
            ci.cancel();
        }
        if (!Objects.equals(screen, event.screen)) {
            this.vandalism$selfInflicted = true;
            this.setScreen(event.screen);
        }
    }

    @Inject(method = "setWorld", at = @At("HEAD"))
    private void callWorldListener_Pre(final ClientWorld world, final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().callExceptionally(WorldListener.WorldLoadEvent.ID, new WorldListener.WorldLoadEvent(StateTypes.PRE));
    }

    @Inject(method = "setWorld", at = @At("RETURN"))
    private void callWorldListener_Post(final ClientWorld world, final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().callExceptionally(WorldListener.WorldLoadEvent.ID, new WorldListener.WorldLoadEvent(StateTypes.POST));
    }

    @Inject(method = "onResolutionChanged", at = @At("RETURN"))
    public void callScreenListener(final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().callExceptionally(ScreenListener.ScreenEvent.ID, new ScreenListener.ScreenEvent());
    }

    @Inject(method = "getTargetMillisPerTick", at = @At("RETURN"), cancellable = true)
    public void callTickTimeListener(final float millis, final CallbackInfoReturnable<Float> cir) {
        final TickTimeListener.TickTimeEvent event = new TickTimeListener.TickTimeEvent(cir.getReturnValue());
        Vandalism.getInstance().getEventSystem().callExceptionally(TickTimeListener.TickTimeEvent.ID, event);
        cir.setReturnValue(event.tickTime);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    public int callMaxClientTickListener(int a, int b) {
        return b;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;getMeasuringTimeMs()J", ordinal = 0))
    public long getTime() {
        final TimeTravelListener.TimeTravelEvent event = new TimeTravelListener.TimeTravelEvent(Util.getMeasuringTimeMs());
        Vandalism.getInstance().getEventSystem().callExceptionally(TimeTravelListener.TimeTravelEvent.ID, event);
        return event.time;
    }

}
