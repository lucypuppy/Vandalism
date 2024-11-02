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

package de.nekosarekawaii.vandalism.base.clientsettings;

import de.nekosarekawaii.vandalism.base.clientsettings.gui.ClientSettingsClientWindow;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.*;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.base.config.template.ConfigWithValues;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.clientwindow.ClientWindowManager;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ClientSettings implements ValueParent {

    private final List<Value<?>> values = new ArrayList<>();

    @Getter
    private final MenuSettings menuSettings = new MenuSettings(this);

    @Getter
    private final ChatSettings chatSettings = new ChatSettings(this);

    @Getter
    private final NetworkingSettings networkingSettings = new NetworkingSettings(this);

    @Getter
    private final VisualSettings visualSettings = new VisualSettings(this);

    @Getter
    private final RotationSettings rotationSettings = new RotationSettings(this);

    @Getter
    private final EnhancedServerListSettings enhancedServerListSettings = new EnhancedServerListSettings(this);

    public ClientSettings(final ConfigManager configManager, final ClientWindowManager clientWindowManager) {
        configManager.add(new ConfigWithValues("client-settings", getValues().stream().map(value -> (ValueParent) value).toList()));
        clientWindowManager.add(new ClientSettingsClientWindow(this));
    }

    @Override
    public List<Value<?>> getValues() {
        return values;
    }

    @Override
    public String getName() {
        return "Client Settings";
    }

}
