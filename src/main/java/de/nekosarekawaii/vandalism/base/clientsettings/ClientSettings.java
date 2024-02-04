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

package de.nekosarekawaii.vandalism.base.clientsettings;

import de.nekosarekawaii.vandalism.base.clientsettings.gui.ClientSettingsClientMenuWindow;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.*;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.base.config.template.ConfigWithValues;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.clientmenu.ClientMenuManager;

import java.util.ArrayList;
import java.util.List;

public class ClientSettings implements ValueParent {

    private final List<Value<?>> values = new ArrayList<>();

    private final MenuSettings menuSettings = new MenuSettings(this);
    private final ChatSettings chatSettings = new ChatSettings(this);
    private final NetworkingSettings networkingSettings = new NetworkingSettings(this);
    private final VisualSettings visualSettings = new VisualSettings(this);
    private final MovementSettings movementSettings = new MovementSettings(this);
    private final RotationSettings rotationSettings = new RotationSettings(this);
    private final TargetSettings targetSettings = new TargetSettings(this);
    private final EnhancedServerListSettings enhancedServerListSettings = new EnhancedServerListSettings(this);

    public ClientSettings(final ConfigManager configManager, final ClientMenuManager clientMenuManager) {
        configManager.add(new ConfigWithValues("client-settings", getValues().stream().map(value -> (ValueParent) value).toList()));
        clientMenuManager.add(new ClientSettingsClientMenuWindow(this));
    }

    public MenuSettings getMenuSettings() {
        return menuSettings;
    }

    public ChatSettings getChatSettings() {
        return chatSettings;
    }

    public NetworkingSettings getNetworkingSettings() {
        return networkingSettings;
    }

    public VisualSettings getVisualSettings() {
        return visualSettings;
    }

    public MovementSettings getMovementSettings() {
        return movementSettings;
    }

    public RotationSettings getRotationSettings() {
        return rotationSettings;
    }

    public TargetSettings getTargetSettings() {
        return targetSettings;
    }

    public EnhancedServerListSettings getEnhancedServerListSettings() {
        return enhancedServerListSettings;
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
