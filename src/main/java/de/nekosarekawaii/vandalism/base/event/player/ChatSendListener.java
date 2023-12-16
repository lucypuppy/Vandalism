package de.nekosarekawaii.vandalism.base.event.player;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface ChatSendListener {

    void onChatSend(final ChatSendEvent event);

    class ChatSendEvent extends AbstractEvent<ChatSendListener> {

        public static final int ID = 16;

        public String message;

        public ChatSendEvent(final String message) {
            this.message = message;
        }

        @Override
        public void call(final ChatSendListener listener) {
            listener.onChatSend(this);
        }

    }

}
