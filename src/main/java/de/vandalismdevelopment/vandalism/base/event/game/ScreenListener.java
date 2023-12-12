package de.vandalismdevelopment.vandalism.base.event.game;

import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.client.gui.screen.Screen;

public interface ScreenListener {

    default void onOpenScreen(final ScreenEvent event) {
    }

    default void onResizeScreen(final ScreenEvent event) {
    }

    class ScreenEvent extends CancellableEvent<ScreenListener> {

        public static final int ID = 8;

        public final Type type;
        public Screen screen;

        public ScreenEvent(final Screen screen) {
            this.type = Type.OPEN;
            this.screen = screen;
        }

        public ScreenEvent() {
            this.type = Type.RESIZE;
        }

        @Override
        public void call(final ScreenListener listener) {
            if (this.type == Type.OPEN) {
                listener.onOpenScreen(this);
            } else {
                listener.onResizeScreen(this);
            }
        }
    }

    enum Type {
        OPEN, RESIZE
    }

}
