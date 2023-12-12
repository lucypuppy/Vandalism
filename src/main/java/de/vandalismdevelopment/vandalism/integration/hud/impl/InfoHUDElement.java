package de.vandalismdevelopment.vandalism.integration.hud.impl;

import de.vandalismdevelopment.vandalism.integration.hud.HUDElement;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.template.ValueGroup;
import de.vandalismdevelopment.vandalism.base.value.impl.primitive.BooleanValue;
import de.vandalismdevelopment.vandalism.base.value.impl.number.IntegerValue;
import de.vandalismdevelopment.vandalism.util.minecraft.WorldUtil;
import net.minecraft.client.gui.DrawContext;

public class InfoHUDElement extends HUDElement {

    private final BooleanValue fps = new BooleanValue(
            this,
            "FPS",
            "Shows the current fps.",
            true
    );

    private final BooleanValue username = new BooleanValue(
            this,
            "Username",
            "Shows the current username.",
            true
    );

    private final BooleanValue position = new BooleanValue(
            this,
            "Position",
            "Shows the current position.",
            true
    );

    private final BooleanValue dimensionalPosition = new BooleanValue(
            this,
            "Dimensional Position",
            "Shows the current position of the dimension you are currently playing in.",
            true
    );

    private final ValueGroup positionElements = new ValueGroup(
            this,
            "Position Elements",
            "Elements that are shown in the position category."
    ).visibleCondition(this.position::getValue);

    private final IntegerValue positionDecimalPlaces = new IntegerValue(
            this.positionElements,
            "Position Decimal Places",
            "Allows you to change the viewable amount of decimal places from the x/y/z position.",
            2,
            1,
            15
    ).visibleCondition(this.position::getValue);

    private final BooleanValue serverBrand = new BooleanValue(
            this,
            "Server Brand",
            "Shows the current server brand.",
            true
    );

    private final BooleanValue difficulty = new BooleanValue(
            this,
            "Difficulty",
            "Shows the current world difficulty.",
            true
    );

    private final BooleanValue permissionsLevel = new BooleanValue(
            this,
            "Permissions Level",
            "Shows the current permissions level.",
            true
    );

    public InfoHUDElement() {
        super("Info", 2, 60);
    }

    @Override
    public void onRender(final DrawContext context, final float delta) {
        int color = -1;
        boolean shadow = false;
        int x = this.x, width = 0, height = 0;
        final int fontHeight = this.mc.textRenderer.fontHeight;
        if (this.fps.getValue()) {
            final String text = "FPS: " + this.mc.getCurrentFps();
            context.drawText(this.mc.textRenderer, text, x, this.y + height, color, shadow);
            height += fontHeight;
            final int textWidth = this.mc.textRenderer.getWidth(text);
            if (textWidth > width) {
                width = textWidth;
            }
        }
        if (this.username.getValue()) {
            final String text = "Username: " + this.mc.session.getUsername();
            context.drawText(this.mc.textRenderer, text, x, this.y + height, color, shadow);
            height += fontHeight;
            final int textWidth = this.mc.textRenderer.getWidth(text);
            if (textWidth > width) {
                width = textWidth;
            }
        }
        final double posX, posY, posZ;
        if (this.mc.player != null) {
            posX = this.mc.player.getX();
            posY = this.mc.player.getY();
            posZ = this.mc.player.getZ();
        } else {
            posX = 0D;
            posY = 0D;
            posZ = 0D;
        }
        if (this.position.getValue()) {
            final int positionDecimalPlacesRawValue = this.positionDecimalPlaces.getValue();
            if (positionDecimalPlacesRawValue < 1) this.positionDecimalPlaces.setValue(1);
            else if (positionDecimalPlacesRawValue > 15) this.positionDecimalPlaces.setValue(15);
            final String positionDecimalPlaces = "%." + this.positionDecimalPlaces.getValue() + "f";
            final String text = "Position: " + String.format(
                    positionDecimalPlaces + ", " + positionDecimalPlaces + ", " + positionDecimalPlaces,
                    posX,
                    posY,
                    posZ
            );
            context.drawText(this.mc.textRenderer, text, x, this.y + height, color, shadow);
            height += fontHeight;
            final int textWidth = this.mc.textRenderer.getWidth(text);
            if (textWidth > width) {
                width = textWidth;
            }
        }
        if (this.dimensionalPosition.getValue()) {
            final WorldUtil.Dimension dimension = mc.player == null ? WorldUtil.Dimension.OVERWORLD : WorldUtil.getDimension();
            if (dimension != WorldUtil.Dimension.END) {
                final int positionDecimalPlacesRawValue = this.positionDecimalPlaces.getValue();
                if (positionDecimalPlacesRawValue < 1) this.positionDecimalPlaces.setValue(1);
                else if (positionDecimalPlacesRawValue > 15) this.positionDecimalPlaces.setValue(15);
                final int decimalPlaces = this.positionDecimalPlaces.getValue();
                final String positionFormat = "%." + decimalPlaces + "f";
                String text = "";
                double correctedX = posX, correctedZ = posZ;
                switch (dimension) {
                    case NETHER -> {
                        text = "Overworld Position: ";
                        correctedX = posX * 8;
                        correctedZ = posZ * 8;
                    }
                    case OVERWORLD -> {
                        text = "Nether Position: ";
                        correctedX = posX / 8;
                        correctedZ = posZ / 8;
                    }
                    default -> {
                    }
                }
                text += String.format(
                        positionFormat + ", " + positionFormat + ", " + positionFormat,
                        correctedX,
                        posY,
                        correctedZ
                );
                context.drawText(this.mc.textRenderer, text, x, this.y + height, color, shadow);
                height += fontHeight;
                final int textWidth = this.mc.textRenderer.getWidth(text);
                if (textWidth > width) {
                    width = textWidth;
                }
            }
        }
        if (this.serverBrand.getValue()) {
            String text = "Server Brand: ";
            String value = "unknown";
            if (this.mc.getNetworkHandler() != null) {
                final String brand = this.mc.getNetworkHandler().getBrand();
                if (brand != null) {
                    value = brand.replaceFirst("\\(.*?\\) ", "");
                }
            }
            text += value;
            context.drawText(this.mc.textRenderer, text, x, this.y + height, color, shadow);
            height += fontHeight;
            final int textWidth = this.mc.textRenderer.getWidth(text);
            if (textWidth > width) {
                width = textWidth;
            }
        }
        if (this.difficulty.getValue()) {
            final String text = "Difficulty: " + (this.mc.world != null ? this.mc.world.getDifficulty().getName() : "unknown");
            context.drawText(this.mc.textRenderer, text, x, this.y + height, color, shadow);
            height += fontHeight;
            final int textWidth = this.mc.textRenderer.getWidth(text);
            if (textWidth > width) {
                width = textWidth;
            }
        }
        if (this.permissionsLevel.getValue()) {
            final String text = "Permissions Level: " + (this.mc.player != null ? this.mc.player.getPermissionLevel() : "unknown");
            context.drawText(this.mc.textRenderer, text, x, this.y + height, color, shadow);
            height += fontHeight;
            final int textWidth = this.mc.textRenderer.getWidth(text);
            if (textWidth > width) {
                width = textWidth;
            }
        }
        this.width = width;
        this.height = height;
    }

}
