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

package de.nekosarekawaii.vandalism.injection.mixins.integration;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.integration.newrotation.Rotation;
import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity implements MinecraftWrapper {

    @Redirect(method = "tickNewAi", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F"))
    private float modifyRotationYaw(final PlayerEntity instance) {
        if (this.mc.player == (PlayerEntity) (Object) this) {
            final Rotation rotation = Vandalism.getInstance().getRotationManager().getRotation();
            if (rotation != null) return rotation.getYaw();
        }
        return instance.getYaw();
    }

}