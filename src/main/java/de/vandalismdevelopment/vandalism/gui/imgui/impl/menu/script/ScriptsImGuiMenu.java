package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.script;

import de.florianmichael.rclasses.common.RandomUtils;
import de.florianmichael.rclasses.common.StringUtils;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.script.Script;
import de.vandalismdevelopment.vandalism.feature.impl.script.ScriptParser;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import imgui.ImGui;
import imgui.flag.ImGuiTabBarFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ScriptsImGuiMenu extends ImGuiMenu {

    private final HashMap<File, ScriptEditor> scriptEditors;

    public ScriptsImGuiMenu() {
        super("Scripts");
        this.scriptEditors = new HashMap<>();
    }

    public void openScriptEditor(final File scriptFile) {
        if (!scriptFile.exists()) {
            Vandalism.getInstance().getLogger().error(
                    "Failed to open script editor for script file '" + scriptFile.getName() + "' because the file doesn't exist!"
            );
            return;
        }
        try {
            final Scanner scanner = new Scanner(scriptFile);
            final StringBuilder codeBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                codeBuilder.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
            final String code = codeBuilder.toString();
            if (!code.isEmpty()) {
                this.scriptEditors.put(scriptFile, new ScriptEditor(
                        StringUtils.replaceLast(scriptFile.getName(), ScriptParser.SCRIPT_FILE_EXTENSION, ""),
                        scriptFile,
                        code
                ));
            }
        } catch (final Throwable throwable) {
            Vandalism.getInstance().getLogger().error("Error while opening script file: " + scriptFile.getName(), throwable);
        }
    }

    private enum TableColumn {
        NAME("Name"),
        VERSION("Version"),
        AUTHOR("Author"),
        DESCRIPTION("Description"),
        CATEGORY("Category"),
        EXPERIMENTAL("Experimental"),
        ACTIONS("Actions");

        private final String name;

        TableColumn(final String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    @Override
    public void render() {
        for (final Map.Entry<File, ScriptEditor> entry : this.scriptEditors.entrySet()) {
            if (entry.getValue().isClosed()) this.scriptEditors.remove(entry.getKey());
        }
        if (ImGui.begin("Scripts", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.MenuBar)) {
            if (ImGui.beginMenuBar()) {
                if (ImGui.beginMenu("File##scriptsmenu")) {
                    if (ImGui.menuItem("Reload All##scriptsreloadall")) {
                        Vandalism.getInstance().getScriptRegistry().load();
                    }
                    if (ImGui.menuItem("Open Directory##scriptsopendir")) {
                        Util.getOperatingSystem().open(Vandalism.getInstance().getScriptRegistry().getDirectory());
                    }
                    if (ImGui.menuItem("Create New##scriptscreatenewscript")) {
                        final String name = "New (" + RandomUtils.randomInt(1000, 9999) + ")";
                        final File scriptFile = new File(
                                Vandalism.getInstance().getScriptRegistry().getDirectory(),
                                name + ScriptParser.SCRIPT_FILE_EXTENSION
                        );
                        this.scriptEditors.put(scriptFile, new ScriptEditor(name, scriptFile));
                    }
                    ImGui.endMenu();
                }
                ImGui.endMenuBar();
            }
            if (ImGui.beginTabBar("##scriptstabbar", ImGuiTabBarFlags.AutoSelectNewTabs)) {
                if (ImGui.beginTabItem("List##scriptstablist")) {
                    if (Vandalism.getInstance().getScriptRegistry().getScripts().isEmpty()) {
                        ImGui.text("No scripts loaded!");
                    } else {
                        ImGui.newLine();
                        final TableColumn[] tableColumns = TableColumn.values();
                        final int maxTableColumns = tableColumns.length;
                        if (ImGui.beginTable("scripts##scriptstable", maxTableColumns, ImGuiTableFlags.Borders | ImGuiTableFlags.RowBg)) {
                            for (final TableColumn tableColumn : tableColumns) {
                                ImGui.tableSetupColumn(tableColumn.getName());
                            }
                            ImGui.tableHeadersRow();
                            for (final Script script : Vandalism.getInstance().getScriptRegistry().getScripts()) {
                                ImGui.tableNextRow();
                                for (int i = 0; i < maxTableColumns; i++) {
                                    ImGui.tableSetColumnIndex(i);
                                    final TableColumn tableColumn = tableColumns[i];
                                    switch (tableColumn) {
                                        case NAME -> ImGui.text(script.getName());
                                        case VERSION -> ImGui.text(script.getVersion());
                                        case AUTHOR -> ImGui.text(script.getAuthor());
                                        case DESCRIPTION -> ImGui.text(script.getDescription());
                                        case CATEGORY -> ImGui.text(script.getCategory().normalName());
                                        case EXPERIMENTAL -> ImGui.text(script.isExperimental() ? "Yes" : "No");
                                        case ACTIONS -> {
                                            if (MinecraftClient.getInstance().player != null) {
                                                if (ImGui.button("Execute##scripts" + script.getName())) {
                                                    Vandalism.getInstance().getScriptRegistry().executeScript(script);
                                                }
                                                ImGui.sameLine();
                                            }
                                            final File scriptFile = script.getFile();
                                            if (scriptFile != null && scriptFile.exists()) {
                                                if (!this.scriptEditors.containsKey(scriptFile)) {
                                                    if (ImGui.button("Edit##scriptsedit" + script.getName())) {
                                                        this.openScriptEditor(scriptFile);
                                                    }
                                                    ImGui.sameLine();
                                                    if (ImGui.button("Delete##scriptsopendelete" + script.getName())) {
                                                        if (!scriptFile.delete()) {
                                                            Vandalism.getInstance().getLogger().error("Failed to delete script: " + script.getName());
                                                        } else
                                                            Vandalism.getInstance().getLogger().info("Deleted script: " + script.getName());
                                                        Vandalism.getInstance().getScriptRegistry().getScripts().remove(script);
                                                    }
                                                } else {
                                                    final ScriptEditor scriptEditor = this.scriptEditors.get(scriptFile);
                                                    if (scriptEditor.isModified()) {
                                                        if (ImGui.button("Save##scriptssave" + script.getName())) {
                                                            scriptEditor.save();
                                                        }
                                                        ImGui.sameLine();
                                                    }
                                                    if (ImGui.button("Close Editor##scriptseditorclose" + script.getName())) {
                                                        scriptEditor.close();
                                                    }
                                                }
                                            }
                                        }
                                        default -> {
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
