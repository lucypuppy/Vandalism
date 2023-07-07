package me.nekosarekawaii.foxglove.gui.imgui.impl;

import imgui.ImGui;
import imgui.ImGuiIO;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.FeatureList;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.gui.imgui.ImGuiMenu;
import me.nekosarekawaii.foxglove.value.Value;

public class MainMenu extends ImGuiMenu {

    private static FeatureCategory currentFeatureCategory = null;
    private static Module currentModule = null;

    private static final Object2ObjectOpenHashMap<FeatureCategory, Module> moduleViewCache = new Object2ObjectOpenHashMap<>();

    private void resetModuleView() {
        currentFeatureCategory = null;
        currentModule = null;
    }

    private static boolean showConfig = true;

    @Override
    public void init() {
    }

    @Override
    public void render(final ImGuiIO imGuiIO) {
        if (ImGui.begin(Foxglove.getInstance().getName())) {
            ImGui.setWindowSize(0, 0);

            final FeatureList<Module> modules = Foxglove.getInstance().getModuleRegistry().getModules();

            if (ImGui.beginListBox("##general", 150, 510)) {

                for (int i = 0; i < 3; i++) ImGui.spacing();

                ImGui.sameLine();

                ImGui.text(Foxglove.getInstance().getName() + " " + Foxglove.getInstance().getVersion());

                for (int i = 0; i < 2; i++) ImGui.spacing();
                if (ImGui.button("Config", 142, 35)) {
                    this.resetModuleView();
                    showConfig = true;
                }
                if (!modules.isEmpty()) {
                    for (int i = 0; i < 5; i++) ImGui.spacing();
                    ImGui.sameLine();
                    ImGui.text("Modules");
                    ImGui.spacing();

                    if (ImGui.beginListBox("##modulecategories", 142, 300)) {
                        for (final FeatureCategory featureCategory : FeatureCategory.values()) {
                            final FeatureList<Module> modulesByCategory = modules.get(featureCategory);
                            if (!modulesByCategory.isEmpty()) {
                                if (ImGui.button(featureCategory.normalName(), 134, 35)) {
                                    showConfig = false;
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
            if (!showConfig && currentFeatureCategory != null) {
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

                    if (ImGui.beginListBox("##moduleConfig", 600, 500)) {
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

            if (showConfig) {
                ImGui.sameLine();
                if (ImGui.beginListBox("##mainConfig", 600, 500)) {
                    ImGui.sameLine();
                    ImGui.text("Config");

                    for (int i = 0; i < 3; i++) ImGui.spacing();

                    final ObjectArrayList<Value<?>> values = Foxglove.getInstance().getConfigManager().getMainConfig().getValues();

                    if (ImGui.button("Reset Config")) {
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

            ImGui.end();
        }
    }

    @Override
    public void tick() {
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        if (Foxglove.getInstance().getConfigManager().getMainConfig().mainMenuKeyCode.getValue() == keyCode) {
            Foxglove.getInstance().setCurrentImGuiMenu(null);
        }
        return false;
    }

    @Override
    public void close() {
    }

}
