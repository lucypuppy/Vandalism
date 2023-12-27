package de.nekosarekawaii.vandalism.addonwurstclient.injection.mixins.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.Feature;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import net.minecraft.command.CommandSource;
import net.wurstclient.WurstClient;
import net.wurstclient.command.CmdList;
import net.wurstclient.command.Command;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.TreeMap;

@Mixin(value = CmdList.class, remap = false)
public abstract class MixinCmdList {

    @Unique
    private void vandalism$runWurstCommand(final String input) {
        if (WurstClient.INSTANCE.isEnabled()) {
            WurstClient.INSTANCE.getCmdProcessor().process(input.replaceFirst("wurst", ""));
        }
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/TreeMap;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object registerWurstCommands(TreeMap instance, Object key, Object value) {
        final Command command = (Command) value;
        Vandalism.getInstance().getCommandManager().add(new AbstractCommand(command.getDescription(), Feature.Category.MISC, "wurst" + command.getName().substring(1)) {

            @Override
            public void build(final LiteralArgumentBuilder<CommandSource> builder) {
                builder.then(AbstractCommand.argument("input", StringArgumentType.greedyString()).executes(context -> {
                    vandalism$runWurstCommand(context.getInput());
                    return AbstractCommand.SINGLE_SUCCESS;
                }));
                builder.executes(context -> {
                    vandalism$runWurstCommand(context.getInput());
                    return AbstractCommand.SINGLE_SUCCESS;
                });
            }

        });
        return instance.put(key, value);
    }

}
