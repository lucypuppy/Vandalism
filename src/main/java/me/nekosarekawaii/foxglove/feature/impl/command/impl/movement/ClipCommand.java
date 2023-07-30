package me.nekosarekawaii.foxglove.feature.impl.command.impl.movement;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.command.Command;
import me.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;

@CommandInfo(name = "Clip", aliases = {"clip"}, description = "This command allows the player to clip to relative positions.", category = FeatureCategory.MOVEMENT)
public class ClipCommand extends Command {

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("vertical", DoubleArgumentType.doubleArg(-10.0, 10.0))
                .then(argument("horizontal", DoubleArgumentType.doubleArg(-10.0, 10.0))
                        .executes(context -> {
                                    final ClientPlayerEntity player = mc.player;
                                    if (player != null) {
                                        final double
                                                yaw = Math.toRadians(player.headYaw),
                                                vertical = context.getArgument("vertical", Double.class),
                                                horizontal = context.getArgument("horizontal", Double.class);
                                        player.setPos(
                                                player.getX() - Math.sin(yaw) * horizontal,
                                                player.getY() + vertical,
                                                player.getZ() + Math.cos(yaw) * horizontal
                                        );
                                    }
                                    return singleSuccess;

                                }
                        )
                )
        );
    }

}
