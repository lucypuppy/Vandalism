package de.vandalismdevelopment.vandalism.event;

import de.florianmichael.dietrichevents2.CancellableEvent;
import net.minecraft.client.gui.screen.Screen;

public interface ScreenListener {

    default void onOpenScreen(final ScreenEvent event) {
    }

    default void onResizeScreen(final ScreenEvent event) {
    }

    enum EventType {
        OPEN, RESIZE
    }

    class ScreenEvent extends CancellableEvent<ScreenListener> {

        public final static int ID = 5;

        public final EventType type;
        public Screen screen;

        public ScreenEvent(final Screen screen) {
            this.type = EventType.OPEN;
            this.screen = screen;
        }

        public ScreenEvent() {
            this.type = EventType.RESIZE;
        }

        @Override
        public void call(final ScreenListener listener) {
            if (this.type == EventType.OPEN) {
                listener.onOpenScreen(this);
            } else {
                listener.onResizeScreen(this);
            }
        }

    }

}
