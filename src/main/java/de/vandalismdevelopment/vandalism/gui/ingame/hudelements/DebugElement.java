package de.vandalismdevelopment.vandalism.gui.ingame.hudelements;

import de.vandalismdevelopment.vandalism.gui.ingame.HUDElement;
import net.minecraft.client.gui.DrawContext;

public class DebugElement extends HUDElement {

    public DebugElement() {
        super(
                "Debug",
                0,
                0
        );
    }

    @Override
    public void onRender(final DrawContext context, final float delta) {
        final String text = alignmentX + " " + alignmentY;

        context.drawText(
                mc().textRenderer,
                text,
                x,
                y,
                0xFFFFFFFF,
                true
        );

        this.width = mc().textRenderer.getWidth(text);
        this.height = mc().textRenderer.fontHeight;
    }

}