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

package de.nekosarekawaii.vandalism.feature.command.impl.misc.module;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.feature.command.arguments.ModuleArgumentType;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import net.minecraft.command.CommandSource;

public class ModuleUnbindCommand extends AbstractCommand {

    public ModuleUnbindCommand() {
        super("Lets you unbind modules.", Category.MISC, "moduleunbind", "unbind");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("module", ModuleArgumentType.create()).executes(context -> {
            final AbstractModule module = ModuleArgumentType.get(context);
            if (module.getKeyBind().isValid()) {
                module.getKeyBind().resetValue();
                ChatUtil.infoChatMessage("Unbound module " + module.getName() + ".");
            } else {
                ChatUtil.infoChatMessage("Module " + module.getName() + " is not bound.");
            }
            return SINGLE_SUCCESS;
        }));
    }

}
