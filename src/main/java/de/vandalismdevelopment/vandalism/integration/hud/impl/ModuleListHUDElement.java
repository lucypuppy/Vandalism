package de.vandalismdevelopment.vandalism.integration.hud.impl;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.integration.hud.HUDElement;
import net.minecraft.client.gui.DrawContext;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleListHUDElement extends HUDElement {

    private final List<String> enabledModules;
    private boolean sort;

    public ModuleListHUDElement() {
        super("Module List", 2, 140);
        this.enabledModules = new CopyOnWriteArrayList<>();
    }

    @Override
    public void reset() {
        super.reset();
        this.forceSort();
    }

    @Override
    public void onRender(final DrawContext context, final float delta) {
        this.sort();
        int yOffset = 0;
        final boolean shadow = false;
        for (final String enabledModule : this.enabledModules) {
            final int textWidth = this.mc.textRenderer.getWidth(enabledModule);
            switch (this.alignmentX) {
                case MIDDLE ->
                        context.drawText(this.mc.textRenderer, enabledModule, (this.x + this.width / 2) - (textWidth / 2), this.y + yOffset, -1, shadow);
                case RIGHT ->
                        context.drawText(this.mc.textRenderer, enabledModule, (this.x + this.width) - textWidth, this.y + yOffset, -1, shadow);
                default -> context.drawText(this.mc.textRenderer, enabledModule, this.x, this.y + yOffset, -1, shadow);
            }

            this.width = Math.max(this.width, textWidth);
            yOffset += this.mc.textRenderer.fontHeight;
        }
        this.height = yOffset;
    }

    @Override
    public void calculateAlignment() {
        super.calculateAlignment();
        this.forceSort();
    }

    private void sort() {
        if (this.sort) {
            this.sort = false;
            this.enabledModules.clear();
            final List<AbstractModule> modules = Vandalism.getInstance().getModuleManager().getList();
            for (final AbstractModule module : modules) {
                if (module.isActive() && module.isShowInHUD()) {
                    this.enabledModules.add(module.getName());
                }
            }
            this.enabledModules.sort((s1, s2) -> {
                final int compare;
                switch (this.alignmentY) {
                    case TOP ->
                            compare = Integer.compare(this.mc.textRenderer.getWidth(s2), this.mc.textRenderer.getWidth(s1));
                    case BOTTOM ->
                            compare = Integer.compare(this.mc.textRenderer.getWidth(s1), this.mc.textRenderer.getWidth(s2));
                    default -> compare = 0;
                }
                return compare;
            });
        }
    }

    public void forceSort() {
        this.sort = true;
    }

}
