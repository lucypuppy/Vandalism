package de.vandalismdevelopment.vandalism.base.event.game;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface MouseInputListener {

    default void onMouseButton(final int button, final int action, final int mods) {
    }

    default void onMouseScroll(final double horizontal, final double vertical) {
    }

    default void onCursorPos(final double x, final double y) {
    }

    class MouseEvent extends AbstractEvent<MouseInputListener> {

        public static final int ID = 7;

        private final Type type;
        public int button, action, mods;
        public double horizontal, vertical;
        public double x, y;

        public MouseEvent(final int button, final int action, final int mods) {
            this.type = Type.BUTTON;

            this.button = button;
            this.action = action;
            this.mods = mods;
        }

        public MouseEvent(final boolean scroll, final double x, final double y) {
            this.type = scroll ? Type.SCROLL : Type.POS;

            if (scroll) {
                this.horizontal = x;
                this.vertical = y;
            } else {
                this.x = x;
                this.y = y;
            }
        }

        @Override
        public void call(final MouseInputListener listener) {
            switch (this.type) {
                case BUTTON -> listener.onMouseButton(button, action, mods);
                case SCROLL -> listener.onMouseScroll(horizontal, vertical);
                case POS -> listener.onCursorPos(x, y);
            }
        }

    }

    enum Type {
        BUTTON, SCROLL, POS
    }

}
