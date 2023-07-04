package me.nekosarekawaii.foxglove.feature.impl.command.impl.render;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.command.Command;
import me.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import me.nekosarekawaii.foxglove.feature.impl.command.arguments.GameModeArgumentType;
import me.nekosarekawaii.foxglove.util.ChatUtils;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

@CommandInfo(name = "Clientside Game Mode", aliases = {"clientsidegamemode", "clientsidegm", "csgm", "fakegamemode", "fakegm"}, description = "Sets your Clientside Game Mode.", category = FeatureCategory.RENDER)
public class ClientsideGameModeCommand extends Command {

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder
                .then(argument("gamemode", GameModeArgumentType.create())
                        .executes(context -> {
                            final ClientPlayerInteractionManager interactionManager = mc().interactionManager;
                            if (interactionManager != null) {
                                final GameMode gameMode = GameModeArgumentType.get(context);
                                interactionManager.setGameMode(gameMode);
                                ChatUtils.infoChatMessage(Formatting.GREEN + "Your Clientside Game Mode has been set to" + Formatting.DARK_GRAY + ": " + Formatting.GOLD + gameMode.name() + " " + Formatting.DARK_GRAY + "(" + Formatting.DARK_AQUA + gameMode.getId() + Formatting.DARK_GRAY + ")");
                            }
                            return SINGLE_SUCCESS;
                        })
                );
    }

}
