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

package de.nekosarekawaii.vandalism.feature.module.impl.movement;

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import de.florianmichael.viafabricplus.protocoltranslator.ProtocolTranslator;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.player.CanSprintListener;
import de.nekosarekawaii.vandalism.event.player.PlayerSlowdownListener;
import de.nekosarekawaii.vandalism.event.player.ShouldSlowdownListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.*;

public class NoSlowModule extends Module implements PlayerSlowdownListener, CanSprintListener, ShouldSlowdownListener {

    public final BooleanValue noHitSlowdown = new BooleanValue(
            this,
            "No Hit Slowdown",
            "Disables slowdown when hitting entities.",
            true
    );

    private final BooleanValue forceHungerSprint = new BooleanValue(
            this,
            "Force Hunger Sprint",
            "Forces sprinting when the player is hungry.",
            true
    );

    public final BooleanValue modifyBlockSlowdown = new BooleanValue(
            this,
            "Modify Block Slowdown",
            "Modifies the slowdown when walking over blocks like soul sand and cobwebs.",
            true
    );

    public final FloatValue blockMultiplier = new FloatValue(
            this,
            "Block Multiplier",
            "Block slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    ).visibleCondition(this.modifyBlockSlowdown::getValue);

    private final BooleanValue modifyFoodSlowdown = new BooleanValue(
            this,
            "Modify Food Slowdown",
            "Modifies the slowdown when eating food.",
            true
    );

    private final FloatValue foodForwardMultiplier = new FloatValue(
            this,
            "Food Forward Multiplier",
            "Food forward slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    ).visibleCondition(this.modifyFoodSlowdown::getValue);

    private final FloatValue foodSidewaysMultiplier = new FloatValue(
            this,
            "Food Sideways Multiplier",
            "Food sideways slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    ).visibleCondition(this.modifyFoodSlowdown::getValue);

    private final BooleanValue modifyPotionSlowdown = new BooleanValue(
            this,
            "Modify Potion Slowdown",
            "Modifies the slowdown when drinking potions.",
            true
    );

    private final FloatValue potionForwardMultiplier = new FloatValue(
            this,
            "Potion Forward Multiplier",
            "Potion forward slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    ).visibleCondition(this.modifyPotionSlowdown::getValue);

    private final FloatValue potionSidewaysMultiplier = new FloatValue(
            this,
            "Potion Sideways Multiplier",
            "Potion sideways slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    ).visibleCondition(this.modifyPotionSlowdown::getValue);

    private final BooleanValue modifyShieldSlowdown = new BooleanValue(
            this,
            "Modify Shield Slowdown",
            "Modifies the slowdown when using a shield.",
            true
    ).visibleCondition(() -> ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_8));

    private final FloatValue shieldForwardMultiplier = new FloatValue(
            this,
            "Shield Forward Multiplier",
            "Shield forward slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    ).visibleCondition(() -> ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_8) && this.modifyShieldSlowdown.getValue());

    private final FloatValue shieldSidewaysMultiplier = new FloatValue(
            this,
            "Shield Sideways Multiplier",
            "Shield sideways slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    ).visibleCondition(() -> ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_8) && this.modifyShieldSlowdown.getValue());

    private final BooleanValue modifySwordSlowdown = new BooleanValue(
            this,
            "Modify Sword Slowdown",
            "Modifies the slowdown when using a sword.",
            true
    ).visibleCondition(() -> ProtocolTranslator.getTargetVersion().olderThan(ProtocolVersion.v1_9));

    private final FloatValue swordForwardMultiplier = new FloatValue(
            this,
            "Sword Forward Multiplier",
            "Sword forward slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    ).visibleCondition(() -> ProtocolTranslator.getTargetVersion().olderThan(ProtocolVersion.v1_9) && this.modifySwordSlowdown.getValue());

    private final FloatValue swordSidewaysMultiplier = new FloatValue(
            this,
            "Sword Sideways Multiplier",
            "Sword sideways slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    ).visibleCondition(() -> ProtocolTranslator.getTargetVersion().olderThan(ProtocolVersion.v1_9) && this.modifySwordSlowdown.getValue());

    private final BooleanValue modifyTridentSlowdown = new BooleanValue(
            this,
            "Modify Trident Slowdown",
            "Modifies the slowdown when using a trident.",
            true
    ).visibleCondition(() -> ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_12));

    private final FloatValue tridentForwardMultiplier = new FloatValue(
            this,
            "Trident Forward Multiplier",
            "Trident forward slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    ).visibleCondition(() -> ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_12) && this.modifyTridentSlowdown.getValue());

    private final FloatValue tridentSidewaysMultiplier = new FloatValue(
            this,
            "Trident Sideways Multiplier",
            "Trident sideways slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    ).visibleCondition(() -> ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_12) && this.modifyTridentSlowdown.getValue());

    private final BooleanValue modifyBowSlowdown = new BooleanValue(
            this,
            "Modify Bow Slowdown",
            "Modifies the slowdown when using a bow.",
            true
    );

    private final FloatValue bowForwardMultiplier = new FloatValue(
            this,
            "Bow Forward Multiplier",
            "Bow forward slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    ).visibleCondition(this.modifyBowSlowdown::getValue);

    private final FloatValue bowSidewaysMultiplier = new FloatValue(
            this,
            "Bow Sideways Multiplier",
            "Bow sideways slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    ).visibleCondition(this.modifyBowSlowdown::getValue);

    private final BooleanValue modifySneakSlowdown = new BooleanValue(
            this,
            "Modify Sneak Slowdown",
            "Modifies the slowdown when sneaking.",
            true
    );

    private final FloatValue sneakForwardMultiplier = new FloatValue(
            this,
            "Sneak Forward Multiplier",
            "Sneak forward slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    ).visibleCondition(this.modifySneakSlowdown::getValue);

    private final FloatValue sneakSidewaysMultiplier = new FloatValue(
            this,
            "Sneak Sideways Multiplier",
            "Sneak sideways slowdown multiplier.",
            1.0f,
            0.2f,
            1.0f
    ).visibleCondition(this.modifySneakSlowdown::getValue);

    public NoSlowModule() {
        super(
                "No Slow",
                "Modifies the slowdown in certain conditions.",
                Category.MOVEMENT
        );
    }

    private float getForwardMultiplier() {
        final float defaultValue = -1.0f;
        final ItemStack stack = mc.player.getActiveItem();
        if (!stack.isEmpty()) {
            final Item item = mc.player.getActiveItem().getItem();
            if (item.getComponents().contains(DataComponentTypes.FOOD) && this.modifyFoodSlowdown.getValue()) {
                return this.foodForwardMultiplier.getValue();
            }
            if (item instanceof PotionItem && this.modifyPotionSlowdown.getValue()) {
                return this.potionForwardMultiplier.getValue();
            }
            if (ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_8)) {
                if (item instanceof ShieldItem && this.modifyShieldSlowdown.getValue()) {
                    return this.shieldForwardMultiplier.getValue();
                }
            }
            if (ProtocolTranslator.getTargetVersion().olderThan(ProtocolVersion.v1_9)) {
                if (item instanceof SwordItem && this.modifySwordSlowdown.getValue()) {
                    return this.swordForwardMultiplier.getValue();
                }
            }
            if (ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_12_2)) {
                if (item instanceof TridentItem && this.modifyTridentSlowdown.getValue()) {
                    return this.tridentForwardMultiplier.getValue();
                }
            }
            if (item instanceof BowItem && this.modifyBowSlowdown.getValue()) {
                return this.bowForwardMultiplier.getValue();
            }
        }
        if (mc.player.isSneaking() && this.modifySneakSlowdown.getValue()) {
            return this.sneakForwardMultiplier.getValue();
        }
        return defaultValue;
    }

    private float getSidewaysMultiplier() {
        final float defaultValue = -1.0f;
        final ItemStack stack = mc.player.getActiveItem();
        if (!stack.isEmpty()) {
            final Item item = mc.player.getActiveItem().getItem();
            if (item.getComponents().contains(DataComponentTypes.FOOD) && this.modifyFoodSlowdown.getValue()) {
                return this.foodSidewaysMultiplier.getValue();
            }
            if (item instanceof PotionItem && this.modifyPotionSlowdown.getValue()) {
                return this.potionSidewaysMultiplier.getValue();
            }
            if (ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_8)) {
                if (item instanceof ShieldItem && this.modifyShieldSlowdown.getValue()) {
                    return this.shieldSidewaysMultiplier.getValue();
                }
            }
            if (ProtocolTranslator.getTargetVersion().olderThan(ProtocolVersion.v1_9)) {
                if (item instanceof SwordItem && this.modifySwordSlowdown.getValue()) {
                    return this.swordSidewaysMultiplier.getValue();
                }
            }
            if (ProtocolTranslator.getTargetVersion().newerThan(ProtocolVersion.v1_12_2)) {
                if (item instanceof TridentItem && this.modifyTridentSlowdown.getValue()) {
                    return this.tridentSidewaysMultiplier.getValue();
                }
            }
            if (item instanceof BowItem && this.modifyBowSlowdown.getValue()) {
                return this.bowSidewaysMultiplier.getValue();
            }
        }
        if (mc.player.isSneaking() && this.modifySneakSlowdown.getValue()) {
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
