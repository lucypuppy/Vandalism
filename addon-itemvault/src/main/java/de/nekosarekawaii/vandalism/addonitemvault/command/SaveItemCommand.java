/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.addonitemvault.command;

import com.itemvault.fabric_platform_api.ItemVaultFabricBase;
import com.itemvault.fabric_platform_api.WrappedItemStack;
import com.itemvault.file_format.WrappedItem;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class SaveItemCommand extends Command {

    private ItemVaultFabricBase instance;

    public SaveItemCommand(final ItemVaultFabricBase instance) {
        super("", Category.MISC, "saveitem");

        this.instance = instance;
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("displayName", StringArgumentType.string())).executes(context -> {
            final ItemStack stack = mc.player.getMainHandStack();
            if (stack.isEmpty())
                return 0;

            instance.addItem(new WrappedItem<>(
                    StringArgumentType.getString(context, "displayName"),
                    Registries.ITEM.getId(stack.getItem()).toString(),
                    new WrappedItemStack(stack)
            ));
            return SINGLE_SUCCESS;
        });
    }
}
