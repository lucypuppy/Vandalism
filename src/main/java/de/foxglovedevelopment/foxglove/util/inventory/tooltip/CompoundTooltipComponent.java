package de.foxglovedevelopment.foxglove.util.inventory.tooltip;

import com.google.common.collect.Lists;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.VertexConsumerProvider;
import org.joml.Matrix4f;

import java.util.List;

public class CompoundTooltipComponent implements TooltipComponent, ITooltipData {

	private final List<TooltipComponent> components = Lists.newArrayList();

	public void addComponent(final TooltipComponent component) {
		components.add(component);
	}

	@Override
	public TooltipComponent getComponent() {
		return this;
	}

	@Override
	public boolean renderPre() {
		return false;
	}

	@Override
	public int getHeight() {
		int height = 0;

        for (final TooltipComponent comp : this.components) {
			height += comp.getHeight();
		}

		return height;
	}

	@Override
	public int getWidth(final TextRenderer textRenderer) {
		int width = 0;

        for (final TooltipComponent comp : this.components) {
			if (comp.getWidth(textRenderer) > width) {
				width = comp.getWidth(textRenderer);
			}
		}

		return width;
	}

	@Override
	public void drawItems(final TextRenderer textRenderer, final int x, final int y, final DrawContext context) {
		int yOff = 0;

        for (final TooltipComponent comp : this.components) {
			comp.drawItems(textRenderer, x, y + yOff, context);
			yOff += comp.getHeight();
		}
	}

	@Override
	public void drawText(final TextRenderer textRenderer, final int x, final int y, final Matrix4f matrix4f, final VertexConsumerProvider.Immediate immediate) {
		int yOff = 0;

        for (final TooltipComponent comp : this.components) {
			comp.drawText(textRenderer, x, y + yOff, matrix4f, immediate);
			yOff += comp.getHeight();
		}
	}

}