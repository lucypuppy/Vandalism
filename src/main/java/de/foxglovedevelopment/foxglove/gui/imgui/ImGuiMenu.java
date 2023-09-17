package de.foxglovedevelopment.foxglove.gui.imgui;

import de.foxglovedevelopment.foxglove.util.MinecraftWrapper;

public abstract class ImGuiMenu implements MinecraftWrapper {

    private final String name;

    private boolean state;

    public ImGuiMenu(final String name) {
        this.name = name;
        this.state = false;
    }

    public String getName() {
        return this.name;
    }

    public boolean getState() {
        return this.state;
    }

    public void toggle() {
        this.setState(!this.state);
    }

    private void setState(final boolean state) {
        if (state == this.state) return;
        this.state = state;
        if (state) this.init();
        else this.close();
    }

    protected void init() {
    }

    protected void close() {
    }

    public abstract void render();

    @Override
    public String toString() {
        return '{' + "name=" + this.getName() + ", state=" + this.state + '}';
    }

}
