package de.nekosarekawaii.foxglove.feature.impl.module.impl.render;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.nekosarekawaii.foxglove.event.PacketListener;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.module.Module;
import de.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import de.nekosarekawaii.foxglove.value.Value;
import de.nekosarekawaii.foxglove.value.values.number.slider.SliderIntegerValue;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

@ModuleInfo(name = "Ambience", description = "Changes the ambience of the world.", category = FeatureCategory.RENDER)
public class AmbienceModule extends Module implements PacketListener {

    public final Value<Integer> worldTime = new SliderIntegerValue(
            "World time",
            "Time of the world.",
            this,
            14000,
            0,
            20000
    );

    @Override
    public void onPacketRead(final PacketEvent event) {
        if (event.packet instanceof final WorldTimeUpdateS2CPacket worldTimeUpdateS2CPacket) {
            worldTimeUpdateS2CPacket.timeOfDay = this.worldTime.getValue();
        }
    }

    @Override
    protected void onEnable() {
        DietrichEvents2.global().subscribe(PacketEvent.ID, this);
    }

    @Override
    protected void onDisable() {
        DietrichEvents2.global().unsubscribe(PacketEvent.ID, this);
    }

}
