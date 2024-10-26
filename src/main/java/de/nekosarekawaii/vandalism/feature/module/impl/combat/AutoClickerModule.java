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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.game.MouseInputListener;
import de.nekosarekawaii.vandalism.feature.module.template.module.ClickerModule;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public class AutoClickerModule extends ClickerModule implements MouseInputListener {

    private final BooleanValue onlyWhenHolding = new BooleanValue(
            this,
            "Only When Holding",
            "Only click when the attack key is pressed.",
            true
    );

    private final BooleanValue blockBreaking = new BooleanValue(
            this,
            "Block Breaking",
            "Allows you to break blocks instead of clicking on them.",
            false
    );

    private boolean isPressed;

    public AutoClickerModule() {
        super(
                "Auto Clicker",
                "Automatically uses the attack / block break key.",
                Category.COMBAT
        );
    }

    @Override
    protected void onActivate() {
        super.onActivate();
        Vandalism.getInstance().getEventSystem().subscribe(this, MouseEvent.ID);
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, MouseEvent.ID);
        super.onDeactivate();
    }

    @Override
    public void onClick() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        mc.options.attackKey.setPressed(this.checkBreakable() && this.isPressed);

        if (mc.options.attackKey.isPressed()) {
            return;
        }

        if (!this.onlyWhenHolding.getValue() || this.isPressed) {
            mc.doAttack();
        }
    }

    @Override
    public void onFailClick() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        mc.options.attackKey.setPressed(this.checkBreakable() && this.isPressed);

        if (mc.options.attackKey.isPressed()) {
            return;
        }

        if (!this.onlyWhenHolding.getValue() || this.isPressed) {
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }

    @Override
    public void onMouse(final MouseEvent event) {
        if (mc.player == null || mc.world == null || mc.currentScreen != null) {
            return;
        }
        if (event.type == Type.BUTTON && event.button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (event.action == GLFW.GLFW_PRESS) {
                this.isPressed = true;
                event.setCancelled(!this.checkBreakable());
            } else if (event.action == GLFW.GLFW_RELEASE) {
                this.isPressed = false;
            }
        }
    }

    private boolean checkBreakable() {
        return this.blockBreaking.getValue() && mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.BLOCK && Math.sqrt(mc.crosshairTarget.squaredDistanceTo(mc.player)) <= mc.player.getBlockInteractionRange();
    }

}
