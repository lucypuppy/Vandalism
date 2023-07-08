package me.nekosarekawaii.foxglove.feature.impl.command.impl.misc;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.nekosarekawaii.foxglove.feature.impl.command.Command;
import me.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import me.nekosarekawaii.foxglove.feature.impl.command.arguments.GameModeArgumentType;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.world.GameMode;

@CommandInfo(name = "Game Mode", aliases = {"gamemode", "gm"}, description = "Sets your Game Mode via. Server Command.")
public class GameModeCommand extends Command {

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder
                .then(argument("gamemode", GameModeArgumentType.create())
                        .executes(context -> {
                            final ClientPlayNetworkHandler clientPlayNetworkHandler = mc.getNetworkHandler();
                            final GameMode gameMode = GameModeArgumentType.get(context);
                            if (clientPlayNetworkHandler != null) {
                                clientPlayNetworkHandler.sendChatCommand("gamemode " + gameMode.name().toLowerCase());
                            }
                            return SINGLE_SUCCESS;
                        })
                );
    }

}
