package de.vandalismdevelopment.vandalism.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.dietrichevents2.Priorities;
import de.vandalismdevelopment.vandalism.event.PacketListener;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.impl.number.slider.SliderIntegerValue;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

public class AmbienceModule extends Module implements PacketListener {

    private final Value<Integer> worldTime = new SliderIntegerValue(
            "World time",
            "Time of the world.",
            this,
            14000,
            0,
            20000
    );

    public AmbienceModule() {
        super(
                "Ambience",
                "Allows you to customize your ambience.",
                FeatureCategory.RENDER,
                false,
                false
        );
    }

    @Override
    public void onPacket(final PacketEvent event) {
        if (event.packet instanceof final WorldTimeUpdateS2CPacket worldTimePacket)
            worldTimePacket.timeOfDay = this.worldTime.getValue();
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this, Priorities.LOW);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
    }

}
