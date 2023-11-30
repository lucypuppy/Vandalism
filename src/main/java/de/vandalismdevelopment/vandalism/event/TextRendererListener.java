package de.vandalismdevelopment.vandalism.event;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface TextRendererListener {

    default void onTextDraw(final TextDrawEvent event) {
    }

    class TextDrawEvent extends AbstractEvent<TextRendererListener> {

        public final static int ID = 16;

        public String text;

        public TextDrawEvent(final String text) {
            this.text = text;
        }

        @Override
        public void call(final TextRendererListener listener) {
            listener.onTextDraw(this);
        }

    }

}
