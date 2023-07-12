package me.nekosarekawaii.foxglove.imgui.impl.menu;

import imgui.ImGui;
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
    private static final Object2ObjectOpenHashMap<FeatureCategory, Module> moduleViewCache = new Object2ObjectOpenHashMap<>();

    public static void render() {
        if (ImGui.begin("Modules", ImGuiWindowFlags.NoCollapse)) {
            ImGui.setWindowSize(0, 0);
            final FeatureList<Module> modules = Foxglove.getInstance().getModuleRegistry().getModules();
            if (ImGui.beginListBox("##general", 150, 510)) {
                if (!modules.isEmpty()) {
                    ImGui.sameLine();
                    ImGui.spacing();

                    if (ImGui.beginListBox("##modulecategories", 142, 500)) {
                        for (final FeatureCategory featureCategory : FeatureCategory.values()) {
                            final FeatureList<Module> modulesByCategory = modules.get(featureCategory);
                            if (!modulesByCategory.isEmpty()) {
                                if (ImGui.button(featureCategory.normalName(), 134, 35)) {
                                    currentFeatureCategory = featureCategory;
                                    currentModule = moduleViewCache.get(featureCategory);
                                }
                            }
                        }

                        ImGui.endListBox();
                    }

                }
                ImGui.endListBox();
            }
            if (currentFeatureCategory != null) {
                ImGui.sameLine();

                if (ImGui.beginListBox("##modules", 200, 0)) {
                    ImGui.sameLine();
                    ImGui.text(currentFeatureCategory.normalName() + " - Modules");

                    for (int i = 0; i < 3; i++) ImGui.spacing();

                    final FeatureList<Module> modulesByCategory = modules.get(currentFeatureCategory);

                    for (final Module module : modulesByCategory) {
                        if (module.isExperimental()) {
                            ImGui.textColored(1f, 1f, 0f, 1f, "Experimental");
                        }

                        if (ImGui.button(module.getName())) {
                            moduleViewCache.remove(currentFeatureCategory);
                            moduleViewCache.put(currentFeatureCategory, module);
                            currentModule = module;
                        }

                        if (module.isExperimental()) {
                            ImGui.newLine();
                        }
                    }

                    ImGui.endListBox();
                }
                if (currentModule != null) {
                    ImGui.sameLine();

                    if (ImGui.beginListBox("##moduleConfig", 1055, 600)) {
                        ImGui.sameLine();
                        ImGui.text(currentModule.getName() + " - Config");

                        for (int i = 0; i < 3; i++) ImGui.spacing();

                        final ObjectArrayList<Value<?>> values = currentModule.getValues();

                        if (ImGui.button(currentModule.isEnabled() ? "Disable" : "Enable")) {
                            currentModule.toggle();
                        } else if (ImGui.button("Reset Config")) {
                            for (final Value<?> value : values) {
                                value.resetValue();
                            }
                        }

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
