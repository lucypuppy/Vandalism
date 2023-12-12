package de.vandalismdevelopment.vandalism.base.event.render;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface TextDrawListener {

    void onTextDraw(final TextDrawEvent event);

    class TextDrawEvent extends AbstractEvent<TextDrawListener> {

        public static final int ID = 25;

        public String text;

        public TextDrawEvent(final String text) {
            this.text = text;
        }

        @Override
        public void call(final TextDrawListener listener) {
            listener.onTextDraw(this);
        }

    }

}
