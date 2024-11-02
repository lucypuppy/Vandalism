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

package de.nekosarekawaii.vandalism.util;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ItemStackUtil implements MinecraftWrapper {

    private static final SimpleCommandExceptionType NOT_IN_GAME = new SimpleCommandExceptionType(Text.literal("You need to be in-game to get items!"));

    private static final SimpleCommandExceptionType NOT_IN_CREATIVE_MODE = new SimpleCommandExceptionType(Text.literal("You must be in creative mode to use this."));

    public static ItemStack appendEnchantmentToItemStack(final ItemStack stack, final RegistryKey<Enchantment> enchantment, final int level) {
        EnchantmentHelper.apply(stack, builder -> builder.add(getEnchantment(enchantment), level));
        return stack;
    }

    public static RegistryEntry<Enchantment> getEnchantment(final RegistryKey<Enchantment> enchantment) {
        if (mc.world == null) {
            throw new IllegalStateException("World is null!");
        }
        final RegistryWrapper.Impl<Enchantment> registry = mc.world.getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return registry.getOptional(enchantment).orElse(null);
    }

    public static void giveItemStack(final ItemStack itemStack) {
        giveItemStack(itemStack, true);
    }

    public static boolean giveItemStack(final ItemStack itemStack, final boolean receiveMessage) {
        return giveItemStack(itemStack, receiveMessage, mc.player.getInventory().selectedSlot + MinecraftConstants.FIRST_SLOT_IN_HOTBAR);
    }

    public static boolean giveItemStack(final ItemStack itemStack, final boolean receiveMessage, final int selectedSlot) {
        try {
            if (mc.player == null || mc.getNetworkHandler() == null) throw NOT_IN_GAME.create();
            if (!mc.player.getAbilities().creativeMode) throw NOT_IN_CREATIVE_MODE.create();
            mc.getNetworkHandler().sendPacket(new CreativeInventoryActionC2SPacket(selectedSlot, itemStack));
            if (receiveMessage) {
                ChatUtil.infoChatMessage("You should have received " + Formatting.GRAY + "'" + Formatting.DARK_AQUA + itemStack.getName().getString() + Formatting.GRAY + "'");
            }
            return true;
        } catch (Throwable throwable) {
            ChatUtil.errorChatMessage("Failed to give item cause of: " + throwable.getMessage());
        }
        return false;
    }

}
