/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.base.config.gui;

import com.google.gson.JsonObject;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.config.template.ConfigWithValues;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.misc.KeyBindValue;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import de.nekosarekawaii.vandalism.feature.Feature;
import de.nekosarekawaii.vandalism.feature.command.impl.misc.ConfigCommand;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.feature.module.ModuleManager;
import de.nekosarekawaii.vandalism.util.StringUtils;
import de.nekosarekawaii.vandalism.util.imgui.ImUtils;
import de.nekosarekawaii.vandalism.util.math.MathUtil;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiMouseButton;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConfigsClientWindow extends ClientWindow {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private static final ImGuiInputTextCallback NAME_FILTER = new ImGuiInputTextCallback() {

        @Override
        public void accept(final ImGuiInputTextCallbackData imGuiInputTextCallbackData) {
            final int eventCharInt = imGuiInputTextCallbackData.getEventChar();
            if (eventCharInt == 0) return;
            final char eventChar = (char) eventCharInt;
            if (!Character.isLetterOrDigit(eventChar) && eventChar != '_') {
                imGuiInputTextCallbackData.setEventChar((char) 0);
            }
        }

    };

    private File hoveredConfig;

    private final ImString configName = new ImString(50);
    private final ImString updatedConfigName = new ImString(50);

    private final ImBoolean activateModules = new ImBoolean(true);

    private File lastConfig;

    public ConfigsClientWindow() {
        super("Configs", Category.CONFIG, 450f, 500f);
    }

    private void renderHoveredConfigPopup(final String id) {
        if (this.hoveredConfig == null) return;
        if (ImGui.beginPopupContextItem("config-popup")) {
            ImGui.setNextItemWidth(400f);
            if (this.hoveredConfig != null) {
                ImGui.text("Name");
                ImGui.setNextItemWidth(500f);
                ImGui.inputText(id + "updatedConfigName", this.updatedConfigName, ImGuiInputTextFlags.CallbackCharFilter, NAME_FILTER);
                final String updatedConfigNameValue = this.updatedConfigName.get();
                if ((!updatedConfigNameValue.equals(StringUtils.replaceLast(this.hoveredConfig.getName(), ".json", "")) || updatedConfigNameValue.isEmpty()) && MathUtil.isBetween(updatedConfigNameValue.length(), 1, 50) && !ConfigCommand.INVALID_CONFIG_NAME_PATTERN.matcher(updatedConfigNameValue).find()) {
                    if (ImUtils.subButton("Update Name")) {
                        final File newName = new File(this.hoveredConfig.getParent(), updatedConfigNameValue.isEmpty() ? this.hoveredConfig.getName() : updatedConfigNameValue + ".json");
                        if (this.hoveredConfig.renameTo(newName)) {
                            this.updatedConfigName.set(StringUtils.replaceLast(newName.getName(), ".json", ""));
                        }
                    }
                }
            }
            if (ImUtils.subButton("Delete Config")) {
                ImGui.closeCurrentPopup();
                this.hoveredConfig.delete();
                this.hoveredConfig = null;
            }
            if (this.hoveredConfig != null) {
                if (ImUtils.subButton("Open")) {
                    Util.getOperatingSystem().open(this.hoveredConfig);
                }
                if (ImUtils.subButton("Copy")) {
                    try {
                        mc.keyboard.setClipboard(Files.readString(this.hoveredConfig.toPath()));
                    } catch (final Exception ignored) {
                    }
                }
                if (ImUtils.subButton("Duplicate")) {
                    final String name = StringUtils.replaceLast(this.hoveredConfig.getName(), ".json", "");
                    int i = 2;
                    File newFile;
                    do {
                        newFile = new File(this.hoveredConfig.getParent(), name + "_" + i + ".json");
                        i++;
                    } while (newFile.exists());
                    try {
                        Files.copy(this.hoveredConfig.toPath(), newFile.toPath());
                    } catch (final Exception e) {
                        Vandalism.getInstance().getLogger().error("Failed to duplicate config {}", this.hoveredConfig.getName(), e);
                    }
                }
            }
            ImGui.endPopup();
        }
    }

    private void renderConfig(final String id, final File config) {
        if (config == null) return;
        final String name = StringUtils.replaceLast(config.getName(), ".json", "");
        final boolean isLastConfig = this.lastConfig != null && this.lastConfig.equals(config);
        if (isLastConfig) {
            final float[] color = {0.8f, 0.1f, 0.1f, 0.30f};
            ImGui.pushStyleColor(ImGuiCol.Button, color[0], color[1], color[2], color[3]);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, color[0], color[1], color[2], color[3] - 0.1f);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, color[0], color[1], color[2], color[3] + 0.1f);
        }
        if (ImGui.button(id + "config" + name, ImGui.getColumnWidth(), ImUtils.modulateDimension(45F))) {
            if (config.exists()) {
                this.lastConfig = config;
                try (final FileReader fr = new FileReader(config)) {
                    final ModuleManager moduleManager = Vandalism.getInstance().getModuleManager();
                    final JsonObject jsonObject = ConfigCommand.GSON.fromJson(fr, JsonObject.class);
                    for (final Module module : moduleManager.getList()) {
                        if (module.getCategory() == Feature.Category.RENDER) {
                            continue;
                        }
                        final String moduleName = module.getName();
                        if (!jsonObject.has(moduleName)) {
                            for (final Value<?> value : module.getValues()) value.resetValue();
                            continue;
                        }
                        final JsonObject moduleJsonObject = jsonObject.getAsJsonObject(moduleName);
                        if (this.activateModules.get()) {
                            if (moduleJsonObject != null && moduleJsonObject.has("active")) {
                                if (moduleJsonObject.get("active").getAsBoolean()) {
                                    module.activate();
                                } else {
                                    module.deactivate();
                                }
                            }
                        }
                        if (moduleJsonObject != null && moduleJsonObject.has("values")) {
                            ConfigWithValues.loadValues(moduleJsonObject.getAsJsonObject("values"), module.getValues());
                        }
                    }
                } catch (final Exception e) {
                    Vandalism.getInstance().getLogger().error("Failed to load config {}", config.getName(), e);
                }
            }
        }
        if (isLastConfig) {
            ImGui.popStyleColor(3);
        }
        if (ImGui.isItemHovered() && ImGui.isItemClicked(ImGuiMouseButton.Right)) {
            this.hoveredConfig = config;
            this.updatedConfigName.set(StringUtils.replaceLast(this.hoveredConfig.getName(), ".json", ""));
            ImGui.openPopup("config-popup");
        }
        ImGui.sameLine(ImUtils.modulateDimension(22));
        ImGui.textWrapped("Name: " + name + "\n" + "Modified: " + DATE_FORMAT.format(new Date(config.lastModified())));
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final String id = "##" + this.getName();
        if (ImGui.beginTabBar(id + "tabBar")) {
            if (ImGui.beginTabItem("Configs")) {
                if (ImGui.button("Open Directory", ImGui.getColumnWidth() / 2, ImGui.getTextLineHeightWithSpacing())) {
                    Util.getOperatingSystem().open(ConfigCommand.CONFIGS_DIR);
                }
                ImGui.sameLine();
                ImGui.text("Activate Modules");
                ImGui.sameLine();
                ImGui.checkbox(id + "activateModules", this.activateModules);
                ImGui.separator();
                final File[] filesInDirectory = ConfigCommand.CONFIGS_DIR.listFiles();
                if (filesInDirectory != null) {
                    for (final File file : filesInDirectory) {
                        if (file.isFile() && file.getName().endsWith(".json")) {
                            this.renderConfig(id, file);
                        }
                    }
                }
                this.renderHoveredConfigPopup(id);
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Save Config")) {
                ImGui.text("Name");
                ImGui.setNextItemWidth(-1);
                ImGui.inputText(id + "configName", this.configName, ImGuiInputTextFlags.CallbackCharFilter, NAME_FILTER);
                ImGui.spacing();
                final String configNameValue = this.configName.get();
                if (!configNameValue.isBlank() && MathUtil.isBetween(configNameValue.length(), 1, 50) && !ConfigCommand.INVALID_CONFIG_NAME_PATTERN.matcher(configNameValue).find()) {
                    if (ImUtils.subButton("Save")) {
                        try {
                            final File file = new File(ConfigCommand.CONFIGS_DIR, configNameValue + ".json");
                            try {
                                file.delete();
                                file.createNewFile();
                                final ModuleManager moduleManager = Vandalism.getInstance().getModuleManager();
                                final JsonObject modulesJsonObject = new JsonObject();
                                for (final Module module : moduleManager.getList()) {
                                    if (module.getCategory() == Feature.Category.RENDER) {
                                        continue;
                                    }
                                    final List<Value<?>> values = new ArrayList<>();
                                    for (final Value<?> value : module.getValues()) {
                                        if (module.getDefaultValues().contains(value)) {
                                            continue;
                                        }
                                        if (value instanceof KeyBindValue) {
                                            continue;
                                        }
                                        values.add(value);
                                    }
                                    final JsonObject valuesJsonObject = new JsonObject();
                                    ConfigWithValues.saveValues(valuesJsonObject, values);
                                    final JsonObject jsonObject = new JsonObject();
                                    jsonObject.add("values", valuesJsonObject);
                                    jsonObject.addProperty("active", module.isActive());
                                    modulesJsonObject.add(module.getName(), jsonObject);
                                }
                                try (final FileWriter fw = new FileWriter(file)) {
                                    fw.write(ConfigCommand.GSON.toJson(modulesJsonObject));
                                    fw.flush();
                                } catch (final Exception e) {
                                    Vandalism.getInstance().getLogger().error("Failed to save config: {}", configNameValue, e);
                                }
                            } catch (final IOException e) {
                                Vandalism.getInstance().getLogger().error("Failed to save config: {}", configNameValue, e);
                            }
                        } catch (final Exception e) {
                            Vandalism.getInstance().getLogger().error("Failed to save config: {}", configNameValue, e);
                        }
                        this.configName.set("");
                    }
                }
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
    }

}
