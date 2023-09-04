package de.nekosarekawaii.foxglove.feature.impl.command.impl.movement;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.command.Command;
import de.nekosarekawaii.foxglove.util.MovementUtil;
import net.minecraft.command.CommandSource;

public class HClipCommand extends Command {

    public HClipCommand() {
        super(
                "HClip",
                "Allows you to teleport yourself by horizontal offset.",
                FeatureCategory.MOVEMENT,
                false,
                "hclip",
                "htp",
                "horizontalteleport"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("horizontal-offset", DoubleArgumentType.doubleArg(-10.0, 10.0))
                .executes(context -> {
                    if (player() != null) {
                        MovementUtil.clip(0.0, context.getArgument("horizontal-offset", Double.class));
                            }
                            return singleSuccess;
                        }
                )
        );
    }

}
