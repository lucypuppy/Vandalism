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

package de.nekosarekawaii.vandalism.feature.module.impl.render;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.render.TextDrawListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.StringUtils;
import net.minecraft.util.Formatting;

public class NoObfuscatedTextModule extends Module implements TextDrawListener {

    public NoObfuscatedTextModule() {
        super(
                "No Obfuscated Text",
                "Removes the obfuscation color code from the text renderer.",
                Category.RENDER
        );
    }

    @Override
    protected void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, TextDrawEvent.ID);
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, TextDrawEvent.ID);
    }

    @Override
    public void onTextDraw(final TextDrawEvent event) {
        if (event.startingStyle != null && event.startingStyle.isObfuscated()) {
            event.startingStyle = event.startingStyle.withObfuscated(false);
        }
        event.text = StringUtils.replaceAll(event.text, Formatting.OBFUSCATED.toString(), "");
    }

}
