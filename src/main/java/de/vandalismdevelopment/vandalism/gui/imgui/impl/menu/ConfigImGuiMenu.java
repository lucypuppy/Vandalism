package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.impl.MainConfig;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import de.vandalismdevelopment.vandalism.value.Value;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class ConfigImGuiMenu extends ImGuiMenu {

    public ConfigImGuiMenu() {
        super("Config");
    }

    @Override
    public void render() {
        if (ImGui.begin("Config", ImGuiWindowFlags.NoCollapse)) {
            final MainConfig mainConfigValues = Vandalism.getInstance().getConfigManager().getMainConfig();
            if (ImGui.button("Reset Config##configresetbutton")) {
                for (final Value<?> value : mainConfigValues.getValues()) {
                    value.resetValue();
                }
            }
            ImGui.separator();
            mainConfigValues.renderValues();
            ImGui.end();
        }
    }

}
