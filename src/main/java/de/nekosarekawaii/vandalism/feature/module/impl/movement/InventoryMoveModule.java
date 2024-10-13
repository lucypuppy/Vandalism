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
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindowScreen;
import de.nekosarekawaii.vandalism.event.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemGroups;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class InventoryMoveModule extends Module implements PlayerUpdateListener, KeyboardInputListener {

    private final BooleanValue allowArrowKeyRotation = new BooleanValue(
            this,
            "Allow Arrow Key Rotation",
            "Allows you to rotate yourself with the arrow keys.",
            false
    );

    private final FloatValue rotationSpeed = new FloatValue(
            this,
            "Rotation Speed",
            "The speed of the rotation for the arrow keys.",
            2.0F,
            0.1F,
            10.0F
    ).visibleCondition(this.allowArrowKeyRotation::getValue);

    private final BooleanValue allowSprint = new BooleanValue(
            this,
            "Allow Sprint",
            "Allows you to sprint while being in a inventory.",
            true
    );

    private final BooleanValue allowJump = new BooleanValue(
            this,
            "Allow Jump",
            "Allows you to jump while being in a inventory.",
            false
    );

    private final BooleanValue allowSneak = new BooleanValue(
            this,
            "Allow Sneak",
            "Allows you to sneak while being in a inventory.",
            false
    );

    public InventoryMoveModule() {
        super(
                "Inventory Move",
                "Allows you to move while being in a inventory.",
                Category.MOVEMENT
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(
                this,
                PlayerUpdateEvent.ID,
                KeyboardInputEvent.ID
        );
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(
                this,
                PlayerUpdateEvent.ID,
                KeyboardInputEvent.ID
        );
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.mc.currentScreen instanceof HandledScreen<?>) {
            final GameOptions options = this.mc.options;
            final List<KeyBinding> keyBindings = new ArrayList<>();
            if (this.allowSprint.getValue()) {
                keyBindings.add(options.sprintKey);
            }
            if (this.allowJump.getValue()) {
                keyBindings.add(options.jumpKey);
            }
            if (this.allowSneak.getValue()) {
                keyBindings.add(options.sneakKey);
            }
            keyBindings.add(options.forwardKey);
            keyBindings.add(options.backKey);
            keyBindings.add(options.leftKey);
            keyBindings.add(options.rightKey);
            if (this.mc.currentScreen instanceof CreativeInventoryScreen) {
                if (CreativeInventoryScreen.selectedTab == ItemGroups.getSearchGroup()) {
                    for (final KeyBinding keyBinding : keyBindings) {
                        keyBinding.setPressed(false);
                    }
                    return;
                }
            }
            for (final KeyBinding keyBinding : keyBindings) {
                keyBinding.setPressed(InputUtil.isKeyPressed(this.mc.getWindow().getHandle(), keyBinding.boundKey.getCode()));
            }
        }
    }

    @Override
    public void onKeyInput(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (!this.allowArrowKeyRotation.getValue()) {
            return;
        }
        if (this.mc.player != null) {
            if (this.mc.currentScreen instanceof AbstractInventoryScreen<?> || this.mc.currentScreen instanceof ClientWindowScreen) {
                if (this.mc.currentScreen instanceof CreativeInventoryScreen) {
                    if (CreativeInventoryScreen.selectedTab == ItemGroups.getSearchGroup()) {
                        return;
                    }
                }
                final float value = this.rotationSpeed.getValue();
                switch (key) {
                    case GLFW.GLFW_KEY_UP -> {
                        float newPitch = this.mc.player.getPitch() - value;
                        if (newPitch > 90) newPitch = 90;
                        if (newPitch < -90) newPitch = -90;
                        this.mc.player.setPitch(newPitch);
                    }
                    case GLFW.GLFW_KEY_DOWN -> {
                        float newPitch = this.mc.player.getPitch() + value;
                        if (newPitch > 90) newPitch = 90;
                        if (newPitch < -90) newPitch = -90;
                        this.mc.player.setPitch(newPitch);
                    }
                    case GLFW.GLFW_KEY_LEFT -> this.mc.player.setYaw(this.mc.player.getYaw() - value);
                    case GLFW.GLFW_KEY_RIGHT -> this.mc.player.setYaw(this.mc.player.getYaw() + value);
                    default -> {
                    }
                }
            }
        }
    }

}
