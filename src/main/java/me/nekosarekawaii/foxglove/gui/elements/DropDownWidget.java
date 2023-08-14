package me.nekosarekawaii.foxglove.gui.elements;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class DropDownWidget extends PressableWidget {

    protected final static DropDownWidget.NarrationSupplier DEFAULT_NARRATION_SUPPLIER = Supplier::get;
    protected final DropDownWidget.PressAction onPress;
    protected final DropDownWidget.NarrationSupplier narrationSupplier;
    protected boolean expanded = false;

    public DropDownWidget(int x, int y, int width, int height, Text message, DropDownWidget.NarrationSupplier narrationSupplier, Screen screen, ClickableWidget... children) {
        super(x, y, width, height, message);

        int childHeight = y + height;
        for (final ClickableWidget child : children) {
            child.setPosition(x + 2, childHeight);
            child.setWidth(width - 4);
            child.visible = expanded;

            screen.addDrawableChild(child);
            childHeight += child.getHeight();
        }

        this.onPress = widget -> {
            this.expanded = !this.expanded;

            for (final ClickableWidget child : children) {
                child.visible = this.expanded;
            }
        };

        this.narrationSupplier = narrationSupplier;
    }

    public DropDownWidget(int x, int y, int width, int height, Text message, Screen screen, ClickableWidget... children) {
        this(x, y, width, height, message, DEFAULT_NARRATION_SUPPLIER, screen, children);
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    @Override
    protected MutableText getNarrationMessage() {
        return this.narrationSupplier.createNarrationMessage(super::getNarrationMessage);
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }

    @Environment(EnvType.CLIENT)
    public interface NarrationSupplier {
        MutableText createNarrationMessage(Supplier<MutableText> textSupplier);
    }

    @Environment(EnvType.CLIENT)
    public interface PressAction {
        void onPress(DropDownWidget widget);
    }
}