package de.nekosarekawaii.foxglove.gui.imgui.impl.menu;

import de.nekosarekawaii.foxglove.Foxglove;
import de.nekosarekawaii.foxglove.config.impl.MainConfig;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.FeatureList;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.gui.imgui.ImGuiMenu;
import de.nekosarekawaii.foxglove.value.Value;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

import java.util.ArrayList;
import java.util.List;

public class ConfigImGuiMenu extends ImGuiMenu {

    public ConfigImGuiMenu() {
        super("Config");
    }

    @Override
    public void render() {
        if (ImGui.begin("Config", ImGuiWindowFlags.NoCollapse)) {
            if (ImGui.beginTabBar("configTabBar")) {
                if (ImGui.beginTabItem("Main##mainconfigtabitem")) {
                    final MainConfig mainConfigValues = Foxglove.getInstance().getConfigManager().getMainConfig();
                    if (ImGui.button("Reset Main Config##mainconfigresetbutton")) {
                        for (final Value<?> value : mainConfigValues.getValues()) {
                            value.resetValue();
                        }
                    }
                    ImGui.separator();
                    mainConfigValues.renderValues();
                    ImGui.endTabItem();
                }
                final FeatureList<Module> modules = Foxglove.getInstance().getModuleRegistry().getModules();
                if (!modules.isEmpty()) {
                    for (final FeatureCategory featureCategory : FeatureCategory.values()) {
                        final FeatureList<Module> modulesByCategory = modules.get(featureCategory);
                        if (modulesByCategory.isEmpty()) continue;
                        if (ImGui.beginTabItem(featureCategory.normalName() + " Modules##modulesfeaturecategory")) {
                            //TODO: Make performance improve to this code with something like caching.
                            final List<Module> enabledModules = new ArrayList<>(), disabledModules = new ArrayList<>();
                            for (final Module module : modulesByCategory) {
                                if (module.isEnabled()) enabledModules.add(module);
                                else disabledModules.add(module);
                            }
                            if (!enabledModules.isEmpty()) {
                                ImGui.text("Enabled Modules (" + enabledModules.size() + ")");
                                for (final Module module : enabledModules) {
                                    this.renderModule(module);
                                }
                            }
                            if (!disabledModules.isEmpty()) {
                                ImGui.text("Disabled Modules (" + disabledModules.size() + ")");
                                for (final Module module : disabledModules) {
                                    this.renderModule(module);
                                }
                            }
                            ImGui.endTabItem();
                        }
                    }
                }
                ImGui.endTabBar();
            }
            ImGui.end();
        }
    }

    private void renderModule(final Module module) {
        if (ImGui.collapsingHeader(module.getName() + "##modules" + module.getCategory().normalName() + "module")) {
            final String moduleIdent = "##" + module.getName() + module.getCategory().normalName() + "module";
            final List<Value<?>> values = module.getValues();
            if (module.isExperimental()) {
                ImGui.textColored(0.8f, 0.1f, 0.1f, 1f, "Warning this is a experimental module which can have issues!");
            }
            if (ImGui.checkbox("Enabled" + moduleIdent, module.isEnabled())) {
                module.toggle();
            }
            ImGui.sameLine();
            final ImBoolean showInModuleList = new ImBoolean(module.isShowInModuleList());
            if (ImGui.checkbox("Show in Enabled Module List" + moduleIdent, showInModuleList)) {
                module.setShowInModuleList(showInModuleList.get());
            }
            ImGui.sameLine();
            if (ImGui.button("Reset Config" + moduleIdent)) {
                for (final Value<?> value : values) {
                    value.resetValue();
                }
            }
            if (!values.isEmpty()) {
                ImGui.separator();
                ImGui.newLine();
            }
            module.renderValues();
            ImGui.newLine();
        }
    }

}
