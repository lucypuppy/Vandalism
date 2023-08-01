package me.nekosarekawaii.foxglove.event;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface ToolTipListener {

    void onTooltipData(final ToolTipEvent event);

    class ToolTipEvent extends AbstractEvent<ToolTipListener> {

        public final static int ID = 12;
        public ItemStack itemStack;
        public final List<TooltipData> tooltipData;

        public ToolTipEvent(final ItemStack itemStack, final List<TooltipData> tooltipData) {
            this.itemStack = itemStack;
            this.tooltipData = tooltipData;
        }

        @Override
        public void call(final ToolTipListener listener) {
            listener.onTooltipData(this);
        }
    }

}
