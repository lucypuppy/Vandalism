package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.FeatureList;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import de.vandalismdevelopment.vandalism.value.Value;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiWindowFlags;

import java.util.ArrayList;
import java.util.List;

public class ModulesImGuiMenu extends ImGuiMenu {

    public ModulesImGuiMenu() {
        super("Modules");
    }

    @Override
    public void render() {
        final FeatureList<Module> modules = Vandalism.getInstance().getModuleRegistry().getModules();
        if (!modules.isEmpty()) {
            for (final FeatureCategory featureCategory : FeatureCategory.values()) {
                final FeatureList<Module> modulesByCategory = modules.get(featureCategory);
                if (modulesByCategory.isEmpty()) continue;
                final String featureCategoryIdentifier = "##" + featureCategory.normalName() + "modulesfeaturecategory";
                final float width = 185, height = 415;
                ImGui.setNextWindowSizeConstraints(width, height, width, height);
                if (ImGui.begin(featureCategory.normalName() + " Modules" + featureCategoryIdentifier,
                        ImGuiWindowFlags.NoCollapse |
                                ImGuiWindowFlags.NoResize |
                                ImGuiWindowFlags.NoScrollbar |
                                ImGuiWindowFlags.NoScrollWithMouse |
                                ImGuiWindowFlags.NoDocking
                )) {
                    ImGui.separator();
                    if (ImGui.beginChild(featureCategoryIdentifier + "scrolllist", -1, -1, true)) {
                        for (final Module module : modulesByCategory) {
                            final float[] color;
                            if (module.isEnabled()) {
                                color = new float[]{
                                        0.1f, 0.8f, 0.1f, 0.45f
                                };
                            } else {
                                color = new float[]{
                                        0.8f, 0.1f, 0.1f, 0.45f
                                };
                            }
                            final boolean moduleEnabled = module.isEnabled();
                            if (moduleEnabled) {
                                ImGui.pushStyleColor(ImGuiCol.Button, color[0], color[1], color[2], color[3]);
                                ImGui.pushStyleColor(ImGuiCol.ButtonHovered, color[0], color[1], color[2], color[3] - 0.1f);
                                ImGui.pushStyleColor(ImGuiCol.ButtonActive, color[0], color[1], color[2], color[3] + 0.1f);
                            }
                            final String moduleIdentifier = "##" + module.getName() + module.getCategory().normalName() + "module" + module.getName();
                            if (ImGui.button(module.getName() + moduleIdentifier + "togglebutton", -1, 25)) {
                                module.toggle();
                            }
                            if (moduleEnabled) {
                                ImGui.popStyleColor(3);
                            }
                            if (ImGui.beginPopupContextItem(moduleIdentifier + "configmenu", ImGuiPopupFlags.MouseButtonRight)) {
                                ImGui.text(module.getName() + " Module");
                                ImGui.separator();
                                ImGui.spacing();
                                final List<Value<?>> values = module.getValues();
                                final List<String> descriptionLines = new ArrayList<>();
                                final String[] descriptionWords = module.getDescription().split(" ");
                                if (descriptionWords.length > 10) {
                                    StringBuilder currentLine = new StringBuilder();
                                    for (final String descriptionWord : descriptionWords) {
                                        if (currentLine.length() + descriptionWord.length() > 50) {
                                            descriptionLines.add(currentLine.toString());
                                            currentLine = new StringBuilder();
                                        }
                                        currentLine.append(descriptionWord).append(" ");
                                    }
                                    descriptionLines.add(currentLine.toString());
                                } else {
                                    descriptionLines.add(module.getDescription());
                                }
                                for (final String descriptionLine : descriptionLines) {
                                    ImGui.text(descriptionLine);
                                }
                                ImGui.spacing();
                                if (module.isExperimental()) {
                                    ImGui.textColored(0.8f, 0.1f, 0.1f, 1f, "Warning this is a unstable experimental module!");
                                }
                                ImGui.separator();
                                if (!values.isEmpty()) {
                                    ImGui.text("Config");
                                    ImGui.separator();
                                    if (ImGui.button("Reset config" + moduleIdentifier + "resetconfigbutton")) {
                                        for (final Value<?> value : values) {
                                            value.resetValue();
                                        }
                                    }
                                    module.renderValues();
                                    ImGui.separator();
                                }
                                ImGui.endPopup();
                            }
                        }
                        ImGui.endChild();
                    }
                    ImGui.separator();
                    ImGui.end();
                }
            }
        }
    }

}
