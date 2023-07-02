package me.nekosarekawaii.foxglove.gui.imgui.impl;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.type.ImInt;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.FeatureList;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.gui.imgui.ImGUIMenu;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.value.BooleanValue;
import me.nekosarekawaii.foxglove.value.value.ColorValue;
import me.nekosarekawaii.foxglove.value.value.number.IntegerValue;

import java.awt.*;

public class MainMenu extends ImGUIMenu {

    private static FeatureCategory currentFeatureCategory = null;
    private static Module currentModule = null;

    private void resetModuleView() {
        currentFeatureCategory = null;
        currentModule = null;
    }

    @Override
    public void render(final ImGuiIO imGuiIO) {
        if (ImGui.begin(Foxglove.getInstance().getName())) {
            ImGui.setWindowSize(0, 0);
            final FeatureList<Module> modules = Foxglove.getInstance().getFeatures().getModules();
            if (ImGui.beginListBox("##general", 150, 510)) {
                for (int i = 0; i < 3; i++) ImGui.spacing();
                ImGui.sameLine();
                ImGui.text(Foxglove.getInstance().getName() + " " + Foxglove.getInstance().getVersion());
                for (int i = 0; i < 2; i++) ImGui.spacing();
				/*if (ImGui.button("Config", 142, 35)) {
					this.resetModuleView();
				} else if (ImGui.button("Accounts", 142, 35)) {
					this.resetModuleView();
				}*/
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
                                    currentFeatureCategory = featureCategory;
                                    currentModule = null;
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
                    if (ImGui.beginListBox("##moduleConfig", 500, 500)) {
                        ImGui.sameLine();
                        ImGui.text(currentModule.getName() + " - Config");
                        for (int i = 0; i < 3; i++) ImGui.spacing();
                        final ObjectArrayList<Value<?>> values = currentModule.getValues();
                        if (ImGui.button(currentModule.isEnabled() ? "Disable" : "Enable")) {
                            currentModule.toggle();
                        } else if (ImGui.button("Reset")) {
                            for (final Value<?> value : values) {
                                value.resetValue();
                            }
                        }
                        for (final Value<?> value : values) {
                            if (value instanceof final BooleanValue booleanValue) {
                                if (ImGui.checkbox(value.getName(), booleanValue.getValue())) {
                                    booleanValue.setValue(!booleanValue.getValue());
                                }
                            } else if (value instanceof final IntegerValue integerValue) {
                                final ImInt imInt = new ImInt(integerValue.getValue());
                                if (ImGui.inputInt(value.getName(), imInt, integerValue.getStep())) {
                                    integerValue.setValue(imInt.get());
                                }
                            } else if (value instanceof final ColorValue colorValue) {
                                final Color color = colorValue.getValue();
                                final float[] colorArray = new float[]{color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f};
                                if (ImGui.colorEdit4(value.getName(), colorArray)) {
                                    colorValue.setValue(new Color(colorArray[0], colorArray[1], colorArray[2], colorArray[3]));
                                }
                            } else ImGui.button(value.getName() + " - Value Placeholder");
                        }
                        ImGui.endListBox();
                    }
                }
            }
            ImGui.end();
        }
    }

    @Override
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        if (Foxglove.getInstance().getConfig().getMainMenuKeyCode() == keyCode) {
            Foxglove.getInstance().setCurrentImGUIMenu(null);
        }
        return false;
    }

}
