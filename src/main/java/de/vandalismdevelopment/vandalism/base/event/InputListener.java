package de.vandalismdevelopment.vandalism.base.event;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface InputListener {

    default void onMouseButton(final int button, final int action, final int mods) {
    }

    default void onMouseScroll(final double horizontal, final double vertical) {
    }

    default void onCursorPos(final double x, final double y) {
    }

    enum MouseEventType {
        BUTTON, SCROLL, POS
    }

    class MouseEvent extends AbstractEvent<InputListener> {

        public static final int ID = 20;
        private final MouseEventType type;
        public int button, action, mods;
        public double horizontal, vertical;
        public double x, y;

        public MouseEvent(final int button, final int action, final int mods) {
            this.type = MouseEventType.BUTTON;
            this.button = button;
            this.action = action;
            this.mods = mods;
        }

        public MouseEvent(final boolean scroll, final double x, final double y) {
            if (scroll) {
                this.type = MouseEventType.SCROLL;
                this.horizontal = x;
                this.vertical = y;
                return;
            }

            this.type = MouseEventType.POS;
            this.x = x;
            this.y = y;
        }

        @Override
        public void call(final InputListener listener) {
            switch (this.type) {
                case BUTTON -> listener.onMouseButton(button, action, mods);
                case SCROLL -> listener.onMouseScroll(horizontal, vertical);
                case POS -> listener.onCursorPos(x, y);
            }
        }

    }

    default void onChar(final long window, final int codePoint, final int modifiers) {
    }

    default void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
    }

    enum KeyboardEventType {
        KEY, CHAR
    }

    class KeyboardEvent extends AbstractEvent<InputListener> {

        public static final int ID = 1;

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
        public void call(final InputListener listener) {
            if (this.type == KeyboardEventType.KEY) {
                listener.onKey(this.window, this.key, this.scanCode, this.action, this.modifiers);
            } else {
                listener.onChar(this.window, this.codePoint, this.modifiers);
            }
        }

    }

}
