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
import net.minecraft.client.gui.DrawContext;
import net.raphimc.vialoader.util.VersionRange;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModulesImGuiMenu extends ImGuiMenu {

    private final ImString searchInput, favoriteModulesSearchInput, enabledModulesSearchInput;

    private final List<Module> openedModules;

    public ModulesImGuiMenu() {
        super("Modules");
        this.searchInput = new ImString();
        this.favoriteModulesSearchInput = new ImString();
        this.enabledModulesSearchInput = new ImString();
        this.openedModules = new CopyOnWriteArrayList<>();
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final FeatureList<Module> modules = Vandalism.getInstance().getModuleRegistry().getModules();
        if (!modules.isEmpty()) {
            final float width = 185, minHeight = 140, maxHeight = 415;
            final int windowFlags = Vandalism.getInstance().getImGuiHandler().getImGuiRenderer().getGlobalWindowFlags() | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse;
            ImGui.setNextWindowSizeConstraints(width, minHeight, width, maxHeight);
            final String modulesIdentifier = "##modules", modulesSearchIdentifier = modulesIdentifier + "search";
            if (ImGui.begin("Search Modules" + modulesSearchIdentifier, windowFlags)) {
                ImGui.separator();
                ImGui.setNextItemWidth(-1);
                ImGui.inputText(modulesSearchIdentifier + "input", this.searchInput);
                ImGui.separator();
                ImGui.beginChild(modulesSearchIdentifier + "scrolllist", -1, -1, true);
                if (!this.searchInput.get().isBlank()) {
                    for (final Module module : Vandalism.getInstance().getModuleRegistry().getModules()) {
                        if (StringUtils.contains(module.getName(), this.searchInput.get()) || StringUtils.contains(module.getDescription(), this.searchInput.get())) {
                            this.renderModule(module, "search");
                        }
                    }
                }
                ImGui.endChild();
                ImGui.separator();
                ImGui.end();
            }
            ImGui.setNextWindowSizeConstraints(width, minHeight, width, maxHeight);
            final String modulesFavoritesIdentifier = modulesIdentifier + "favorites";
            if (ImGui.begin("Favorite Modules" + modulesFavoritesIdentifier, windowFlags)) {
                ImGui.separator();
                ImGui.setNextItemWidth(-1);
                ImGui.inputText(modulesFavoritesIdentifier + "input", this.favoriteModulesSearchInput);
                ImGui.separator();
                ImGui.beginChild(modulesFavoritesIdentifier + "scrolllist", -1, -1, true);
                for (final Module module : Vandalism.getInstance().getModuleRegistry().getModules()) {
                    if (module.isFavorite()) {
                        if (!this.favoriteModulesSearchInput.get().isBlank()) {
                            if (!(StringUtils.contains(module.getName(), this.favoriteModulesSearchInput.get()) || StringUtils.contains(module.getDescription(), this.favoriteModulesSearchInput.get()))) {
                                continue;
                            }
                        }
                        this.renderModule(module, "favorites");
                    }
                }
                ImGui.endChild();
                ImGui.separator();
                ImGui.end();
            }
            ImGui.setNextWindowSizeConstraints(width, minHeight, width, maxHeight);
            final String modulesEnabledIdentifier = modulesIdentifier + "enabled";
            if (ImGui.begin("Enabled Modules" + modulesEnabledIdentifier, windowFlags)) {
                ImGui.separator();
                ImGui.setNextItemWidth(-1);
                ImGui.inputText(modulesEnabledIdentifier + "input", this.enabledModulesSearchInput);
                ImGui.separator();
                ImGui.beginChild(modulesEnabledIdentifier + "scrolllist", -1, -1, true);
                for (final Module module : Vandalism.getInstance().getModuleRegistry().getModules()) {
                    if (module.isEnabled()) {
                        if (!this.enabledModulesSearchInput.get().isBlank()) {
                            if (!(StringUtils.contains(module.getName(), this.enabledModulesSearchInput.get()) || StringUtils.contains(module.getDescription(), this.enabledModulesSearchInput.get()))) {
                                continue;
                            }
                        }
                        this.renderModule(module, "enabled");
                    }
                }
                ImGui.endChild();
                ImGui.separator();
                ImGui.end();
            }
            for (final FeatureCategory featureCategory : FeatureCategory.values()) {
                final FeatureList<Module> modulesByCategory = modules.get(featureCategory);
                if (modulesByCategory.isEmpty()) continue;
                final String featureCategoryIdentifier = "##" + featureCategory.normalName() + "modulesfeaturecategory";
                ImGui.setNextWindowSizeConstraints(width, minHeight, width, maxHeight);
                if (ImGui.begin(featureCategory.normalName() + " Modules" + featureCategoryIdentifier, windowFlags)) {
                    ImGui.separator();
                    ImGui.beginChild(featureCategoryIdentifier + "scrolllist", -1, -1, true);
                    for (final Module module : modulesByCategory) {
                        this.renderModule(module, "category");
                    }
                    ImGui.endChild();
                    ImGui.separator();
                    ImGui.end();
                }
            }
            for (final Module module : this.openedModules) {
                final String id = "##opened" + module.getCategory().normalName() + "module" + module.getName();
                if (ImGui.begin(module.getName() + " Config" + id, windowFlags)) {
                    this.renderModuleData(module, id, -1, -1);
                    ImGui.end();
                }
            }
        }
    }

    private void renderModule(final Module module, final String id) {
        final String moduleId = "##" + id + module.getCategory().normalName() + "module" + module.getName();
        final float[] color;
        if (module.isEnabled()) {
            color = new float[]{0.1f, 0.8f, 0.1f, 0.45f};
        } else {
            color = new float[]{0.8f, 0.1f, 0.1f, 0.45f};
        }
        final boolean moduleEnabled = module.isEnabled();
        if (moduleEnabled) {
            ImGui.pushStyleColor(ImGuiCol.Button, color[0], color[1], color[2], color[3]);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, color[0], color[1], color[2], color[3] - 0.1f);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, color[0], color[1], color[2], color[3] + 0.1f);
        }
        if (ImGui.button(module.getName() + moduleId + "togglebutton", -1, 25)) {
            module.toggle();
        }
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            this.renderModuleInfo(module);
            ImGui.endTooltip();
        }
        if (moduleEnabled) {
            ImGui.popStyleColor(3);
        }
        if (ImGui.beginPopupContextItem(moduleId + "configmenu", ImGuiPopupFlags.MouseButtonRight)) {
            ImGui.text(module.getName() + " Module");
            //TODO: Make the width and height customizable or use calculations.
            this.renderModuleData(module, moduleId, 400, 300);
            ImGui.endPopup();
        }
    }

    private void renderModuleData(final Module module, final String id, final int width, final int height) {
        ImGui.separator();
        ImGui.spacing();
        this.renderModuleInfo(module);
        ImGui.separator();
        final List<Value<?>> values = module.getValues();
        if (!values.isEmpty()) {
            ImGui.text("Config");
            ImGui.separator();
            if (ImGui.button("Reset config" + id + "resetconfigbutton")) {
                for (final Value<?> value : values) {
                    value.resetValue();
                }
            }
            if (ImGui.button((this.openedModules.contains(module) ? "Close" : "Open") + " config window" + id + "toggleconfigwindowbutton")) {
                if (this.openedModules.contains(module)) {
                    this.openedModules.remove(module);
                } else {
                    this.openedModules.add(module);
                }
            }
            ImGui.pushStyleColor(ImGuiCol.ChildBg, 0.0f, 0.0f, 0.0f, 0.15f);
            ImGui.beginChild(id + "configscrolllist", width, height, true, ImGuiWindowFlags.HorizontalScrollbar);
            module.renderValues();
            ImGui.endChild();
            ImGui.popStyleColor();
            ImGui.separator();
        }
    }

    private void renderModuleInfo(final Module module) {
        final String description = module.getDescription();
        if (!description.isBlank()) {
            final List<String> descriptionLines = new ArrayList<>();
            final String[] descriptionWords = description.split(" ");
            if (descriptionWords.length > 10) {
                StringBuilder currentLine = new StringBuilder();
                for (final String descriptionWord : descriptionWords) {
                    if (currentLine.length() + descriptionWord.length() > 60) {
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
        if (module.isExperimental()) {
            ImGui.spacing();
            ImGui.textColored(0.8f, 0.1f, 0.1f, 1f, "Warning this is a unstable experimental module!");
        }
        final VersionRange supportedVersions = module.getSupportedVersions();
        if (supportedVersions != null) {
            ImGui.spacing();
            ImGui.text("Supported Versions:");
            ImGui.text(supportedVersions.toString());
        }
    }

}
