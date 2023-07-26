package me.nekosarekawaii.foxglove.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.LivingEntityListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.util.render.ColorUtils;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.BooleanValue;
import me.nekosarekawaii.foxglove.value.values.ColorValue;

import java.awt.*;

@ModuleInfo(name = "True Sight", description = "Makes invisible blocks and entities visible.", category = FeatureCategory.RENDER)
public class TrueSightModule extends Module implements LivingEntityListener {

    public final Value<Boolean> blocks = new BooleanValue("Blocks", "Makes invisible blocks visible.", this, true);

    private final Value<Boolean> entities = new BooleanValue("Entities", "Makes invisible entities visible.", this, true);

    public final Value<Boolean> illusionerEntity = new BooleanValue(
            "Illusioner Entity", "Makes the illusioner entity visible.", this, true
    ).visibleConsumer(this.entities::getValue);

    private final Value<Color> entityColor = new ColorValue("Entity Color", "The Color of invisible entities.", this, ColorUtils.withAlpha(Color.WHITE, 100)).visibleConsumer(this.entities::getValue);

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(LivingEntityRenderEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(LivingEntityRenderEvent.ID, this);
    }

    @Override
    public void onRenderLivingEntity(final LivingEntityRenderEvent event) {
        if (this.entities.getValue() && !event.showBody) {
            final var color = this.entityColor.getValue();
            event.red = color.getRed() / 255F;
            event.green = color.getGreen() / 255F;
            event.blue = color.getBlue() / 255F;
            event.alpha = color.getAlpha() / 255F;

            if (event.alpha < 1.0f) event.translucent = true;
            else event.showBody = true;
        }
    }

}
