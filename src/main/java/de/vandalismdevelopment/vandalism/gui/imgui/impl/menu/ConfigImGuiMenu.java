package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.impl.MainConfig;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.ValueCategory;
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
            if (ImGui.beginTabBar("configTabBar##configtabbar")) {
                for (final Value<?> value : mainConfigValues.getValues()) {
                    if (value instanceof final ValueCategory valueCategory) {
                        if (ImGui.beginTabItem(
                                valueCategory.getValueName() + "##configvaluecategory" +
                                        valueCategory.getName()
                        )) {
                            if (ImGui.button(
                                    "Reset " + valueCategory.getName() + " Config##configresetbutton" +
                                            valueCategory.getName()
                            )) {
                                for (final Value<?> valueCategoryValue : valueCategory.getValues()) {
                                    valueCategoryValue.resetValue();
                                }
                            }
                            ImGui.separator();
                            valueCategory.renderValues();
                            ImGui.endTabItem();
                        }
                    } else {
                        mainConfigValues.renderValue(value);
                    }
                }
                ImGui.endTabBar();
            }
            ImGui.end();
        }
    }

}
