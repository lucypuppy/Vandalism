/*
 * This file is part of fabric-imgui-example-mod - https://github.com/FlorianMichael/fabric-imgui-example-mod
 * by FlorianMichael/EnZaXD and contributors
 */
package me.nekosarekawaii.foxglove.imgui;

import imgui.*;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.compress.utils.IOUtils;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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

        try {
            final ImFontAtlas fonts = imGuiIO.getFonts();
            final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder();
            rangesBuilder.addRanges(imGuiIO.getFonts().getGlyphRangesDefault());
            rangesBuilder.addRanges(imGuiIO.getFonts().getGlyphRangesCyrillic());
            rangesBuilder.addRanges(imGuiIO.getFonts().getGlyphRangesJapanese());
            final short[] glyphRanges = rangesBuilder.buildRanges();
            final ImFontConfig basicConfig = new ImFontConfig();
            basicConfig.setGlyphRanges(imGuiIO.getFonts().getGlyphRangesCyrillic());
            final String fontName = "Roboto-Regular";
            final int size = 30;
            final InputStream fontStream = ImGuiRenderer.class.getResourceAsStream("/assets/" + Foxglove.getInstance().getLowerCaseName() + "/font/" + fontName + ".ttf");
            if (fontStream != null) {
                basicConfig.setName(fontName + " " + size + "px");
                final ImFont font = fonts.addFontFromMemoryTTF(IOUtils.toByteArray(fontStream), size, basicConfig, glyphRanges);
                imGuiIO.setFontDefault(font);
            }
            fonts.build();
            basicConfig.destroy();
        } catch (final IOException ioException) {
            Foxglove.getInstance().getLogger().error("Failed to load ImGui font: " + ioException);
        }

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
