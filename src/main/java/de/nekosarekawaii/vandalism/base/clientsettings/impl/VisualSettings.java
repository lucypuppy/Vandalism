/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

package de.nekosarekawaii.vandalism.base.clientsettings.impl;

import de.nekosarekawaii.vandalism.base.clientsettings.ClientSettings;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.util.common.IName;
import de.nekosarekawaii.vandalism.util.common.StringUtils;

public class VisualSettings extends ValueGroup {

    public final BooleanValue fixTitleTextsOnConnect = new BooleanValue(
            this,
            "Fix Title Texts On Connect",
            "If activated fixes the title texts when connecting to a server.",
            true
    );

    public final BooleanValue customBobView = new BooleanValue(
            this,
            "Custom Bob View",
            "If activated allows you to customize the bob view camera effect.",
            false
    );

    public final FloatValue customBobViewValue = new FloatValue(
            this,
            "Custom Bob View Value",
            "Here you can change the custom bob view value.",
            5.0f,
            0.0f,
            50.0f
    ).visibleCondition(this.customBobView::getValue);

    public final FloatValue shieldAlpha = new FloatValue(
            this,
            "Shield Alpha",
            "Change the alpha of a shield.",
            1.0f,
            0.1f,
            1.0f
    );

    public final BooleanValue showOwnDisplayName = new BooleanValue(
            this,
            "Show Own Display Name",
            "If activated allows you to see your own display name.",
            true
    );

    public final BooleanValue customDisplayNameVisibility = new BooleanValue(
            this,
            "Custom Display Name Visibility",
            "If activated allows you to customize the display name visibility.",
            false
    );

    public final EnumModeValue<DisplayNameVisibility> displayNameVisibilityMode = new EnumModeValue<>(
            this,
            "Display Name Visibility Mode",
            "Here you can change the display name visibility mode.",
            DisplayNameVisibility.SHOW_ALL,
            DisplayNameVisibility.values()
    ).visibleCondition(this.customDisplayNameVisibility::getValue);

    public enum DisplayNameVisibility implements IName {

        SHOW_ALL,
        HIDE_ALL,
        HIDE_FRIENDS;

        private final String name;

        DisplayNameVisibility() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }

        @Override
        public String getName() {
            return this.name;
        }

    }

    public VisualSettings(final ClientSettings parent) {
        super(parent, "Visual", "Visual related settings.");
    }

}
