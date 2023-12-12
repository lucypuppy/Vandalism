package de.vandalismdevelopment.vandalism.base.event.render;

import de.florianmichael.dietrichevents2.AbstractEvent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface TooltipDrawListener {

    void onTooltipDraw(final TooltipDrawEvent event);

    class TooltipDrawEvent extends AbstractEvent<TooltipDrawListener> {

        public static final int ID = 25;

        public ItemStack itemStack;

        public final List<TooltipData> tooltipData;

        public TooltipDrawEvent(final ItemStack itemStack, final List<TooltipData> tooltipData) {
            this.itemStack = itemStack;
            this.tooltipData = tooltipData;
        }

        @Override
        public void call(final TooltipDrawListener listener) {
            listener.onTooltipDraw(this);
        }

    }

}
