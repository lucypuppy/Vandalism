package me.nekosarekawaii.foxglove.feature.impl.command.impl.movement;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.command.Command;
import me.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import net.minecraft.command.CommandSource;

@CommandInfo(name = "Clip", aliases = {"clip"}, description = "This command allows the player to clip to other positions.", category = FeatureCategory.MOVEMENT)
public class ClipCommand extends Command {

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("vertical", DoubleArgumentType.doubleArg(-10.0, 10.0))
                .then(argument("horizontal", DoubleArgumentType.doubleArg(-10.0, 10.0))
                        .executes(context -> {
            final double yaw = Math.toRadians(mc.player.headYaw);
            final double vertical = context.getArgument("vertical", Double.class);
            final double horizontal = context.getArgument("horizontal", Double.class);

            mc.player.setPos(mc.player.getX() - Math.sin(yaw) * horizontal, mc.player.getY() + vertical,
                    mc.player.getZ() + Math.cos(yaw) * horizontal);
            return singleSuccess;
        })));
    }

}
