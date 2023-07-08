/*
 * This file is part of fabric-imgui-example-mod - https://github.com/FlorianMichael/fabric-imgui-example-mod
 * by FlorianMichael/EnZaXD and contributors
 */
package me.nekosarekawaii.foxglove.gui.imgui;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class ImGuiRenderer {

    private final ImGuiImplGl3 imGuiImplGl3;
    private final ImGuiImplGlfw imGuiImplGlfw;
    private final ObjectArrayList<RenderInterface> renderInterfaces = new ObjectArrayList<>();

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

        this.imGuiImplGlfw.init(MinecraftClient.getInstance().getWindow().getHandle(), true);
        this.imGuiImplGl3.init();
    }

    public void render() {
        if (this.renderInterfaces.isEmpty())
            return;

        //Setup rendering
        this.imGuiImplGlfw.newFrame(); // Handle keyboard and mouse interactions
        ImGui.newFrame();

        //Render
        final ImGuiIO imGuiIO = ImGui.getIO();
        for (final RenderInterface renderInterface : this.renderInterfaces) {
            renderInterface.render(imGuiIO);
        }

        //Clear render stuff and end frame
        this.renderInterfaces.clear();
        ImGui.endFrame();

        //Render
        ImGui.render();
        this.imGuiImplGl3.renderDrawData(ImGui.getDrawData());

        //Viewport
        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long pointer = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();

            GLFW.glfwMakeContextCurrent(pointer);
        }
    }

    public void addRenderInterface(final RenderInterface renderInterface) {
        this.renderInterfaces.add(renderInterface);
    }

}
