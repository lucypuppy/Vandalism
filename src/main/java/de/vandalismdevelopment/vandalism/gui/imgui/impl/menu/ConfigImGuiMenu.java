package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.impl.MainConfig;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.ValueCategory;
import imgui.ImGui;
import net.minecraft.client.gui.DrawContext;

public class ConfigImGuiMenu extends ImGuiMenu {

    public ConfigImGuiMenu() {
        super("Config");
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        if (ImGui.begin(
                "Config##config",
                Vandalism.getInstance().getImGuiHandler().getImGuiRenderer().getGlobalWindowFlags()
        )) {
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
                            ImGui.beginChild("##configvalues" + valueCategory.getName());
                            valueCategory.renderValues();
                            ImGui.endChild();
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
