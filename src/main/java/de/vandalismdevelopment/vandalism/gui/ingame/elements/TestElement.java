package de.vandalismdevelopment.vandalism.gui.ingame.elements;

import de.vandalismdevelopment.vandalism.gui.ingame.Element;
import net.minecraft.client.gui.DrawContext;

public class TestElement extends Element {

    public TestElement() {
        super("Test");
    }

    @Override
    public void render(DrawContext context, float delta) {
        context.drawText(mc().textRenderer, "Test", x, y, 0xFFFFFFFF, true);

        width = mc().textRenderer.getWidth("Test");
        height = mc().textRenderer.fontHeight;
    }

}
