/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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

package de.nekosarekawaii.vandalism.injection.mixins;

import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import net.minecraft.client.main.Main;
import net.minecraft.client.util.Icons;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;

@Mixin(Icons.class)
public abstract class MixinIcons {

    @Inject(method = "getIcon", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ResourcePack;openRoot([Ljava/lang/String;)Lnet/minecraft/resource/InputSupplier;"), cancellable = true)
    private void forceClientIcon(final ResourcePack resourcePack, final String fileName, final CallbackInfoReturnable<InputSupplier<InputStream>> cir) {
        if (fileName.endsWith(".png")) { // We just nope there aren't any other png files
            cir.setReturnValue(() -> Main.class.getClassLoader().getResourceAsStream("assets/" + FabricBootstrap.MOD_ID + "/textures/icon/" + fileName));
        }
    }

}
