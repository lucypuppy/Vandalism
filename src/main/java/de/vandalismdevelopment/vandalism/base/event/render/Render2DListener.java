package de.vandalismdevelopment.vandalism.base.event.render;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.client.gui.DrawContext;

public interface Render2DListener {


    default void onRender2DInGame(final DrawContext context, final float delta) {
    }

    default void onRender2DOutGamePre(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
    }

    default void onRender2DOutGamePost(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
    }

    enum Type {
        IN_GAME, OUT_GAME_PRE, OUT_GAME_POST
    }

    class Render2DEvent extends AbstractEvent<Render2DListener> {

        public static final int ID = 24;

        private final Type type;
        private final DrawContext context;
        private final int mouseX, mouseY;
        private final float delta;

        public Render2DEvent(final DrawContext context, final int mouseX, final int mouseY, final float delta, final boolean post) {
            this.type = post ? Type.OUT_GAME_POST : Type.OUT_GAME_PRE;
            this.context = context;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.delta = delta;
        }

        public Render2DEvent(final DrawContext context, final float delta) {
            this.type = Type.IN_GAME;
            this.context = context;
            this.delta = delta;
            this.mouseX = 0;
            this.mouseY = 0;
        }

        @Override
        public void call(final Render2DListener listener) {
            switch (this.type) {
                case IN_GAME -> listener.onRender2DInGame(this.context, this.delta);
                case OUT_GAME_PRE -> listener.onRender2DOutGamePre(this.context, this.mouseX, this.mouseY, this.delta);
                case OUT_GAME_POST -> listener.onRender2DOutGamePost(this.context, this.mouseX, this.mouseY, this.delta);
                default -> {}
            }
        }

    }

}
