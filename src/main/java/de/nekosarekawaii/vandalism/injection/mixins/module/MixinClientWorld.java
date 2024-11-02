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

package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.render.TrueSightModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientWorld.class)
public abstract class MixinClientWorld {

    @Redirect(method = "getBlockParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;getCurrentGameMode()Lnet/minecraft/world/GameMode;"))
    private GameMode hookTrueSight(final ClientPlayerInteractionManager instance) {
        if (Vandalism.getInstance().getModuleManager().getTrueSightModule().isActive()) {
            return GameMode.CREATIVE;
        }
        return instance.getCurrentGameMode();
    }

    @Redirect(method = "getBlockParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
    private Item hookTrueSight(final ItemStack instance) {
        if (Vandalism.getInstance().getModuleManager().getTrueSightModule().isActive()) {
            return Items.BARRIER;
        }
        return instance.getItem();
    }

    @Redirect(method = "randomBlockDisplayTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    private void hookTrueSight(final ClientWorld instance, final ParticleEffect parameters, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
        final TrueSightModule trueSightModule = Vandalism.getInstance().getModuleManager().getTrueSightModule();
        if (parameters instanceof final BlockStateParticleEffect particleEffect) {
            final BlockState blockState = particleEffect.getBlockState();
            if (blockState != null) {
                final Block block = blockState.getBlock();
                if (block != null) {
                    if (trueSightModule.isActive() && !trueSightModule.markerBlocks.isSelected(block.asItem())) {
                        return;
                    }
                }
            }
        }
        instance.addParticle(parameters, x, y, z, velocityX, velocityY, velocityZ);
    }

}
