package de.nekosarekawaii.vandalism.integration.hud.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.internal.ModuleToggleListener;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.awt.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.integration.hud.HUDElement;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleListHUDElement extends HUDElement implements ModuleToggleListener {

    private final List<String> activatedModules = new CopyOnWriteArrayList<>();
    private final List<String> externalModules = new CopyOnWriteArrayList<>();

    private boolean sort;

    private final ValueGroup visualElements = new ValueGroup(
            this,
            "Visual Elements",
            "Elements that are shown in the visual category."
    );

    private final ValueGroup textElements = new ValueGroup(
            visualElements,
            "Text Elements",
            "Elements that are shown in the text category."
    );

    private final BooleanValue shadow = new BooleanValue(
            this.textElements,
            "Shadow",
            "Whether or not the text should have a shadow.",
            true
    );

    private final Value<Integer> heightOffset = new IntegerValue(
            this.textElements,
            "Height Offset",
            "The height offset of the text.",
            0,
            0,
            5
    );

    private final BooleanValue background = new BooleanValue(
            this.visualElements,
            "Background",
            "Whether or not to draw a background.",
            false
    );

    private final Value<Integer> widthOffset = new IntegerValue(
            this.visualElements,
            "Width Offset",
            "The width offset of background.",
            0,
            0,
            5
    );

    private final ColorValue color = new ColorValue(
            this.visualElements,
            "Color",
            "The color of the text.",
            Color.WHITE
    );

    public ModuleListHUDElement() {
        super("Module List", 2, 159);
        Vandalism.getInstance().getEventSystem().subscribe(ModuleToggleEvent.ID, this);
    }

    @Override
    public void onModuleToggle(final ModuleToggleEvent event) {
        this.sort = true;
    }

    @Override
    public void reset() {
        super.reset();
        this.sort = true;
    }

    @Override
    public void onRender(final DrawContext context, final float delta) {
        this.sort();
        int yOffset = 0;
        for (final String activatedModule : this.activatedModules) {
            final int textWidth = this.mc.textRenderer.getWidth(activatedModule);
            switch (this.alignmentX) {
                case MIDDLE -> {
                    if (this.background.getValue()) {
                        context.fill(
                                (this.x + this.width / 2) - (textWidth / 2) - this.widthOffset.getValue(),
                                this.y + yOffset,
                                (this.x + this.width / 2) + (textWidth / 2) + this.widthOffset.getValue(),
                                this.y + yOffset + this.mc.textRenderer.fontHeight + this.heightOffset.getValue(),
                                Integer.MIN_VALUE
                        );
                    }

                    drawText(context, activatedModule, (this.x + this.width / 2) - (textWidth / 2), this.y + yOffset + this.heightOffset.getValue());
                }
                case RIGHT -> {
                    if (this.background.getValue()) {
                        context.fill(
                                (this.x + this.width) - textWidth - this.widthOffset.getValue(),
                                this.y + yOffset,
                                (this.x + this.width) + this.widthOffset.getValue(),
                                this.y + yOffset + this.mc.textRenderer.fontHeight + this.heightOffset.getValue(),
                                Integer.MIN_VALUE
                        );
                    }

                    drawText(context, activatedModule, (this.x + this.width) - textWidth, this.y + yOffset + this.heightOffset.getValue());
                }
                default -> {
                    if (this.background.getValue()) {
                        context.fill(
                                this.x - this.widthOffset.getValue(),
                                this.y + yOffset,
                                this.x + textWidth + this.widthOffset.getValue(),
                                this.y + yOffset + this.mc.textRenderer.fontHeight + this.heightOffset.getValue(),
                                Integer.MIN_VALUE
                        );
                    }

                    drawText(context, activatedModule, this.x, this.y + yOffset + this.heightOffset.getValue());
                }
            }

            this.width = Math.max(this.width, textWidth);
            yOffset += this.mc.textRenderer.fontHeight + this.heightOffset.getValue();
        }
        this.height = yOffset;
    }

    @Override
    public void calculateAlignment() {
        super.calculateAlignment();
        this.sort = true;
    }

    private void sort() {
        if (this.sort) {
            this.sort = false;
            this.activatedModules.clear();
            final List<AbstractModule> modules = Vandalism.getInstance().getModuleManager().getList();
            for (final AbstractModule module : modules) {
                if (module.isActive() && module.isShowInHUD()) {
                    this.activatedModules.add(module.getName());
                }
            }
            this.activatedModules.addAll(this.externalModules);
            this.activatedModules.sort((s1, s2) -> {
                final int compare;
                switch (this.alignmentY) {
                    case TOP, MIDDLE ->
                            compare = Integer.compare(this.mc.textRenderer.getWidth(s2), this.mc.textRenderer.getWidth(s1));
                    case BOTTOM ->
                            compare = Integer.compare(this.mc.textRenderer.getWidth(s1), this.mc.textRenderer.getWidth(s2));
                    default -> compare = 0;
                }
                return compare;
            });
        }
    }

    private void drawText(final DrawContext context, final String text, final int x, final int y) {
        context.drawText(
                this.mc.textRenderer,
                text, x, y,
                this.color.getColor(-y * 20).getRGB(),
                this.shadow.getValue()
        );
    }

    public void addExternalModule(final String source, final String name) {
        final String module = source + " " + name;
        if (this.externalModules.contains(module)) {
            return;
        }

        this.externalModules.add(module);
        this.sort = true;
    }

    public void removeExternalModule(final String source, final String name) {
        this.externalModules.remove(source + " " + name);
        this.sort = true;
    }

}
