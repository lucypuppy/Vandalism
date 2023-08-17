package de.nekosarekawaii.foxglove.feature.impl.command.impl.movement;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.command.Command;
import de.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import de.nekosarekawaii.foxglove.util.minecraft.player.MovementUtil;
import net.minecraft.command.CommandSource;

@CommandInfo(name = "H-Clip", aliases = {"hclip"}, description = "This command allows the player to clip to relative x / z positions.", category = FeatureCategory.MOVEMENT)
public class HClipCommand extends Command {

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("horizontal", DoubleArgumentType.doubleArg(-10.0, 10.0))
                .executes(context -> {
                            final var player = mc.player;

                            if (player != null) {
                                MovementUtil.clip(0.0, context.getArgument("horizontal", Double.class));
                            }

                            return singleSuccess;
                        }
                )
        );
    }

}
