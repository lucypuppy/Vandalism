package me.nekosarekawaii.foxglove.event;

import de.florianmichael.dietrichevents2.core.Listener;
import de.florianmichael.dietrichevents2.type.CancellableEvent;
import net.minecraft.client.gui.screen.Screen;

public interface ScreenListener extends Listener {

    void onOpenScreen(final OpenScreenEvent event);

    class OpenScreenEvent extends CancellableEvent<ScreenListener> {

        public final static int ID = 5;

        public Screen screen;

        public OpenScreenEvent(final Screen screen) {
            this.screen = screen;
        }

        @Override
        public void call(final ScreenListener listener) {
            listener.onOpenScreen(this);
        }

    }

}
