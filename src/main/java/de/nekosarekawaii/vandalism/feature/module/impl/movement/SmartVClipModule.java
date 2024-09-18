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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.misc.KeyBindValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.event.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.MovementUtil;
import net.minecraft.block.AirBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

public class SmartVClipModule extends Module implements KeyboardInputListener {

    private final KeyBindValue upKey = new KeyBindValue(this, "Up Key", "The key that needs to be pressed to go upward.", GLFW.GLFW_KEY_UP);
    private final KeyBindValue downKey = new KeyBindValue(this, "Down Key", "The key that needs to be pressed to go downward.", GLFW.GLFW_KEY_DOWN);
    private final IntegerValue maxScanRange = new IntegerValue(this, "Max Scan Range", "The maximum range to port to.", 9, 3, 100);

    public SmartVClipModule() {
        super("Smart V Clip", "A automatic v-clip module.", Category.MOVEMENT);
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(KeyboardInputEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(KeyboardInputEvent.ID, this);
    }

    @Override
    public void onKeyInput(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action != GLFW.GLFW_PRESS) {
            return;
        }

        if (this.upKey.isPressed() || this.downKey.isPressed()) {
            final BlockPos oldPos = this.mc.player.getBlockPos().mutableCopy();

            for (int i = 1; i < this.maxScanRange.getValue(); i++) {
                final BlockPos safeBlockPos = oldPos.add(0, this.upKey.isPressed() ? i : (-(i + 1)), 0);
                final BlockPos airBlock1 = safeBlockPos.add(0, 1, 0);
                final BlockPos airBlock2 = safeBlockPos.add(0, 2, 0);

                final BlockState safeBlock = this.mc.world.getBlockState(safeBlockPos);
                final BlockState airBlock1State = this.mc.world.getBlockState(airBlock1);
                final BlockState airBlock2State = this.mc.world.getBlockState(airBlock2);

                if ((!(safeBlock.getBlock() instanceof AirBlock) && !(safeBlock.getBlock() instanceof FluidBlock)) && airBlock1State.isAir() && airBlock2State.isAir()) {
                    MovementUtil.bypassClip(
                            mc.player.getPos().getX(),
                            mc.player.getPos().getY(),
                            mc.player.getPos().getZ(),
                            safeBlockPos.getX() + 0.5,
                            safeBlockPos.getY() + 1,
                            safeBlockPos.getZ() + 0.5
                    );

                    break;
                }
            }
        }
    }

}
