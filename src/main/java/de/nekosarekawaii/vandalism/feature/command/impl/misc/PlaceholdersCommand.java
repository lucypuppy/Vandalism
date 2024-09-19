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

package de.nekosarekawaii.vandalism.feature.command.impl.misc;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.command.Command;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.Placeholders;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PlaceholdersCommand extends Command {

    public PlaceholdersCommand() {
        super(
                "Shows a list of all available placeholders that you can use in some commands or modules.",
                Category.MISC,
                "placeholders",
                "placeholderlist",
                "listplaceholders",
                "showplaceholders"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> this.placeholders(1));
        builder.then(argument("page", IntegerArgumentType.integer(0)).executes(context -> this.placeholders(IntegerArgumentType.getInteger(context, "page"))));
    }

    private int placeholders(final int pageInput) {
        final int maxPlaceholdersPerPage = 5;
        final int totalPlaceholders = Placeholders.values().length;
        int maxPages = (int) Math.ceil((double) totalPlaceholders / maxPlaceholdersPerPage) - 1;
        int page = Math.max(0, Math.min(pageInput - 1, maxPages));
        ChatUtil.emptyChatMessage(false);
        ChatUtil.chatMessage(Text.literal(
                Formatting.DARK_GRAY + "[" + Formatting.GOLD + "Page " +
                        Formatting.DARK_AQUA + (page + 1) + Formatting.GRAY + " / " + Formatting.DARK_AQUA + (maxPages + 1) +
                        Formatting.DARK_GRAY + " | " + Formatting.GOLD + "Placeholders" + Formatting.GRAY + ": " + Formatting.DARK_AQUA + totalPlaceholders + Formatting.DARK_GRAY + "]"
        ));
        ChatUtil.emptyChatMessage(false);
        final String placeholderChar = Placeholders.PLACEHOLDER_CHAR;
        for (int i = page * maxPlaceholdersPerPage; i < Math.min((page + 1) * maxPlaceholdersPerPage, totalPlaceholders); i++) {
            final Placeholders placeholder = Placeholders.values()[i];
            final String placeholderValue = placeholderChar + placeholder.name().toLowerCase() + placeholderChar;
            final MutableText placeholderText = Text.literal(placeholderValue);
            placeholderText.formatted(Formatting.YELLOW);
            placeholderText.append(Text.literal(" > ").formatted(Formatting.DARK_GRAY));
            placeholderText.styled(style ->
                    style.withClickEvent(new ClickEvent(
                            ClickEvent.Action.COPY_TO_CLIPBOARD,
                            placeholderValue
                    )).withHoverEvent(new HoverEvent(
                            HoverEvent.Action.SHOW_TEXT,
                            Text.literal("Copy the placeholder to your clipboard")
                    ))
            );
            String description = placeholder.getDescription();
            if (description.isEmpty()) {
                description = "No description available.";
            }
            placeholderText.append(Text.literal(description).formatted(Formatting.GRAY));
            ChatUtil.chatMessage(placeholderText);
        }
        page++;
        maxPages += 2;
        final int prevPage = Math.max(1, page - 1);
        final int nextPage = Math.min(maxPages, page + 1);
        final MutableText buttons = Text.literal("");
        if (prevPage != page) {
            final MutableText prevPageButton = Text.literal("<< Previous Page")
                    .formatted(Formatting.RED)
                    .styled(style -> style
                            .withClickEvent(Vandalism.getInstance().getCommandManager().generateClickEvent("placeholders " + prevPage))
                            .withHoverEvent(new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT, Text.literal("Go to page " + prevPage))
                            )
                    );
            buttons.append(prevPageButton);
            if (nextPage < maxPages) {
                buttons.append(Text.literal(" | ").formatted(Formatting.GRAY));
            }
        }
        if (nextPage < maxPages) {
            final MutableText nextPageButton = Text.literal("Next Page >>")
                    .formatted(Formatting.GREEN)
                    .styled(style -> style
                            .withClickEvent(Vandalism.getInstance().getCommandManager().generateClickEvent("placeholders " + nextPage))
                            .withHoverEvent(new HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT, Text.literal("Go to page " + nextPage))
                            )
                    );
            buttons.append(nextPageButton);
        }
        ChatUtil.emptyChatMessage(false);
        ChatUtil.chatMessage(buttons);
        ChatUtil.emptyChatMessage(false);
        return SINGLE_SUCCESS;
    }

}
