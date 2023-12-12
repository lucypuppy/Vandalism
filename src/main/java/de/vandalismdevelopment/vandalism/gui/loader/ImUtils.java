package de.vandalismdevelopment.vandalism.gui.loader;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.FabricBootstrap;
import imgui.ImFont;
import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.apache.commons.compress.utils.IOUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ImUtils {

    public static boolean subButton(final String str) {
        return ImGui.button(str, ImGui.getColumnWidth(), ImGui.getTextLineHeightWithSpacing());
    }

    public static ImFont loadFont(final String fontName, final int size, final ImFontAtlas atlas, final ImFontConfig fontConfig, final short[] glyphRanges) {
        final Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(FabricBootstrap.MOD_ID);
        if (modContainer.isEmpty()) {
            Vandalism.getInstance().getLogger().error("Could not find mod container for mod " + FabricBootstrap.MOD_ID);
            return null;
        }
        final String pathString = "assets/" + FabricBootstrap.MOD_ID + "/font/" + fontName + ".ttf";
        final Optional<Path> path = modContainer.get().findPath(pathString);
        if (path.isEmpty()) {
            Vandalism.getInstance().getLogger().error("Could not find font file: " + pathString);
            return null;
        }
        try {
            fontConfig.setName(fontName + " " + size + "px");
            return atlas.addFontFromMemoryTTF(IOUtils.toByteArray(Files.newInputStream(path.get())), size, fontConfig, glyphRanges);
        } catch (final IOException e) {
            Vandalism.getInstance().getLogger().error("Failed to load font: " + pathString, e);
        }
        return null;
    }

}
