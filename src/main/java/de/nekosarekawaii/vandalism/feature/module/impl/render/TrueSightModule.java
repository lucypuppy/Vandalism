package de.nekosarekawaii.vandalism.feature.module.impl.render;

import de.florianmichael.rclasses.common.ColorUtils;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.render.LivingEntityRenderBottomLayerListener;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.awt.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;

import java.awt.*;


public class TrueSightModule extends AbstractModule implements LivingEntityRenderBottomLayerListener {

    public final BooleanValue blocks = new BooleanValue(
            this,
            "Blocks",
            "Makes invisible blocks visible.",
            true
    );

    private final BooleanValue entities = new BooleanValue(
            this,
            "Entities",
            "Makes invisible entities visible.",
            true
    );

    public final BooleanValue showIllusionerEntity = new BooleanValue(
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
        Vandalism.getInstance().getEventSystem().subscribe(LivingEntityRenderBottomLayerEvent.ID, this);
    }

    @Override
    public void onDisable() {
        Vandalism.getInstance().getEventSystem().unsubscribe(LivingEntityRenderBottomLayerEvent.ID, this);
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
