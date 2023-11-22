package de.vandalismdevelopment.vandalism.injection.mixins.libs.brigadier;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.exploit.ExploitFixerModule;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;

@Mixin(value = CommandDispatcher.class, remap = false)
public abstract class MixinCommandDispatcher<S> {

    @Unique
    private final static SimpleCommandExceptionType STACK_OVERFLOW_EXCEPTION = new SimpleCommandExceptionType(
            new LiteralMessage("Stack overflow error while parsing command")
    );

    @Shadow
    protected abstract ParseResults<S> parseNodes(
            final CommandNode<S> node,
            final StringReader originalReader,
            final CommandContextBuilder<S> contextSoFar
    );

    @Shadow
    @Final
    private RootCommandNode<S> root;

    @Redirect(
            method = "parse(Lcom/mojang/brigadier/StringReader;Ljava/lang/Object;)Lcom/mojang/brigadier/ParseResults;",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/brigadier/CommandDispatcher;parseNodes(Lcom/mojang/brigadier/tree/CommandNode;Lcom/mojang/brigadier/StringReader;Lcom/mojang/brigadier/context/CommandContextBuilder;)Lcom/mojang/brigadier/ParseResults;"
            )
    )
    private ParseResults<S> redirectParse(
            final CommandDispatcher<S> instance,
            final CommandNode<S> ex,
            final StringReader command,
            final CommandContextBuilder<S> context
    ) {
        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleRegistry().getExploitFixerModule();
        if (exploitFixerModule.isEnabled() && exploitFixerModule.blockBrigadierStackOverflowCrash.getValue()) {
            try {
                return this.parseNodes(this.root, command, context);
            } catch (final StackOverflowError ignored) {
                return new ParseResults<>(
                        context,
                        command,
                        Collections.singletonMap(
                                this.root,
                                STACK_OVERFLOW_EXCEPTION.createWithContext(command)
                        )
                );
            }
        }
        return this.parseNodes(this.root, command, context);
    }

}
