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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.StringUtils;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ShowClickEventsModule extends Module {

    private final ColorValue clickEventColor = new ColorValue(
            this,
            "Click Event Color",
            "The color message indicators should get if a click event is present.",
            new Color(221, 96, 10)
    );

    public ShowClickEventsModule() {
        super(
                "Show Click Events",
                "Shows click events from text components in chat via message indicators.",
                Category.MISC
        );
    }

    public MessageIndicator modifyIndicator(final Text message, final MessageIndicator indicator) {
        final List<ClickEvent> clickEvents = new ArrayList<>();
        final Style style = message.getStyle();
        if (style != null) {
            final ClickEvent clickEvent = style.getClickEvent();
            if (clickEvent != null) {
                clickEvents.add(clickEvent);
            }
        }
        for (final Text sibling : message.getSiblings()) {
            final Style siblingStyle = sibling.getStyle();
            if (siblingStyle != null) {
                final ClickEvent siblingClickEvent = siblingStyle.getClickEvent();
                if (siblingClickEvent != null) {
                    clickEvents.add(siblingClickEvent);
                }
            }
        }
        final int size = clickEvents.size();
        if (size < 1) return indicator;
        final MutableText text = Text.empty();
        final MutableText clickEventData = Text.literal("\n\n");
        for (int i = 0; i < clickEvents.size(); i++) {
            final ClickEvent clickEvent = clickEvents.get(i);
            final String action = StringUtils.normalizeEnumName(clickEvent.getAction().name());
            final String value = clickEvent.getValue();
            clickEventData.append(Text.literal(
                    Formatting.DARK_GRAY + "[" +
                            Formatting.GOLD + "Click Event" +
                            (size > 1 ? " " + Formatting.DARK_AQUA + (i < 10 ? "0" : "") + (i + 1) : "") +
                            Formatting.DARK_GRAY + "]\n" +
                            Formatting.YELLOW + "Action" +
                            Formatting.GRAY + ": " +
                            Formatting.GREEN + action +
                            "\n" +
                            Formatting.YELLOW + "Value" +
                            Formatting.GRAY + ": " +
                            Formatting.GREEN + value +
                            "\n"
            ));
        }
        if (indicator != null) {
            final Text indiText = indicator.text();
            if (indiText != null) text.append(indiText);
            text.append(clickEventData);
        } else {
            text.append(clickEventData);
        }
        return new MessageIndicator(
                this.clickEventColor.getColor().getRGB(),
                indicator != null ? indicator.icon() : null,
                text,
                indicator != null ? indicator.loggedName() : MessageIndicator.system().loggedName()
        );
    }

}
