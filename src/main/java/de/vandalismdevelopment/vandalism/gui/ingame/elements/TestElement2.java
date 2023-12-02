package de.vandalismdevelopment.vandalism.gui.ingame.elements;

import de.vandalismdevelopment.vandalism.gui.ingame.Element;
import net.minecraft.client.gui.DrawContext;

public class TestElement2 extends Element {

    public TestElement2() {
        super("TestLongLong");
    }

    @Override
    public void render(DrawContext context, float delta) {
        context.drawText(mc().textRenderer, "TestLongLong", x, y, 0xFFFFFFFF, true);

        width = mc().textRenderer.getWidth("TestLongLong");
        height = mc().textRenderer.fontHeight;
    }

}
