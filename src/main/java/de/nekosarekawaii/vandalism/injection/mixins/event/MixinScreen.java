/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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
import de.nekosarekawaii.vandalism.event.render.Render2DListener;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V", shift = At.Shift.BEFORE))
    private void callRender2DListener_Pre(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().callExceptionally(Render2DListener.Render2DEvent.ID, new Render2DListener.Render2DEvent(context, mouseX, mouseY, delta, false));
    }

    @Inject(method = "render", at = @At(value = "RETURN"))
    private void callRender2DListener_Post(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        Vandalism.getInstance().getEventSystem().callExceptionally(Render2DListener.Render2DEvent.ID, new Render2DListener.Render2DEvent(context, mouseX, mouseY, delta, true));
    }

}
