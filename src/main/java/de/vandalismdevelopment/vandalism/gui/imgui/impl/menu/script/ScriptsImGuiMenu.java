package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.script;

import de.florianmichael.rclasses.common.RandomUtils;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.FeatureList;
import de.vandalismdevelopment.vandalism.feature.impl.script.Script;
import de.vandalismdevelopment.vandalism.feature.impl.script.ScriptExecutor;
import de.vandalismdevelopment.vandalism.feature.impl.script.ScriptParser;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import de.vandalismdevelopment.vandalism.value.Value;
import imgui.ImGui;
import imgui.flag.ImGuiTabBarFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptsImGuiMenu extends ImGuiMenu {

    private static final SimpleDateFormat MODIFICATION_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss a");

    private final ConcurrentHashMap<File, ScriptEditor> scriptEditors;

    private boolean hideHint;

    public ScriptsImGuiMenu() {
        super("Scripts");
        this.scriptEditors = new ConcurrentHashMap<>();
        this.hideHint = false;
    }

    @Override
    public void render() {
        final FeatureList<Script> scripts = Vandalism.getInstance().getScriptRegistry().getScripts();
        for (final Map.Entry<File, ScriptEditor> entry : this.scriptEditors.entrySet()) {
            if (entry.getValue().isClosed()) this.scriptEditors.remove(entry.getKey());
        }
        if (ImGui.begin("Scripts", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.MenuBar)) {
            if (ImGui.beginMenuBar()) {
                if (ImGui.button("Open directory##scriptsopendir")) {
                    Util.getOperatingSystem().open(Vandalism.getInstance().getScriptRegistry().getDirectory());
                }
                if (ImGui.button("Reload##scriptsreload")) {
                    Vandalism.getInstance().getScriptRegistry().load();
                }
                if (ImGui.button("Create new script##scriptscreatenewscript")) {
                    final String name = "Template#" + RandomUtils.randomInt(1000, 9999);
                    final File scriptFile = new File(
                            Vandalism.getInstance().getScriptRegistry().getDirectory(),
                            name + ScriptParser.SCRIPT_FILE_EXTENSION
                    );
                    this.scriptEditors.put(scriptFile, new ScriptEditor(scriptFile));
                }
                if (!this.scriptEditors.isEmpty()) {
                    if (ImGui.button((this.scriptEditors.size() < 2 ? "Close editor" : "Close all editors (" + this.scriptEditors.size() + ")") + "##scriptsclosealleditors")) {
                        for (final ScriptEditor scriptEditor : this.scriptEditors.values()) {
                            scriptEditor.close();
                        }
                    }
                }
                if (ScriptExecutor.getRunningScriptsCount() > 0) {
                    if (ImGui.button("Kill " + ScriptExecutor.getRunningScriptsCount() + " running script/s##scriptskill")) {
                        ScriptExecutor.killAllRunningScripts();
                    }
                }
                ImGui.endMenuBar();
            }
            if (ImGui.beginTabBar("##scriptstabbar", ImGuiTabBarFlags.AutoSelectNewTabs)) {
                if (ImGui.beginTabItem("List##scriptstablist")) {
                    if (!this.hideHint) {
                        ImGui.textColored(1.0f, 1.0f, 0.0f, 0.8f, "Hint: You can enable execution logging in: config -> main config -> menu category -> script execution logging");
                        ImGui.sameLine();
                        if (ImGui.button("Hide hint##scriptshidehint", 0, 22)) this.hideHint = true;
                    } else if (ImGui.button("Show hint##scriptshidehint", 0, 22)) this.hideHint = false;
                    ImGui.newLine();
                    if (scripts.isEmpty()) {
                        ImGui.textWrapped("No scripts loaded!");
                    } else {
                        final TableColumn[] tableColumns = TableColumn.values();
                        final int maxTableColumns = tableColumns.length;
                        if (ImGui.beginTable("scripts##scriptstable", maxTableColumns, ImGuiTableFlags.Borders | ImGuiTableFlags.RowBg)) {
                            for (final TableColumn tableColumn : tableColumns) {
                                ImGui.tableSetupColumn(tableColumn.normalName());
                            }
                            ImGui.tableHeadersRow();
                            for (final Script script : scripts) {
                                if (!script.getFile().exists()) {
                                    scripts.remove(script);
                                    Vandalism.getInstance().getConfigManager().save(Vandalism.getInstance().getConfigManager().getScriptConfig());
                                    continue;
                                }
                                ImGui.tableNextRow();
                                for (int i = 0; i < maxTableColumns; i++) {
                                    ImGui.tableSetColumnIndex(i);
                                    final TableColumn tableColumn = tableColumns[i];
                                    final File scriptFile = script.getFile();
                                    if (scriptFile != null && scriptFile.exists()) {
                                        switch (tableColumn) {
                                            case NAME ->
                                                    ImGui.textWrapped(script.getName() + (!scriptFile.canWrite() ? " (Read-Only)" : ""));
                                            case VERSION -> ImGui.textWrapped(script.getVersion());
                                            case AUTHOR -> ImGui.textWrapped(script.getAuthor());
                                            case DESCRIPTION -> ImGui.textWrapped(script.getDescription());
                                            case CATEGORY -> ImGui.textWrapped(script.getCategory().normalName());
                                            case EXPERIMENTAL ->
                                                    ImGui.textWrapped(script.isExperimental() ? "Yes" : "No");
                                            case MODIFICATION_DATE -> {
                                                ImGui.textWrapped(MODIFICATION_DATE_FORMAT.format(new Date(script.getFile().lastModified())));
                                            }
                                            case ACTIONS -> {
                                                ImGui.spacing();
                                                final int buttonWidth = -1, buttonHeight = 28;
                                                for (final Value<?> scriptValue : script.getValues())
                                                    scriptValue.render();
                                                if (scriptFile.length() > 0 && MinecraftClient.getInstance().player != null) {
                                                    if (ImGui.button(
                                                            (ScriptExecutor.isScriptRunning(scriptFile) ? "Kill" : "Execute") +
                                                                    "##scriptsexecute" + script.getName(),
                                                            buttonWidth, buttonHeight
                                                    )) {
                                                        if (ScriptExecutor.isScriptRunning(scriptFile)) {
                                                            ScriptExecutor.killRunningScriptByScriptFile(scriptFile);
                                                        } else ScriptExecutor.executeScriptByScriptFile(scriptFile);
                                                    }
                                                }
                                                if (!this.scriptEditors.containsKey(scriptFile)) {
                                                    if (ImGui.button("Edit##scriptsedit" + script.getName(), buttonWidth, buttonHeight)) {
                                                        if (!scriptFile.exists()) {
                                                            Vandalism.getInstance().getLogger().error(
                                                                    "Failed to open script editor for script file '" +
                                                                            scriptFile.getName() +
                                                                            "' because the file doesn't exist!"
                                                            );
                                                            return;
                                                        }
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
                                                        } catch (final Throwable throwable) {
                                                            Vandalism.getInstance().getLogger().error("Error while opening script file: " + scriptFile.getName(), throwable);
                                                        }
                                                    }
                                                } else {
                                                    final ScriptEditor scriptEditor = this.scriptEditors.get(scriptFile);
                                                    if (ImGui.button("Close editor##scriptseditorclose" + script.getName(), buttonWidth, buttonHeight)) {
                                                        scriptEditor.close();
                                                    }
                                                    if (scriptEditor.canBeSaved()) {
                                                        ImGui.sameLine();
                                                        if (ImGui.button("Save##scriptssave" + script.getName(), buttonWidth, buttonHeight)) {
                                                            scriptEditor.save();
                                                        }
                                                    }
                                                }
                                                if (ImGui.button("Delete##scriptsdelete" + script.getName(), buttonWidth, buttonHeight)) {
                                                    if (!script.getFile().delete()) {
                                                        Vandalism.getInstance().getLogger().error("Failed to delete script: " + script.getName());
                                                    } else {
                                                        scripts.remove(script);
                                                        Vandalism.getInstance().getLogger().info("Deleted script: " + script.getName());
                                                    }
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
                    ImGui.endTabItem();
                }
                for (final ScriptEditor scriptEditor : this.scriptEditors.values()) {
                    scriptEditor.render();
                }
                ImGui.endTabBar();
            }
            ImGui.end();
        }
    }

}
