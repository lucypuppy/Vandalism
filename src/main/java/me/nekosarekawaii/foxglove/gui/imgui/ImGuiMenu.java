package me.nekosarekawaii.foxglove.gui.imgui;

import imgui.ImGuiIO;
import me.nekosarekawaii.foxglove.wrapper.MinecraftWrapper;


public abstract class ImGuiMenu implements MinecraftWrapper {

    protected final ImGuiMenu parent;


    public ImGuiMenu(final ImGuiMenu parent) {
        this.parent = parent;
    }


    public ImGuiMenu() {
        this(null);
    }


    public abstract void init();

    public abstract void render(final ImGuiIO imGuiIO);

    public abstract void tick();

    public boolean keyPress(final int keyCode, final int scanCode, final int modifiers) {
        return true;
    }

    public abstract void close();

}
