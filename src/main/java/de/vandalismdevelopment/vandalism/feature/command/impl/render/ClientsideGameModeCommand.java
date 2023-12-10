package de.vandalismdevelopment.vandalism.feature.command.impl.render;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.vandalismdevelopment.vandalism.feature.command.AbstractCommand;
import de.vandalismdevelopment.vandalism.feature.command.arguments.GameModeArgumentType;
import de.vandalismdevelopment.vandalism.util.ChatUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

public class ClientsideGameModeCommand extends AbstractCommand {

    public ClientsideGameModeCommand() {
        super("Clientside Game Mode", "Allows you to set your clientside game mode.", FeatureCategory.RENDER, false, "clientsidegamemode", "fakegamemode", "cgm", "fgm");
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("gamemode", GameModeArgumentType.create()).executes(context -> {
            if (this.mc.interactionManager != null) {
                final GameMode gameMode = GameModeArgumentType.get(context);
                this.mc.interactionManager.setGameMode(gameMode);
                ChatUtil.infoChatMessage(Formatting.GREEN + "Your Clientside Game Mode has been set to" + Formatting.DARK_GRAY + ": " + Formatting.GOLD + gameMode.name() + " " + Formatting.DARK_GRAY + "(" + Formatting.DARK_AQUA + gameMode.getId() + Formatting.DARK_GRAY + ")");
            }

            return SINGLE_SUCCESS;
        }));
    }

}
