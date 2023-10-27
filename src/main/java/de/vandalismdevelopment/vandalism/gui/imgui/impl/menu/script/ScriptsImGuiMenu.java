package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.script;

import de.florianmichael.rclasses.common.RandomUtils;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.FeatureList;
import de.vandalismdevelopment.vandalism.feature.impl.script.Script;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.ScriptParser;
import de.vandalismdevelopment.vandalism.gui.imgui.ImGuiMenu;
import de.vandalismdevelopment.vandalism.value.Value;
import imgui.ImGui;
import imgui.flag.ImGuiPopupFlags;
import imgui.flag.ImGuiTabBarFlags;
import imgui.flag.ImGuiTableFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptsImGuiMenu extends ImGuiMenu {

    private final static SimpleDateFormat MODIFICATION_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss a");

    private final static String[] HINTS = new String[]{
            "You can enable execution logging in: " +
                    "config -> main config -> menu category -> script execution logging"
    };

    private final ConcurrentHashMap<File, ScriptEditor> scriptEditors;

    private final ImBoolean hideHints;

    public ScriptsImGuiMenu() {
        super("Scripts");
        this.scriptEditors = new ConcurrentHashMap<>();
        this.hideHints = new ImBoolean(false);
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
                    if (ImGui.button(
                            (this.scriptEditors.size() < 2 ? "Close editor" : "Close all editors (" + this.scriptEditors.size() + ")") +
                                    "##scriptsclosealleditors"
                    )
                    ) {
                        for (final ScriptEditor scriptEditor : this.scriptEditors.values()) {
                            scriptEditor.close();
                        }
                    }
                }
                if (Vandalism.getInstance().getScriptRegistry().getRunningScriptsCount() > 0) {
                    if (ImGui.button("Kill " + Vandalism.getInstance().getScriptRegistry().getRunningScriptsCount() +
                            " running script/s##scriptskill")
                    ) {
                        Vandalism.getInstance().getScriptRegistry().killAllRunningScripts();
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
                    if (scripts.isEmpty()) {
                        ImGui.textWrapped("No scripts loaded!");
                    } else {
                        final ScriptsTableColumn[] scriptsTableColumns = ScriptsTableColumn.values();
                        final int maxTableColumns = scriptsTableColumns.length;
                        if (ImGui.beginTable("scripts##scriptstable", maxTableColumns,
                                ImGuiTableFlags.Borders |
                                        ImGuiTableFlags.Resizable |
                                        ImGuiTableFlags.RowBg |
                                        ImGuiTableFlags.ContextMenuInBody
                        )) {
                            for (final ScriptsTableColumn scriptsTableColumn : scriptsTableColumns) {
                                ImGui.tableSetupColumn(scriptsTableColumn.normalName());
                            }
                            ImGui.tableHeadersRow();
                            for (final Script script : scripts) {
                                if (!script.getFile().exists()) {
                                    scripts.remove(script);
                                    Vandalism.getInstance().getConfigManager().save(Vandalism.getInstance().getConfigManager().getScriptConfig());
                                    continue;
                                }
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
                                            case CATEGORY -> ImGui.textWrapped(script.getCategory().normalName());
                                            case EXPERIMENTAL -> {
                                                ImGui.textWrapped(script.isExperimental() ? "Yes" : "No");
                                            }
                                            case MODIFICATION_DATE -> {
                                                ImGui.textWrapped(MODIFICATION_DATE_FORMAT.format(new Date(script.getFile().lastModified())));
                                            }
                                            case QUICK_ACTIONS -> {
                                                ImGui.spacing();
                                                ImGui.button("...##scriptsmoreactions" + script.getName());
                                                if (ImGui.beginPopupContextItem("##scriptsmoreactionspopup" + script.getName(),
                                                        ImGuiPopupFlags.MouseButtonLeft
                                                )) {
                                                    ImGui.text(script.getName());
                                                    ImGui.separator();
                                                    ImGui.spacing();
                                                    if (MinecraftClient.getInstance().player != null) {
                                                        if (ImGui.button(
                                                                (Vandalism.getInstance().getScriptRegistry()
                                                                        .isScriptRunning(scriptFile) ? "Kill" : "Execute") + "##scriptsexecuteorkill" +
                                                                        script.getName(),
                                                                buttonWidth, buttonHeight
                                                        )) {
                                                            if (Vandalism.getInstance().getScriptRegistry().isScriptRunning(scriptFile)) {
                                                                Vandalism.getInstance().getScriptRegistry().killRunningScriptByScriptFile(scriptFile);
                                                            } else {
                                                                Vandalism.getInstance().getScriptRegistry().executeScriptByScriptFile(scriptFile);
                                                            }
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
                                                                } catch (final Throwable throwable) {
                                                                    Vandalism.getInstance().getLogger().error(
                                                                            "Error while opening script file: " + scriptFile.getName(),
                                                                            throwable
                                                                    );
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
                                                    if (ImGui.button("Delete##scriptsdelete" + script.getName(), buttonWidth, buttonHeight)) {
                                                        if (!script.getFile().delete()) {
                                                            Vandalism.getInstance().getLogger().error("Failed to delete script: " + script.getName());
                                                        } else {
                                                            scripts.remove(script);
                                                            Vandalism.getInstance().getLogger().info("Deleted script: " + script.getName());
                                                        }
                                                    }
                                                    ImGui.endPopup();
                                                }
                                                ImGui.sameLine();
                                                for (final Value<?> scriptValue : script.getValues()) {
                                                    scriptValue.render();
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
