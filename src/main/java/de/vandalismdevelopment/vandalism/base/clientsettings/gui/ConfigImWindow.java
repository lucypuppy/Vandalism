package de.vandalismdevelopment.vandalism.base.clientsettings.gui;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.clientsettings.ClientSettings;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.ValueCategory;
import de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.ImGuiMenu;
import imgui.ImGui;
import net.minecraft.client.gui.DrawContext;

public class ConfigImWindow extends ImGuiMenu {

    private final ClientSettings clientSettings;

    public ConfigImWindow(final ClientSettings clientSettings) {
        super("Config");

        this.clientSettings = clientSettings;
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        if (ImGui.begin(getName(), Vandalism.getInstance().getImGuiHandler().getImGuiRenderer().getGlobalWindowFlags())) {
            if (ImGui.beginTabBar("configTabBar##configtabbar")) {
                for (final Value<?> value : clientSettings.getValues()) {
                    if (value instanceof final ValueCategory valueCategory) {
                        if (ImGui.beginTabItem(valueCategory.getValueName() + "##configvaluecategory" + valueCategory.getName())) {
                            if (ImGui.button("Reset " + valueCategory.getName() + " Config##configresetbutton" + valueCategory.getName())) {
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
                        clientSettings.renderValue(value);
                    }
                }
                ImGui.endTabBar();
            }
            ImGui.end();
        }
    }

}
