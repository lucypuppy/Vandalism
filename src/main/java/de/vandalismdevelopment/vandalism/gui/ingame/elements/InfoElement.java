package de.vandalismdevelopment.vandalism.gui.ingame.elements;

import de.vandalismdevelopment.vandalism.gui.ingame.Element;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.WorldUtil;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.ValueCategory;
import de.vandalismdevelopment.vandalism.value.impl.BooleanValue;
import de.vandalismdevelopment.vandalism.value.impl.number.slider.SliderIntegerValue;
import net.minecraft.client.gui.DrawContext;

public class InfoElement extends Element {

    private final Value<Boolean> fps = new BooleanValue(
            "FPS",
            "Shows the current fps.",
            this,
            true
    );
    private final Value<Boolean> username = new BooleanValue(
            "Username",
            "Shows the current username.",
            this,
            true
    );
    private final Value<Boolean> position = new BooleanValue(
            "Position",
            "Shows the current position.",
            this,
            true
    );
    private final Value<Boolean> dimensionalPosition = new BooleanValue(
            "Dimensional Position",
            "Shows the current position of the dimension you are currently playing in.",
            this,
            true
    );
    private final ValueCategory positionElements = new ValueCategory(
            "Position Elements",
            "Elements that are shown in the position category.",
            this
    ).visibleConsumer(this.position::getValue);
    private final Value<Integer> positionDecimalPlaces = new SliderIntegerValue(
            "Position Decimal Places",
            "Allows you to change the viewable amount of decimal places from the x/y/z position.",
            this.positionElements,
            2,
            1,
            15
    ).visibleConsumer(this.position::getValue);
    private final Value<Boolean> serverBrand = new BooleanValue(
            "Server Brand",
            "Shows the current server brand.",
            this,
            true
    );
    private final Value<Boolean> difficulty = new BooleanValue(
            "Difficulty",
            "Shows the current world difficulty.",
            this,
            true
    );
    private final Value<Boolean> permissionsLevel = new BooleanValue(
            "Permissions Level",
            "Shows the current permissions level.",
            this,
            true
    );

    public InfoElement() {
        super("Info");
    }

    @Override
    public void render(final DrawContext context, final float delta) {
        int color = -1;
        boolean shadow = false;
        int x = this.x, width = 0, height = 0;
        final int fontHeight = this.textRenderer().fontHeight;
        if (this.fps.getValue()) {
            final String text = "FPS: " + this.mc().getCurrentFps();
            context.drawText(this.textRenderer(), text, x, this.y + height, color, shadow);
            height += fontHeight;
            final int textWidth = this.textRenderer().getWidth(text);
            if (textWidth > width) {
                width = textWidth;
            }
        }
        if (this.username.getValue()) {
            final String text = "Username: " + this.player().getGameProfile().getName();
            context.drawText(this.textRenderer(), text, x, this.y + height, color, shadow);
            height += fontHeight;
            final int textWidth = this.textRenderer().getWidth(text);
            if (textWidth > width) {
                width = textWidth;
            }
        }
        if (this.position.getValue()) {
            final int positionDecimalPlacesRawValue = this.positionDecimalPlaces.getValue();
            if (positionDecimalPlacesRawValue < 1) this.positionDecimalPlaces.setValue(1);
            else if (positionDecimalPlacesRawValue > 15) this.positionDecimalPlaces.setValue(15);
            final String positionDecimalPlaces = "%." + this.positionDecimalPlaces.getValue() + "f";
            final String text = "Position: " + String.format(
                    positionDecimalPlaces + ", " + positionDecimalPlaces + ", " + positionDecimalPlaces,
                    this.player().getX(),
                    this.player().getY(),
                    this.player().getZ()
            );
            context.drawText(this.textRenderer(), text, x, this.y + height, color, shadow);
            height += fontHeight;
            final int textWidth = this.textRenderer().getWidth(text);
            if (textWidth > width) {
                width = textWidth;
            }
        }
        if (this.dimensionalPosition.getValue()) {
            final WorldUtil.Dimension dimension = WorldUtil.getDimension();
            if (dimension != WorldUtil.Dimension.END) {
                final int positionDecimalPlacesRawValue = this.positionDecimalPlaces.getValue();
                if (positionDecimalPlacesRawValue < 1) this.positionDecimalPlaces.setValue(1);
                else if (positionDecimalPlacesRawValue > 15) this.positionDecimalPlaces.setValue(15);
                final int decimalPlaces = this.positionDecimalPlaces.getValue();
                final String positionFormat = "%." + decimalPlaces + "f";
                String text = "";
                double posX = this.player().getX(), posY = this.player().getY(), posZ = this.player().getZ();
                switch (dimension) {
                    case NETHER -> {
                        text = "Overworld Position: ";
                        posX *= 8;
                        posZ *= 8;
                    }
                    case OVERWORLD -> {
                        text = "Nether Position: ";
                        posX /= 8;
                        posZ /= 8;
                    }
                    default -> {
                    }
                }
                if (!text.isBlank()) {
                    context.drawText(
                            this.textRenderer(),
                            text + String.format(
                                    positionFormat + ", " + positionFormat + ", " + positionFormat,
                                    posX,
                                    posY,
                                    posZ
                            ),
                            x,
                            this.y + height,
                            color,
                            shadow
                    );
                    height += fontHeight;
                    final int textWidth = this.textRenderer().getWidth(text);
                    if (textWidth > width) {
                        width = textWidth;
                    }
                }
            }
        }
        if (this.serverBrand.getValue()) {
            final String serverBrand = this.networkHandler().getBrand();
            if (serverBrand != null) {
                final String text = "Server Brand: " + serverBrand.replaceFirst("\\(.*?\\) ", "");
                context.drawText(this.textRenderer(), text, x, this.y + height, color, shadow);
                height += fontHeight;
                final int textWidth = this.textRenderer().getWidth(text);
                if (textWidth > width) {
                    width = textWidth;
                }
            }
        }
        if (this.difficulty.getValue()) {
            final String text = "Difficulty: " + this.world().getDifficulty().getName();
            context.drawText(this.textRenderer(), text, x, this.y + height, color, shadow);
            height += fontHeight;
            final int textWidth = this.textRenderer().getWidth(text);
            if (textWidth > width) {
                width = textWidth;
            }
        }
        if (this.permissionsLevel.getValue()) {
            final String text = "Permissions Level: " + this.player().getPermissionLevel();
            context.drawText(this.textRenderer(), text, x, this.y + height, color, shadow);
            height += fontHeight;
            final int textWidth = this.textRenderer().getWidth(text);
            if (textWidth > width) {
                width = textWidth;
            }
        }
        this.width = width;
        this.height = height;
    }

}
