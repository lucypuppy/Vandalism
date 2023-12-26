package de.nekosarekawaii.vandalism.util.minecraft;

import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Items;

public class CombatUtil implements MinecraftWrapper {

    public static boolean handleAttack(final boolean autoBlock) {
        float baseAttackDamage = (float) mc.player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);

            /*float enchantmentBonus;
            if (target instanceof LivingEntity) {
                enchantmentBonus = EnchantmentHelper.getAttackDamage(mc.player.getMainHandStack(), ((LivingEntity) target).getGroup());
            } else {
                enchantmentBonus = EnchantmentHelper.getAttackDamage(mc.player.getMainHandStack(), EntityGroup.DEFAULT);
            }*/

        float additionalBaseDelayOffset = 0;

        if (TimerHack.getSpeed() > 1) {
            additionalBaseDelayOffset = -(TimerHack.getSpeed() - 1);
        }

        float attackCooldown = mc.player.getAttackCooldownProgress(additionalBaseDelayOffset);
        baseAttackDamage *= 0.2F + attackCooldown * attackCooldown * 0.8F;

        // enchantmentBonus *= attackCooldown;

        if (baseAttackDamage >= 0.98) {
            mc.doAttack();
            return true;
        } else if (autoBlock) {
            if (mc.player.getOffHandStack().isEmpty()) {
                return false;
            }

            if (mc.player.getOffHandStack().getItem().equals(Items.SHIELD)) {
                mc.doItemUse();
            }
        }

        return false;
    }

}
