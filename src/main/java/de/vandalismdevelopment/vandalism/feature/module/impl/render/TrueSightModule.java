package de.vandalismdevelopment.vandalism.feature.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.common.ColorUtils;
import de.vandalismdevelopment.vandalism.base.event.render.CameraClipRaytraceListener;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.primitive.BooleanValue;
import de.vandalismdevelopment.vandalism.base.value.impl.awt.ColorValue;

import java.awt.*;

//TODO: Fix the entity layer rendering.
public class TrueSightModule extends AbstractModule implements CameraClipRaytraceListener {

    public final Value<Boolean> blocks = new BooleanValue(
            this,
            "Blocks",
            "Makes invisible blocks visible.",
            true
    );

    private final Value<Boolean> entities = new BooleanValue(
            this,
            "Entities",
            "Makes invisible entities visible.",
            true
    );

    public final Value<Boolean> showIllusionerEntity = new BooleanValue(
            this,
            "Show Illusioner Entity",
            "Makes the illusioner entity visible.",
            true
    ).visibleCondition(this.entities::getValue);

    private final Value<Color> entityColor = new ColorValue(
            this,
            "Entity Color",
            "The color of invisible entities.",
            ColorUtils.withAlpha(Color.WHITE, 100)
    ).visibleCondition(this.entities::getValue);

    public TrueSightModule() {
        super("True Sight", "Makes invisible blocks or entities visible.", Category.RENDER);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(LivingEntityRenderBottomLayerEvent.ID, this);
    }

    @Override
    public void onDisable() {
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
