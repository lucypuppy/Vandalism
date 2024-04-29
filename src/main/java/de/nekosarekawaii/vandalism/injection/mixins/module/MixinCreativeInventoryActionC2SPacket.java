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

package de.nekosarekawaii.vandalism.injection.mixins.module;

import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CreativeInventoryActionC2SPacket.class)
public abstract class MixinCreativeInventoryActionC2SPacket {

    // TODO: Fix
    /*@Redirect(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketByteBuf;writeItemStack(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/network/PacketByteBuf;"))
    private PacketByteBuf hookConsoleSpammer(final PacketByteBuf instance, final ItemStack stack) {
        final NbtCompound nbt = stack.getNbt();
        if (stack.hasNbt() && nbt != null && nbt.contains(NBTModuleMode.MARKER)) {
            return NBTModuleMode.writeBuf(instance, nbt.getString(NBTModuleMode.MARKER));
        }
        return instance.writeItemStack(stack);
    }*/

}
