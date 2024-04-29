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

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public abstract class MixinItemStack {

    // TODO: Fix
    /*@Redirect(method = "hasGlint", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;hasGlint(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean applyClientsideGlint(final Item instance, final ItemStack stack) {
        final var nbt = stack.getNbt();
        return instance.hasGlint(stack) || (nbt != null && nbt.contains(CreativeTabManager.CLIENTSIDE_GLINT));
    }*/

}