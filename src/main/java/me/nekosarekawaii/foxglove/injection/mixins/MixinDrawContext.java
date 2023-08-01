package me.nekosarekawaii.foxglove.injection.mixins;

import me.nekosarekawaii.foxglove.util.minecraft.inventory.tooltip.ITooltipData;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.item.TooltipData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mixin(DrawContext.class)
public abstract class MixinDrawContext {

    /**
     * @author FooFieOwO
     * @reason Dumb
     */
    @Overwrite
    public void drawTooltip(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int x, int y) {
        final List<TooltipComponent> list = text.stream().map(Text::asOrderedText).map(TooltipComponent::of).collect(Collectors.toList());

        if (data.isPresent()) {
            if (data.get() instanceof final ITooltipData tooltipData && !tooltipData.renderPre()) {
                list.add(TooltipComponent.of(data.get()));
            } else {
                list.add(1, TooltipComponent.of(data.get()));
            }
        }

        this.drawTooltip(textRenderer, list, x, y, HoveredTooltipPositioner.INSTANCE);
    }

    @Shadow
    private void drawTooltip(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner) {
    }

}
