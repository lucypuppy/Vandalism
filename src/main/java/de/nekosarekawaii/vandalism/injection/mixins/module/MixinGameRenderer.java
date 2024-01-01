package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.combat.KillAuraModule;
import de.nekosarekawaii.vandalism.util.minecraft.MathUtil;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @ModifyConstant(method = "updateTargetedEntity", constant = @Constant(doubleValue = 9.0))
    private double hookKillAura(final double constant) {
        final KillAuraModule killauraModule = Vandalism.getInstance().getModuleManager().getKillAuraModule();
        return killauraModule.isActive() ? MathUtil.getFixedMinecraftReach(killauraModule.range.getValue()) : constant;
    }

}
