/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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
import de.nekosarekawaii.vandalism.base.value.impl.number.BezierValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.event.normal.player.RotationListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.click.ClickType;
import de.nekosarekawaii.vandalism.util.click.Clicker;
import de.nekosarekawaii.vandalism.util.click.impl.BezierClicker;
import de.nekosarekawaii.vandalism.util.click.impl.BoxMuellerClicker;

public class AutoClickerModule extends AbstractModule implements PlayerUpdateListener, RotationListener {

    private final EnumModeValue<ClickType> clickType = new EnumModeValue<>(
            this,
            "Click Type",
            "The type of clicking.",
            ClickType.COOLDOWN,
            ClickType.values()
    ).onValueChange((oldValue, newValue) -> {
        oldValue.getClicker().setClickAction(aBoolean -> {
        });
        this.updateClicker(newValue.getClicker());
    });

    private final FloatValue std = new FloatValue(
            this,
            "Standard Deviation",
            "The standard deviation for the Box-Mueller clicker.",
            5.0f,
            1.0f,
            10.0f
    ).onValueChange((oldValue, newValue) -> this.updateClicker(this.clickType.getValue().getClicker()))
            .visibleCondition(() -> this.clickType.getValue() == ClickType.BOXMUELLER);

    private final FloatValue mean = new FloatValue(
            this,
            "Mean",
            "The mean for the Box-Mueller clicker.",
            15.0f,
            1.0f,
            30.0f
    ).onValueChange((oldValue, newValue) -> this.updateClicker(this.clickType.getValue().getClicker()))
            .visibleCondition(() -> this.clickType.getValue() == ClickType.BOXMUELLER);

    private final IntegerValue minCps = new IntegerValue(
            this,
            "Minimum CPS",
            "The minimum CPS for the Box-Mueller clicker.",
            10,
            1,
            20
    ).onValueChange((oldValue, newValue) -> this.updateClicker(this.clickType.getValue().getClicker()))
            .visibleCondition(() -> this.clickType.getValue() == ClickType.BOXMUELLER);

    private final IntegerValue maxCps = new IntegerValue(
            this,
            "Maximum CPS",
            "The maximum CPS for the Box-Mueller clicker.",
            20,
            1,
            30
    ).onValueChange((oldValue, newValue) -> this.updateClicker(this.clickType.getValue().getClicker()))
            .visibleCondition(() -> this.clickType.getValue() == ClickType.BOXMUELLER);

    private final BezierValue cpsBezier = new BezierValue(
            this,
            "CPS Bezier Curve",
            "The bezier curve for the CPS.",
            25.0f,
            17.0f,
            14.0f,
            25.0f,
            1.0f,
            25.0f
    ).visibleCondition(() -> this.clickType.getValue() == ClickType.BEZIER);

    private final IntegerValue updatePossibility = new IntegerValue(
            this,
            "Update Possibility",
            "The possibility of the CPS update.",
            80,
            0,
            100
    ).visibleCondition(() -> this.clickType.getValue() != ClickType.COOLDOWN);


    public AutoClickerModule() {
        super(
                "Auto Clicker",
                "Automatically uses the attack / block break key.",
                Category.COMBAT
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID, RotationEvent.ID);
        updateClicker(this.clickType.getValue().getClicker());
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID, RotationEvent.ID);
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (this.mc.options.attackKey.isPressed()) {
            this.clickType.getValue().getClicker().onUpdate();
        }
    }

    @Override
    public void onRotation(final RotationEvent event) {
        if (this.mc.options.attackKey.isPressed()) {
            this.clickType.getValue().getClicker().onRotate();
        }
    }

    private void updateClicker(final Clicker clicker) {
        if (clicker instanceof final BoxMuellerClicker boxMuellerClicker) {
            boxMuellerClicker.setStd(this.std.getValue());
            boxMuellerClicker.setMean(this.mean.getValue());
            boxMuellerClicker.setMinCps(this.minCps.getValue());
            boxMuellerClicker.setMaxCps(this.maxCps.getValue());
            boxMuellerClicker.setCpsUpdatePossibility(this.updatePossibility.getValue());
            boxMuellerClicker.setKillAuraModule(null);
        }

        if (clicker instanceof final BezierClicker bezierClicker) {
            bezierClicker.setBezierValue(this.cpsBezier);
            bezierClicker.setCpsUpdatePossibility(this.updatePossibility.getValue());
        }

        clicker.setClickAction(attack -> {
            if (attack) {
                this.mc.doAttack();
            }
        });
    }

}
