package de.vandalismdevelopment.vandalism.gui.ingame.elements;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.FeatureList;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.gui.ingame.Element;
import de.vandalismdevelopment.vandalism.gui.ingame.ElementAlignment;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class ModuleListElement extends Element {

    private final List<String> enabledModules;

    private boolean sort, fixPosition;

    public ModuleListElement() {
        super("Module List");
        this.enabledModules = new ArrayList<>();
    }

    @Override
    public void render(DrawContext context, float delta) {
        sort();

        int yShift = 0;
        for (final String enabledModule : this.enabledModules) {
            final int textWidth = this.textRenderer().getWidth(enabledModule);
            switch (this.alignmentX) {
                case MIDDLE -> {
                    context.drawText(this.textRenderer(), enabledModule, (x + width / 2) - (textWidth / 2), y + yShift, -1, true);
                }
                case RIGHT -> {
                    context.drawText(this.textRenderer(), enabledModule, (x + width) - textWidth, y + yShift, -1, true);
                }
                default -> {
                    context.drawText(this.textRenderer(), enabledModule, x, y + yShift, -1, true);
                }
            }

            width = Math.max(width, textWidth);
            yShift += this.textRenderer().fontHeight;
        }

        height = yShift;

        if (this.fixPosition) {
            this.fixPosition = false;
            this.calculatePosition();
        }
    }

    @Override
    public void calculateAlignment() {
        super.calculateAlignment();
        this.sort = true;
    }

    private void sort() {
        if (this.sort) {
            this.sort = false;
            this.enabledModules.clear();

            final FeatureList<Module> modules = Vandalism.getInstance().getModuleRegistry().getModules();

            for (final Module module : modules) {
                if (module.isEnabled() && module.isShowInModuleList()) {
                    this.enabledModules.add(module.getName());
                }
            }

            this.enabledModules.sort((s1, s2) -> {
                final int compare;

                switch (this.alignmentY) {
                    case TOP ->
                            compare = Integer.compare(this.textRenderer().getWidth(s2), this.textRenderer().getWidth(s1));
                    case BOTTOM ->
                            compare = Integer.compare(this.textRenderer().getWidth(s1), this.textRenderer().getWidth(s2));
                    default -> compare = 0;
                }

                return compare;
            });
        }
    }

    public void onModuleToggle() {
        this.sort = true;

        if (this.alignmentY == ElementAlignment.BOTTOM) { //This fixes the position bug when toggling a module
            this.fixPosition = true;
        }
    }

}
