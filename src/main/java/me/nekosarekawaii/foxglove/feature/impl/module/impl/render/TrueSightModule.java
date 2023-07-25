package me.nekosarekawaii.foxglove.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import me.nekosarekawaii.foxglove.event.impl.LivingEntityListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.value.Value;
import me.nekosarekawaii.foxglove.value.values.BooleanValue;
import me.nekosarekawaii.foxglove.value.values.ColorValue;

@ModuleInfo(name = "True Sight", description = "Makes invisible blocks and entities visible.", category = FeatureCategory.RENDER)
public class TrueSightModule extends Module implements LivingEntityListener {

    public final Value<Boolean> blocks = new BooleanValue("Blocks", "Makes invisible blocks visible.", this, true);

    private final Value<Boolean> entities = new BooleanValue("Entities", "Makes invisible entities visible.", this, true);

    private final Value<float[]> entityColor = new ColorValue("Entity Color", "The Color of invisible entities.", this, 1.0f, 1.0f, 1.0f, 0.498f).visibleConsumer(this.entities::getValue);

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
            final float[] color = this.entityColor.getValue();
            event.red = color[0];
            event.green = color[1];
            event.blue = color[2];
            event.alpha = color[3];
            if (event.alpha < 1.0f) event.translucent = true;
            else event.showBody = true;
        }
    }

}
