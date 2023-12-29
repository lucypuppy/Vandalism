package de.nekosarekawaii.vandalism.feature.module.impl.misc.modes.suicide;

import de.florianmichael.rclasses.common.RandomUtils;
import de.florianmichael.rclasses.math.integration.MSTimer;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.SuicideModule;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleMulti;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;

public class BoatModuleMode extends ModuleMulti<SuicideModule> implements TickGameListener {

    private final IntegerValue maxPacketsPerTick = new IntegerValue(
            this.getParent(),
            "Max Packets Per Tick",
            "The maximum amount of packets sent per tick.",
            1000,
            500,
            10000
    ).visibleCondition(() -> this.getParent().mode.getValue().equals(this));

    private final IntegerValue delay = new IntegerValue(
            this.getParent(),
            "Delay",
            "The delay between packets.",
            0,
            0,
            2000
    ).visibleCondition(() -> this.getParent().mode.getValue().equals(this));

    private final MSTimer delayTimer = new MSTimer();

    public BoatModuleMode(final SuicideModule parent) {
        super("Boat", parent);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(TickGameEvent.ID, this);
    }

    @Override
    public void onTick() {
        if (this.mc.player == null) return;
        if (this.delayTimer.hasReached(this.delay.getValue(), true)) {
            for (int i = 0; i < this.maxPacketsPerTick.getValue(); i++) {
                final VehicleMoveC2SPacket packet = new VehicleMoveC2SPacket(this.mc.player);
                packet.y *= RandomUtils.randomDouble(1, 3);
                this.mc.getNetworkHandler().getConnection().send(packet, null, true);
            }
        }
    }

}
