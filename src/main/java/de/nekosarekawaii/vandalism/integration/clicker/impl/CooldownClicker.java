package de.nekosarekawaii.vandalism.integration.clicker.impl;

import de.nekosarekawaii.vandalism.integration.clicker.Clicker;
import de.nekosarekawaii.vandalism.util.minecraft.TimerHack;
import net.minecraft.entity.attribute.EntityAttributes;

public class CooldownClicker extends Clicker {

    @Override
    public void onUpdate() {
        final float baseAttackDamage = (float) mc.player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);

        float additionalBaseDelayOffset = 0;

        if (TimerHack.getSpeed() > 1) {
            additionalBaseDelayOffset = -(TimerHack.getSpeed() - 1);
        }

        final float attackCooldown = mc.player.getAttackCooldownProgress(additionalBaseDelayOffset);
        final float finalAttackDamage = baseAttackDamage * (0.2F + attackCooldown * attackCooldown * 0.8F);

        if (finalAttackDamage >= 0.98) {
            this.clickAction.accept(true);
            this.mc.player.resetLastAttackedTicks();
        } else {
            this.clickAction.accept(false);
        }
    }


}
