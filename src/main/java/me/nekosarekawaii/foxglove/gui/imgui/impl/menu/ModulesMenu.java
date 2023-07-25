package me.nekosarekawaii.foxglove.gui.imgui.impl.menu;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.FeatureList;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.value.Value;

public class ModulesMenu {

    private static FeatureCategory currentFeatureCategory = null;
    private static Module currentModule = null;
    private final static Object2ObjectOpenHashMap<FeatureCategory, Module> moduleViewCache = new Object2ObjectOpenHashMap<>();

    public static void render() {
        if (ImGui.begin("Modules" + (currentFeatureCategory != null ? " > " + currentFeatureCategory.normalName() + (currentModule != null ? " > " + currentModule.getName() : "") : "") + "###modulesMenu", ImGuiWindowFlags.NoCollapse)) {
            ImGui.setWindowSize(0, 0);

            final FeatureList<Module> modules = Foxglove.getInstance().getModuleRegistry().getModules();
            if (ImGui.beginListBox("##general", 150, 600)) {
                if (!modules.isEmpty()) {

                    for (final FeatureCategory featureCategory : FeatureCategory.values()) {
                        final FeatureList<Module> modulesByCategory = modules.get(featureCategory);

                        if (!modulesByCategory.isEmpty()) {
                            if (ImGui.button(featureCategory.normalName() + " (" + modulesByCategory.size() + ")", 140, 35)) {
                                currentFeatureCategory = featureCategory;
                                currentModule = moduleViewCache.get(featureCategory);
                            }
                        }
                    }
                }

                ImGui.endListBox();
            }

            if (currentFeatureCategory != null) {
                ImGui.sameLine();

                if (ImGui.beginListBox("##modules", 200, 600)) {

                    final FeatureList<Module> modulesByCategory = modules.get(currentFeatureCategory);
                    for (final Module module : modulesByCategory) {
                        ImGui.pushStyleColor(ImGuiCol.Button, module.isEnabled() ? ImGui.getColorU32(ImGuiCol.ButtonActive) : ImGui.getColorU32(ImGuiCol.Button));

                        if (ImGui.button(module.getName(), 190, 40)) {
                            moduleViewCache.remove(currentFeatureCategory);
                            moduleViewCache.put(currentFeatureCategory, module);
                            currentModule = module;
                        }

                        ImGui.popStyleColor();
                    }

                    ImGui.endListBox();
                }
                if (currentModule != null) {
                    ImGui.sameLine();

                    if (ImGui.beginListBox("##moduleConfig", 1055, 600)) {
                        ImGui.textColored(1f, 1f, 0f, 1f, currentModule.getDescription());

                        if (currentModule.isExperimental()) {
                            ImGui.newLine();
                            ImGui.textColored(0.8f, 0.1f, 0.1f, 1f, "Warning this is a experimental module which can have issues!");
                        }

                        ImGui.newLine();

                        final ObjectArrayList<Value<?>> values = currentModule.getValues();

                        if (ImGui.checkbox("Enabled", currentModule.isEnabled())) {
                            currentModule.toggle();
                        }

                        if (ImGui.checkbox("Show in Module List", currentModule.isShowInModuleList())) {
                            currentModule.setShowInModuleList(!currentModule.isShowInModuleList());
                        }

                        if (ImGui.button("Reset Config")) {
                            for (final Value<?> value : values) {
                                value.resetValue();
                            }
                        }

                        ImGui.newLine();

                        for (final Value<?> value : values) {
                            if (value.isVisible() != null && !value.isVisible().getAsBoolean())
                                continue;

                            value.render();
                        }

                        ImGui.endListBox();
                    }
                }
            }

            ImGui.end();
        }
    }

}
