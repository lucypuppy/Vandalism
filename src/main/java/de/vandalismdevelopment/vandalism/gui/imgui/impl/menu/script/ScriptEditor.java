package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.script;

import de.florianmichael.rclasses.common.StringUtils;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.ScriptParser;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.command.ScriptCommand;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.info.IScriptInfo;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.info.ScriptInfo;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.info.impl.BooleanScriptInfo;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.info.impl.CategoryScriptInfo;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.info.impl.StringScriptInfo;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ScriptEditor {

    private final static ImGuiInputTextCallback FILE_NAME_FILTER = new ImGuiInputTextCallback() {

        @Override
        public void accept(final ImGuiInputTextCallbackData imGuiInputTextCallbackData) {
            if (imGuiInputTextCallbackData.getEventChar() == 0) return;
            if (!Character.isLetterOrDigit(imGuiInputTextCallbackData.getEventChar()) &&
                    imGuiInputTextCallbackData.getEventChar() != '_' &&
                    imGuiInputTextCallbackData.getEventChar() != '-' &&
                    imGuiInputTextCallbackData.getEventChar() != '(' &&
                    imGuiInputTextCallbackData.getEventChar() != ')' &&
                    imGuiInputTextCallbackData.getEventChar() != '#'
            ) {
                imGuiInputTextCallbackData.setEventChar((char) 0);
            }
        }

    };

    private final static String EXAMPLE_CODE;

    static {
        final StringBuilder exampleCodeBuilder = new StringBuilder();
        for (final ScriptInfo scriptInfo : ScriptInfo.values()) {
            exampleCodeBuilder.append(ScriptParser.INFO_CHAR).append(scriptInfo.getTag()).append(' ');
            final IScriptInfo<?> iScriptInfo = scriptInfo.get();
            if (iScriptInfo instanceof final StringScriptInfo stringScriptInfo) {
                exampleCodeBuilder.append(stringScriptInfo.defaultValue());
            } else if (iScriptInfo instanceof final CategoryScriptInfo categoryScriptInfo) {
                exampleCodeBuilder.append(categoryScriptInfo.defaultValue().normalName());
            } else if (iScriptInfo instanceof final BooleanScriptInfo booleanScriptInfo) {
                exampleCodeBuilder.append(booleanScriptInfo.defaultValue());
            }
            exampleCodeBuilder.append('\n');
        }
        exampleCodeBuilder.append("\n\n");
        exampleCodeBuilder.append("You can use \"").append(ScriptParser.CODE_CHAR).append("\" as prefix to execute a command like this: ");
        exampleCodeBuilder.append(ScriptParser.CODE_CHAR).append(ScriptCommand.RUN.name().toLowerCase()).append(" say Hello World!\n");
        exampleCodeBuilder.append("You can also use variables with \"").append(ScriptParser.VARIABLE_CHAR).append("\" as prefix and suffix like this: ");
        exampleCodeBuilder.append(ScriptParser.CODE_CHAR).append(ScriptCommand.RUN.name().toLowerCase()).append(" say Hello ");
        exampleCodeBuilder.append(ScriptParser.VARIABLE_CHAR).append("username").append(ScriptParser.VARIABLE_CHAR).append("!\n\n\n");
        EXAMPLE_CODE = exampleCodeBuilder.toString();
    }

    private File scriptFile;
    private long lastScriptFileModification;
    private final TextEditor textEditor;
    private final ImString scriptName, infoTextField;
    private String originalScriptName;
    private boolean rename, closed;

    public ScriptEditor(final File scriptFile) {
        this(scriptFile, EXAMPLE_CODE, true);
    }

    public ScriptEditor(final File scriptFile, final String code) {
        this(scriptFile, code, false);
    }

    public ScriptEditor(final File scriptFile, final String code, final boolean rename) {
        this.scriptFile = scriptFile;
        this.lastScriptFileModification = this.scriptFile.lastModified();
        this.textEditor = new TextEditor();
        this.textEditor.setLanguageDefinition(TextEditorLanguageDefinition.c());
        this.textEditor.setText(code);
        this.textEditor.setShowWhitespaces(false);
        this.originalScriptName = StringUtils.replaceLast(scriptFile.getName(), ScriptParser.SCRIPT_FILE_EXTENSION, "");
        this.scriptName = new ImString(this.originalScriptName, Math.max(this.originalScriptName.length(), 50));
        this.infoTextField = new ImString(100);
        this.rename = rename;
        this.closed = false;
    }

    private boolean isReadOnly() {
        return this.scriptFile.exists() && !this.scriptFile.canWrite();
    }

    private boolean isUnsaved() {
        return this.textEditor.canUndo() ||
                !this.scriptFile.exists() ||
                (!this.scriptName.get().isBlank() && !this.scriptName.get().equals(this.originalScriptName)) ||
                this.scriptFile.lastModified() != this.lastScriptFileModification;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void close() {
        this.closed = true;
    }

    public boolean canBeSaved() {
        if (this.isReadOnly()) return false;
        if (!this.isUnsaved()) return false;
        if (this.rename) {
            final File[] files = Vandalism.getInstance().getScriptRegistry().getDirectory().listFiles();
            if (files != null) {
                for (final File file : files) {
                    if (this.scriptName.get().equalsIgnoreCase(StringUtils.replaceLast(file.getName(), ScriptParser.SCRIPT_FILE_EXTENSION, ""))) {
                        return false;
                    }
                }
            }
            if (this.scriptName.get().isBlank()) return false;
            if (this.scriptFile.exists() && this.scriptName.get().equals(this.originalScriptName)) return false;
        }
        return true;
    }

    public void save() {
        try {
            if (!this.canBeSaved()) {
                Vandalism.getInstance().getLogger().error("Error script " + this.scriptFile.getName() + " can't be saved!");
                return;
            }
            if (this.rename) {
                if (this.scriptFile.exists()) {
                    if (!this.scriptFile.delete()) {
                        Vandalism.getInstance().getLogger().warn("Failed to delete old script file: " + this.scriptFile.getName());
                    }
                }
                this.scriptFile = new File(
                        Vandalism.getInstance().getScriptRegistry().getDirectory(),
                        this.scriptName.get() + ScriptParser.SCRIPT_FILE_EXTENSION
                );
                this.originalScriptName = this.scriptName.get();
                this.rename = false;
            }
            if (!this.scriptFile.exists() && !this.scriptFile.createNewFile()) {
                Vandalism.getInstance().getLogger().error("Failed to create script file: " + this.scriptFile.getName());
                return;
            }
            final PrintWriter printerWriter = new PrintWriter(this.scriptFile);
            final String[] lines = this.textEditor.getTextLines();
            for (final String line : lines) printerWriter.println(line);
            printerWriter.close();
            Vandalism.getInstance().getScriptRegistry().loadScriptFromFile(this.scriptFile);
            this.lastScriptFileModification = this.scriptFile.lastModified();
            this.textEditor.setTextLines(lines);
            Vandalism.getInstance().getConfigManager().save(Vandalism.getInstance().getConfigManager().getScriptConfig());
        } catch (final Throwable throwable) {
            Vandalism.getInstance().getLogger().error("Error while saving script file: " + this.scriptFile.getName(), throwable);
        }
    }

    public void render() {
        final boolean unsaved = this.isUnsaved() || this.rename;
        if (unsaved) {
            ImGui.pushStyleColor(ImGuiCol.Tab, 1.0f, 0.0f, 0.0f, 0.4f);
            ImGui.pushStyleColor(ImGuiCol.TabActive, 1.0f, 0.0f, 0.0f, 0.4f);
            ImGui.pushStyleColor(ImGuiCol.TabHovered, 0.8f, 0.0f, 0.0f, 0.4f);
        }
        if (ImGui.beginTabItem(this.scriptFile.getName() + (this.isReadOnly() ? " (Read-Only)" : "") +
                "##scriptstab" + this.originalScriptName + "editor")
        ) {
            final Map<Integer, String> errorMarkers = new HashMap<>();
            final String[] textLines = this.textEditor.getTextLines();
            if (textLines.length < 1) errorMarkers.put(0, "Script is empty!");
            else {
                for (int i = 0; i < textLines.length; i++) {
                    String textLine = textLines[i];
                    final int lineNumber = i + 1;
                    try {
                        if (lineNumber < ScriptInfo.values().length + 1) {
                            final Object line = ScriptParser.parseInfoFromScriptLine(this.scriptName.get(), textLine, lineNumber, false);
                            if (line == null) {
                                if (textLine.startsWith(ScriptParser.INFO_CHAR)) {
                                    if (textLine.length() > 1) {
                                        throw new RuntimeException(
                                                "Unknown script info '" +
                                                        textLine.split("( )+")[0].replaceFirst(ScriptParser.INFO_CHAR, "")
                                                        + "'"
                                        );
                                    } else {
                                        throw new RuntimeException("Empty script info");
                                    }
                                }
                            }
                        } else {
                            final Pair<ScriptCommand, Pair<Integer, String>> line = ScriptParser.parseCodeFromScriptLine(
                                    this.scriptName.get(), textLine, lineNumber, false
                            );
                            if (line == null) {
                                if (textLine.startsWith(ScriptParser.CODE_CHAR)) {
                                    if (textLine.length() > 1) {
                                        throw new RuntimeException(
                                                "Unknown script command '" + textLine.replaceFirst(ScriptParser.CODE_CHAR, "") + "'"
                                        );
                                    } else {
                                        throw new RuntimeException("Empty script command");
                                    }
                                }
                            } else {
                                final Pair<Integer, String> codeLine = line.getRight();
                                if (codeLine == null) {
                                    throw new RuntimeException(
                                            "Invalid script command '" + textLine.replaceFirst(ScriptParser.CODE_CHAR, "") + "'"
                                    );
                                }
                            }
                        }
                    } catch (final Exception e) {
                        errorMarkers.put(lineNumber, e.getMessage().replace(e.getClass().getName() + ": ", ""));
                    }
                }
            }
            this.textEditor.setErrorMarkers(errorMarkers);
            if (this.scriptFile.exists() && this.lastScriptFileModification != this.scriptFile.lastModified()) {
                ImGui.inputText(
                        "##scriptsnewnametextfield" + this.originalScriptName + "editor",
                        new ImString("Another process has modified the file, do you want to reload it?"),
                        ImGuiInputTextFlags.ReadOnly
                );
                ImGui.sameLine();
                if (ImGui.button("Reload##scriptsreloadfromdiskin" + this.originalScriptName + "editor")) {
                    try {
                        final Scanner scanner = new Scanner(this.scriptFile);
                        final StringBuilder code = new StringBuilder();
                        while (scanner.hasNextLine()) code.append(scanner.nextLine()).append('\n');
                        scanner.close();
                        this.textEditor.setText(code.toString());
                        this.lastScriptFileModification = this.scriptFile.lastModified();
                    } catch (final IOException ioException) {
                        Vandalism.getInstance().getLogger().error(
                                "Error while reloading script file from disk: " + this.scriptFile.getName(),
                                ioException
                        );
                    }
                }
                ImGui.sameLine();
                if (ImGui.button("Ignore##scriptsignorereloadfromdiskin" + this.originalScriptName + "editor")) {
                    this.lastScriptFileModification = this.scriptFile.lastModified();
                }
            }
            final int cPosX = this.textEditor.getCursorPositionLine(), cPosY = this.textEditor.getCursorPositionColumn();
            this.infoTextField.set(cPosX + "/" + cPosY + " " + this.textEditor.getTotalLines() + " lines");
            int offset = -445, buttonHeight = 27;
            if (unsaved) offset -= 99;
            ImGui.setNextItemWidth(offset);
            ImGui.inputText(
                    "##scriptsinfotextfield" + this.originalScriptName + "editor",
                    this.infoTextField,
                    ImGuiInputTextFlags.ReadOnly
            );
            offset += 99;
            ImGui.sameLine();
            if (
                    Vandalism.getInstance().getScriptRegistry().isScriptRunning(this.scriptFile) ||
                            (!this.canBeSaved() && this.scriptFile.exists() && this.scriptFile.length() > 0 && MinecraftClient.getInstance().player != null)
            ) {
                if (ImGui.button(
                        (Vandalism.getInstance().getScriptRegistry().isScriptRunning(this.scriptFile) ? "Kill" : "Execute") +
                                "##scriptsexecutein" + this.originalScriptName + "editor",
                        offset, buttonHeight
                )) {
                    if (Vandalism.getInstance().getScriptRegistry().isScriptRunning(this.scriptFile)) {
                        Vandalism.getInstance().getScriptRegistry().killRunningScriptByScriptFile(this.scriptFile);
                    } else Vandalism.getInstance().getScriptRegistry().executeScriptByScriptFile(this.scriptFile);
                }
            }
            offset += 99;
            ImGui.sameLine();
            if (!this.rename && !this.isReadOnly()) {
                if (ImGui.button("Rename##scriptsrenamein" + this.originalScriptName + "editor", offset, buttonHeight)) {
                    this.rename = true;
                }
                offset += 99;
            }
            if (this.canBeSaved()) {
                ImGui.sameLine();
                if (ImGui.button(
                        (this.scriptFile.exists() ? "Save" : "Create") + "##scriptssavein" + this.originalScriptName + "editor",
                        offset,
                        buttonHeight
                )) {
                    this.save();
                }
                offset += 140;
            }
            ImGui.sameLine();
            if (ImGui.button((unsaved ? "Cancel" : "Close") + "##scriptsclosein" + this.originalScriptName + "editor", offset, buttonHeight)) {
                if (this.rename && this.scriptFile.exists()) {
                    this.rename = false;
                    this.scriptName.set(this.originalScriptName);
                } else this.close();
            }
            if (this.rename) {
                ImGui.inputText(
                        "Enter new name##scriptsnewnametextfield" + this.originalScriptName + "editor",
                        this.scriptName,
                        ImGuiInputTextFlags.CallbackCharFilter,
                        FILE_NAME_FILTER
                );
            }
            this.textEditor.render(this.originalScriptName);
            this.textEditor.setReadOnly(this.isReadOnly());
            ImGui.endTabItem();
        }
        if (unsaved) ImGui.popStyleColor(3);
    }

}