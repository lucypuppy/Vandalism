/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.ScreenListener;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.base.event.network.WorldListener;
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
    private void callTickGameListener(final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().postInternal(TickGameListener.TickGameEvent.ID, new TickGameListener.TickGameEvent());
    }

    @Inject(method = "setScreen", at = @At(value = "HEAD"), cancellable = true)
    private void callScreenListener(Screen screen, final CallbackInfo ci) {
        if (vandalism$selfCall) {
            vandalism$selfCall = false;
            return;
        }
        final var openScreenEvent = new ScreenListener.ScreenEvent(screen);
        Vandalism.getInstance().getEventSystem().postInternal(ScreenListener.ScreenEvent.ID, openScreenEvent);
        if (openScreenEvent.isCancelled()) {
            ci.cancel();
        }
        if (!Objects.equals(screen, openScreenEvent.screen)) {
            this.vandalism$selfCall = true;
            this.setScreen(openScreenEvent.screen);
        }
    }

    @Inject(method = "setWorld", at = @At("HEAD"))
    private void callWorldListener_Pre(final ClientWorld world, final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().postInternal(WorldListener.WorldLoadEvent.ID, new WorldListener.WorldLoadEvent(WorldListener.WorldEventState.PRE));
    }

    @Inject(method = "setWorld", at = @At("RETURN"))
    private void callWorldListener_Post(final ClientWorld world, final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().postInternal(WorldListener.WorldLoadEvent.ID, new WorldListener.WorldLoadEvent(WorldListener.WorldEventState.POST));
    }

    @Inject(method = "onResolutionChanged", at = @At("RETURN"))
    public void callScreenListener(final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().postInternal(ScreenListener.ScreenEvent.ID, new ScreenListener.ScreenEvent());
    }

}