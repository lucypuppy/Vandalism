package de.nekosarekawaii.vandalism.base.event.player;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.text.MutableText;

public interface ChatModifyReceiveListener {

    void onChatModifyReceive(final ChatModifyReceiveEvent event);

    class ChatModifyReceiveEvent extends AbstractEvent<ChatModifyReceiveListener> {

        public static final int ID = 32;

        public final MutableText mutableText;

        public ChatModifyReceiveEvent(final MutableText mutableText) {
            this.mutableText = mutableText;
        }

        @Override
        public void call(final ChatModifyReceiveListener listener) {
            listener.onChatModifyReceive(this);
        }

    }

}
