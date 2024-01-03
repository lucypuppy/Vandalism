package de.nekosarekawaii.vandalism.injection.mixins.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.player.RaytraceListener;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @ModifyConstant(method = "updateTargetedEntity", constant = @Constant(doubleValue = 9.0))
    private double modifyRaytraceRange(double constant) {
        final RaytraceListener.RaytraceEvent event = new RaytraceListener.RaytraceEvent(constant);
        Vandalism.getInstance().getEventSystem().postInternal(RaytraceListener.RaytraceEvent.ID, event);
        return event.range;
    }

}
