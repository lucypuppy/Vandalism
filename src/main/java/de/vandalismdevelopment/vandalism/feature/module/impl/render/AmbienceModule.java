package de.vandalismdevelopment.vandalism.feature.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.dietrichevents2.Priorities;
import de.vandalismdevelopment.vandalism.base.event.PacketListener;
import de.vandalismdevelopment.vandalism.feature.module.AbstractModule;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.impl.number.IntegerValue;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

public class AmbienceModule extends AbstractModule implements PacketListener {

    private final Value<Integer> worldTime = new IntegerValue(
            "World time",
            "Time of the world.",
            this,
            14000,
            0,
            20000
    );

    public AmbienceModule() {
        super("Ambience", "Allows you to customize your ambience.", Category.RENDER);
    }

    @Override
    public void onPacket(final PacketEvent event) {
        if (event.packet instanceof final WorldTimeUpdateS2CPacket worldTimePacket)
            worldTimePacket.timeOfDay = this.worldTime.getValue();
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this, Priorities.LOW);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
    }

}
