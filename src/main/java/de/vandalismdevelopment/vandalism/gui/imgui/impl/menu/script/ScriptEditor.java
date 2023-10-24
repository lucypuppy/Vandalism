package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu.script;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.script.ScriptCommand;
import de.vandalismdevelopment.vandalism.feature.impl.script.ScriptParser;
import imgui.ImGui;
import imgui.ImGuiInputTextCallbackData;
import imgui.callback.ImGuiInputTextCallback;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;

import java.io.File;
import java.io.PrintWriter;

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
                    imGuiInputTextCallbackData.getEventChar() != ' '
            ) {
                imGuiInputTextCallbackData.setEventChar((char) 0);
            }
        }

    };

    private final static String EXAMPLE_CODE = ScriptParser.INFO_CHAR + ScriptParser.INFO_VERSION + ' ' + ScriptParser.EXAMPLE_SCRIPT_VERSION + '\n' +
            ScriptParser.INFO_CHAR + ScriptParser.INFO_AUTHOR + ' ' + ScriptParser.EXAMPLE_SCRIPT_AUTHOR + '\n' +
            ScriptParser.INFO_CHAR + ScriptParser.INFO_DESCRIPTION + ' ' + ScriptParser.EXAMPLE_SCRIPT_DESCRIPTION + '\n' +
            ScriptParser.INFO_CHAR + ScriptParser.INFO_CATEGORY + ' ' + ScriptParser.EXAMPLE_SCRIPT_CATEGORY.normalName() + '\n' +
            ScriptParser.INFO_CHAR + ScriptParser.INFO_EXPERIMENTAL + ' ' + ScriptParser.EXAMPLE_SCRIPT_EXPERIMENTAL + "\n\n" +
            "You can use \"" + ScriptParser.CODE_CHAR + "\" as prefix to execute a command like this: " +
            ScriptParser.CODE_CHAR + ScriptCommand.RUN.name().toLowerCase() + " say Hello World!\n" +
            "You can also use variables with \"" + ScriptParser.VARIABLE_CHAR + "\" as prefix and suffix like this: " +
            ScriptParser.CODE_CHAR + ScriptCommand.RUN.name().toLowerCase() + " say Hello " +
            ScriptParser.VARIABLE_CHAR + "username" + ScriptParser.VARIABLE_CHAR + "!\n\n\n";

    private final String name;
    private File scriptFile;
    private final TextEditor textEditor;
    private final ImString scriptName;
    private boolean selfCreated, rename, closed;

    public ScriptEditor(final String name, final File scriptFile) {
        this(name, scriptFile, EXAMPLE_CODE);
        this.rename = true;
        this.selfCreated = true;
    }

    public ScriptEditor(final String name, final File scriptFile, final String code) {
        this.closed = false;
        this.name = name;
        this.scriptFile = scriptFile;
        this.textEditor = new TextEditor();
        this.textEditor.setLanguageDefinition(TextEditorLanguageDefinition.c());
        this.textEditor.setText(code);
        this.textEditor.setShowWhitespaces(false);
        this.scriptName = new ImString(name, 50);
        this.selfCreated = false;
    }

    public boolean isModified() {
        return this.textEditor.canUndo() || this.rename;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void close() {
        this.closed = true;
    }

    public void save() {
        try {
            if (this.rename) {
                if (this.scriptName.isEmpty()) {
                    Vandalism.getInstance().getLogger().error("Error script name can't be empty!");
                    return;
                }
                final File oldScriptFile = this.scriptFile;
                this.scriptFile = new File(
                        Vandalism.getInstance().getScriptRegistry().getDirectory(),
                        this.scriptName.get() + ScriptParser.SCRIPT_FILE_EXTENSION
                );
                if (!oldScriptFile.getName().equals(this.scriptFile.getName())) {
                    Vandalism.getInstance().getLogger().error("Error script name can't be the same as before!");
                    return;
                }
                if (oldScriptFile.exists()) {
                    oldScriptFile.delete();
                }
            }
            if (!this.scriptFile.exists()) {
                if (!this.scriptFile.createNewFile()) {
                    Vandalism.getInstance().getLogger().error("Failed to create script file: " + this.scriptFile.getName());
                    return;
                }
            }
            final PrintWriter printerWriter = new PrintWriter(this.scriptFile);
            final String[] lines = this.textEditor.getTextLines();
            for (final String line : lines) printerWriter.println(line);
            printerWriter.close();
            this.textEditor.setText(this.textEditor.getText());
        } catch (final Throwable throwable) {
            Vandalism.getInstance().getLogger().error("Error while saving script file: " + this.scriptFile.getName(), throwable);
        }
    }

    public void render() {
        final StringBuilder titleBuilder = new StringBuilder();
        titleBuilder.append(this.scriptFile.getName());
        if (this.isModified()) {
            titleBuilder.append(" ...");
        }
        if (ImGui.beginTabItem(titleBuilder + "##scriptstab" + this.name + "editor")) {
            final int cPosX = this.textEditor.getCursorPositionLine(), cPosY = this.textEditor.getCursorPositionColumn();
            int x = -200, buttonHeight = 27;
            if (this.isModified()) x -= 99;
            ImGui.setNextItemWidth(x);
            ImGui.inputText(
                    "##scripts" + this.name + "editortitle",
                    new ImString(cPosX + "/" + cPosY + " " + this.textEditor.getTotalLines() + " lines"),
                    ImGuiInputTextFlags.ReadOnly
            );
            if (this.isModified()) {
                ImGui.sameLine();
                x += 99;
                if (ImGui.button("Save##scriptssavein" + this.name + "editor", x, buttonHeight)) {
                    this.save();
                }
            }
            x += 99;
            ImGui.sameLine();
            if (!this.rename) {
                if (ImGui.button("Rename##scriptsrenamein" + this.name + "editor", x, buttonHeight)) {
                    this.rename = true;
                }
                x += 99;
            } else if (!this.selfCreated) {
                x += 26;
                if (ImGui.button("Cancel Renaming##scriptscanelrenamemingin" + this.name + "editor", x, buttonHeight)) {
                    this.rename = false;
                    this.scriptName.set(this.name);
                }
                x += 140;
            }
            ImGui.sameLine();
            if (ImGui.button("Close##scriptsclosein" + this.name + "editor", x, buttonHeight)) {
                this.closed = true;
            }
            if (this.rename) {
                ImGui.inputText(
                        "Enter New Name##scripts" + this.name + "editortitle",
                        this.scriptName,
                        ImGuiInputTextFlags.CallbackCharFilter,
                        FILE_NAME_FILTER
                );
            }
            this.textEditor.render(this.name);
            ImGui.endTabItem();
        }
    }

}