package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.base.event.player.AttackListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.item.Items;
import net.raphimc.vialoader.util.VersionEnum;

public class AutoBlockModule extends AbstractModule implements AttackListener, TickGameListener {

    private boolean blocking = false;

    public AutoBlockModule() {
        super(
                "Auto Block",
                "Automatically blocks attacks.",
                Category.COMBAT
        );
    }

    private long lastAttack;

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, AttackSendEvent.ID, TickGameEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, AttackSendEvent.ID, TickGameEvent.ID);
    }

    @Override
    public void onAttackSend(AttackSendEvent event) {
        setBlocking(false);
        this.lastAttack = System.currentTimeMillis();
    }

    @Override
    public void onTick() {
        final long timeSinceLastAttack = System.currentTimeMillis() - this.lastAttack;
        setBlocking(timeSinceLastAttack < 1000L);
    }

    public void setBlocking(boolean blocking) {
        if (blocking) {
            if (ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8) && !this.blocking) {
                mc.options.useKey.setPressed(true);
            } else {
                if (!mc.player.getOffHandStack().isEmpty() && mc.player.getOffHandStack().getItem().equals(Items.SHIELD)) {
                    mc.doItemUse();
                }
            }
        } else {
            mc.options.useKey.setPressed(false);
        }

        this.blocking = blocking;
    }

    public boolean isBlocking() {
        return blocking;
    }

}
