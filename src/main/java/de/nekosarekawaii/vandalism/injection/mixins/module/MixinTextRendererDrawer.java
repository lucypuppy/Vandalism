/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.exploitfixer.ExploitFixerModule;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/client/font/TextRenderer$Drawer")
public abstract class MixinTextRendererDrawer {

    @Unique
    private int vandalism$glyphCount;

    @Inject(method = "accept", at = @At("HEAD"), cancellable = true)
    private void hookExploitFixer(final int i, final Style style, final int j, final CallbackInfoReturnable<Boolean> cir) {
        if (!FabricBootstrap.INITIALIZED) return;
        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleManager().getExploitFixerModule();
        if (exploitFixerModule.isActive() && exploitFixerModule.renderSettings.blockTooManyTextGlyphs.getValue()) {
            this.vandalism$glyphCount++;
            if (this.vandalism$glyphCount >= exploitFixerModule.renderSettings.countToBlockTextGlyphs.getValue()) {
                cir.setReturnValue(false);
            }
        }
    }

}
