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
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
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

    private static final Identifier GLEICHMITTE_RICHTIG_SAUER_SOUND = Identifier.of(FabricBootstrap.MOD_ID + ":gleichmitte_richtig_sauer");
    private static final SoundEvent GLEICHMITTE_RICHTIG_SAUER_SOUND_EVENT = SoundEvent.of(GLEICHMITTE_RICHTIG_SAUER_SOUND);

    private static final Identifier GLEICHMITTE_SCHEISSE_MAN_SOUND = Identifier.of(FabricBootstrap.MOD_ID + ":gleichmitte_scheisse_man");
    private static final SoundEvent GLEICHMITTE_SCHEISSE_MAN_SOUND_EVENT = SoundEvent.of(GLEICHMITTE_SCHEISSE_MAN_SOUND);

    private static final Identifier GLEICHMITTE_DU_HURENSOHN_SOUND = Identifier.of(FabricBootstrap.MOD_ID + ":gleichmitte_du_hurensohn");
    private static final SoundEvent GLEICHMITTE_DU_HURENSOHN_SOUND_EVENT = SoundEvent.of(GLEICHMITTE_DU_HURENSOHN_SOUND);

    private static final Identifier GLEICHMITTE_FICK_DICH_SOUND = Identifier.of(FabricBootstrap.MOD_ID + ":gleichmitte_fick_dich");
    private static final SoundEvent GLEICHMITTE_FICK_DICH_SOUND_EVENT = SoundEvent.of(GLEICHMITTE_FICK_DICH_SOUND);

    private static final Identifier GLEICHMITTE_ZUFRIEDEN_SOUND = Identifier.of(FabricBootstrap.MOD_ID + ":gleichmitte_zufrieden");
    private static final SoundEvent GLEICHMITTE_ZUFRIEDEN_SOUND_EVENT = SoundEvent.of(GLEICHMITTE_ZUFRIEDEN_SOUND);

    public static void register() {
        Registry.register(Registries.SOUND_EVENT, MODULE_ACTIVATE_SOUND, MODULE_ACTIVATE_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, MODULE_DEACTIVATE_SOUND, MODULE_DEACTIVATE_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, GLEICHMITTE_RICHTIG_SAUER_SOUND, GLEICHMITTE_RICHTIG_SAUER_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, GLEICHMITTE_SCHEISSE_MAN_SOUND, GLEICHMITTE_SCHEISSE_MAN_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, GLEICHMITTE_SCHEISSE_MAN_SOUND, GLEICHMITTE_DU_HURENSOHN_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, GLEICHMITTE_FICK_DICH_SOUND, GLEICHMITTE_FICK_DICH_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, GLEICHMITTE_ZUFRIEDEN_SOUND, GLEICHMITTE_ZUFRIEDEN_SOUND_EVENT);
    }

    public static void playModuleActivate() {
        playInGameSound(MODULE_ACTIVATE_SOUND_EVENT);
    }

    public static void playModuleDeactivate() {
        playInGameSound(MODULE_DEACTIVATE_SOUND_EVENT);
    }

    public static void playGleichMitteRichtigSauer() {
        playSound(GLEICHMITTE_RICHTIG_SAUER_SOUND_EVENT);
    }

    public static void playGleichMitteScheisseMan() {
        playSound(GLEICHMITTE_SCHEISSE_MAN_SOUND_EVENT);
    }

    public static void playGleichMitteDuHurensohn() {
        playSound(GLEICHMITTE_DU_HURENSOHN_SOUND_EVENT);
    }

    public static void playGleichMitteFickDich() {
        playSound(GLEICHMITTE_FICK_DICH_SOUND_EVENT);
    }

    public static void playGleichMitteZufrieden() {
        playSound(GLEICHMITTE_ZUFRIEDEN_SOUND_EVENT);
    }

    private static void playSound(final SoundEvent soundEvent) {
        final SoundManager soundManager = MinecraftClient.getInstance().getSoundManager();
        if (soundManager == null) return;
        soundManager.play(PositionedSoundInstance.master(soundEvent, 1f, 0.5f));
    }

    private static void playInGameSound(final SoundEvent event) {
        final ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null) return;
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        world.playSound(player, player.getBlockPos(), event, SoundCategory.BLOCKS, 0.5f, 1f);
    }

}
