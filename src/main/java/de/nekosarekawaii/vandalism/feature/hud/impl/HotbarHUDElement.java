/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.feature.hud.impl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.misc.EasingTypeValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.hud.HUDElement;
import de.nekosarekawaii.vandalism.util.Animator;
import de.nekosarekawaii.vandalism.util.RenderUtil;
import de.nekosarekawaii.vandalism.util.interfaces.Easing;
import de.nekosarekawaii.vandalism.util.render.Shaders;
import de.nekosarekawaii.vandalism.util.render.effect.fill.GaussianBlurFillEffect;
import de.nekosarekawaii.vandalism.util.render.gl.shader.GlobalUniforms;
import de.nekosarekawaii.vandalism.util.render.gl.shader.ShaderProgram;
import de.nekosarekawaii.vandalism.util.render.util.AlignmentX;
import de.nekosarekawaii.vandalism.util.render.util.AlignmentY;
import de.nekosarekawaii.vandalism.util.render.util.ColorUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.world.GameMode;

import java.awt.*;

public class HotbarHUDElement extends HUDElement {

    private final BooleanValue blurEnabled = new BooleanValue(this, "Blur", "Enable / Disable hotbar blur", true);
    private final ColorValue backgroundColor = new ColorValue(this, "Background Color", "Color of the hotbar background", new Color(168, 10, 225, 150));

    public final EasingTypeValue selectItemAnimatino = new EasingTypeValue(this, "Select item animation", "Hotbar select item animation", Easing.LINEAR);
    private final FloatValue hotbarAnimationSpeed = new FloatValue(
            this,
            "Select item Animation Speed",
            "Speed of the hotbar select item animation.",
            1f,
            0.1f,
            2.0f
    );

    public final EasingTypeValue barAnimation = new EasingTypeValue(this, "Bar animation", "Select the Bar animation", Easing.LINEAR);
    private final FloatValue barAnimationSpeed = new FloatValue(
            this,
            "Bar Animation Speed",
            "Speed of the bar animations.",
            0.3f,
            0.1f,
            2.0f
    );

    private final BooleanValue showBarsBelow = new BooleanValue(this, "Show Bars Below", "Show the bars below the hotbar", false);

    private final Animator hotbarSlotAnimation = new Animator();
    private final Animator xpBarAnimation = new Animator();
    private final Animator healthBarAnimation = new Animator();
    private final Animator foodBarAnimation = new Animator();
    private final Animator saturationBarAnimation = new Animator();
    private final Animator absorbtionBarAnimation = new Animator();
    private final Animator armorBarAnimation = new Animator();

    public HotbarHUDElement() {
        super("Hotbar", true, AlignmentX.MIDDLE, AlignmentY.BOTTOM);
    }

    @Override
    protected void onRender(final DrawContext context, final float delta, final boolean inGame) {
        this.width = mc.getWindow().getScaledWidth();
        this.height = 22;

        int elementX = 70;
        int elementY = this.height;

        switch (this.alignmentX.getValue()) {
            case MIDDLE -> elementX = (context.getScaledWindowWidth() / 2) - 20;
            case RIGHT -> elementX = context.getScaledWindowWidth() - 110;
        }

        switch (this.alignmentY.getValue()) {
            case MIDDLE -> elementY = context.getScaledWindowHeight() / 2;
            case BOTTOM -> elementY = mc.getWindow().getScaledHeight();
        }

        elementX += this.xOffset.getValue();
        elementY += this.yOffset.getValue();

        if (mc.player != null && mc.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR)
            return;

        renderBackgroundRect(context, 0, elementY - this.height, mc.getWindow().getScaledWidth(), elementY);

        if (mc.player == null)
            return;

        renderItems(context, elementX - 70, elementY);

        if (mc.interactionManager.getCurrentGameMode() != GameMode.CREATIVE)
            renderBars(context, elementX - 70, elementY);

        final int experienceLevel = mc.player.experienceLevel;
        if (mc.player.getJumpingMount() == null && mc.interactionManager.hasExperienceBar() && experienceLevel > 0) {
            final String experienceString = experienceLevel + "";
            drawText(experienceString, context, elementX + 22.5f - getTextWidth(experienceString) / 1.5f, elementY - 34, true, Color.GREEN.getRGB());
        }
    }

