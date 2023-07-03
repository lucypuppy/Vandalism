package me.nekosarekawaii.foxglove.event;

import de.florianmichael.dietrichevents2.core.AbstractEvent;
import de.florianmichael.dietrichevents2.core.Listener;
import net.minecraft.client.gui.DrawContext;

public interface MultiplayerServerEntriesListener extends Listener {

    void onRenderText(final DrawContext context, final int index, final int y, final int x, final int entryWidth, final int entryHeight, final int mouseX, final int mouseY, final boolean hovered, final float tickDelta);

    class TextRenderEvent extends AbstractEvent<MultiplayerServerEntriesListener> {

        public final static int ID = 7;

        private final DrawContext context;

        private final int index, y, x, entryWidth, entryHeight, mouseX, mouseY;

        private final boolean hovered;

        private final float tickDelta;

        public TextRenderEvent(final DrawContext context, final int index, final int y, final int x, final int entryWidth, final int entryHeight, final int mouseX, final int mouseY, final boolean hovered, final float tickDelta) {
            this.context = context;
            this.index = index;
            this.y = y;
            this.x = x;
            this.entryWidth = entryWidth;
            this.entryHeight = entryHeight;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.hovered = hovered;
            this.tickDelta = tickDelta;
        }

        @Override
        public void call(final MultiplayerServerEntriesListener listener) {
            listener.onRenderText(this.context, this.index, this.y, this.x, this.entryWidth, this.entryHeight, this.mouseX, this.mouseY, this.hovered, this.tickDelta);
        }

    }

}
