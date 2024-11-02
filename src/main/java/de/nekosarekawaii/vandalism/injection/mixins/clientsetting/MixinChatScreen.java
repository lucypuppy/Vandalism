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

package de.nekosarekawaii.vandalism.injection.mixins.clientsetting;

import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.account.Account;
import de.nekosarekawaii.vandalism.base.clientsettings.impl.ChatSettings;
import de.nekosarekawaii.vandalism.injection.access.IChatHud;
import de.nekosarekawaii.vandalism.util.WorldUtil;
import de.nekosarekawaii.vandalism.util.interfaces.MinecraftWrapper;
import de.nekosarekawaii.vandalism.util.render.util.GLStateTracker;
import de.nekosarekawaii.vandalism.util.render.util.PlayerSkinRenderer;
import de.nekosarekawaii.vandalism.util.render.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL45C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.awt.*;

@Mixin(value = ChatScreen.class, priority = 9999)
public abstract class MixinChatScreen extends Screen implements MinecraftWrapper {

    @Shadow
    protected TextFieldWidget chatField;

    @Unique
    private int vandalism$realMaxLength = 0;

    @Unique
    private static final Style vandalism$RED_COLORED_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(Color.RED.getRGB()));

    @Unique
    private final DirectionalLayoutWidget vandalism$layout = DirectionalLayoutWidget.vertical();

    @Unique
    private boolean vandalism$renderButtonContext;

    @Unique
    private ChatHudLine lastHovered;

    protected MixinChatScreen(final Text ignored) {
        super(ignored);
    }

    @Inject(method = "init", at = @At(value = "RETURN"))
    private void customChatInputField(final CallbackInfo ci) {
        final ChatSettings chatSettings = Vandalism.getInstance().getClientSettings().getChatSettings();
        this.vandalism$realMaxLength = this.chatField.getMaxLength();
        if (chatSettings.moreChatInput.getValue()) {
            this.chatField.setMaxLength(Integer.MAX_VALUE);
        }
        if (chatSettings.displayAccountHead.getValue()) {
            this.chatField.setX(20);
        }
        if (chatSettings.fixChatFieldWidth.getValue()) {
            this.chatField.setWidth(this.chatField.getWidth() - this.chatField.getX() - 5);
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(final CallbackInfo ci) {
        if (!Vandalism.getInstance().getClientSettings().getChatSettings().quickAccessOnRightClick.getValue()) {
            return;
        }

        this.vandalism$layout.spacing(0).getMainPositioner().alignHorizontalCenter();
        this.vandalism$renderButtonContext = false;

        this.vandalism$layout.add(
                ButtonWidget.builder(Text.of("Copy String"), (button) ->
                        mc.keyboard.setClipboard(lastHovered.content().getString())).size(50, 10).build()
        );

        this.vandalism$layout.add(
                ButtonWidget.builder(Text.of("Copy Context"), (button) ->
                        mc.keyboard.setClipboard(lastHovered.content().toString())).size(50, 10).build()
        );

        this.vandalism$layout.add(
                ButtonWidget.builder(Text.of("Reply"), (button) -> {
                    final String text = lastHovered.content().getString();
                    final PlayerListEntry sender = WorldUtil.getPlayerFromTab(text);

                    if (sender == null) {
                        return;
                    }

                    this.chatField.setText("/msg " + sender.getProfile().getName());
                }).size(50, 10).build()
        );

        this.vandalism$layout.add(
                ButtonWidget.builder(Text.of("Teleport to"), (button) -> {
                    final String text = lastHovered.content().getString();
                    final PlayerListEntry sender = WorldUtil.getPlayerFromTab(text);

                    if (sender == null) {
                        return;
                    }

                    this.chatField.setText("/tp " + sender.getProfile().getName());
                }).size(50, 10).build()
        );
    }

    @ModifyConstant(method = "init", constant = @Constant(intValue = 10))
    private int moreChatInputSuggestions(final int constant) {
        if (Vandalism.getInstance().getClientSettings().getChatSettings().moreChatInputSuggestions.getValue()) {
            return (this.height - 12 - 3) / 12;
        } else {
            return constant;
        }
    }

    @ModifyArgs(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"))
    private void modifyChatFieldBackground(final Args args) {
        final ChatSettings chatSettings = Vandalism.getInstance().getClientSettings().getChatSettings();
        if (chatSettings.displayAccountHead.getValue()) {
            args.set(0, this.chatField.getX() - 2 - (chatSettings.modulateHead.getValue() ? 0 : 2));
            if (chatSettings.modulateHead.getValue()) {
                args.set(3, this.height - 1);
            }
        }
    }

    @Inject(method = "render", at = @At(value = "HEAD"))
    private void customChatAreaRendering(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        ((IChatHud) this.client.inGameHud.getChatHud()).vandalism$resetLastHovered(); // Reset last hovered line

        final ChatSettings chatSettings = Vandalism.getInstance().getClientSettings().getChatSettings();
        if (chatSettings.displayTypedChars.getValue()) {
            final int currentLength = this.chatField.getText().length();
            final MutableText text = Text.literal(String.valueOf(currentLength) + Formatting.DARK_GRAY + " / ");
            text.append(Text.literal(String.valueOf(this.vandalism$realMaxLength)).setStyle(vandalism$RED_COLORED_STYLE));
            final int x = this.width - 2 - mc.textRenderer.getWidth(text) - 2;
            final int y = this.chatField.getY() - mc.textRenderer.fontHeight - 2;
            final Color color = RenderUtil.interpolateColor(
                    Color.GREEN,
                    Color.YELLOW,
                    Color.RED,
                    Math.min((float) currentLength / this.vandalism$realMaxLength, 1.0f)
            );
            context.drawText(mc.textRenderer, text, x, y, color.getRGB(), true);
        }
        if (chatSettings.displayAccountHead.getValue()) {
            final Account currentAccount = Vandalism.getInstance().getAccountManager().getCurrentAccount();
            if (currentAccount != null) {
                final PlayerSkinRenderer accountPlayerSkin = currentAccount.getPlayerSkin();
                if (accountPlayerSkin != null) {
                    final Identifier playerSkin = accountPlayerSkin.getSkin();
                    if (playerSkin != null) {
                        GLStateTracker.BLEND.save(true);
                        if (chatSettings.modulateHead.getValue()) {
                            PlayerSkinDrawer.draw(context, playerSkin, 1, this.chatField.getY() - 4, 15, true, false);
                        } else {
                            PlayerSkinDrawer.draw(context, playerSkin, 2, this.chatField.getY() - 2, 12, true, false);
                        }
                        GLStateTracker.BLEND.revert();
                    }
                }
            }
        }
    }

    @Redirect(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;handleTextClick(Lnet/minecraft/text/Style;)Z"))
    private boolean addTPQuickActionToShowEntityComponent(final ChatScreen instance, final Style style) {
        final ChatSettings chatSettings = Vandalism.getInstance().getClientSettings().getChatSettings();
        if (chatSettings.addTPQuickActionToShowEntityComponent.getValue()) {
            final ClickEvent clickEvent = style.getClickEvent();
            if (clickEvent == null) {
                final HoverEvent hoverEvent = style.getHoverEvent();
                if (hoverEvent != null) {
                    final HoverEvent.Action<?> action = hoverEvent.getAction();
                    if (action == HoverEvent.Action.SHOW_ENTITY) {
                        if (hoverEvent.getValue(action) instanceof final HoverEvent.EntityContent entityContent) {
                            this.chatField.setText("/tp " + entityContent.uuid);
                        }
                    }
                }
            }
        }
        return instance.handleTextClick(style);
    }

    @Inject(method = "mouseClicked", at = @At(value = "HEAD"), cancellable = true)
    private void mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (!Vandalism.getInstance().getClientSettings().getChatSettings().quickAccessOnRightClick.getValue()) {
            return;
        }

        if (this.vandalism$renderButtonContext) {
            this.vandalism$layout.forEachChild(child -> {
                if (child.mouseClicked(mouseX, mouseY, button)) {
                    cir.setReturnValue(true);
                    return;
                }
            });
        }

        if (button == 1 && (cir.getReturnValue() == null || !cir.getReturnValue())) {
            final ChatHudLine lastHovered = ((IChatHud) this.client.inGameHud.getChatHud()).vandalism$getLastHovered();

            if (lastHovered != null) {
                this.vandalism$layout.refreshPositions();
                this.vandalism$layout.setPosition((int) mouseX, (int) mouseY - this.vandalism$layout.getHeight());
                this.lastHovered = lastHovered;

                final boolean detectedPlayer = WorldUtil.getPlayerFromTab(lastHovered.content().getString()) != null;
                this.vandalism$layout.forEachChild(child -> {
                    if (child instanceof final ButtonWidget buttonWidget) {
                        final String name = buttonWidget.getMessage().getString().toLowerCase();

                        if (!name.startsWith("copy")) {
                            buttonWidget.active = detectedPlayer;
                        }
                    }
                });

                this.vandalism$renderButtonContext = true;
            } else {
                this.vandalism$renderButtonContext = false;
            }
        } else {
            this.vandalism$renderButtonContext = false;
        }
    }

    @Inject(method = "render", at = @At(value = "RETURN"))
    private void renderButtons(final DrawContext context, final int mouseX, final int mouseY, final float delta, final CallbackInfo ci) {
        if (!Vandalism.getInstance().getClientSettings().getChatSettings().quickAccessOnRightClick.getValue() ||
                !this.vandalism$renderButtonContext) {
            return;
        }

        RenderSystem.clear(GL45C.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
        RenderSystem.depthFunc(GL45C.GL_ALWAYS);
        context.fill(this.vandalism$layout.getX(), this.vandalism$layout.getY(),
                this.vandalism$layout.getX() + this.vandalism$layout.getWidth(),
                this.vandalism$layout.getY() + this.vandalism$layout.getHeight(), Integer.MIN_VALUE);

        this.vandalism$layout.forEachChild(child -> child.render(context, mouseX, mouseY, delta));
        RenderSystem.depthFunc(GL45C.GL_LEQUAL);
    }

}