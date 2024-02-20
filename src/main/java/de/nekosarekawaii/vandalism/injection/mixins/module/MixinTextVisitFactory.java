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

package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.feature.module.impl.render.VisualThrottleModule;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;
import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextVisitFactory.class)
public abstract class MixinTextVisitFactory {

    @Inject(method = "visitForwards", at = @At("HEAD"), cancellable = true)
    private static void hookVisualThrottle(final String text, final Style style, final CharacterVisitor visitor, final CallbackInfoReturnable<Boolean> cir) {
        if (!FabricBootstrap.INITIALIZED) return;
        final VisualThrottleModule visualThrottleModule = Vandalism.getInstance().getModuleManager().getVisualThrottleModule();
        if (visualThrottleModule.isActive() && visualThrottleModule.blockTooLongTexts.getValue()) {
            if (text.length() >= visualThrottleModule.countToBlockTooLongTexts.getValue()) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "visitBackwards", at = @At("HEAD"), cancellable = true)
    private static void hookVisualThrottle2(final String text, final Style style, final CharacterVisitor visitor, final CallbackInfoReturnable<Boolean> cir) {
        if (!FabricBootstrap.INITIALIZED) return;
        final VisualThrottleModule visualThrottleModule = Vandalism.getInstance().getModuleManager().getVisualThrottleModule();
        if (visualThrottleModule.isActive() && visualThrottleModule.blockTooLongTexts.getValue()) {
            if (text.length() >= visualThrottleModule.countToBlockTooLongTexts.getValue()) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z", at = @At("HEAD"), cancellable = true)
    private static void hookVisualThrottle3(final String text, final int startIndex, final Style startingStyle, final Style resetStyle, final CharacterVisitor visitor, final CallbackInfoReturnable<Boolean> cir) {
        if (!FabricBootstrap.INITIALIZED) return;
        final VisualThrottleModule visualThrottleModule = Vandalism.getInstance().getModuleManager().getVisualThrottleModule();
        if (visualThrottleModule.isActive() && visualThrottleModule.blockTooLongTexts.getValue()) {
            if (text.length() >= visualThrottleModule.countToBlockTooLongTexts.getValue()) {
                cir.setReturnValue(true);
            }
        }
    }

}
