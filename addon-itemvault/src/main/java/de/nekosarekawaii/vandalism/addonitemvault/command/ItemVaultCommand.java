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
import com.itemvault.fabric_platform_api.commands.CommandBase;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.Command;
import net.minecraft.command.CommandSource;

public class ItemVaultCommand extends Command {

    private final ItemVaultFabricBase vaultHolder;

    public ItemVaultCommand(final ItemVaultFabricBase vaultHolder) {
        super(null, Category.MISC, "itemvault");

        this.vaultHolder = vaultHolder;
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        for (final CommandBase command : this.vaultHolder.commands()) {
            final LiteralArgumentBuilder<CommandSource> node = literal(command.name());
            command.build(node);
            builder.then(node);
        }
    }

}
