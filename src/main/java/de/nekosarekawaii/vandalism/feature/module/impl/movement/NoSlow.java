/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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
import de.nekosarekawaii.vandalism.base.event.normal.player.PlayerSlowdownListener;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;

public class NoSlow extends AbstractModule implements PlayerSlowdownListener {

    private final ValueGroup swordSlowDown = new ValueGroup(this, "Sword", "Sword Slowdown settings.");

    private final FloatValue swordForwardMultiplier = new FloatValue(
            this.swordSlowDown,
            "Forward",
            "Forward slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final FloatValue swordSidewaysMultiplier = new FloatValue(
            this.swordSlowDown,
            "Sideways",
            "Sideways slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final ValueGroup bowSlowDown = new ValueGroup(this, "Bow", "Bow Slowdown settings.");

    private final FloatValue bowForwardMultiplier = new FloatValue(
            this.bowSlowDown,
            "Forward",
            "Forward slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final FloatValue bowSidewaysMultiplier = new FloatValue(
            this.bowSlowDown,
            "Sideways",
            "Sideways slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final ValueGroup foodSlowDown = new ValueGroup(this, "Food", "Food Slowdown settings.");

    private final FloatValue foodForwardMultiplier = new FloatValue(
            this.foodSlowDown,
            "Forward",
            "Forward slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final FloatValue foodSidewaysMultiplier = new FloatValue(
            this.foodSlowDown,
            "Sideways",
            "Sideways slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final ValueGroup sneakSlowDown = new ValueGroup(this, "Sneak", "Sneak Slowdown settings.");

    private final FloatValue sneakForwardMultiplier = new FloatValue(
            this.sneakSlowDown,
            "Forward",
            "Forward slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final FloatValue sneakSidewaysMultiplier = new FloatValue(
            this.sneakSlowDown,
            "Sideways",
            "Sideways slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    public NoSlow() {
        super(
                "No Slow",
                "Modifies the slowdown in certain conditions.",
                Category.MOVEMENT
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(PlayerSlowdownEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerSlowdownEvent.ID, this);
    }


    @Override
    public void onSlowdown(final PlayerSlowdownEvent event) {
        final float forwardMultiplier = getForwardMultiplier();
        final float sidewaysMultiplier = getSidewaysMultiplier();

        if (forwardMultiplier > 0.0f) {
            event.movementForward = forwardMultiplier;
        }

        if (sidewaysMultiplier > 0.0f) {
            event.movementSideways = sidewaysMultiplier;
        }
    }

    private float getForwardMultiplier() {
        if (!this.mc.player.getActiveItem().isEmpty()) {
            final Item item = this.mc.player.getActiveItem().getItem();

            if (item instanceof SwordItem) {
                return this.swordForwardMultiplier.getValue();
            }

            if (item == Items.BOW || item == Items.CROSSBOW) {
                return this.bowForwardMultiplier.getValue();
            }

            if (item.isFood()) {
                return this.foodForwardMultiplier.getValue();
            }
        }

        if (this.mc.player.isSneaking()) {
            return this.sneakForwardMultiplier.getValue();
        }

        return -1.0f;
    }

    private float getSidewaysMultiplier() {
        if (!this.mc.player.getActiveItem().isEmpty()) {
            final Item item = this.mc.player.getActiveItem().getItem();

            if (item instanceof SwordItem) {
                return this.swordSidewaysMultiplier.getValue();
            }

            if (item == Items.BOW || item == Items.CROSSBOW) {
                return this.bowSidewaysMultiplier.getValue();
            }

            if (item.isFood()) {
                return this.foodSidewaysMultiplier.getValue();
            }
        }

        if (this.mc.player.isSneaking()) {
            return this.sneakSidewaysMultiplier.getValue();
        }

        return -1.0f;
    }

}
