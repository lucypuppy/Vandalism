package de.foxglovedevelopment.foxglove.event;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

public interface RenderListener {

    default void onRender2DInGame(final DrawContext context, final float delta, final Window window) {
    }

    default void onRender2D(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
    }

    enum Render2DEventType {
        IN_GAME, OUT_GAME
    }

    class Render2DEvent extends AbstractEvent<RenderListener> {

        public final static int ID = 3;

        private final Render2DEventType type;
        private final DrawContext context;
        private final int mouseX, mouseY;
        private final float delta;
        private final Window window;

        public Render2DEvent(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
            this.type = Render2DEventType.OUT_GAME;
            this.context = context;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.delta = delta;
            this.window = null;
        }

        public Render2DEvent(final DrawContext context, final float delta, final Window window) {
            this.type = Render2DEventType.IN_GAME;
            this.context = context;
            this.delta = delta;
            this.window = window;
            this.mouseX = 0;
            this.mouseY = 0;
        }

        @Override
        public void call(final RenderListener listener) {
            if (this.type == Render2DEventType.IN_GAME) {
                listener.onRender2DInGame(this.context, this.delta, this.window);
            } else {
                listener.onRender2D(this.context, this.mouseX, this.mouseY, this.delta);
            }
        }

    }

}
