/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.feature.module.impl.combat;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.target.TargetGroup;
import de.nekosarekawaii.vandalism.event.player.AttackListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.raphimc.vialoader.util.VersionRange;

public class MaceInstaKillModule extends Module implements AttackListener {

    private final TargetGroup targets = new TargetGroup(this, "Targets", "The entities to target.");

    public MaceInstaKillModule() {
        super(
                "Mace Insta Kill",
                "Allows you to instant kill entities with the mace.",
                Category.COMBAT,
                VersionRange.andNewer(ProtocolVersion.v1_21)
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(AttackSendEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(AttackSendEvent.ID, this);
    }

    @Override
    public void onAttackSend(final AttackSendEvent event) {
        if (!mc.player.getMainHandStack().isOf(Items.MACE) || !this.targets.isTarget(event.target)) {
            return;
        }
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
        for (int i = 0; i < 4; ++i) {
            this.spoofPosition(0.0);
        }
        this.spoofPosition(Math.sqrt(500.0));
        this.spoofPosition(0.0);
    }

    private void spoofPosition(final double offset) {
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY() + offset, mc.player.getZ(), false));
    }

}
