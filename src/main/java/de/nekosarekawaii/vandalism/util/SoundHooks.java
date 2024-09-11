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

package de.nekosarekawaii.vandalism.util;

import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundHooks {

    private static final Identifier MODULE_ACTIVATE_SOUND = Identifier.of(FabricBootstrap.MOD_ID + ":module_activate");
    private static final SoundEvent MODULE_ACTIVATE_SOUND_EVENT = SoundEvent.of(MODULE_ACTIVATE_SOUND);

    private static final Identifier MODULE_DEACTIVATE_SOUND = Identifier.of(FabricBootstrap.MOD_ID + ":module_deactivate");
    private static final SoundEvent MODULE_DEACTIVATE_SOUND_EVENT = SoundEvent.of(MODULE_DEACTIVATE_SOUND);

    public static void register() {
        Registry.register(Registries.SOUND_EVENT, MODULE_ACTIVATE_SOUND, MODULE_ACTIVATE_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, MODULE_DEACTIVATE_SOUND, MODULE_DEACTIVATE_SOUND_EVENT);
    }

    public static void playModuleActivate() {
        playSound(MODULE_ACTIVATE_SOUND_EVENT);
    }

    public static void playModuleDeactivate() {
        playSound(MODULE_DEACTIVATE_SOUND_EVENT);
    }

    private static void playSound(final SoundEvent event) {
        final ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        world.playSound(player, player.getBlockPos(), event, SoundCategory.BLOCKS, 0.5f, 1f);
    }

}