    private void renderBars(final DrawContext context, final int x, int y) {
        final boolean below = this.showBarsBelow.getValue();

        if (below) {
            y += 24;
        }

        // XPBar or JumpBar
        if (mc.player.getJumpingMount() != null) {
            RenderUtil.fillWidth(context, x, y - 24, 180, 2, Integer.MIN_VALUE);
            RenderUtil.fillWidth(context, x, y - 24, 180 * mc.player.getMountJumpStrength(), 2, ColorUtils.withAlpha(Color.GREEN, 150).getRGB());
        } else {
            this.xpBarAnimation.ease(this.barAnimation.getValue(), mc.player.experienceProgress, this.barAnimationSpeed.getValue());
            RenderUtil.fillWidth(context, x, y - 24, 180, 2, Integer.MIN_VALUE);
            RenderUtil.fillWidth(context, x, y - 24, 180 * this.xpBarAnimation.getCurrentX(), 2, ColorUtils.withAlpha(Color.GREEN, 150).getRGB());
        }

        y -= below ? -4 : 6;

        // HealthBar
        float healthPercentage = mc.player.getHealth() / mc.player.getMaxHealth();
        this.healthBarAnimation.ease(this.barAnimation.getValue(), healthPercentage, this.barAnimationSpeed.getValue());
        RenderUtil.fillWidth(context, x, y - 24, 80, 4, Integer.MIN_VALUE);
        RenderUtil.fillWidth(context, x, y - 24, 80 * this.healthBarAnimation.getCurrentX(), 4, 0xffff2b63);

        // FoodBar or Horse Health
        if (mc.player.getVehicle() != null) {
            if (mc.player.getVehicle() instanceof final LivingEntity livingEntity) {
                final float vehicleHealthPercentage = livingEntity.getHealth() / livingEntity.getMaxHealth();
                RenderUtil.fillWidth(context, x + 100, y - 24, 80, 4, Integer.MIN_VALUE);
                RenderUtil.fillWidth(context, x + 100, y - 24, 80 * vehicleHealthPercentage, 4, 0xfffc3566);
            }
        } else {
            final float foodPercentage = mc.player.getHungerManager().getFoodLevel() / 20.0f;
            this.foodBarAnimation.ease(this.barAnimation.getValue(), foodPercentage, this.barAnimationSpeed.getValue());
            RenderUtil.fillWidth(context, x + 100, y - 24, 80, 4, Integer.MIN_VALUE);
            RenderUtil.fillWidth(context, x + 100, y - 24, 80 * this.foodBarAnimation.getCurrentX(), 4, 0xffffd749);
        }

        y -= below ? -6 : 6;

        // Saturation Bar
        final float saturationPercentage = mc.player.getHungerManager().getSaturationLevel() / 20.0f;
        this.saturationBarAnimation.ease(this.barAnimation.getValue(), saturationPercentage, this.barAnimationSpeed.getValue());
        RenderUtil.fillWidth(context, x + 100, y - 24, 80, 4, Integer.MIN_VALUE);
        RenderUtil.fillWidth(context, x + 100, y - 24, 80 * this.saturationBarAnimation.getCurrentX(), 4, 0xffffd749);

        // Absorption Bar
        if (mc.player.getAbsorptionAmount() > 0) {
            final float absorptionPercentage = mc.player.getAbsorptionAmount() / mc.player.getMaxAbsorption();
            this.absorbtionBarAnimation.ease(this.barAnimation.getValue(), absorptionPercentage, this.barAnimationSpeed.getValue());
            RenderUtil.fillWidth(context, x, y - 24, 80, 4, Integer.MIN_VALUE);
            RenderUtil.fillWidth(context, x, y - 24, 80 * this.armorBarAnimation.getCurrentX(), 4, Color.YELLOW.getRGB());
        }

        y -= below ? -6 : 6;

        // AirBar
        final float maxAir = mc.player.getMaxAir();
        final float air = Math.min(Math.max(mc.player.getAir(), 0), maxAir);
        if (mc.player.isSubmergedIn(FluidTags.WATER) || air < maxAir) {
            final float airPercentage = air / maxAir;
            RenderUtil.fillWidth(context, x + 100, y - 24, 80, 4, Integer.MIN_VALUE);
            RenderUtil.fillWidth(context, x + 100, y - 24, 80 * airPercentage, 4, 0xff7cffff);
        }

        // ArmorBar
        final float armorPercentage = mc.player.getArmor() / 20.0f;
        if (armorPercentage > 0) {
            this.armorBarAnimation.ease(this.barAnimation.getValue(), armorPercentage, this.barAnimationSpeed.getValue());
            RenderUtil.fillWidth(context, x, y - 24, 80, 4, Integer.MIN_VALUE);
            RenderUtil.fillWidth(context, x, y - 24, 80 * this.armorBarAnimation.getCurrentX(), 4, 0xFFa3a3a3);
        }
    }

