package de.vandalismdevelopment.vandalism.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.event.RenderListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.util.GLStateTracker;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.WorldUtil;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.ValueCategory;
import de.vandalismdevelopment.vandalism.value.impl.BooleanValue;
import de.vandalismdevelopment.vandalism.value.impl.number.slider.SliderIntegerValue;
import net.minecraft.client.gui.DrawContext;

public class HUDModule extends Module implements RenderListener {

    private final Value<Boolean> watermark = new BooleanValue("Watermark", "Shows the watermark.", this, true);

    private final Value<Boolean> infos = new BooleanValue("Infos", "Shows general infos.", this, true);
    private final ValueCategory infoElements = new ValueCategory("Info Elements", "Elements that are shown in the infos category.", this).visibleConsumer(this.infos::getValue);

    private final Value<Boolean> fps = new BooleanValue("FPS", "Shows the current fps.", this.infoElements, true).visibleConsumer(this.infos::getValue);
    private final Value<Boolean> username = new BooleanValue("Username", "Shows the current username.", this.infoElements, true).visibleConsumer(this.infos::getValue);

    private final Value<Boolean> position = new BooleanValue("Position", "Shows the current position.", this.infoElements, true).visibleConsumer(this.infos::getValue);
    private final Value<Boolean> dimensionalPosition = new BooleanValue("Dimensional Position", "Shows the current position of the dimension you are currently playing in.", this.infoElements, true).visibleConsumer(this.infos::getValue);
    private final ValueCategory positionElements = new ValueCategory("Position Elements", "Elements that are shown in the position category.", this).visibleConsumer(this.position::getValue);
    private final Value<Integer> positionDecimalPlaces = new SliderIntegerValue("Position Decimal Places", "Allows you to change the viewable amount of decimal places from the x/y/z position.", this.positionElements, 2, 1, 15).visibleConsumer(this.position::getValue);

    private final Value<Boolean> serverBrand = new BooleanValue("Server Brand", "Shows the current server brand.", this.infoElements, true).visibleConsumer(this.infos::getValue);
    private final Value<Boolean> difficulty = new BooleanValue("Difficulty", "Shows the current world difficulty.", this.infoElements, true).visibleConsumer(this.infos::getValue);
    private final Value<Boolean> permissionsLevel = new BooleanValue("Permissions Level", "Shows the current permissions level.", this.infoElements, true).visibleConsumer(this.infos::getValue);

    public HUDModule() {
        super("HUD", "Shows various infos from the game and the mod in game.", FeatureCategory.RENDER, false, false);
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(Render2DEvent.ID, this);
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta) {
        int color = -1, x = 0, y = 2;
        boolean shadow = false;
        if (this.watermark.getValue()) {
            final int width = 156, height = 44;
            this.mc().getTextureManager().getTexture(Vandalism.getInstance().getLogo()).setFilter(true, true);
            GLStateTracker.BLEND.save(true);
            context.drawTexture(Vandalism.getInstance().getLogo(), x, y, 0, 0, width, height, width, height);
            GLStateTracker.BLEND.revert();
            y += height;
        }
        if (this.infos.getValue()) {
            y += 10;
            if (this.fps.getValue()) {
                context.drawText(this.textRenderer(), "FPS: " + this.mc().getCurrentFps(), x, y, color, shadow);
                y += this.textRenderer().fontHeight;
            }
            if (this.username.getValue()) {
                context.drawText(this.textRenderer(), "Username: " + this.player().getGameProfile().getName(), x, y, color, shadow);
                y += this.textRenderer().fontHeight;
            }
            if (this.position.getValue()) {
                final int positionDecimalPlacesRawValue = this.positionDecimalPlaces.getValue();
                if (positionDecimalPlacesRawValue < 1) this.positionDecimalPlaces.setValue(1);
                else if (positionDecimalPlacesRawValue > 15) this.positionDecimalPlaces.setValue(15);
                final String positionDecimalPlaces = "%." + this.positionDecimalPlaces.getValue() + "f";
                context.drawText(this.textRenderer(), "Position: " + String.format(positionDecimalPlaces + ", " + positionDecimalPlaces + ", " + positionDecimalPlaces, this.player().getX(), this.player().getY(), this.player().getZ()), x, y, color, shadow);
                y += this.textRenderer().fontHeight;
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
                        context.drawText(this.textRenderer(), text + String.format(positionFormat + ", " + positionFormat + ", " + positionFormat, posX, posY, posZ), x, y, color, shadow);
                        y += this.textRenderer().fontHeight;
                    }
                }
            }
            if (this.serverBrand.getValue()) {
                final String serverBrand = this.networkHandler().getBrand();
                if (serverBrand != null) {
                    final String brand = "Server Brand: " + serverBrand.replaceFirst("\\(.*?\\) ", "");
                    context.drawText(this.textRenderer(), brand, x, y, color, shadow);
                    y += this.textRenderer().fontHeight;
                }
            }
            if (this.difficulty.getValue()) {
                context.drawText(this.textRenderer(), "Difficulty: " + this.world().getDifficulty().getName(), x, y, color, shadow);
                y += this.textRenderer().fontHeight;
            }
            if (this.permissionsLevel.getValue()) {
                context.drawText(this.textRenderer(), "Permissions Level: " + this.player().getPermissionLevel(), x, y, color, shadow);
            }
        }
    }

}
