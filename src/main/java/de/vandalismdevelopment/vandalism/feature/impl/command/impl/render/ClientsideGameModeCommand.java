package de.vandalismdevelopment.vandalism.feature.impl.command.impl.render;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.command.Command;
import de.vandalismdevelopment.vandalism.feature.impl.command.arguments.GameModeArgumentType;
import de.vandalismdevelopment.vandalism.util.PlayerUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

public class ClientsideGameModeCommand extends Command {

    public ClientsideGameModeCommand() {
        super("Clientside Game Mode", "Allows you to set your clientside game mode.", FeatureCategory.RENDER, false, "clientsidegamemode", "fakegamemode", "cgm", "fgm");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("gamemode", GameModeArgumentType.create()).executes(context -> {
            if (this.interactionManager() != null) {
                final GameMode gameMode = GameModeArgumentType.get(context);
                this.interactionManager().setGameMode(gameMode);
                PlayerUtil.infoChatMessage(Formatting.GREEN + "Your Clientside Game Mode has been set to" + Formatting.DARK_GRAY + ": " + Formatting.GOLD + gameMode.name() + " " + Formatting.DARK_GRAY + "(" + Formatting.DARK_AQUA + gameMode.getId() + Formatting.DARK_GRAY + ")");
            }
            return SINGLE_SUCCESS;
        }));
    }

}
