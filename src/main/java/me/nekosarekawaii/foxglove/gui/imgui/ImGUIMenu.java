package me.nekosarekawaii.foxglove.gui.imgui;

import imgui.ImGuiIO;
import me.nekosarekawaii.foxglove.wrapper.MinecraftWrapper;

/**
 * The ImGUIMenu class represents an abstract base class for ImGui menus in the Foxglove mod.
 * It implements the MinecraftWrapper interface and provides methods for initialization, rendering,
 * ticking, handling key presses, and closing the menu.
 */
public abstract class ImGUIMenu implements MinecraftWrapper {

    protected final ImGUIMenu parent;

    /**
     * Constructs a new ImGUIMenu instance with a parent menu.
     *
     * @param parent The parent menu.
     */
    public ImGUIMenu(final ImGUIMenu parent) {
        this.parent = parent;
    }

    /**
     * Constructs a new ImGUIMenu instance with no parent menu.
     */
    public ImGUIMenu() {
        this(null);
    }

    /**
     * Initializes the menu.
     */
    public void init() {
        // Implementation specific to each menu
    }

    /**
     * Renders the menu using ImGui.
     *
     * @param imGuiIO The ImGuiIO instance.
     */
    public abstract void render(final ImGuiIO imGuiIO);

    /**
     * Executes logic on each tick of the menu.
     */
    public void tick() {
        // Implementation specific to each menu
    }

    /**
     * Handles key presses in the menu.
     *
     * @param keyCode   The key code of the pressed key.
     * @param scanCode  The scan code of the pressed key.
     * @param modifiers The modifiers applied to the key press.
     * @return True if the key press was handled, false otherwise.
     */
    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        return true; // By default, assumes the key press was handled
    }

    /**
     * Closes the menu.
     */
    public void close() {
        // Implementation specific to each menu
    }

}
