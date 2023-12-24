package de.nekosarekawaii.vandalism.util.tooltip.impl;

import de.nekosarekawaii.vandalism.util.tooltip.ConvertibleTooltipData;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.OrderedText;

public class TextTooltipComponent extends OrderedTextTooltipComponent implements ConvertibleTooltipData {

    public TextTooltipComponent(final OrderedText text) {
        super(text);
    }

    @Override
    public TooltipComponent getComponent() {
        return this;
    }

}
