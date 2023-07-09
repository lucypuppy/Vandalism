package me.nekosarekawaii.foxglove.event.impl;

import de.florianmichael.dietrichevents2.core.AbstractEvent;
import de.florianmichael.dietrichevents2.core.Listener;

public interface KeyboardListener extends Listener {

    default void onChar(final long window, final int codePoint, final int modifiers) {
    }

    default void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
    }

    enum KeyboardEventType {
        KEY, CHAR
    }

    class KeyboardEvent extends AbstractEvent<KeyboardListener> {

        public final static int ID = 1;

        private final KeyboardEventType type;

        public final long window;
        public final int key, codePoint, scanCode, action, modifiers;

        public KeyboardEvent(final KeyboardEventType type, final long window, final int key, final int codePoint, final int scanCode, final int action, final int modifiers) {
            this.type = type;
            this.window = window;
            this.key = key;
            this.codePoint = codePoint;
            this.scanCode = scanCode;
            this.action = action;
            this.modifiers = modifiers;
        }

        @Override
        public void call(final KeyboardListener listener) {
            if (this.type == KeyboardEventType.KEY) {
                listener.onKey(this.window, this.key, this.scanCode, this.action, this.modifiers);
            } else {
                listener.onChar(this.window, this.codePoint, this.modifiers);
            }
        }

    }

}
