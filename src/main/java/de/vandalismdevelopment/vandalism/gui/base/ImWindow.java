package de.vandalismdevelopment.vandalism.gui.base;

import de.florianmichael.rclasses.common.StringUtils;
import de.florianmichael.rclasses.pattern.functional.IName;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import net.minecraft.client.gui.DrawContext;

public class ImWindow implements IName, MinecraftWrapper {

    private final String name;
    private final Category category;

    private boolean active;

    public ImWindow(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    protected void onEnable() {
    }
    protected void onDisable() {
    }

    public void render(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
    }
    public void mouseClicked(final double mouseX, final double mouseY, final int button, final boolean release) {
    }
    public void keyPressed(final int key, final int scanCode, final int modifiers, final boolean release) {
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;

        if (active) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void toggle() {
        setActive(!isActive());
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Category getCategory() {
        return category;
    }

    public enum Category {

        CONFIGURATION,
        SERVER_UTILS,
        MISC_UTILS;

        public String getName() {
            return StringUtils.normalizeEnumName(this.name());
        }
    }
}
