package me.nekosarekawaii.foxglove.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.LivingEntityListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.BooleanValue;
import me.nekosarekawaii.foxglove.value.values.ColorValue;

import java.awt.*;

@ModuleInfo(name = "True Sight", description = "Makes invisible blocks and entities visible.", category = FeatureCategory.RENDER)
public class TrueSightModule extends Module implements LivingEntityListener {

    public final BooleanValue blocks = new BooleanValue("Blocks", "Makes invisible blocks visible.", this, true);

    private final BooleanValue entities = new BooleanValue("Entities", "Makes invisible entities visible.", this, true);

    private final Value<Color> entityColor = new ColorValue("Entity Color", "The Color of invisible entities.", this, new Color(255, 255, 255, 127)).visibleConsumer(this.entities::getValue);

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
            final Color color = this.entityColor.getValue();
            event.red = color.getRed() / 255f;
            event.green = color.getGreen() / 255f;
            event.blue = color.getBlue() / 255f;
            event.alpha = color.getAlpha() / 255f;
            event.showBody = event.alpha == 1.0f;
            event.translucent = event.alpha < 1.0f;
        }
    }

}
