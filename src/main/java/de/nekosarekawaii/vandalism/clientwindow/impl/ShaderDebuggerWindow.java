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

package de.nekosarekawaii.vandalism.clientwindow.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import de.nekosarekawaii.vandalism.util.DebugHelper;
import de.nekosarekawaii.vandalism.util.ShittyGameNotifications;
import de.nekosarekawaii.vandalism.util.render.Shaders;
import de.nekosarekawaii.vandalism.util.render.effect.PostProcessEffect;
import de.nekosarekawaii.vandalism.util.render.gl.shader.ShaderException;
import de.nekosarekawaii.vandalism.util.render.gl.shader.ShaderProgram;
import imgui.ImGui;
import imgui.extension.texteditor.TextEditor;
import imgui.extension.texteditor.TextEditorLanguageDefinition;
import imgui.flag.*;
import imgui.type.ImString;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL45C;

import java.util.*;
import java.util.regex.Pattern;

public class ShaderDebuggerWindow extends ClientWindow {

    private static final Pattern NVIDIA_COMPILE_ERROR_PATTERN = Pattern.compile("[0-9]+\\(([0-9]+)\\)?\\s*:\\s*(error|warning|info)\\s*([A-Za-z0-9][0-9]{4})\\s*:\\s*(.*)");

    private static final int BRUTEFORCE_COUNT = 1_000_000;
    private final List<Program> programs = new ArrayList<>();
    private final ImString searchText = new ImString(128);
    private long reloadTimer;
    private int selectedShaderIndex = -1;

    public ShaderDebuggerWindow() {
        super("Shader Debugger", Category.MISC, 900.0f, 500.0f, ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoScrollbar);
    }

    @Override
    protected void onRender(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.programs.isEmpty() && System.currentTimeMillis() - this.reloadTimer >= 1000) {
            this.reloadTimer = System.currentTimeMillis();
            this.reloadShaders();
        }

        super.onRender(context, mouseX, mouseY, delta);

