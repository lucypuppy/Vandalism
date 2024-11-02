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

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.render.TextDrawListener;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;
import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextVisitFactory.class)
public abstract class MixinTextVisitFactory {

    @Inject(method = "visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z", at = @At(value = "HEAD"))
    private static void callTextDrawListener(final String t, final int startIndex, final Style s, final Style resetStyle, final CharacterVisitor visitor, final CallbackInfoReturnable<Boolean> cir, final @Local(argsOnly = true) LocalRef<String> text, final @Local(argsOnly = true, ordinal = 0) LocalRef<Style> startingStyle) {
        final TextDrawListener.TextDrawEvent event = new TextDrawListener.TextDrawEvent(text.get(), startingStyle.get());
        Vandalism.getInstance().getEventSystem().callExceptionally(TextDrawListener.TextDrawEvent.ID, event);
        text.set(event.text);
        startingStyle.set(event.startingStyle);
    }

}