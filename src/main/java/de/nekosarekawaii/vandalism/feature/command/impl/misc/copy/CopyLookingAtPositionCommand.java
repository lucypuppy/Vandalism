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

package de.nekosarekawaii.vandalism.feature.command.impl.misc.copy;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.Command;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;

public class CopyLookingAtPositionCommand extends Command {

    public CopyLookingAtPositionCommand() {
        super(
                "Copies the position you are currently looking at into your clipboard.",
                Category.MISC,
                "copylookingatposition",
                "copylookpos",
                "copytargetedblock"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            final BlockPos lookingAt;
            if (mc.crosshairTarget instanceof final BlockHitResult blockHitResult) {
                lookingAt = blockHitResult.getBlockPos();
            } else if (mc.crosshairTarget instanceof final EntityHitResult entityHitResult) {
                lookingAt = entityHitResult.getEntity().getBlockPos();
            } else {
                lookingAt = null;
            }
            if (lookingAt != null) {
                mc.keyboard.setClipboard(mc.player.getBlockPos().toShortString().replace(",", ""));
                ChatUtil.infoChatMessage("Looking at position copied into the clipboard.");
            }
            return SINGLE_SUCCESS;
        });
    }

}
