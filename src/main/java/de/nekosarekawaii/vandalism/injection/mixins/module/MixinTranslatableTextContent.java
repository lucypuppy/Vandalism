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

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.exploitfixer.ComponentResolverContainer;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.exploitfixer.ExploitFixerModule;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.Counter;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Mixin(TranslatableTextContent.class)
public abstract class MixinTranslatableTextContent {

    @Shadow
    public abstract void updateTranslations();

    @Shadow
    public List<StringVisitable> translations;

    @Shadow
    public abstract <T> Optional<T> visit(final StringVisitable.Visitor<T> visitor);

    @Inject(method = "visit(Lnet/minecraft/text/StringVisitable$Visitor;)Ljava/util/Optional;", at = @At("HEAD"), cancellable = true)
    private <T> void hookExploitFixer(final StringVisitable.Visitor<T> visitor, final CallbackInfoReturnable<Optional<T>> cir) {
        if (!FabricBootstrap.INITIALIZED) {
            return;
        }

        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleManager().getExploitFixerModule();
        if (exploitFixerModule.isActive() && exploitFixerModule.renderSettings.blockTooComplexTranslateTexts.getValue()) {
            final Counter counter = new Counter(0, exploitFixerModule.renderSettings.translateTextNestingLimit.getValue());
            try {
                cir.setReturnValue(MixinTranslatableTextContent.visitWithCounter((TranslatableTextContent) ((Object) this), visitor, counter));
            } catch (final Counter.CounterMaxReachedException ignored) {
                if (exploitFixerModule.showWarnings.getValue()) {
                    ChatUtil.warningChatMessage(Text.of("Blocked too highly nested translate text."), true);
                }
                cir.setReturnValue(Optional.empty());
            }
        }
    }

    @Redirect(method = "updateTranslations", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Language;get(Ljava/lang/String;)Ljava/lang/String;"))
    private String hookExploitFixer(final Language instance, final String key) {
        if (((Object) this) instanceof ComponentResolverContainer.TranslationResolvedTranslatableTextContent resolved) {
            return resolved.resolved();
        }
        return instance.get(key);
    }

    @Redirect(method = "updateTranslations", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Language;get(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"))
    private String hookExploitFixer(final Language instance, final String key, final String fallback) {
        if (((Object) this) instanceof ComponentResolverContainer.TranslationResolvedTranslatableTextContent resolved) {
            return resolved.resolved();
        }
        return instance.get(key, fallback);
    }

    @Inject(method = "visit(Lnet/minecraft/text/StringVisitable$StyledVisitor;Lnet/minecraft/text/Style;)Ljava/util/Optional;", at = @At("HEAD"), cancellable = true)
    public <T> void hookExploitFixer(final StringVisitable.StyledVisitor<T> visitor, final Style style, final CallbackInfoReturnable<Optional<T>> cir) {
        if (!FabricBootstrap.INITIALIZED) {
            return;
        }

        final ExploitFixerModule exploitFixerModule = Vandalism.getInstance().getModuleManager().getExploitFixerModule();
        if (exploitFixerModule.isActive() && exploitFixerModule.renderSettings.blockTooComplexTranslateTexts.getValue()) {
            final Counter counter = new Counter(0, exploitFixerModule.renderSettings.translateTextNestingLimit.getValue());

            try {
                cir.setReturnValue(MixinTranslatableTextContent.visitWithCounter((TranslatableTextContent) ((Object) this), visitor, style, counter));
            } catch (final Counter.CounterMaxReachedException ignored) {
                if (exploitFixerModule.showWarnings.getValue()) {
                    ChatUtil.warningChatMessage(Text.of("Blocked too highly nested translate text."), true);
                }
                cir.setReturnValue(Optional.empty());
            }
        }
    }

    @Unique
    private static <T> Optional<T> visitWithCounter(final TranslatableTextContent instance, final StringVisitable.Visitor<T> visitor, final Counter counter) {
        instance.updateTranslations();

        final List<StringVisitable> list = instance.translations;

        counter.count();

        final Iterator<StringVisitable> iterator = list.iterator();

        Optional<T> optional;
        do {
            if (!iterator.hasNext()) {
                return Optional.empty();
            }

            StringVisitable stringVisitable = iterator.next();
            if (stringVisitable instanceof Text text) {
                optional = MixinTranslatableTextContent.visitWithCounter(text, visitor, counter);
            } else {
                optional = stringVisitable.visit(visitor);
            }
        } while (optional.isEmpty());

        return optional;
    }

    @Unique
    private static <T> Optional<T> visitWithCounter(final Text instance, final StringVisitable.Visitor<T> visitor, final Counter counter) {
        final Optional<T> optional = switch (instance.getContent()) {
            case final TranslatableTextContent content ->
                    MixinTranslatableTextContent.visitWithCounter(content, visitor, counter);
            default -> instance.getContent().visit(visitor);
        };

        if (optional.isPresent()) {
            return optional;
        } else {
            Iterator<Text> iterator = instance.getSiblings().iterator();

            Optional<T> optional2;
            do {
                if (!iterator.hasNext()) {
                    return Optional.empty();
                }

                Text text = iterator.next();
                optional2 = MixinTranslatableTextContent.visitWithCounter(text, visitor, counter);
            } while (optional2.isEmpty());

            return optional2;
        }
    }

    @Unique
    private static <T> Optional<T> visitWithCounter(final TranslatableTextContent instance, final StringVisitable.StyledVisitor<T> visitor, Style style, final Counter counter) {
        instance.updateTranslations();

        counter.count();

        final List<StringVisitable> list = instance.translations;
        final Iterator<StringVisitable> iterator = list.iterator();

        Optional<T> optional;
        do {
            if (!iterator.hasNext()) {
                return Optional.empty();
            }

            StringVisitable stringVisitable = iterator.next();
            if (stringVisitable instanceof Text text) {
                optional = MixinTranslatableTextContent.visitWithCounter(text, visitor, style, counter);
            } else {
                optional = stringVisitable.visit(visitor, style);
            }
        } while (optional.isEmpty());

        return optional;
    }

    @Unique
    private static <T> Optional<T> visitWithCounter(final Text instance, final StringVisitable.StyledVisitor<T> styledVisitor, final Style style, final Counter counter) {
        final Style style2 = instance.getStyle().withParent(style);
        final Optional<T> optional = switch (instance.getContent()) {
            case final TranslatableTextContent content ->
                    MixinTranslatableTextContent.visitWithCounter(content, styledVisitor, style2, counter);
            default -> instance.getContent().visit(styledVisitor, style2);
        };

        if (optional.isPresent()) {
            return optional;
        } else {
            final Iterator<Text> iterator = instance.getSiblings().iterator();

            Optional<T> optional2;
            do {
                if (!iterator.hasNext()) {
                    return Optional.empty();
                }

                final Text text = iterator.next();
                optional2 = MixinTranslatableTextContent.visitWithCounter(text, styledVisitor, style2, counter);
            } while (optional2.isEmpty());

            return optional2;
        }
    }
}
