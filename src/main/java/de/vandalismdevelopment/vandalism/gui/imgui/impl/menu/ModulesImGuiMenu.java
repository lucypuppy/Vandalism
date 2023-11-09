package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu;

import de.florianmichael.rclasses.common.StringUtils;
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
import imgui.type.ImString;

import java.util.ArrayList;
import java.util.List;

public class ModulesImGuiMenu extends ImGuiMenu {

    private final ImString searchInput;

    public ModulesImGuiMenu() {
        super("Modules");
        this.searchInput = new ImString();
    }

    @Override
    public void render() {
        final FeatureList<Module> modules = Vandalism.getInstance().getModuleRegistry().getModules();
        if (!modules.isEmpty()) {
            final float width = 185, height = 415; //TODO: Make this customizable or use calculations.
            final int windowFlags = ImGuiWindowFlags.NoCollapse |
                    ImGuiWindowFlags.NoResize |
                    ImGuiWindowFlags.NoScrollbar |
                    ImGuiWindowFlags.NoScrollWithMouse |
                    ImGuiWindowFlags.NoDocking;
            ImGui.setNextWindowSizeConstraints(width, height, width, height);
            if (ImGui.begin("Search##modulessearch", windowFlags)) {
                ImGui.separator();
                ImGui.setNextItemWidth(-1);
                ImGui.inputText("##modulessearchinput", this.searchInput);
                ImGui.separator();
                if (!this.searchInput.get().isBlank()) {
                    if (ImGui.beginChild("##modulessearchscrolllist", -1, -1, true)) {
                        for (final Module module : Vandalism.getInstance().getModuleRegistry().getModules()) {
                            if (
                                    StringUtils.contains(module.getName(), this.searchInput.get()) ||
                                            StringUtils.contains(module.getDescription(), this.searchInput.get())
                            ) {
                                this.renderModule(module, "search");
                            }
                        }
                        ImGui.endChild();
                    }
                    ImGui.separator();
                }
                ImGui.end();
            }
            for (final FeatureCategory featureCategory : FeatureCategory.values()) {
                final FeatureList<Module> modulesByCategory = modules.get(featureCategory);
                if (modulesByCategory.isEmpty()) continue;
                final String featureCategoryIdentifier = "##" + featureCategory.normalName() + "modulesfeaturecategory";
                ImGui.setNextWindowSizeConstraints(width, height, width, height);
                if (ImGui.begin(featureCategory.normalName() + " Modules" + featureCategoryIdentifier, windowFlags)) {
                    ImGui.separator();
                    if (ImGui.beginChild(featureCategoryIdentifier + "scrolllist", -1, -1, true)) {
                        for (final Module module : modulesByCategory) {
                            this.renderModule(module, "category");
                        }
                        ImGui.endChild();
                    }
                    ImGui.separator();
                    ImGui.end();
                }
            }
        }
    }

    private void renderModule(final Module module, final String id) {
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
        final String moduleIdentifier = "##" + id + module.getName() + module.getCategory().normalName() + "module" + module.getName();
        if (ImGui.button(module.getName() + moduleIdentifier + "togglebutton", -1, 25)) {
            module.toggle();
        }
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            this.renderModuleDescription(module);
            ImGui.endTooltip();
        }
        if (moduleEnabled) {
            ImGui.popStyleColor(3);
        }
        if (ImGui.beginPopupContextItem(moduleIdentifier + "configmenu", ImGuiPopupFlags.MouseButtonRight)) {
            ImGui.text(module.getName() + " Module");
            ImGui.separator();
            ImGui.spacing();
            final List<Value<?>> values = module.getValues();
            this.renderModuleDescription(module);
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
                ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.0f, 0.0f, 0.0f, 0.15f);
                if (ImGui.beginChild("##" + moduleIdentifier + "configscrolllist", -1, 300, true)) { //TODO: Make the height customizable or use calculations.
                    module.renderValues();
                    ImGui.endChild();
                }
                ImGui.popStyleColor();
                ImGui.separator();
            }
            ImGui.endPopup();
        }
    }

    private void renderModuleDescription(final Module module) {
        final String description = module.getDescription();
        if (!description.isBlank()) {
            final List<String> descriptionLines = new ArrayList<>();
            final String[] descriptionWords = description.split(" ");
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
                descriptionLines.add(description);
            }
            for (final String descriptionLine : descriptionLines) {
                ImGui.text(descriptionLine);
            }
        } else {
            ImGui.text("No description found.");
        }
    }

}