    private void renderItems(final DrawContext context, final int x, final int y) {
        if (mc.getCameraEntity() instanceof final PlayerEntity playerEntity) {
            RenderUtil.fill(context, x, y - 22, x + 180, y, new Color(0, 0, 0, 50).getRGB());

            final int selectedX = x + playerEntity.getInventory().selectedSlot * 20;
            this.hotbarSlotAnimation.ease(this.selectItemAnimatino.getValue(), selectedX, this.hotbarAnimationSpeed.getValue());
            RenderUtil.fill(context, this.hotbarSlotAnimation.getCurrentX(), y - 22, this.hotbarSlotAnimation.getCurrentX() + 20, y, 0x80FFFFFF);

            for (int slot = 0; slot < 9; ++slot) {
                mc.inGameHud.renderHotbarItem(
                        context,
                        x + slot * 20 + 2,
                        y - 19,
                        mc.getRenderTickCounter(),
                        playerEntity,
                        playerEntity.getInventory().main.get(slot),
                        slot + 1);
            }

            final ItemStack itemStack = playerEntity.getOffHandStack();
            if (!itemStack.isEmpty()) {
                if (this.alignmentX.getValue() == AlignmentX.LEFT) {
                    mc.inGameHud.renderHotbarItem(context, x + 185, y - 16 - 3, mc.getRenderTickCounter(), playerEntity, itemStack, 10);
                } else {
                    mc.inGameHud.renderHotbarItem(context, x - 20, y - 16 - 3, mc.getRenderTickCounter(), playerEntity, itemStack, 10);
                }
            }
        }
    }

    private void renderBackgroundRect(final DrawContext context, float x, float y, float x2, float y2) {
        if (this.blurEnabled.getValue()) {
            final GaussianBlurFillEffect gaussianBlurFillEffect = Shaders.getGaussianBlurFillEffect();
            gaussianBlurFillEffect.setDirections(16.0f);
            gaussianBlurFillEffect.setQuality(8.0f);
            gaussianBlurFillEffect.setRadius(16.0f);
            gaussianBlurFillEffect.setTextureId(mc.getFramebuffer().getColorAttachment());
            gaussianBlurFillEffect.bindMask();
            RenderUtil.fill(context, x, y, x2, y2, Integer.MIN_VALUE);
            gaussianBlurFillEffect.renderScissoredScaled(mc.getFramebuffer(), true, (int) x, (int) y, (int) x2, (int) y2);
        }

        final ShaderProgram backgroundShader = Shaders.getHotbarBackgroundShader();
        backgroundShader.bind();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableDepthTest();
        GlobalUniforms.setBackgroundUniforms(backgroundShader);
        backgroundShader.uniform("alpha").set(backgroundColor.getColor().getAlpha() / 255.0f);
        backgroundShader.uniform("color").set(backgroundColor.getColor(), false);
        RenderUtil.drawShaderRect(x, y + 0.5f, x2, y2);
        RenderSystem.disableBlend();
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        backgroundShader.unbind();
    }

}

