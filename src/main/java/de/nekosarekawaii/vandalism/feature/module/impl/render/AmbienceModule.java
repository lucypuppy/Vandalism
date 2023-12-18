package de.nekosarekawaii.vandalism.feature.module.impl.render;

import de.florianmichael.dietrichevents2.Priorities;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

public class AmbienceModule extends AbstractModule implements IncomingPacketListener {

    private final IntegerValue worldTime = new IntegerValue(
            this,
            "World time",
            "Time of the world.",
            14000,
            0,
            20000
    );

    public AmbienceModule() {
        super("Ambience", "Allows you to customize your ambience.", Category.RENDER);
    }

    @Override
    public void onEnable() {
        Vandalism.getInstance().getEventSystem().subscribe(IncomingPacketEvent.ID, this, Priorities.LOW);
    }

    @Override
    public void onDisable() {
        Vandalism.getInstance().getEventSystem().unsubscribe(IncomingPacketEvent.ID, this);
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        if (event.packet instanceof final WorldTimeUpdateS2CPacket worldTimePacket) {
            worldTimePacket.timeOfDay = this.worldTime.getValue();
        }
    }

}
