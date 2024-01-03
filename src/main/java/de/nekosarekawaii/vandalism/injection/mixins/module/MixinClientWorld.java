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
import de.nekosarekawaii.vandalism.feature.module.impl.render.TrueSightModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld {

    @Inject(method = "getBlockParticle", at = @At(value = "HEAD"), cancellable = true)
    private void hookTrueSight(final CallbackInfoReturnable<Block> cir) {
        final TrueSightModule trueSightModule = Vandalism.getInstance().getModuleManager().getTrueSightModule();
        final Block block = cir.getReturnValue();
        if (block != null && trueSightModule.isActive() && trueSightModule.markerBlocks.isSelected(block.asItem())) {
            cir.setReturnValue(null);
        }
    }

    @Redirect(method = "randomBlockDisplayTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", ordinal = 1))
    private Block hookTrueSight(final BlockState instance) {
        final TrueSightModule trueSightModule = Vandalism.getInstance().getModuleManager().getTrueSightModule();
        final Block block = instance.getBlock();
        if (trueSightModule.isActive() && trueSightModule.markerBlocks.isSelected(block.asItem())) {
            if (ClientWorld.BLOCK_MARKER_ITEMS.contains(block.asItem())) {
                return null;
            }
        }
        return block;
    }

}
