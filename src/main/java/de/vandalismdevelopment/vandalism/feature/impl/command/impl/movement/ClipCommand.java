package de.vandalismdevelopment.vandalism.feature.impl.command.impl.movement;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.Command;
import de.vandalismdevelopment.vandalism.util.minecraft.impl.MovementUtil;
import net.minecraft.command.CommandSource;

public class ClipCommand extends Command {

    public ClipCommand() {
        super("Clip", "Allows you to teleport yourself by vertical and horizontal offset.", FeatureCategory.MOVEMENT, false, "clip", "tp", "teleport");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("vertical-offset", DoubleArgumentType.doubleArg(-10.0, 10.0)).then(argument("horizontal-offset", DoubleArgumentType.doubleArg(-10.0, 10.0)).executes(context -> {
            if (this.player() != null) {
                MovementUtil.clip(context.getArgument("vertical-offset", Double.class), context.getArgument("horizontal-offset", Double.class));
            }
            return SINGLE_SUCCESS;
        })));
    }

}
