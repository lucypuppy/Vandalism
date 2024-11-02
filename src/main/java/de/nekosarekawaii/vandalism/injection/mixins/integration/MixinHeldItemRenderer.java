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

package de.nekosarekawaii.vandalism.injection.mixins.integration;

import de.nekosarekawaii.vandalism.Vandalism;
import net.minecraft.client.render.item.HeldItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class MixinHeldItemRenderer {

    @Shadow
    private float equipProgressMainHand;

    @Shadow
    private float equipProgressOffHand;

    @Shadow
    private float prevEquipProgressMainHand;

    @Shadow
    private float prevEquipProgressOffHand;

    @Inject(method = "updateHeldItems", at = @At("HEAD"))
    public void updateHeldItemsPre(final CallbackInfo ci) {
        if (!Vandalism.getInstance().getClientSettings().getVisualSettings().removeEquipAnimation.getValue()) {
            return;
        }

        this.equipProgressMainHand = 0.0F;
        this.prevEquipProgressMainHand = 0.0F;
        this.equipProgressOffHand = 0.0F;
        this.prevEquipProgressOffHand = 0.0F;
    }

    @Inject(method = "updateHeldItems", at = @At("RETURN"))
    public void updateHeldItemsPost(final CallbackInfo ci) {
        if (!Vandalism.getInstance().getClientSettings().getVisualSettings().removeEquipAnimation.getValue()) {
            return;
        }

        this.equipProgressMainHand = 1.0F;
        this.prevEquipProgressMainHand = 1.0F;
        this.equipProgressOffHand = 1.0F;
        this.prevEquipProgressOffHand = 1.0F;
    }

}
