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

package de.nekosarekawaii.vandalism.feature.command.impl.misc;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.feature.command.arguments.EnchantmentArgumentType;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import de.nekosarekawaii.vandalism.util.game.ItemStackUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

public class EnchantCommand extends AbstractCommand {

    public EnchantCommand() {
        super(
                "Enchants the item in your main hand with a specified enchantment.",
                Category.MISC,
                "enchant",
                "enchantitem",
                "itemenchant"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("enchantment", EnchantmentArgumentType.create())
                .then(argument("level", IntegerArgumentType.integer(Short.MIN_VALUE, Short.MAX_VALUE))
                        .executes(context -> {
                                    this.enchantItem(EnchantmentArgumentType.get(context), IntegerArgumentType.getInteger(context, "level"));
                                    return SINGLE_SUCCESS;
                                }
                        )
                )
        );
    }

    private void enchantItem(final Enchantment enchantment, final int level) {
        final ItemStack stack = this.mc.player.getInventory().getMainHandStack();
        if (stack != null && !stack.isEmpty()) {
            if (ItemStackUtil.giveItemStack(ItemStackUtil.appendEnchantmentToItemStack(stack, enchantment, level), false)) {
                ChatUtil.infoChatMessage("Enchanted the item in your main hand.");
            } else {
                ChatUtil.errorChatMessage("Failed to enchant the item in your main hand.");
            }
        } else {
            ChatUtil.errorChatMessage("You must hold an item in your main hand.");
        }
    }

}
