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

package de.nekosarekawaii.vandalism.feature.command.impl.movement.clip;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.util.game.MovementUtil;
import net.minecraft.command.CommandSource;

public class ClipCommand extends AbstractCommand {

    public ClipCommand() {
        super("Allows you to teleport yourself by vertical and horizontal offset.", Category.MOVEMENT, "clip", "tp", "teleport");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.
                then(argument("vertical-offset", DoubleArgumentType.doubleArg(-10.0, 10.0)).
                        then(argument("horizontal-offset", DoubleArgumentType.doubleArg(-10.0, 10.0)).
                                executes(context -> {
                                    if (this.mc.player != null)
                                        MovementUtil.clip(
                                                DoubleArgumentType.getDouble(context, "vertical-offset"),
                                                DoubleArgumentType.getDouble(context, "horizontal-offset")
                                        );

                                    return SINGLE_SUCCESS;
                                })));
    }

}
