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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.StringValue;
import de.nekosarekawaii.vandalism.event.game.WorldListener;
import de.nekosarekawaii.vandalism.event.network.DisconnectListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.*;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class KaboomFuckerModule extends Module implements PlayerUpdateListener, WorldListener, DisconnectListener {

    private final IntegerValue commandBlockRescanDelay = new IntegerValue(
            this,
            "Command Block Rescan Delay",
            "The delay in ticks in which command blocks should be rescanned.",
            50,
            10,
            100
    );

    private final IntegerValue range = new IntegerValue(
            this,
            "Range",
            "The range in which the command blocks should be scanned.",
            40,
            1,
            100
    );

    private final IntegerValue times = new IntegerValue(
            this,
            "Times",
            "The amount of times the function should be executed.",
            1,
            1,
            100
    );

    private final IntegerValue delay = new IntegerValue(
            this,
            "Delay",
            "The delay between executions.",
            0,
            0,
            10000
    );

    private final BooleanValue destroyCmdBlocks = new BooleanValue(
            this,
            "Destroy Command Blocks",
            "Destroys every found command block by using a command.",
            false
    );

    private final BooleanValue targetAllPlayers = new BooleanValue(
            this,
            "Target All Players",
            "Targets all players instead of a specific player.",
            true
    ).visibleCondition(() -> !this.destroyCmdBlocks.getValue());

    private final StringValue target = new StringValue(
            this,
            "Target",
            "The target player for the commands.",
            ""
    ).visibleCondition(() -> !this.destroyCmdBlocks.getValue() && !this.targetAllPlayers.getValue());

    private final BooleanValue kaboomServerOnly = new BooleanValue(
            this,
            "Kaboom Server Only",
            "Only works on kaboom based servers.",
            true
    );

    private final MSTimer delayTimer = new MSTimer();
    private final List<BlockPos> commandBlocks = new CopyOnWriteArrayList<>();
    private int current = 0;

    public KaboomFuckerModule() {
        super("Kaboom Fucker", "Allows you to mess around with kaboom themed servers.", Category.MISC);
        this.deactivateAfterSessionDefault();
    }

    private void reset() {
        this.current = 0;
        this.commandBlocks.clear();
    }

    @Override
    public void onActivate() {
        this.reset();
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, WorldLoadEvent.ID, DisconnectEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, WorldLoadEvent.ID, DisconnectEvent.ID);
        this.reset();
    }

    @Override
    public void onPreWorldLoad() {
        this.reset();
    }

    @Override
    public void onDisconnect(final ClientConnection clientConnection, final Text disconnectReason) {
        this.reset();
        SessionUtil.setSessionAsync(NameGenerationUtil.generateUsername(), "");
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.delayTimer.hasReached(this.delay.getValue(), true)) {
            for (int i = 0; i < this.times.getValue(); i++) {
                if (this.mc.player == null) {
                    continue;
                }
                this.onExecute();
            }
        }
    }

    private void onExecute() {
        if (this.kaboomServerOnly.getValue()) {
            if (!ServerUtil.lastServerExists()) {
                this.deactivate();
                return;
            } else {
                final String address = ServerUtil.getLastServerInfo().address.toLowerCase();
                if (!address.equalsIgnoreCase("kaboom.pw") && !address.equalsIgnoreCase("chipmunk.land") && !address.equalsIgnoreCase("nekoboom.bongocatsmp.site")) {
                    this.deactivate();
                }
            }
        }

        final int range = this.range.getValue();
        if (this.commandBlocks.isEmpty()) {
            for (int x = -range; x < range; x++) {
                for (int y = -range; y < range; y++) {
                    for (int z = -range; z < range; z++) {
                        final BlockPos blockPos = new BlockPos(
                                (int) (this.mc.player.getX() + x),
                                (int) (this.mc.player.getY() + y),
                                (int) (this.mc.player.getZ() + z)
                        );
                        if (this.mc.world.getBlockState(blockPos).getBlock() instanceof CommandBlock) {
                            this.commandBlocks.add(blockPos);
                        }
                    }
                }
            }
            this.current = 0;
            return;
        }

        if (!this.mc.interactionManager.getCurrentGameMode().isCreative()) {
            this.mc.getNetworkHandler().sendChatCommand("gmc");
        }

        final String username = this.mc.session.getUsername();
        if (this.mc.player.getPermissionLevel() < 4) {
            this.mc.getNetworkHandler().sendChatCommand("op " + username);
        }

        final List<String> commands = new ArrayList<>();

        if (this.destroyCmdBlocks.getValue()) {
            commands.add("setblock ~ ~ ~ minecraft:sponge");
        } else {
            final String target = this.target.getValue();
            if (!this.targetAllPlayers.getValue() && !target.isEmpty()) {
                commands.addAll(Arrays.asList(
                        "essentials:mute " + target,
                        "deop " + target,
                        "gamemode adventure " + target,
                        "de",
                        "clear " + target
                ));
            } else {
                final List<PlayerListEntry> players = this.mc.getNetworkHandler().getPlayerList().stream().filter(p -> !p.getProfile().getName().equals(username)).toList();
                if (!players.isEmpty()) {
                    final String randomPlayer = players.get(RandomUtils.randomIndex(players.size())).getProfile().getName();
                    commands.addAll(Arrays.asList(
                            "deop @a[name=!" + username + "]",
                            "gamemode adventure @a[name=!" + username + "]",
                            "clear @a[name=!" + username + "]",
                            "essentials:mute " + randomPlayer,
                            "execute as @a[name=!" + username + "] run playsound minecraft:ui.toast.challenge_complete block @a[name=!" + username + "] ~ ~ ~ 100 1",
                            "de",
                            "effect give @a[name=!" + username + "] minecraft:darkness infinite 255 true",
                            "sudo " + randomPlayer + " god off",
                            "sudo " + randomPlayer + " v off",
                            "sudo " + randomPlayer + " suicide"
                    ));
                }
            }
        }

        if (!this.commandBlocks.isEmpty() && !commands.isEmpty()) {
            this.mc.getNetworkHandler().getConnection().send(new UpdateCommandBlockC2SPacket(
                    this.commandBlocks.get(this.current),
                    commands.get(RandomUtils.randomIndex(commands.size())),
                    CommandBlockBlockEntity.Type.AUTO,
                    false,
                    false,
                    true
            ));
        }

        if (this.current < this.commandBlocks.size() - 1) this.current++;
        else this.current = 0;

        if (this.mc.player.age % this.commandBlockRescanDelay.getValue() == 0) {
            this.commandBlocks.clear();
        }
    }

}
