package de.vandalismdevelopment.vandalism.event;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface TooltipListener {

    void onTooltipData(final TooltipEvent event);

    class TooltipEvent extends AbstractEvent<TooltipListener> {

        public final static int ID = 12;

        public ItemStack itemStack;

        public final List<TooltipData> tooltipData;

        public TooltipEvent(final ItemStack itemStack, final List<TooltipData> tooltipData) {
            this.itemStack = itemStack;
            this.tooltipData = tooltipData;
        }

        @Override
        public void call(final TooltipListener listener) {
            listener.onTooltipData(this);
        }

    }

}
