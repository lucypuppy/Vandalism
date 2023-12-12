package de.vandalismdevelopment.vandalism.gui.base;

import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ImGuiScreen extends Screen implements MinecraftWrapper {

    private final Screen prevScreen;

    public ImGuiScreen(final Screen prevScreen) {
        super(Text.literal("ImGUI"));
        this.prevScreen = prevScreen;
    }


}
