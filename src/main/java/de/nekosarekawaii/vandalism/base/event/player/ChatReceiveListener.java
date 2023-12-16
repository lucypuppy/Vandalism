package de.nekosarekawaii.vandalism.base.event.player;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;

public interface ChatReceiveListener {

    void onChatReceive(final ChatReceiveEvent event);

    class ChatReceiveEvent extends AbstractEvent<ChatReceiveListener> {

        public static final int ID = 31;

        public final Text text;
        public final MessageSignatureData signature;
        public final MessageIndicator indicator;

        public ChatReceiveEvent(final Text text, final MessageSignatureData signature, final MessageIndicator indicator) {
            this.text = text;
            this.signature = signature;
            this.indicator = indicator;
        }

        @Override
        public void call(final ChatReceiveListener listener) {
            listener.onChatReceive(this);
        }

    }

}
