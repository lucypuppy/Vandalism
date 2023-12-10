package de.vandalismdevelopment.vandalism.feature.command;

import com.mojang.brigadier.CommandDispatcher;
import de.florianmichael.rclasses.pattern.storage.Storage;
import de.vandalismdevelopment.vandalism.feature.command.impl.development.TestCommand;
import de.vandalismdevelopment.vandalism.feature.command.impl.exploit.*;
import de.vandalismdevelopment.vandalism.feature.command.impl.misc.*;
import de.vandalismdevelopment.vandalism.feature.command.impl.movement.ClipCommand;
import de.vandalismdevelopment.vandalism.feature.command.impl.movement.FlipCommand;
import de.vandalismdevelopment.vandalism.feature.command.impl.movement.HClipCommand;
import de.vandalismdevelopment.vandalism.feature.command.impl.movement.VClipCommand;
import de.vandalismdevelopment.vandalism.feature.command.impl.render.ClientsideGameModeCommand;
import de.vandalismdevelopment.vandalism.feature.command.impl.render.ClientsideInventoryClearCommand;
import de.vandalismdevelopment.vandalism.util.MinecraftWrapper;
import net.minecraft.command.CommandSource;
import net.minecraft.text.ClickEvent;

import java.util.UUID;

public class CommandManager extends Storage<AbstractCommand> implements MinecraftWrapper {

    public static final String COMMAND_SECRET = UUID.randomUUID().toString();

    private final CommandDispatcher<CommandSource> commandDispatcher = new CommandDispatcher<>();

    public CommandManager() {
        setAddConsumer(command -> command.publish(this.commandDispatcher));
    }

    @Override
    public void init() {
        add(
                new TestCommand(),
                new ChatClearCommand(),
                new ModuleCommand(),
                new ClientsideGameModeCommand(),
                new SayCommand(),
                new NbtCommand(),
                new CommandBlockStateCommand(),
                new PluginsCommand(),
                new GiveCommand(),
                new CopyServerIPCommand(),
                new CopyPositionCommand(),
                new TeleportEntitySpawnEggCommand(),
                new SoundEntitySpawnEggCommand(),
                new SoundHeadCommand(),
                new ClipCommand(),
                new VClipCommand(),
                new HClipCommand(),
                new ServerBrandCommand(),
                new SkriptDupeCommand(),
                new NavigateXCommand(),
                new ScriptCommand(),
                new ArmorCarryCommand(),
                new InventoryClearCommand(),
                new CopyInvisibleCharCommand(),
                new CopyUsernameCommand(),
                new CreativeItemControlCommand(),
                new EnchantCommand(),
                new FlipCommand(),
                new ClientsideInventoryClearCommand()
        );
    }

    public CommandDispatcher<CommandSource> getCommandDispatcher() {
        return commandDispatcher;
    }

    public ClickEvent generateClickEvent(final String command) {
        return new ClickEvent(ClickEvent.Action.RUN_COMMAND, COMMAND_SECRET + command);
    }

}
