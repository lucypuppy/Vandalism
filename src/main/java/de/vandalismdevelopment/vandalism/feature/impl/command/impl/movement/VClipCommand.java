package de.vandalismdevelopment.vandalism.feature.impl.command.impl.movement;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.Command;
import de.vandalismdevelopment.vandalism.util.MovementUtil;
import de.vandalismdevelopment.vandalism.util.PlayerUtil;
import net.minecraft.command.CommandSource;

public class VClipCommand extends Command {

    public VClipCommand() {
        super("VClip", "Allows you to teleport yourself by vertical offset.", FeatureCategory.MOVEMENT, false, "vclip", "vtp", "verticalteleport");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("vertical-offset", DoubleArgumentType.doubleArg(-10.0, 10.0)).executes(context -> {
            if (this.player() != null) {
                MovementUtil.clip(context.getArgument("vertical-offset", Double.class), 0.0);
            }
            return SINGLE_SUCCESS;
        }));
    }

}
