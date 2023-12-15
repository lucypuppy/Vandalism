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
