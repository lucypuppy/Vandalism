package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.script;

import de.florianmichael.rclasses.common.StringUtils;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.script.parse.ScriptParser;
import de.vandalismdevelopment.vandalism.feature.script.parse.ScriptVariable;
import de.vandalismdevelopment.vandalism.feature.script.parse.command.ScriptCommand;
import de.vandalismdevelopment.vandalism.feature.script.parse.info.IScriptInfo;
import de.vandalismdevelopment.vandalism.feature.script.parse.info.ScriptInfo;
import de.vandalismdevelopment.vandalism.feature.script.parse.info.impl.BooleanScriptInfo;
import de.vandalismdevelopment.vandalism.feature.script.parse.info.impl.CategoryScriptInfo;
import de.vandalismdevelopment.vandalism.feature.script.parse.info.impl.StringScriptInfo;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import net.minecraft.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class ScriptEditor implements MinecraftWrapper {

    private static final ImGuiInputTextCallback FILE_NAME_FILTER = new ImGuiInputTextCallback() {

        @Override
        public void accept(final ImGuiInputTextCallbackData imGuiInputTextCallbackData) {
            if (imGuiInputTextCallbackData.getEventChar() == 0) return;
            if (!Character.isLetterOrDigit(imGuiInputTextCallbackData.getEventChar()) && imGuiInputTextCallbackData.getEventChar() != '_' && imGuiInputTextCallbackData.getEventChar() != '-' && imGuiInputTextCallbackData.getEventChar() != '(' && imGuiInputTextCallbackData.getEventChar() != ')' && imGuiInputTextCallbackData.getEventChar() != '#') {
                if (imGuiInputTextCallbackData.getEventChar() == ' ') {
                    imGuiInputTextCallbackData.setEventChar('-');
                } else {
                    imGuiInputTextCallbackData.setEventChar((char) 0);
                }
            }
        }

    };

    private static String getInfoExample() {
        final StringBuilder exampleInfoBuilder = new StringBuilder();
        for (final ScriptInfo scriptInfo : ScriptInfo.values()) {
            exampleInfoBuilder.append(ScriptParser.INFO_CHAR).append(scriptInfo.getTag()).append(' ');
            final IScriptInfo<?> iScriptInfo = scriptInfo.get();
            if (iScriptInfo instanceof final StringScriptInfo stringScriptInfo) {
                exampleInfoBuilder.append(stringScriptInfo.defaultValue());
            } else if (iScriptInfo instanceof final CategoryScriptInfo categoryScriptInfo) {
                exampleInfoBuilder.append(categoryScriptInfo.defaultValue().normalName());
            } else if (iScriptInfo instanceof final BooleanScriptInfo booleanScriptInfo) {
                exampleInfoBuilder.append(booleanScriptInfo.defaultValue());
            }
            exampleInfoBuilder.append('\n');
        }
        exampleInfoBuilder.append('\n');
        return exampleInfoBuilder.toString();
    }

    private static String getExampleCode() {
        final StringBuilder exampleCodeBuilder = new StringBuilder();
        exampleCodeBuilder.append(getInfoExample());
        exampleCodeBuilder.append("This is a list of all the available script commands and their examples:\n\n");
        for (final ScriptCommand scriptCommand : ScriptCommand.values()) {
            exampleCodeBuilder.append("\t\t").append(ScriptParser.CODE_CHAR).append(scriptCommand.name().toLowerCase()).append(" - ").append(StringUtils.replaceAll(scriptCommand.getExample(), "\n", "\n\t\t")).append("\n\n");
        }
        exampleCodeBuilder.append("\n\n");
        exampleCodeBuilder.append("This is a list of all the available script variables and their descriptions:\n\n");
        for (final ScriptVariable scriptVariable : ScriptVariable.values()) {
            exampleCodeBuilder.append("\t\t").append(ScriptParser.VARIABLE_CHAR).append(scriptVariable.name().toLowerCase()).append(ScriptParser.VARIABLE_CHAR).append(" - ").append(scriptVariable.getDescription()).append('\n');
        }
        exampleCodeBuilder.append("\n\n\n");
        return exampleCodeBuilder.toString();
    }

    private File scriptFile;
    private long lastScriptFileModification;
    private final TextEditor textEditor;
    private final ImString scriptName, infoTextField;
    private String originalScriptName;
    private boolean rename, closed;

    public ScriptEditor(final File scriptFile, final boolean exampleCode) {
        this(scriptFile, exampleCode ? getExampleCode() : getInfoExample(), true);
    }

    public ScriptEditor(final File scriptFile, final String code) {
        this(scriptFile, code, false);
    }

    public ScriptEditor(final File scriptFile, final String code, final boolean rename) {
        this.scriptFile = scriptFile;
        this.lastScriptFileModification = this.scriptFile.lastModified();
        this.textEditor = new TextEditor();
        final TextEditorLanguageDefinition languageDefinition = TextEditorLanguageDefinition.c();
        final List<String> keywords = new ArrayList<>();
        for (final ScriptCommand scriptCommand : ScriptCommand.values()) {
            keywords.add(scriptCommand.name().toLowerCase());
        }
        for (final ScriptVariable scriptVariable : ScriptVariable.values()) {
            keywords.add(scriptVariable.name().toLowerCase());
        }
        languageDefinition.setKeywords(keywords.toArray(new String[0]));
        final Map<String, String> preprocIdentifierMap = new HashMap<>();
        for (final ScriptCommand scriptCommand : ScriptCommand.values()) {
            preprocIdentifierMap.put(scriptCommand.name().toLowerCase(), scriptCommand.getExample());
        }
        for (final ScriptVariable scriptVariable : ScriptVariable.values()) {
            preprocIdentifierMap.put(scriptVariable.name().toLowerCase(), scriptVariable.getDescription());
        }
        languageDefinition.setPreprocIdentifiers(preprocIdentifierMap);
        this.textEditor.setLanguageDefinition(languageDefinition);
        this.textEditor.setText(code);
        this.textEditor.setShowWhitespaces(false);
        this.originalScriptName = StringUtils.replaceLast(scriptFile.getName(), ScriptParser.SCRIPT_FILE_EXTENSION, "");
        this.scriptName = new ImString(this.originalScriptName, Math.max(this.originalScriptName.length(), 50));
        this.infoTextField = new ImString();
        this.rename = rename;
        this.closed = false;
    }

    private boolean isReadOnly() {
        return this.scriptFile.exists() && !this.scriptFile.canWrite();
    }

    private boolean isUnsaved() {
        return this.textEditor.canUndo() || !this.scriptFile.exists() || (!this.scriptName.get().isBlank() && !this.scriptName.get().equals(this.originalScriptName)) || this.scriptFile.lastModified() != this.lastScriptFileModification;
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
            if (this.scriptFile.exists() && this.scriptName.get().equals(this.originalScriptName)) {
                return false;
            }
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
                this.scriptFile = new File(Vandalism.getInstance().getScriptRegistry().getDirectory(), this.scriptName.get() + ScriptParser.SCRIPT_FILE_EXTENSION);
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
            Vandalism.getInstance().getScriptRegistry().loadScriptFromFile(this.scriptFile, true);
            this.lastScriptFileModification = this.scriptFile.lastModified();
            this.textEditor.setTextLines(lines);
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
        if (ImGui.beginTabItem(this.scriptFile.getName() + (this.isReadOnly() ? " (Read-Only)" : "") + "##scriptstab" + this.originalScriptName + "editor")) {
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
                                        throw new RuntimeException("Unknown script info '" + textLine.split("( )+")[0].replaceFirst(ScriptParser.INFO_CHAR, "") + "'");
                                    } else {
                                        throw new RuntimeException("Empty script info");
                                    }
                                }
                            }
                        } else {
                            final Pair<ScriptCommand, Pair<Integer, String>> line = ScriptParser.parseCodeFromScriptLine(this.scriptName.get(), textLine, lineNumber, false);
                            if (line == null) {
                                if (textLine.startsWith(ScriptParser.CODE_CHAR)) {
                                    final StringBuilder commands = new StringBuilder("Available script commands are:\n");
                                    for (final ScriptCommand scriptCommand : ScriptCommand.values()) {
                                        commands.append("\t\t").append(scriptCommand.name().toLowerCase()).append(" - ").append(scriptCommand.getExample().split("\n")[0]).append('\n');
                                    }
                                    if (textLine.length() > 1) {
                                        throw new RuntimeException("Unknown script command '" + textLine.replaceFirst(ScriptParser.CODE_CHAR, "") + "'\n\n" + commands);
                                    } else {
                                        throw new RuntimeException(commands.toString());
                                    }
                                }
                            } else {
                                final Pair<Integer, String> codeLine = line.getRight();
                                if (codeLine == null) {
                                    throw new RuntimeException("Invalid script command '" + textLine.replaceFirst(ScriptParser.CODE_CHAR, "") + "'");
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
                ImGui.inputText("##scriptsnewnametextfield" + this.originalScriptName + "editor", new ImString("Another process has modified the file, do you want to reload it?"), ImGuiInputTextFlags.ReadOnly);
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
                        Vandalism.getInstance().getLogger().error("Error while reloading script file from disk: " + this.scriptFile.getName(), ioException);
                    }
                }
                ImGui.sameLine();
                if (ImGui.button("Ignore##scriptsignorereloadfromdiskin" + this.originalScriptName + "editor")) {
                    this.lastScriptFileModification = this.scriptFile.lastModified();
                }
            }
            final int cPosX = this.textEditor.getCursorPositionLine(), cPosY = this.textEditor.getCursorPositionColumn();
            this.infoTextField.set(cPosX + "/" + cPosY + " " + this.textEditor.getTotalLines() + " lines");
            int buttonWidth = 0, buttonHeight = 27;
            ImGui.setNextItemWidth(-300);
            ImGui.inputText("##scriptsinfotextfield" + this.originalScriptName + "editor", this.infoTextField, ImGuiInputTextFlags.ReadOnly);
            if (Vandalism.getInstance().getScriptRegistry().isScriptRunning(this.scriptFile) || (!this.canBeSaved() && this.scriptFile.exists() && this.scriptFile.length() > 0 && this.mc.player != null)) {
                ImGui.sameLine();
                if (ImGui.button((Vandalism.getInstance().getScriptRegistry().isScriptRunning(this.scriptFile) ? "Kill" : "Execute") + "##scriptsexecuteorkillin" + this.originalScriptName + "editor", buttonWidth, buttonHeight)) {
                    if (Vandalism.getInstance().getScriptRegistry().isScriptRunning(this.scriptFile)) {
                        Vandalism.getInstance().getScriptRegistry().killRunningScriptByScriptFile(this.scriptFile);
                    } else Vandalism.getInstance().getScriptRegistry().executeScriptByScriptFile(this.scriptFile);
                }
            }
            if (!this.rename && !this.isReadOnly()) {
                ImGui.sameLine();
                if (ImGui.button("Rename##scriptsrenamein" + this.originalScriptName + "editor", buttonWidth, buttonHeight)) {
                    this.rename = true;
                }
            }
            if (this.canBeSaved()) {
                ImGui.sameLine();
                if (ImGui.button((this.scriptFile.exists() ? "Save" : "Create") + "##scriptssavein" + this.originalScriptName + "editor", buttonWidth, buttonHeight)) {
                    this.save();
                }
                if (this.textEditor.canUndo()) {
                    ImGui.sameLine();
                    if (ImGui.button("Undo##scriptsundoin" + this.originalScriptName + "editor", buttonWidth, buttonHeight)) {
                        this.textEditor.undo(1);
                    }
                }
                if (this.textEditor.canRedo()) {
                    ImGui.sameLine();
                    if (ImGui.button("Redo##scriptsredoin" + this.originalScriptName + "editor", buttonWidth, buttonHeight)) {
                        this.textEditor.redo(1);
                    }
                }
            }
            ImGui.sameLine();
            if (ImGui.button((unsaved ? "Cancel" : "Close") + "##scriptsclosein" + this.originalScriptName + "editor", buttonWidth, buttonHeight)) {
                if (this.rename && this.scriptFile.exists()) {
                    this.rename = false;
                    this.scriptName.set(this.originalScriptName);
                } else this.close();
            }
            if (this.rename) {
                ImGui.inputText("Enter new name##scriptsnewnametextfield" + this.originalScriptName + "editor", this.scriptName, ImGuiInputTextFlags.CallbackCharFilter, FILE_NAME_FILTER);
            }
            this.textEditor.render(this.originalScriptName);
            this.textEditor.setReadOnly(this.isReadOnly());
            ImGui.endTabItem();
        }
        if (unsaved) ImGui.popStyleColor(3);
    }

}