package de.vandalismdevelopment.vandalism.gui.imgui.impl.menu;

import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
import net.minecraft.client.gui.DrawContext;

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

    public abstract void render(final DrawContext context, final int mouseX, final int mouseY, final float delta);

    public void mouseClick(final double mouseX, final double mouseY, final int button, final boolean release) {
    }

    public void keyPress(final int key, final int scanCode, final int modifiers, final boolean release) {
    }

    @Override
    public String toString() {
        return '{' + "name=" + this.getName() + ", state=" + this.state + '}';
    }

}
