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

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.normal.player.CanSprintListener;
import de.nekosarekawaii.vandalism.event.normal.player.PlayerSlowdownListener;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import net.minecraft.item.*;

import java.util.ArrayList;
import java.util.List;

public class NoSlowModule extends AbstractModule implements PlayerSlowdownListener, CanSprintListener {

    private static final String FORWARD = "Forward";

    private static final String SIDEWAYS = "Sideways";

    private static final List<Item> ITEMS = new ArrayList<>();

    private final BooleanValue forceHungerSprint = new BooleanValue(
            this,
            "Force Hunger Sprint",
            "Forces sprinting when the player is hungry.",
            false
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

    private final ValueGroup swordSlowDown = new ValueGroup(this, "Sword", "Sword Slowdown settings.").visibleCondition(() -> {
        return ProtocolTranslator.getTargetVersion().olderThan(ProtocolVersion.v1_9);
    });

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
        ITEMS.add(Items.TRIDENT);
        ITEMS.add(Items.SHIELD);
        for (final Item item : ITEMS) {
            final String name = item.toString().substring(0, 1).toUpperCase() + item.toString().substring(1);
            final ValueGroup group = new ValueGroup(this, name, name + " settings.");
            if (item instanceof ShieldItem) {
                group.visibleCondition(() -> ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_8));
            } else if (item instanceof TridentItem) {
                group.visibleCondition(() -> ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_12));
            }
            new FloatValue(
                    group,
                    "Forward",
                    "Forward slowdown multiplier.",
                    1.0f,
                    0.2f,
                    1.0f
            );
            new FloatValue(
                    group,
                    "Sideways",
                    "Sideways slowdown multiplier.",
                    1.0f,
                    0.2f,
                    1.0f
            );
        }
    }

    private float getValue(final float defaultValue, final Item item, final String name) {
        for (final Value<?> value : this.getValues()) {
            if (value instanceof final ValueGroup valueGroup) {
                if (valueGroup.isVisible().getAsBoolean()) {
                    if (valueGroup.getName().equals(item.getTranslationKey())) {
                        for (final Value<?> value1 : valueGroup.getValues()) {
                            if (value1 instanceof final FloatValue floatValue) {
                                if (value1.toString().equalsIgnoreCase(name)) {
                                    return floatValue.getValue();
                                }
                            }
                        }
                    }
                }
            }
        }
        return defaultValue;
    }

    private float getForwardMultiplier() {
        final float defaultValue = -1.0f;
        final ItemStack stack = this.mc.player.getActiveItem();
        if (!stack.isEmpty()) {
            final Item item = this.mc.player.getActiveItem().getItem();
            if (ITEMS.contains(item)) {
                return this.getValue(defaultValue, item, FORWARD);
            }
            if (item.isFood()) {
                return this.foodForwardMultiplier.getValue();
            }
            if (item instanceof PotionItem) {
                return this.potionForwardMultiplier.getValue();
            }
            if (ProtocolTranslator.getTargetVersion().olderThan(ProtocolVersion.v1_9)) {
                if (item instanceof SwordItem) {
                    return this.swordForwardMultiplier.getValue();
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
            if (ITEMS.contains(item)) {
                return this.getValue(defaultValue, item, SIDEWAYS);
            }
            if (item.isFood()) {
                return this.foodSidewaysMultiplier.getValue();
            }
            if (item instanceof PotionItem) {
                return this.potionSidewaysMultiplier.getValue();
            }
            if (ProtocolTranslator.getTargetVersion().olderThan(ProtocolVersion.v1_9)) {
                if (item instanceof SwordItem) {
                    return this.swordSidewaysMultiplier.getValue();
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

    @Override
    public void onCanSprint(CanSprintEvent event) {
        if (forceHungerSprint.getValue()) {
            event.canSprint = true;
        }
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerSlowdownEvent.ID, CanSprintEvent.ID);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerSlowdownEvent.ID, CanSprintEvent.ID);
    }
}
