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

package de.nekosarekawaii.vandalism.injection.mixins.event;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.player.MoveInputListener;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class MixinKeyboardInput extends Input {

    @Inject(method = "tick", at = @At("RETURN"))
    private void callMoveInputListener(final boolean slowDown, final float slowDownFactor, final CallbackInfo ci) {
        final MoveInputListener.MoveInputEvent event = new MoveInputListener.MoveInputEvent(this.movementForward, this.movementSideways, this.jumping, this.sneaking, slowDown, slowDownFactor);
        Vandalism.getInstance().getEventSystem().callExceptionally(MoveInputListener.MoveInputEvent.ID, event);
        if (event.isCancelled()) {
            this.movementForward = 0;
            this.movementSideways = 0;
            this.jumping = false;
            this.sneaking = false;
            return;
        }
        this.movementForward = event.movementForward;
        this.movementSideways = event.movementSideways;
        this.jumping = event.jumping;
        this.sneaking = event.sneaking;
    }

}
