package de.vandalismdevelopment.vandalism.base.event.game;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface KeyboardInputListener {

    void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers);

    default void onChar(final long window, final int codePoint, final int modifiers) {
    }

    class KeyboardInputEvent extends AbstractEvent<KeyboardInputListener> {

        public static final int ID = 5;

        private final Type type;

        public final long window;
        public final int key, codePoint, scanCode, action, modifiers;

        public KeyboardInputEvent(final Type type, final long window, final int key, final int codePoint, final int scanCode, final int action, final int modifiers) {
            this.type = type;
            this.window = window;
            this.key = key;
            this.codePoint = codePoint;
            this.scanCode = scanCode;
            this.action = action;
            this.modifiers = modifiers;
        }

        @Override
        public void call(final KeyboardInputListener listener) {
            if (this.type == Type.KEY) {
                listener.onKey(this.window, this.key, this.scanCode, this.action, this.modifiers);
            } else {
                listener.onChar(this.window, this.codePoint, this.modifiers);
            }
        }
    }

    enum Type {
        KEY, CHAR
    }

}
