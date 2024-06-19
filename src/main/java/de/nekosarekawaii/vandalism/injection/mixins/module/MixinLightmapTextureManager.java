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

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.render.FullBrightModule;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.awt.*;

@Mixin(LightmapTextureManager.class)
public abstract class MixinLightmapTextureManager {

    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;setColor(III)V"))
    private void hookFullBright(final NativeImage instance, final int x, final int y, final int color, final Operation<Void> original) {
        final FullBrightModule fullBrightModule = Vandalism.getInstance().getModuleManager().getFullBrightModule();
        if (fullBrightModule.isActive() && !fullBrightModule.useEffect.getValue()) {
            original.call(instance, x, y, Color.WHITE.getRGB());
        } else {
            original.call(instance, x, y, color);
        }
    }

}
