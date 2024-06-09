/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Verschlxfene, Recyz and contributors
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

package de.nekosarekawaii.vandalism.feature.module.gui;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.config.template.ConfigWithValues;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.misc.KeyBindValue;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import de.nekosarekawaii.vandalism.feature.Feature;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.feature.module.ModuleManager;
import de.nekosarekawaii.vandalism.util.common.StringUtils;
import de.nekosarekawaii.vandalism.integration.imgui.ImUtils;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.minecraft.client.gui.DrawContext;
import net.raphimc.vialoader.util.VersionRange;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModulesClientWindow extends ClientWindow {

    private final ImString searchInput = new ImString();
    private final ImString favoriteModulesSearchInput = new ImString();
    private final ImString activatedModulesSearchInput = new ImString();

    private String lastPopupId = "";
    private boolean closePopup = false;

    public ModulesClientWindow() {
        super("Modules", Category.CONFIG);
    }

    @Override
    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final ModuleManager moduleManager = Vandalism.getInstance().getModuleManager();
        if (!moduleManager.getList().isEmpty()) {
            float width = 195, minHeight = 140, maxHeight = Vandalism.getInstance().getClientSettings().getMenuSettings().moduleTabMaxHeight.getValue();
            final int windowFlags = ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse;
            width = ImUtils.modulateDimension(width);
            maxHeight = ImUtils.modulateDimension(maxHeight);
            ImGui.setNextWindowSizeConstraints(width, minHeight, width, maxHeight);
            final String modulesIdentifier = "##modules", modulesSearchIdentifier = modulesIdentifier + "search";
            ImGui.begin("Search Modules" + modulesSearchIdentifier, windowFlags);
            ImGui.separator();
            ImGui.setNextItemWidth(-1);
            ImGui.inputText(modulesSearchIdentifier + "input", this.searchInput);
            ImGui.separator();
            ImGui.beginChild(modulesSearchIdentifier + "scrolllist", -1, -1, true);
            final String searchInput = this.searchInput.get();
            if (!searchInput.isBlank()) {
                for (final AbstractModule module : moduleManager.getList()) {
                    final String moduleName = module.getName();
                    final boolean doesContainName = StringUtils.contains(moduleName, searchInput) || StringUtils.contains(moduleName.replace(" ", ""), searchInput);
                    if (doesContainName || StringUtils.contains(module.getDescription(), searchInput)) {
                        this.renderModule(module, "search");
                    }
                }
            }
            ImGui.endChild();
            ImGui.separator();
            ImGui.end();
            final List<AbstractModule> favoriteModules = new ArrayList<>();
            for (final AbstractModule module : moduleManager.getList()) {
                if (module.isFavorite()) {
                    favoriteModules.add(module);
                }
            }
            if (!favoriteModules.isEmpty()) {
                ImGui.setNextWindowSizeConstraints(width, minHeight, width, maxHeight);
                final String modulesFavoritesIdentifier = modulesIdentifier + "favorites";
                ImGui.begin("Favorite Modules" + modulesFavoritesIdentifier, windowFlags);
                ImGui.separator();
                ImGui.setNextItemWidth(-1);
                ImGui.inputText(modulesFavoritesIdentifier + "input", this.favoriteModulesSearchInput);
                ImGui.separator();
                ImGui.beginChild(modulesFavoritesIdentifier + "scrolllist", -1, -1, true);
                for (final AbstractModule module : favoriteModules) {
                    if (!this.favoriteModulesSearchInput.get().isBlank()) {
                        if (!(StringUtils.contains(module.getName(), this.favoriteModulesSearchInput.get()) || StringUtils.contains(module.getDescription(), this.favoriteModulesSearchInput.get()))) {
                            continue;
                        }
                    }
                    this.renderModule(module, "favorites");
                }
                ImGui.endChild();
                ImGui.separator();
                ImGui.end();
            }
            final List<AbstractModule> activatedModules = new ArrayList<>();
            for (final AbstractModule module : moduleManager.getList()) {
                if (module.isActive()) {
                    activatedModules.add(module);
                }
            }
            if (!activatedModules.isEmpty()) {
                ImGui.setNextWindowSizeConstraints(width, minHeight, width, maxHeight);
                final String modulesActivatedIdentifier = modulesIdentifier + "activated";
                ImGui.begin("Activated Modules" + modulesActivatedIdentifier, windowFlags);
                ImGui.separator();
                ImGui.setNextItemWidth(-1);
                ImGui.inputText(modulesActivatedIdentifier + "input", this.activatedModulesSearchInput);
                ImGui.separator();
                ImGui.beginChild(modulesActivatedIdentifier + "scrolllist", -1, -1, true);
                for (final AbstractModule module : activatedModules) {
                    if (!this.activatedModulesSearchInput.get().isBlank()) {
                        if (!(StringUtils.contains(module.getName(), this.activatedModulesSearchInput.get()) || StringUtils.contains(module.getDescription(), this.activatedModulesSearchInput.get()))) {
                            continue;
                        }
                    }
                    this.renderModule(module, "activated");
                }
                ImGui.endChild();
                ImGui.separator();
                ImGui.end();
            }
            for (final Feature.Category featureCategory : Feature.Category.values()) {
                final List<AbstractModule> modulesByCategory = moduleManager.getByCategory(featureCategory);
                if (modulesByCategory.isEmpty()) continue;
                final String featureCategoryIdentifier = "##" + featureCategory.getName() + "modulesfeaturecategory";
                ImGui.setNextWindowSizeConstraints(width, minHeight, width, maxHeight);
                ImGui.begin(featureCategory.getName() + " Modules" + featureCategoryIdentifier, windowFlags);
                ImGui.separator();
                ImGui.beginChild(featureCategoryIdentifier + "scrolllist", -1, -1, true);
                for (final AbstractModule module : modulesByCategory) {
                    this.renderModule(module, "category");
                }
                ImGui.endChild();
                ImGui.separator();
                ImGui.end();
            }
        }
    }

    private void renderModule(final AbstractModule module, final String id) {
        final String moduleId = "##" + id + module.getCategory().getName() + "module" + module.getName();
        final boolean isActive = module.isActive();
        if (isActive) {
            final Color color = Vandalism.getInstance().getClientSettings().getMenuSettings().activatedModuleColor.getColor();
            final float[] colorArray = new float[]{ color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f };
            ImGui.pushStyleColor(ImGuiCol.Button, colorArray[0], colorArray[1], colorArray[2], colorArray[3]);
            ImGui.pushStyleColor(ImGuiCol.ButtonHovered, colorArray[0], colorArray[1], colorArray[2], colorArray[3]);
            ImGui.pushStyleColor(ImGuiCol.ButtonActive, colorArray[0], colorArray[1], colorArray[2], colorArray[3]);
        }
        if (ImGui.button(module.getName() + moduleId + "togglebutton", -1, ImUtils.modulateDimension(25))) {
            module.toggle();
        }
        if (isActive) ImGui.popStyleColor(3);
        final String popupId = module.getName() + " Module" + moduleId + "popup";
        if (ImGui.isItemClicked(ImGuiMouseButton.Right)) {
            ImGui.openPopup(popupId);
            this.lastPopupId = popupId;
        }
        final ImVec2 displaySize = ImGui.getIO().getDisplaySize();
        final float centerFactor = 0.5f;
        ImGui.setNextWindowPos(displaySize.x * centerFactor, displaySize.y * centerFactor, ImGuiCond.Always, centerFactor, centerFactor);
        ImGui.setNextWindowSizeConstraints(300f, 50f, 1000000f, 1000000f);
        if (ImGui.beginPopupModal(popupId, ImGuiWindowFlags.AlwaysAutoResize)) {
            this.renderModuleInfo(module, true);
            ImGui.separator();
            module.renderValues();
            ImGui.separator();
            if (ImGui.button("Copy Config" + moduleId + "copyconfigbutton", ImGui.getColumnWidth() / 2f, ImGui.getTextLineHeightWithSpacing())) {
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
                final JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("module", module.getName());
                final JsonObject valuesJsonObject = new JsonObject();
                ConfigWithValues.saveValues(valuesJsonObject, values);
                jsonObject.add("values", valuesJsonObject);
                this.mc.keyboard.setClipboard(jsonObject.toString());
            }
            ImGui.sameLine();
            if (ImGui.button("Paste Config" + moduleId + "pasteconfigbutton", ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing())) {
                final String clipboard = this.mc.keyboard.getClipboard();
                if (clipboard != null && !clipboard.isBlank()) {
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
                    try {
                        final JsonObject jsonObject = JsonParser.parseString(clipboard).getAsJsonObject();
                        if (jsonObject.has("module")) {
                            if (jsonObject.get("module").getAsString().equals(module.getName())) {
                                if (jsonObject.has("values")) {
                                    ConfigWithValues.loadValues(jsonObject.getAsJsonObject("values"), values);
                                }
                            }
                        }
                    }
                    catch (Exception exception) {
                        Vandalism.getInstance().getLogger().error("Failed to paste module config from clipboard.", exception);
                    }
                }
            }
            if (ImUtils.subButton("Reset Config" + moduleId + "resetconfigbutton")) {
                for (final Value<?> value : module.getValues()) {
                    value.resetValue();
                }
            }
            if (ImUtils.subButton("Close" + moduleId + "closebutton")) {
                this.closePopup = true;
            }
            if (this.closePopup) {
                this.closePopup = false;
                this.lastPopupId = "";
                ImGui.closeCurrentPopup();
            }
            ImGui.endPopup();
        }
        if (ImGui.isItemHovered()) {
            ImGui.beginTooltip();
            this.renderModuleInfo(module, false);
            ImGui.endTooltip();
        }
    }

    private void renderModuleInfo(final AbstractModule module, final boolean wrapText) {
        final String description = module.getDescription();
        if (wrapText) ImGui.textWrapped(description);
        else ImGui.text(description);
        if (module.isExperimental()) {
            ImGui.spacing();
            ImGui.pushStyleColor(ImGuiCol.Text, 0.8f, 0.1f, 0.1f, 1f);
            if (wrapText) ImGui.textWrapped(AbstractModule.EXPERIMENTAL_WARNING_TEXT);
            else ImGui.text(AbstractModule.EXPERIMENTAL_WARNING_TEXT);
            ImGui.popStyleColor();
        }
        final VersionRange supportedVersions = module.getSupportedVersions();
        if (supportedVersions != null) {
            ImGui.spacing();
            if (wrapText) ImGui.textWrapped(AbstractModule.SUPPORTED_VERSIONS_TEXT);
            else ImGui.text(AbstractModule.SUPPORTED_VERSIONS_TEXT);
            if (wrapText) ImGui.textWrapped(supportedVersions.toString());
            else ImGui.text(supportedVersions.toString());
        }
    }

    @Override
    public boolean keyPressed(final int key, final int scanCode, final int modifiers, final boolean release) {
        if (key == GLFW.GLFW_KEY_ESCAPE && !release && !this.lastPopupId.isBlank()) {
            this.closePopup = true;
            return false;
        }
        return super.keyPressed(key, scanCode, modifiers, release);
    }

}
