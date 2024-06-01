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

package de.nekosarekawaii.vandalism.addonscripts.gui;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.addonscripts.AddonScripts;
import de.nekosarekawaii.vandalism.addonscripts.parse.ScriptParser;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import de.nekosarekawaii.vandalism.addonscripts.base.Script;
import de.nekosarekawaii.vandalism.addonscripts.base.ScriptManager;
import de.nekosarekawaii.vandalism.util.common.RandomUtils;
import de.nekosarekawaii.vandalism.util.render.imgui.ImUtils;
import imgui.ImGui;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiTabBarFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptsClientWindow extends ClientWindow {

    private static final SimpleDateFormat MODIFICATION_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss a");

    private static final String[] HINTS = new String[]{"You can enable execution logging in: " + "config -> main config -> menu category -> script execution logging"};

    private final ConcurrentHashMap<File, ScriptEditor> scriptEditors;

    private final ImBoolean hideHints;

    public ScriptsClientWindow() {
        super("Scripts", Category.CONFIG, ImGuiWindowFlags.MenuBar);
        this.scriptEditors = new ConcurrentHashMap<>();
        this.hideHints = new ImBoolean(false);
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final ScriptManager scriptManager = AddonScripts.getInstance().getScriptManager();
        final List<Script> scripts = scriptManager.getList();
        for (final Map.Entry<File, ScriptEditor> entry : this.scriptEditors.entrySet()) {
            if (entry.getValue().isClosed()) this.scriptEditors.remove(entry.getKey());
        }
        if (ImGui.beginMenuBar()) {
            if (ImGui.button("Open directory##scriptsopendir")) {
                Util.getOperatingSystem().open(scriptManager.getDirectory());
            }
            if (ImGui.button("Reload##scriptsreload")) {
                scriptManager.init();
            }
            if (ImGui.button("Create new example script##scriptscreatenewexamplescript")) {
                final String name = "Example#" + RandomUtils.randomInt(1000, 9999);
                final File scriptFile = new File(scriptManager.getDirectory(), name + ScriptParser.SCRIPT_FILE_EXTENSION);
                this.scriptEditors.put(scriptFile, new ScriptEditor(scriptFile, true));
            }
            if (ImGui.button("Create new script##scriptscreatenewscript")) {
                final String name = "Template#" + RandomUtils.randomInt(1000, 9999);
                final File scriptFile = new File(scriptManager.getDirectory(), name + ScriptParser.SCRIPT_FILE_EXTENSION);
                this.scriptEditors.put(scriptFile, new ScriptEditor(scriptFile, false));
            }
            if (!this.scriptEditors.isEmpty()) {
                if (ImGui.button((this.scriptEditors.size() < 2 ? "Close editor" : "Close all editors (" + this.scriptEditors.size() + ")") + "##scriptsclosealleditors")) {
                    for (final ScriptEditor scriptEditor : this.scriptEditors.values()) {
                        scriptEditor.close();
                    }
                }
            }
            if (scriptManager.getRunningScriptsCount() > 0) {
                if (ImGui.button("Kill " + scriptManager.getRunningScriptsCount() + " running scripts##scriptskill")) {
                    scriptManager.killAllRunningScripts();
                }
            }
            ImGui.endMenuBar();
        }
        if (ImGui.beginTabBar("##scriptstabbar", ImGuiTabBarFlags.AutoSelectNewTabs)) {
            if (ImGui.beginTabItem("List##scriptstablist")) {
                if (!this.hideHints.get()) {
                    ImGui.textWrapped("Hints");
                    ImGui.separator();
                    if (ImGui.beginListBox("##scriptshints", -1, 26 * HINTS.length)) {
                        for (final String hint : HINTS) {
                            ImGui.textWrapped(hint);
                        }
                        ImGui.endListBox();
                    }
                }
                ImGui.checkbox("Hide hints##scriptshidehints", this.hideHints);
                ImGui.separator();
                ImGui.spacing();
                ImGui.textWrapped("Scripts");
                ImGui.separator();
                ImGui.beginChild("##scriptstablechild", -1, -1, true);
                if (scripts.isEmpty()) {
                    ImGui.textWrapped("No scripts loaded!");
                } else {
                    final ScriptsTableColumn[] scriptsTableColumns = ScriptsTableColumn.values();
                    final int maxTableColumns = scriptsTableColumns.length;
                    ImGui.text("Excel Simulator 2024");
                    if (ImGui.beginTable("scripts##scriptstable", maxTableColumns, ImGuiTableFlags.Borders | ImGuiTableFlags.Resizable | ImGuiTableFlags.RowBg | ImGuiTableFlags.ContextMenuInBody)) {
                        for (final ScriptsTableColumn scriptsTableColumn : scriptsTableColumns) {
                            ImGui.tableSetupColumn(scriptsTableColumn.getName());
                        }
                        ImGui.tableHeadersRow();
                        for (final Script script : scripts) {
                            ImGui.tableNextRow();
                            final File scriptFile = script.getFile();
                            if (scriptFile != null && scriptFile.exists()) {
                                final int buttonWidth = 100, buttonHeight = 28;
                                for (int i = 0; i < maxTableColumns; i++) {
                                    ImGui.tableSetColumnIndex(i);
                                    final ScriptsTableColumn scriptsTableColumn = scriptsTableColumns[i];
                                    switch (scriptsTableColumn) {
                                        case NAME -> {
                                            ImGui.textWrapped(script.getName() + (!scriptFile.canWrite() ? " (Read-Only)" : ""));
                                        }
                                        case VERSION -> ImGui.textWrapped(script.getVersion());
                                        case AUTHOR -> ImGui.textWrapped(script.getAuthor());
                                        case DESCRIPTION -> ImGui.textWrapped(script.getDescription());
                                        case CATEGORY -> ImGui.textWrapped(script.getCategory().getName());
                                        case EXPERIMENTAL -> {
                                            ImGui.textWrapped(script.isExperimental() ? "Yes" : "No");
                                        }
                                        case MODIFICATION_DATE -> {
                                            ImGui.textWrapped(MODIFICATION_DATE_FORMAT.format(new Date(script.getFile().lastModified())));
                                        }
                                        case ACTIONS -> {
                                            ImGui.spacing();
                                            script.renderValues(false);
                                            ImUtils.subButton("...##scriptsmoreactions" + script.getName());
                                            if (ImGui.beginPopupContextItem("##scriptsmoreactionspopup" + script.getName(), ImGuiPopupFlags.MouseButtonLeft)) {
                                                ImGui.text(script.getName());
                                                ImGui.separator();
                                                ImGui.spacing();
                                                if (this.mc.player != null) {
                                                    final Script scriptFromList = scriptManager.getList().stream().filter(s -> s.getFile().getName().equalsIgnoreCase(scriptFile.getName())).findFirst().orElse(null);
                                                    if (scriptFromList != null) {
                                                        final UUID uuid = scriptFromList.getUuid();
                                                        if (ImUtils.subButton((scriptManager.isScriptRunning(uuid) ? "Kill" : "Execute") + "##scriptsexecuteorkill" + script.getName())) {
                                                            if (scriptManager.isScriptRunning(uuid)) {
                                                                scriptManager.killRunningScript(uuid);
                                                            } else {
                                                                scriptManager.executeScript(uuid);
                                                            }
                                                        }
                                                    }
                                                }
                                                if (!this.scriptEditors.containsKey(scriptFile)) {
                                                    if (ImUtils.subButton("Edit##scriptsedit" + script.getName())) {
                                                        if (!scriptFile.exists()) {
                                                            Vandalism.getInstance().getLogger().error("Failed to open script editor for script file '" + scriptFile.getName() + "' because the file doesn't exist!");
                                                        } else {
                                                            try {
                                                                final Scanner scanner = new Scanner(scriptFile);
                                                                final StringBuilder codeBuilder = new StringBuilder();
                                                                while (scanner.hasNextLine()) {
                                                                    codeBuilder.append(scanner.nextLine()).append('\n');
                                                                }
                                                                scanner.close();
                                                                final String code = codeBuilder.toString();
                                                                if (!code.isBlank()) {
                                                                    this.scriptEditors.put(scriptFile, new ScriptEditor(scriptFile, code));
                                                                }
                                                            } catch (Throwable throwable) {
                                                                Vandalism.getInstance().getLogger().error("Error while opening script file: " + scriptFile.getName(), throwable);
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    final ScriptEditor scriptEditor = this.scriptEditors.get(scriptFile);
                                                    if (ImGui.button("Close editor##scriptseditorclose" + script.getName(), buttonWidth, buttonHeight)) {
                                                        scriptEditor.close();
                                                    }
                                                    if (scriptEditor.canBeSaved()) {
                                                        if (ImGui.button("Save##scriptssave" + script.getName(), buttonWidth, buttonHeight)) {
                                                            scriptEditor.save();
                                                        }
                                                    }
                                                }
                                                if (ImUtils.subButton("Delete##scriptsdelete" + script.getName())) {
                                                    if (!script.getFile().delete()) {
                                                        Vandalism.getInstance().getLogger().error("Failed to delete script: {}", script.getName());
                                                    } else {
                                                        Vandalism.getInstance().getLogger().info("Deleted script: {}", script.getName());
                                                    }
                                                }
                                                ImGui.endPopup();
                                            }
                                            ImGui.spacing();
                                        }
                                        default -> {
                                        }
                                    }
                                }
                            }
                        }
                        ImGui.endTable();
                    }
                }
                ImGui.endChild();
                ImGui.endTabItem();
            }
            for (final ScriptEditor scriptEditor : this.scriptEditors.values()) {
                scriptEditor.render();
            }
            ImGui.endTabBar();
        }
    }

}
