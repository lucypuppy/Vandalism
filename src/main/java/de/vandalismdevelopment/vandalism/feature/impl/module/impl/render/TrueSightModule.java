package de.vandalismdevelopment.vandalism.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.LivingEntityListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.util.render.ColorUtils;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.values.BooleanValue;
import de.vandalismdevelopment.vandalism.value.values.ColorValue;

import java.awt.*;

public class TrueSightModule extends Module implements LivingEntityListener {

    public final Value<Boolean> blocks = new BooleanValue(
            "Blocks",
            "Makes invisible blocks visible.",
            this,
            true
    );

    private final Value<Boolean> entities = new BooleanValue(
            "Entities",
            "Makes invisible entities visible.",
            this,
            true
    );

    public final Value<Boolean> illusionerEntity = new BooleanValue(
            "Illusioner Entity",
            "Makes the illusioner entity visible.",
            this,
            true
    ).visibleConsumer(this.entities::getValue);

    private final Value<Color> entityColor = new ColorValue(
            "Entity Color",
            "The color of invisible entities.",
            this,
            ColorUtils.withAlpha(Color.WHITE, 100)
    ).visibleConsumer(this.entities::getValue);

    public TrueSightModule() {
        super(
                "True Sight",
                "Makes invisible blocks or entities visible.",
                FeatureCategory.RENDER,
                false,
                false
        );
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(LivingEntityRenderBottomLayerEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(LivingEntityRenderBottomLayerEvent.ID, this);
    }

    @Override
    public void onLivingEntityRenderBottomLayer(final LivingEntityRenderBottomLayerEvent event) {
        if (this.entities.getValue() && event.livingEntity.isInvisible()) {
            final Color color = this.entityColor.getValue();
            event.red = color.getRed() / 255F;
            event.green = color.getGreen() / 255F;
            event.blue = color.getBlue() / 255F;
            event.alpha = color.getAlpha() / 255F;
        }
    }

}
