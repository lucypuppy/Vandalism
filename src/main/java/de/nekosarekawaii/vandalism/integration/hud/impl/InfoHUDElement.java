package de.nekosarekawaii.vandalism.integration.hud.impl;

import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.integration.hud.HUDElement;
import de.nekosarekawaii.vandalism.util.minecraft.ServerUtil;
import de.nekosarekawaii.vandalism.util.minecraft.WorldUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;

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

    private final ValueGroup serverElements = new ValueGroup(
            this,
            "Server Elements",
            "Elements that are shown in the server category."
    );

    private final BooleanValue serverBrand = new BooleanValue(
            this.serverElements,
            "Server Brand",
            "Shows the current server brand.",
            true
    );

    private final BooleanValue serverVersion = new BooleanValue(
            this.serverElements,
            "Server Version",
            "Shows the current server version.",
            true
    );

    private final BooleanValue serverAddress = new BooleanValue(
            this.serverElements,
            "Server Address",
            "Shows the current server address.",
            true
    );

    public InfoHUDElement() {
        super("Info", 2, 60);
    }

    @Override
    public void onRender(final DrawContext context, final float delta) {
        final Map<String, String> infoMap = new HashMap<>();
        if (this.fps.getValue()) {
            infoMap.put("FPS", Integer.toString(this.mc.getCurrentFps()));
        }
        if (this.username.getValue()) {
            infoMap.put("Username", this.mc.session.getUsername());
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
            infoMap.put("Position", String.format(
                    positionDecimalPlaces + ", " + positionDecimalPlaces + ", " + positionDecimalPlaces,
                    posX,
                    posY,
                    posZ
            ));
        }
        if (this.dimensionalPosition.getValue()) {
            final WorldUtil.Dimension dimension = mc.player == null ? WorldUtil.Dimension.OVERWORLD : WorldUtil.getDimension();
            if (dimension != WorldUtil.Dimension.END) {
                final int positionDecimalPlacesRawValue = this.positionDecimalPlaces.getValue();
                if (positionDecimalPlacesRawValue < 1) this.positionDecimalPlaces.setValue(1);
                else if (positionDecimalPlacesRawValue > 15) this.positionDecimalPlaces.setValue(15);
                final int decimalPlaces = this.positionDecimalPlaces.getValue();
                final String positionFormat = "%." + decimalPlaces + "f";
                String name = "";
                double correctedX = posX, correctedZ = posZ;
                switch (dimension) {
                    case NETHER -> {
                        name = "Overworld Position: ";
                        correctedX = posX * 8;
                        correctedZ = posZ * 8;
                    }
                    case OVERWORLD -> {
                        name = "Nether Position: ";
                        correctedX = posX / 8;
                        correctedZ = posZ / 8;
                    }
                    default -> {
                    }
                }
                infoMap.put(name, String.format(
                        positionFormat + ", " + positionFormat + ", " + positionFormat,
                        correctedX,
                        posY,
                        correctedZ
                ));
            }
        }
        if (this.difficulty.getValue()) {
            infoMap.put("Difficulty", this.mc.world != null ? this.mc.world.getDifficulty().getName() : "unknown");
        }
        if (this.permissionsLevel.getValue()) {
            infoMap.put("Permissions Level", this.mc.player != null ? Integer.toString(this.mc.player.getPermissionLevel()) : "unknown");
        }
        if (this.serverBrand.getValue()) {
            String value = "unknown";
            if (this.mc.getNetworkHandler() != null) {
                final String brand = this.mc.getNetworkHandler().getBrand();
                if (brand != null) {
                    value = brand.replaceFirst("\\(.*?\\) ", "");
                }
            }
            infoMap.put("Server Brand", value);
        }
        if (this.serverVersion.getValue()) {
            String value = "unknown";
            if (ServerUtil.lastServerExists() && this.mc.player != null) {
                final Text version = ServerUtil.getLastServerInfo().version;
                if (version != null) {
                    value = version.getString();
                }
            }
            infoMap.put("Server Version", value);
        }
        if (this.serverAddress.getValue()) {
            String value = "unknown";
            if (ServerUtil.lastServerExists() && this.mc.player != null) {
                final String address = ServerUtil.getLastServerInfo().address;
                if (address != null) {
                    value = address;
                }
            }
            infoMap.put("Server Address", value);
        }
        int width = 0, height = 0;
        final int fontHeight = this.mc.textRenderer.fontHeight;
        for (final Map.Entry<String, String> infoEntry : infoMap.entrySet()) {
            String text = infoEntry.getKey() + ": " + infoEntry.getValue();
            context.drawText(this.mc.textRenderer, text, this.x, this.y + height, -1, false);
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
