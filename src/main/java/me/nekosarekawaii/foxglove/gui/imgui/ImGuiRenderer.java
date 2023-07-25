/*
 * This file is part of fabric-imgui-example-mod - https://github.com/FlorianMichael/fabric-imgui-example-mod
 * by FlorianMichael/EnZaXD and contributors
 */
package me.nekosarekawaii.foxglove.gui.imgui;

import imgui.*;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.compress.utils.IOUtils;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

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

        loadFonts(imGuiIO);

        this.imGuiImplGlfw.init(MinecraftClient.getInstance().getWindow().getHandle(), true);
        this.imGuiImplGl3.init();
    }

    private void loadFonts(final ImGuiIO imGuiIO) {
        final ImFontAtlas atlas = imGuiIO.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig();
        fontConfig.setPixelSnapH(true);

        final ImFontGlyphRangesBuilder rangesBuilder = new ImFontGlyphRangesBuilder();
        rangesBuilder.addRanges(imGuiIO.getFonts().getGlyphRangesDefault());
        rangesBuilder.addRanges(imGuiIO.getFonts().getGlyphRangesCyrillic());
        rangesBuilder.addRanges(imGuiIO.getFonts().getGlyphRangesJapanese());
        final short[] glyphRanges = rangesBuilder.buildRanges();

        final ImFont robotoRegular16 = loadFont("roboto-regular", 16, atlas, fontConfig, glyphRanges);
        if (robotoRegular16 != null) imGuiIO.setFontDefault(robotoRegular16);

        atlas.build();
        fontConfig.destroy();
    }

    private ImFont loadFont(final String fontName, final int size, final ImFontAtlas atlas, final ImFontConfig fontConfig, final short[] glyphRanges) {
        final Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(Foxglove.getInstance().getLowerCaseName());

        if (modContainer.isEmpty()) {
            Foxglove.getInstance().getLogger().error("Could not find mod container for mod " + Foxglove.getInstance().getLowerCaseName());
            return null;
        }

        final String pathString = "assets/" + Foxglove.getInstance().getLowerCaseName() + "/font/" + fontName + ".ttf";
        final Optional<Path> path = modContainer.get().findPath(pathString);

        if (path.isEmpty()) {
            Foxglove.getInstance().getLogger().error("Could not find font file " + pathString);
            return null;
        }

        try {
            fontConfig.setName(fontName + " " + size + "px");
            return atlas.addFontFromMemoryTTF(IOUtils.toByteArray(Files.newInputStream(path.get())), size, fontConfig, glyphRanges);
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return null;
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
