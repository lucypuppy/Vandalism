/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class CopySignTextCommand extends AbstractCommand {

    public CopySignTextCommand() {
        super(
                "Copies all the text from the sign you are currently looking at into your clipboard.",
                Category.MISC,
                "copysigntext",
                "signtextcopy"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            if (mc.crosshairTarget instanceof final BlockHitResult hitResult) {
                final BlockPos pos = hitResult.getBlockPos();
                final BlockEntity blockEntity = mc.world.getBlockEntity(pos);
                if (blockEntity instanceof final SignBlockEntity signBlockEntity) {
                    final ServerInfo serverInfo = mc.getCurrentServerEntry();
                    final StringBuilder textBuilder = new StringBuilder("Text from a sign on " + (mc.isInSingleplayer() || serverInfo == null ? "singleplayer" : serverInfo.address) + "\n\n");
                    textBuilder.append("[Position]");
                    textBuilder.append("\n X:");
                    textBuilder.append(pos.getX());
                    textBuilder.append("\n Y:");
                    textBuilder.append(pos.getY());
                    textBuilder.append("\n Z:");
                    textBuilder.append(pos.getZ());
                    textBuilder.append("\n\n");
                    boolean containsText = false;
                    final SignText frontText = signBlockEntity.getText(true);
                    if (frontText != null) {
                        final Text[] frontLines = frontText.getMessages(false);
                        if (frontLines.length > 0) {
                            containsText = true;
                        }
                        textBuilder.append("[Front]\n");
                        for (int i = 0; i < frontLines.length; i++) {
                            final Text line = frontLines[i];
                            textBuilder.append(" Line ");
                            textBuilder.append(i + 1);
                            textBuilder.append(": ");
                            textBuilder.append(line.getString());
                            textBuilder.append("\n");
                        }
                    }
                    final SignText backText = signBlockEntity.getText(false);
                    if (backText != null) {
                        final Text[] backLines = backText.getMessages(false);
                        if (backLines.length > 0) {
                            containsText = true;
                        }
                        textBuilder.append("\n\n[Back]\n");
                        for (int i = 0; i < backLines.length; i++) {
                            final Text line = backLines[i];
                            textBuilder.append(" Line ");
                            textBuilder.append(i + 1);
                            textBuilder.append(": ");
                            textBuilder.append(line.getString());
                            textBuilder.append("\n");
                        }
                    }
                    if (containsText) {
                        this.mc.keyboard.setClipboard(textBuilder.toString());
                        ChatUtil.infoChatMessage("Sign text copied into the clipboard.");
                    }
                    else {
                        ChatUtil.errorChatMessage("The sign you are looking at does not contain any text.");
                    }
                }
                else {
                    ChatUtil.errorChatMessage("The block you are looking at is not a sign.");
                }
            }
            else {
                ChatUtil.errorChatMessage("You are not looking at a block.");
            }
            return SINGLE_SUCCESS;
        });
    }

}
