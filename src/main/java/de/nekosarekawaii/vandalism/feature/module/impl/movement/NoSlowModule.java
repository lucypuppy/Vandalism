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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.event.player.CanSprintListener;
import de.nekosarekawaii.vandalism.event.player.PlayerSlowdownListener;
import de.nekosarekawaii.vandalism.event.player.ShouldSlowdownListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;

public class NoSlowModule extends Module implements PlayerSlowdownListener, CanSprintListener, ShouldSlowdownListener {

    private static final String FORWARD = "Forward";

    private static final String SIDEWAYS = "Sideways";

    private final BooleanValue forceHungerSprint = new BooleanValue(
            this,
            "Force Hunger Sprint",
            "Forces sprinting when the player is hungry.",
            true
    );

    private final ValueGroup foodSlowDown = new ValueGroup(this, "Food", "Food Slowdown settings.");

    private final FloatValue foodForwardMultiplier = new FloatValue(
            this.foodSlowDown,
            FORWARD,
            FORWARD + " slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final FloatValue foodSidewaysMultiplier = new FloatValue(
            this.foodSlowDown,
            SIDEWAYS,
            SIDEWAYS + " slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final ValueGroup potionSlowDown = new ValueGroup(this, "Potion", "Potion Slowdown settings.");

    private final FloatValue potionForwardMultiplier = new FloatValue(
            this.potionSlowDown,
            FORWARD,
            FORWARD + " slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final FloatValue potionSidewaysMultiplier = new FloatValue(
            this.potionSlowDown,
            SIDEWAYS,
            SIDEWAYS + " slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final ValueGroup shieldSlowDown = new ValueGroup(this, "Shield", "Shield Slowdown settings.").visibleCondition(() ->
            ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_8));

    private final FloatValue shieldForwardMultiplier = new FloatValue(
            this.shieldSlowDown,
            FORWARD,
            FORWARD + " slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final FloatValue shieldSidewaysMultiplier = new FloatValue(
            this.shieldSlowDown,
            SIDEWAYS,
            SIDEWAYS + " slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final ValueGroup swordSlowDown = new ValueGroup(this, "Sword", "Sword Slowdown settings.").visibleCondition(() ->
            ProtocolTranslator.getTargetVersion().olderThan(ProtocolVersion.v1_9));

    private final FloatValue swordForwardMultiplier = new FloatValue(
            this.swordSlowDown,
            FORWARD,
            FORWARD + " slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final FloatValue swordSidewaysMultiplier = new FloatValue(
            this.swordSlowDown,
            SIDEWAYS,
            SIDEWAYS + " slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final ValueGroup tridentSlowDown = new ValueGroup(this, "Trident", "Trident Slowdown settings.").visibleCondition(() ->
            ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_12));

    private final FloatValue tridentForwardMultiplier = new FloatValue(
            this.tridentSlowDown,
            FORWARD,
            FORWARD + " slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final FloatValue tridentSidewaysMultiplier = new FloatValue(
            this.tridentSlowDown,
            SIDEWAYS,
            SIDEWAYS + " slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final ValueGroup bowSlowDown = new ValueGroup(this, "Bow", "Bow Slowdown settings.");

    private final FloatValue bowForwardMultiplier = new FloatValue(
            this.bowSlowDown,
            FORWARD,
            FORWARD + " slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final FloatValue bowSidewaysMultiplier = new FloatValue(
            this.bowSlowDown,
            SIDEWAYS,
            SIDEWAYS + " slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final ValueGroup sneakSlowDown = new ValueGroup(this, "Sneak", "Sneak Slowdown settings.");

    private final FloatValue sneakForwardMultiplier = new FloatValue(
            this.sneakSlowDown,
            FORWARD,
            FORWARD + " slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    private final FloatValue sneakSidewaysMultiplier = new FloatValue(
            this.sneakSlowDown,
            SIDEWAYS,
            SIDEWAYS + " slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    );

    public NoSlowModule() {
        super(
                "No Slow",
                "Modifies the slowdown in certain conditions.",
                Category.MOVEMENT
        );
    }

    private float getForwardMultiplier() {
        final float defaultValue = -1.0f;
        final ItemStack stack = this.mc.player.getActiveItem();
        if (!stack.isEmpty()) {
            final Item item = this.mc.player.getActiveItem().getItem();
            if (item.getComponents().contains(DataComponentTypes.FOOD)) {
                return this.foodForwardMultiplier.getValue();
            }
            if (item instanceof PotionItem) {
                return this.potionForwardMultiplier.getValue();
            }
            if (ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_8)) {
                if (item instanceof ShieldItem) {
                    return this.shieldForwardMultiplier.getValue();
                }
            }
            if (ProtocolTranslator.getTargetVersion().olderThan(ProtocolVersion.v1_9)) {
                if (item instanceof SwordItem) {
                    return this.swordForwardMultiplier.getValue();
                }
            }
            if (ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_12_2)) {
                if (item instanceof TridentItem) {
                    return this.tridentForwardMultiplier.getValue();
                }
            }
            if (item instanceof BowItem) {
                return this.bowForwardMultiplier.getValue();
            }
        }
        if (this.mc.player.isSneaking()) {
            return this.sneakForwardMultiplier.getValue();
        }
        return defaultValue;
    }

    private float getSidewaysMultiplier() {
        final float defaultValue = -1.0f;
        final ItemStack stack = this.mc.player.getActiveItem();
        if (!stack.isEmpty()) {
            final Item item = this.mc.player.getActiveItem().getItem();
            if (item.getComponents().contains(DataComponentTypes.FOOD)) {
                return this.foodSidewaysMultiplier.getValue();
            }
            if (item instanceof PotionItem) {
                return this.potionSidewaysMultiplier.getValue();
            }
            if (ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_8)) {
                if (item instanceof ShieldItem) {
                    return this.shieldSidewaysMultiplier.getValue();
                }
            }
            if (ProtocolTranslator.getTargetVersion().olderThan(ProtocolVersion.v1_9)) {
                if (item instanceof SwordItem) {
                    return this.swordSidewaysMultiplier.getValue();
                }
            }
            if (ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_12_2)) {
                if (item instanceof TridentItem) {
                    return this.tridentSidewaysMultiplier.getValue();
                }
            }
            if (item instanceof BowItem) {
                return this.bowSidewaysMultiplier.getValue();
            }
        }
        if (this.mc.player.isSneaking()) {
            return this.sneakSidewaysMultiplier.getValue();
        }
        return defaultValue;
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerSlowdownEvent.ID, CanSprintEvent.ID, ShouldSlowdownEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerSlowdownEvent.ID, CanSprintEvent.ID, ShouldSlowdownEvent.ID);
    }

    @Override
    public void onSlowdown(final PlayerSlowdownEvent event) {
        final float forwardMultiplier = this.getForwardMultiplier();
        final float sidewaysMultiplier = this.getSidewaysMultiplier();
        if (forwardMultiplier > 0.0f) {
            event.movementForward = forwardMultiplier;
        }
        if (sidewaysMultiplier > 0.0f) {
            event.movementSideways = sidewaysMultiplier;
        }
    }

    @Override
    public void onCanSprint(final CanSprintEvent event) {
        if (this.forceHungerSprint.getValue()) {
            event.canSprint = true;
        }
    }

    @Override
    public void onShouldSlowdown(final ShouldSlowdownEvent event) {
        event.shouldSlowdown = false;
    }

}