        final boolean windowFocused = ImGui.isWindowFocused(ImGuiFocusedFlags.RootAndChildWindows);
        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("Actions")) {
                if (ImGui.menuItem("Reload")) {
                    this.onDisable();
                }
                ImGui.endMenu();
            }
            ImGui.endMenuBar();
        }
        if (ImGui.beginTable("Shaders", 2, ImGuiTableFlags.Resizable | ImGuiTableFlags.SizingStretchProp)) {
            ImGui.tableNextRow();
            ImGui.tableSetColumnIndex(0);
            ImGui.inputText("Search", this.searchText);
            if (ImGui.beginTable("Programs", 2, ImGuiTableFlags.Resizable
                    | ImGuiTableFlags.RowBg | ImGuiTableFlags.SizingStretchProp
                    | ImGuiTableFlags.ScrollY)) {
                ImGui.tableSetupColumn("ID");
                ImGui.tableSetupColumn("Name");
                ImGui.tableHeadersRow();
                for (int i = 0; i < this.programs.size(); i++) {
                    final Program program = this.programs.get(i);
                    final String searchText = this.searchText.get().toLowerCase(Locale.ROOT);
                    if (!this.searchText.isEmpty() && !(program.getName().toLowerCase(Locale.ROOT).contains(searchText) || String.valueOf(program.getId()).contains(searchText))) continue;
                    ImGui.tableNextRow();
                    ImGui.tableSetColumnIndex(0);
                    if (ImGui.selectable(String.valueOf(program.getId()), this.selectedShaderIndex == i, ImGuiSelectableFlags.SpanAllColumns)) {
                        if (this.selectedShaderIndex == i && ImGui.getIO().getKeyCtrl()) this.selectedShaderIndex = -1;
                        else this.selectedShaderIndex = i;
                    }
                    ImGui.tableSetColumnIndex(1);
                    ImGui.textUnformatted(program.getName());
                }
                ImGui.endTable();
            }

            ImGui.tableSetColumnIndex(1);
            if (this.selectedShaderIndex < 0 || this.selectedShaderIndex >= this.programs.size()) {
                ImGui.textUnformatted("No shader program selected!");
            } else {
                final Program program = this.programs.get(this.selectedShaderIndex);
                if (ImGui.beginTabBar("ShaderTypes", ImGuiTabBarFlags.Reorderable)) {
                    for (int i = 0; i < program.shaders.size(); i++) {
                        final Shader shader = program.shaders.get(i);
                        if (ImGui.beginTabItem(shader.typeString(), shader.modified ? ImGuiTabItemFlags.UnsavedDocument : 0)) {
                            shader.getEditor().render(shader.typeString());
                            if (shader.getEditor().isTextChanged()) {
                                shader.modified = true;
                            }
                            if (windowFocused && ImGui.getIO().getKeyCtrl() && GLFW.glfwGetKey(ImGui.getWindowViewport().getPlatformHandle(), GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
                                final long now = System.currentTimeMillis();
                                if (now - shader.lastSave >= 1000L) {
                                    shader.lastSave = now;
                                    shader.save();
                                }
                            }
                            ImGui.endTabItem();
                        }
                    }
                    ImGui.endTabBar();
                }
            }
            ImGui.endTable();
        }
    }

    public void reloadShaders() {
        this.selectedShaderIndex = -1;
        this.onDisable();
        for (int id = 0; id < BRUTEFORCE_COUNT; id++) {
            if (!GL45C.glIsProgram(id)) continue;

            final Program program = new Program(id, this.provideProgramName(id));

            final int[] count = new int[1];
            GL45C.glGetProgramiv(id, GL45C.GL_ATTACHED_SHADERS, count);
            final int[] shaderIds = new int[count[0]];
            GL45C.glGetAttachedShaders(id, count, shaderIds);
            for (int shaderId : shaderIds) {
                final int type = GL45C.glGetShaderi(shaderId, GL45C.GL_SHADER_TYPE);
                final String source = GL45C.glGetShaderSource(shaderId);
                program.shaders.add(new Shader(id, shaderId, type, source));
            }

            this.programs.add(program);
        }
    }

    public String provideProgramName(int program) {
        for (PostProcessEffect effect : Shaders.getPostProcessEffects()) {
            for (int i = 0; i < effect.numPasses(); i++) {
                final ShaderProgram prog = effect.shader(i);
                if (prog.id() == program) {
                    return FabricBootstrap.MOD_ID + "/" + effect.getName() + "/" + i;
                }
            }
        }
        for (net.minecraft.client.gl.ShaderProgram entry : mc.gameRenderer.programs.values()) {
            if (entry.getGlRef() == program) {
                return "minecraft/" + entry.getName();
            }
        }
        final String label = DebugHelper.getObjectLabel(program, GL45C.GL_PROGRAM);
        if (!label.isEmpty()) return label;
        return "unknown/program-" + program;
    }

    @Override
    protected void onDisable() {
        for (Program program : this.programs) {
            for (Shader shader : program.shaders) {
                shader.close();
            }
        }
        this.programs.clear();
    }

    @Getter
    public static class Program {
        private final int id;
        private final String name;
        private final List<Shader> shaders = new ArrayList<>();

        private Program(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public static class Shader implements AutoCloseable {
        public final int programId;
        public final int id;
        public final int type;
        public final String originalSource;
        public String lastSuccessfulSource;
        public TextEditor editor;
        public boolean modified;
        public long lastSave;

        public Shader(int programId, int id, int type, String originalSource) {
            this.programId = programId;
            this.id = id;
            this.type = type;
            this.originalSource = originalSource;
            this.lastSuccessfulSource = originalSource;
        }

        public String typeString() {
            return switch (this.type) {
                case GL45C.GL_VERTEX_SHADER -> "Vertex";
                case GL45C.GL_FRAGMENT_SHADER -> "Fragment";
                case GL45C.GL_GEOMETRY_SHADER -> "Geometry";
                case GL45C.GL_TESS_CONTROL_SHADER -> "Tessellation Control";
                case GL45C.GL_TESS_EVALUATION_SHADER -> "Tessellation Evaluation";
                case GL45C.GL_COMPUTE_SHADER -> "Compute";
                default -> "Unknown";
            };
        }

        public TextEditor getEditor() {
            if (this.editor == null) {
                this.editor = new TextEditor();
                this.editor.setLanguageDefinition(TextEditorLanguageDefinition.glsl());
                this.editor.setTabSize(4);
                this.editor.setShowWhitespaces(false);
                this.editor.setColorizerEnable(true);
                this.editor.setHandleKeyboardInputs(true);
                this.editor.setHandleMouseInputs(true);
                this.editor.setText(this.originalSource);
            }
            return editor;
        }

        public void save() throws ShaderException {
            if (this.editor != null) {
                GL45C.glShaderSource(this.id, this.editor.getText());
                GL45C.glCompileShader(this.id);
                int status = GL45C.glGetShaderi(this.id, GL45C.GL_COMPILE_STATUS);
                if (status != GL45C.GL_TRUE) {
                    GL45C.glShaderSource(this.id, this.lastSuccessfulSource);
                    final String error = GL45C.glGetShaderInfoLog(this.id);
                    Vandalism.getInstance().getLogger().error("Shader compile failed: {}", error);
                    ShittyGameNotifications.multiline("Shader Compile Error", GL45C.glGetShaderInfoLog(this.id));
                    this.setErrorMarkers(GL45C.glGetShaderInfoLog(this.id));
                    return;
                }
                GL45C.glLinkProgram(this.programId);
                status = GL45C.glGetProgrami(this.programId, GL45C.GL_LINK_STATUS);
                if (status != GL45C.GL_TRUE) {
                    GL45C.glShaderSource(this.id, this.lastSuccessfulSource);
                    final String error = GL45C.glGetProgramInfoLog(this.programId);
                    Vandalism.getInstance().getLogger().error("Shader link failed: {}", error);
                    ShittyGameNotifications.multiline("Shader Link Error", error);
                    return;
                }
                GL45C.glValidateProgram(this.programId);
                status = GL45C.glGetProgrami(this.programId, GL45C.GL_VALIDATE_STATUS);
                if (status != GL45C.GL_TRUE) {
                    GL45C.glShaderSource(this.id, this.lastSuccessfulSource);
                    final String error = GL45C.glGetProgramInfoLog(this.programId);
                    Vandalism.getInstance().getLogger().error("Shader validation failed: {}", error);
                    ShittyGameNotifications.multiline("Shader Validation Error", error);
                    return;
                }
                this.editor.setErrorMarkers(Collections.emptyMap());
                this.lastSuccessfulSource = this.editor.getText();
                this.modified = false;
                Shaders.clearUniformCaches();
            }
        }

        private void setErrorMarkers(String error) {
            final Map<Integer, String> errorMap = new HashMap<>();
            final String[] lines = error.replace("\r", "").split("\n");
            for (String line : lines) {
                NVIDIA_COMPILE_ERROR_PATTERN.matcher(line).results().forEach(m -> {
                    final String lineNumber = m.group(1);
                    final String level = m.group(2);
                    final String code = m.group(3);
                    final String message = m.group(4);
                    int lineNum = 0;
                    try {
                        lineNum = Integer.parseInt(lineNumber);
                    } catch (NumberFormatException ignored) {}
                    errorMap.put(lineNum, level + " " + code + ": " + message);
                });
            }
            this.getEditor().setErrorMarkers(errorMap);
        }

        @Override
        public void close() {
            if (this.editor != null) {
                this.editor.destroy();
                this.editor = null;
            }
        }
    }
}
