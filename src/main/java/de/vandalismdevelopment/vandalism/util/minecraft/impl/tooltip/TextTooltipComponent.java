package de.vandalismdevelopment.vandalism.util.minecraft.impl.tooltip;

import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.OrderedText;

public class TextTooltipComponent extends OrderedTextTooltipComponent implements ITooltipData {

    public TextTooltipComponent(final OrderedText text) {
        super(text);
    }

    @Override
    public TooltipComponent getComponent() {
        return this;
    }

    @Override
    public boolean renderPre() {
        return false;
    }

}
