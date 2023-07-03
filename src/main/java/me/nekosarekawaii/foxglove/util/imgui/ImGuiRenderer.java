/*
 * This file is part of fabric-imgui-example-mod - https://github.com/FlorianMichael/fabric-imgui-example-mod
 * by FlorianMichael/EnZaXD and contributors
 */
package me.nekosarekawaii.foxglove.util.imgui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import me.nekosarekawaii.foxglove.wrapper.MinecraftWrapper;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class ImGuiRenderer implements MinecraftWrapper {

    private final ImGuiImplGl3 imGuiImplGl3;
    private final ImGuiImplGlfw imGuiImplGlfw;

    public ImGuiRenderer(final File dir) {
        this.imGuiImplGl3 = new ImGuiImplGl3();
        this.imGuiImplGlfw = new ImGuiImplGlfw();

        // Create context
        ImGui.createContext();
        ImPlot.createContext();

        //Default settings
        final ImGuiIO imGuiIO = ImGui.getIO();
        imGuiIO.setConfigFlags(ImGuiConfigFlags.DockingEnable);
        imGuiIO.setFontGlobalScale(1f);
        imGuiIO.setIniFilename(dir.getName() + "/imgui.ini");

        this.imGuiImplGlfw.init(mc().getWindow().getHandle(), true);
        this.imGuiImplGl3.init();
    }

    public void render(final RenderInterface renderInterface) {
        this.imGuiImplGlfw.newFrame(); // Handle keyboard and mouse interactions
        ImGui.newFrame();

        renderInterface.render(ImGui.getIO());

        ImGui.endFrame();
        ImGui.render();

        this.imGuiImplGl3.renderDrawData(ImGui.getDrawData());
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long pointer = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();

            GLFW.glfwMakeContextCurrent(pointer);
        }
    }

}
