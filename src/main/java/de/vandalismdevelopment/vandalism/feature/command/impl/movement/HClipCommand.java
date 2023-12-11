package de.vandalismdevelopment.vandalism.feature.command.impl.movement;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.command.AbstractCommand;
import de.vandalismdevelopment.vandalism.util.minecraft.MovementUtil;
import net.minecraft.command.CommandSource;

public class HClipCommand extends AbstractCommand {

    public HClipCommand() {
        super("HClip", "Allows you to teleport yourself by horizontal offset.", FeatureCategory.MOVEMENT, false, "hclip", "htp", "horizontalteleport");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("horizontal-offset", DoubleArgumentType.doubleArg(-10.0, 10.0)).executes(context -> {
            if (this.mc.player != null)
                MovementUtil.clip(
                        0.0,
                        DoubleArgumentType.getDouble(context, "horizontal-offset")
                );

            return SINGLE_SUCCESS;
        }));
    }

}
