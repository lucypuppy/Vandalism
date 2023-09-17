package de.foxglovedevelopment.foxglove.event;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface ChatListener {

    void onChatSend(final ChatSendEvent event);

    class ChatSendEvent extends AbstractEvent<ChatListener> {

        public final static int ID = 14;

        public String message;

        public ChatSendEvent(final String message) {
            this.message = message;
        }

        @Override
        public void call(final ChatListener listener) {
            listener.onChatSend(this);
        }

    }

}
