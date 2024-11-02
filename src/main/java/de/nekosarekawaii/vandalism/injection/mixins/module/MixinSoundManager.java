/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.SoundBlockerModule;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.TickableSoundInstance;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public abstract class MixinSoundManager {

    @Unique
    private void vandalism$cancelSoundCheck(final Identifier id, final CallbackInfo ci) {
        final SoundBlockerModule soundBlockerModule = Vandalism.getInstance().getModuleManager().getSoundBlockerModule();
        if (soundBlockerModule.isActive() && soundBlockerModule.blockedSounds.isSelected(id)) {
            ci.cancel();
        }
    }

    @Inject(method = "playNextTick", at = @At("HEAD"), cancellable = true)
    private void hookSoundBlocker(final TickableSoundInstance sound, final CallbackInfo ci) {
        vandalism$cancelSoundCheck(sound.getId(), ci);
    }

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    private void hookSoundBlocker(final SoundInstance sound, final CallbackInfo ci) {
        vandalism$cancelSoundCheck(sound.getId(), ci);
    }

    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;I)V", at = @At("HEAD"), cancellable = true)
    private void hookSoundBlocker(final SoundInstance sound, final int delay, final CallbackInfo ci) {
        vandalism$cancelSoundCheck(sound.getId(), ci);
    }

}
