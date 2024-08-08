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

package de.nekosarekawaii.vandalism.feature.command.impl.movement.clip;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.util.MovementUtil;
import net.minecraft.command.CommandSource;

public class HClipCommand extends AbstractCommand {

    public HClipCommand() {
        super("Allows you to teleport yourself by horizontal offset.", Category.MOVEMENT, "hclip", "htp", "horizontalteleport");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("horizontal-offset", DoubleArgumentType.doubleArg(-200.0, 200.0)).executes(context -> {
            if (this.mc.player != null) {
                MovementUtil.bypassClip(DoubleArgumentType.getDouble(context, "horizontal-offset"), 0);
            }
            return SINGLE_SUCCESS;
        }));
    }

}
