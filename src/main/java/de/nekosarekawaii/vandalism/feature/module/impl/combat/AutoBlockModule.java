package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import de.florianmichael.viafabricplus.protocolhack.ProtocolHack;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.game.TickGameListener;
import de.nekosarekawaii.vandalism.base.event.player.AttackListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.raphimc.vialoader.util.VersionEnum;

public class AutoBlockModule extends AbstractModule implements AttackListener, TickGameListener {

    private boolean blocking = false;

    private long lastAttack;

    public AutoBlockModule() {
        super(
                "Auto Block",
                "Automatically blocks attacks.",
                Category.COMBAT
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, AttackSendEvent.ID, TickGameEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, AttackSendEvent.ID, TickGameEvent.ID);
    }

    @Override
    public void onAttackSend(final AttackSendEvent event) {
        this.setBlocking(false);
        this.lastAttack = System.currentTimeMillis();
    }

    @Override
    public void onTick() {
        final long timeSinceLastAttack = System.currentTimeMillis() - this.lastAttack;
        this.setBlocking(timeSinceLastAttack < 1000L);
    }

    public void setBlocking(final boolean blocking) {
        final ItemStack mainHandStack = this.mc.player.getMainHandStack();
        final ItemStack offHandStack = this.mc.player.getOffHandStack();
        if (mainHandStack.isEmpty() && offHandStack.isEmpty()) {
            return;
        }
        final Item mainHandItem = mainHandStack.getItem();
        final Item offHandItem = offHandStack.getItem();
        final boolean isOldVersion = ProtocolHack.getTargetVersion().isOlderThanOrEqualTo(VersionEnum.r1_8);
        if (isOldVersion) {
            if (!(mainHandItem instanceof SwordItem)) {
                return;
            }
        } else if (mainHandItem.equals(Items.SHIELD) || offHandItem.equals(Items.SHIELD)) {
            return;
        }
        if (blocking) {
            if (isOldVersion && !this.blocking) {
                this.mc.options.useKey.setPressed(true);
            } else {
                this.mc.doItemUse();
            }
        } else {
            this.mc.options.useKey.setPressed(false);
        }
        this.blocking = blocking;
    }

    public boolean isBlocking() {
        return this.blocking;
    }

}
