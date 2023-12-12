package de.vandalismdevelopment.vandalism.base.clientsettings;

import de.vandalismdevelopment.vandalism.base.clientsettings.gui.ConfigImWindow;
import de.vandalismdevelopment.vandalism.base.clientsettings.impl.*;
import de.vandalismdevelopment.vandalism.base.config.ConfigManager;
import de.vandalismdevelopment.vandalism.base.config.template.ConfigWithValues;
import de.vandalismdevelopment.vandalism.base.value.ValueParent;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.gui.ImGuiManager;

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

    public ClientSettings(final ConfigManager configManager, final ImGuiManager imGuiManager) {
        configManager.add(new ConfigWithValues("clientsettings", getValues().stream().map(value -> (ValueParent) value).toList()));
        imGuiManager.add(new ConfigImWindow(this));
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
