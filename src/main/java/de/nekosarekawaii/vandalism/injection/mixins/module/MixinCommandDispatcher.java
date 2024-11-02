/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.injection.mixins.module;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.exploitfixer.ExploitFixerModule;
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
    private static final SimpleCommandExceptionType vandalism$STACK_OVERFLOW_EXCEPTION = new SimpleCommandExceptionType(
            new LiteralMessage("Stack overflow error while parsing command")
    );

    @Shadow
    protected abstract ParseResults<S> parseNodes(final CommandNode<S> node, final StringReader originalReader, final CommandContextBuilder<S> contextSoFar);

    @Shadow
    @Final
    private RootCommandNode<S> root;

    @Redirect(method = "parse(Lcom/mojang/brigadier/StringReader;Ljava/lang/Object;)Lcom/mojang/brigadier/ParseResults;", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/CommandDispatcher;parseNodes(Lcom/mojang/brigadier/tree/CommandNode;Lcom/mojang/brigadier/StringReader;Lcom/mojang/brigadier/context/CommandContextBuilder;)Lcom/mojang/brigadier/ParseResults;"))
    private ParseResults<S> hookExploitFixer(final CommandDispatcher<S> instance, final CommandNode<S> ex, final StringReader command, final CommandContextBuilder<S> context) {
        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleManager().getExploitFixerModule();
        if (exploitFixerModule.isActive() && exploitFixerModule.miscSettings.blockBrigadierStackOverflowCrash.getValue()) {
            try {
                return this.parseNodes(this.root, command, context);
            } catch (StackOverflowError ignored) {
                return new ParseResults<>(context, command, Collections.singletonMap(
                        this.root, vandalism$STACK_OVERFLOW_EXCEPTION.createWithContext(command))
                );
            }
        }
        return this.parseNodes(this.root, command, context);
    }

}
