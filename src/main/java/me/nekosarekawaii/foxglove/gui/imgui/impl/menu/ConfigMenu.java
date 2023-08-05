package me.nekosarekawaii.foxglove.gui.imgui.impl.menu;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.config.impl.MainConfig;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.FeatureList;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.Value;

import java.util.List;

public class ConfigMenu {

    public static void render() {
        if (ImGui.begin("Config", ImGuiWindowFlags.NoCollapse)) {
            final FeatureList<Module> modules = Foxglove.getInstance().getModuleRegistry().getModules();
            if (modules.isEmpty()) {
                ImGui.end();
                return;
            }

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

                for (final FeatureCategory featureCategory : FeatureCategory.values()) {
                    final FeatureList<Module> modulesByCategory = modules.get(featureCategory);

                    if (modulesByCategory.isEmpty())
                        continue;

                    if (ImGui.beginTabItem(featureCategory.normalName() + " Modules##modulesfeaturecategory")) {
                        for (final Module module : modulesByCategory) {
                            if (ImGui.collapsingHeader(module.getName() + "##modules" + featureCategory.normalName() + "module")) {
                                final String moduleIdent = "##" + module.getName() + featureCategory.normalName() + "module";
                                final List<Value<?>> values = module.getValues();

                                if (module.isExperimental())
                                    ImGui.textColored(0.8f, 0.1f, 0.1f, 1f, "Warning this is a experimental module which can have issues!");

                                if (ImGui.checkbox("Enabled" + moduleIdent, module.isEnabled()))
                                    module.toggle();

                                ImGui.sameLine();

                                final var showInModuleList = new ImBoolean(module.isShowInModuleList());
                                if (ImGui.checkbox("Show in Module List" + moduleIdent, showInModuleList))
                                    module.setShowInModuleList(showInModuleList.get());

                                ImGui.sameLine();

                                if (ImGui.button("Reset Config" + moduleIdent)) {
                                    for (final Value<?> value : values)
                                        value.resetValue();
                                }

                                if (!values.isEmpty()) {
                                    ImGui.separator();
                                    ImGui.newLine();
                                }

                                module.renderValues();
                                ImGui.newLine();
                            }
                        }

                        ImGui.endTabItem();
                    }
                }

                ImGui.endTabBar();
            }

            ImGui.end();
        }
    }

}
