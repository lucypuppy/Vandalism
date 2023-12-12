package de.vandalismdevelopment.vandalism.base.event.player;

import de.florianmichael.dietrichevents2.AbstractEvent;

public interface ChatListener {

    void onChatSend(final ChatSendEvent event);

    class ChatSendEvent extends AbstractEvent<ChatListener> {

        public static final int ID = 16;

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
