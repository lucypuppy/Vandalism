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
import de.nekosarekawaii.vandalism.event.game.BlockStateListener;
import de.nekosarekawaii.vandalism.event.game.BlockStateUpdateListener;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldChunk.class)
public abstract class MixinWorldChunk {

    @Inject(method = "getBlockState", at = @At("RETURN"))
    private void callBlockStateEvent(final BlockPos pos, final CallbackInfoReturnable<BlockState> cir) {
        Vandalism.getInstance().getEventSystem().callExceptionally(BlockStateListener.BlockStateEvent.ID, new BlockStateListener.BlockStateEvent(pos, cir.getReturnValue()));
    }

    @Inject(method = "setBlockState", at = @At("RETURN"))
    private void callBlockStateUpdateEvent(final BlockPos pos, final BlockState state, final boolean moved, final CallbackInfoReturnable<BlockState> cir) {
        Vandalism.getInstance().getEventSystem().callExceptionally(BlockStateUpdateListener.BlockStateUpdateEvent.ID, new BlockStateUpdateListener.BlockStateUpdateEvent(pos, state, cir.getReturnValue()));
    }

}
