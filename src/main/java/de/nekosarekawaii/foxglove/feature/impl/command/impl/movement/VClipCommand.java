package de.nekosarekawaii.foxglove.feature.impl.command.impl.movement;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.foxglove.feature.Feature;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.command.Command;
import de.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;

@CommandInfo(name = "V-Clip", aliases = {"vclip"}, description = "This command allows the player to clip to relative y positions.", category = FeatureCategory.MOVEMENT)
public class VClipCommand extends Command {

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("vertical", DoubleArgumentType.doubleArg(-10.0, 10.0))
                .executes(context -> {
                    final ClientPlayerEntity player = Feature.mc.player;
                            if (player != null) {
                                final double vertical = context.getArgument("vertical", Double.class);
                                player.setPos(
                                        player.getX(),
                                        player.getY() + vertical,
                                        player.getZ()
                                );
                            }
                            return singleSuccess;
                        }
                )
        );
    }

}
