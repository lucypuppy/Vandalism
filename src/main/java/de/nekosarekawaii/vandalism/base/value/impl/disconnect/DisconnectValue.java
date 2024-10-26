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

package de.nekosarekawaii.vandalism.base.value.impl.disconnect;

import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.util.ServerUtil;
import de.nekosarekawaii.vandalism.util.StringUtils;
import de.nekosarekawaii.vandalism.util.interfaces.IName;
import lombok.Getter;

public class DisconnectValue extends ValueGroup {

    @Getter
    private final BooleanValue activeValue = new BooleanValue(
            this,
            "Active",
            "Whether the condition is active",
            true
    );

    private final EnumModeValue<DisconnectType> disconnectType = new EnumModeValue<>(
            this,
            "Disconnect type",
            "The type of disconnection.",
            DisconnectType.DISCONNECT,
            DisconnectType.values()
    );

    @Getter
    private final IDisconnectCondition disconnectCondition;

    public DisconnectValue(final ValueParent parent, final String name, final String description, final IDisconnectCondition disconnectCondition) {
        super(parent, name, description);
        this.disconnectCondition = disconnectCondition;
    }

    public DisconnectValue(final ValueParent valueParent, final String name, final String description, final DisconnectType disconnectType, final IDisconnectCondition disconnectCondition) {
        this(valueParent, name, description, disconnectCondition);
        this.disconnectType.setValue(disconnectType);
    }

    public void executeDisconnect() {
        switch (this.disconnectType.getValue()) {
            case DISCONNECT -> ServerUtil.disconnect("Automatically disconnected -> " + this.getName());
            case RECONNECT -> ServerUtil.connectToLastServer();
        }
    }

    public enum DisconnectType implements IName {
        DISCONNECT, RECONNECT;

        private final String name;

        DisconnectType() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

}
