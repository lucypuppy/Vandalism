package de.vandalismdevelopment.vandalism.event;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface MouseListener {

    default void onMouseButton(final int button, final int action, final int mods) {
    }

    default void onMouseScroll(final double horizontal, final double vertical) {
    }

    default void onCursorPos(final double x, final double y) {
    }

    enum MouseEventType {
        BUTTON, SCROLL, POS
    }

    class MouseEvent extends AbstractEvent<MouseListener> {

        public final static int ID = 16;
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
        public void call(final MouseListener listener) {
            switch (this.type) {
                case BUTTON -> listener.onMouseButton(button, action, mods);
                case SCROLL -> listener.onMouseScroll(horizontal, vertical);
                case POS -> listener.onCursorPos(x, y);
            }
        }
    }

}
