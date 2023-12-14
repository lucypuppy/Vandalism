package de.nekosarekawaii.vandalism.util.tooltip;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;

public interface ITooltipData extends TooltipData {

    TooltipComponent getComponent();

    boolean renderPre();

}