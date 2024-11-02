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

package de.nekosarekawaii.vandalism.feature.module.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import de.nekosarekawaii.vandalism.base.value.impl.minecraft.MultiRegistryValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.DebugHelper;
import de.nekosarekawaii.vandalism.util.interfaces.IName;
import de.nekosarekawaii.vandalism.util.render.Buffers;
import de.nekosarekawaii.vandalism.util.render.Shaders;
import de.nekosarekawaii.vandalism.util.render.effect.fill.FillEffect;
import de.nekosarekawaii.vandalism.util.render.effect.outline.GlowOutlineEffect;
import de.nekosarekawaii.vandalism.util.render.effect.outline.OutlineEffect;
import de.nekosarekawaii.vandalism.util.render.gl.render.ImmediateRenderer;
import de.nekosarekawaii.vandalism.util.render.gl2mc.ImmediateRendererBufferSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;

import java.awt.*;
import java.util.List;

public class ESP2Module extends Module {

    public final MultiRegistryValue<EntityType<?>> entities = new MultiRegistryValue<>(
            this,
            "Entities",
            "Which entities to highlight",
            Registries.ENTITY_TYPE,
            List.of(EntityType.PLAYER)
    );

    public final BooleanValue includeSelf = new BooleanValue(
            this,
            "Include Self",
            "Whether to include the player in the ESP",
            false
    );

    public final EnumModeValue<ESPMode> mode = new EnumModeValue<>(
            this,
            "Mode",
            "Which shader to use for the outline",
            ESPMode.OUTLINE,
            ESPMode.values()
    );

    public final FloatValue outlineWidth = new FloatValue(
            this,
            "Outline Width",
            "Width of the outline",
            (float) Math.PI,
            3.0f,
            10.0f
    );

    public final FloatValue outlineAccuracy = new FloatValue(
            this,
            "Outline Accuracy",
            "Accuracy of the outline (higher = less accurate)",
            1.0f,
            1.0f,
            10.0f
    );

    public final FloatValue glowExponent = new FloatValue(
            this,
            "Glow Exponent",
            "Exponent of the glow effect",
            0.05f,
            0.6f,
            8.0f
    ).visibleCondition(() -> mode.getValue() == ESPMode.GLOW);

    public final BooleanValue rainbowOutline = new BooleanValue(
            this,
            "Rainbow Outline",
            "Whether to use a rainbow color for the outline",
            false
    );

    public final BooleanValue textureOutline = new BooleanValue(
            this,
            "Texture Outline",
            "Whether to use a texture outline instead of a colored one",
            false
    ).visibleCondition(() -> !rainbowOutline.getValue());

    public final EnumModeValue<FillMode> fillMode = new EnumModeValue<>(
            this,
            "Fill",
            "Which fill mode to use",
            FillMode.NONE,
            FillMode.values()
    );

    public final EnumModeValue<FillShader> fillShader = new EnumModeValue<>(
            this,
            "Fill Shader",
            "Which shader to use for the fill",
            FillShader.WHITE,
            FillShader.values()
    ).visibleCondition(() -> fillMode.getValue() != FillMode.NONE);

    public final FloatValue fillOpacity = new FloatValue(
            this,
            "Fill Opacity",
            "Opacity of the fill",
            0.0f,
            0.4f,
            1.0f
    ).visibleCondition(() -> fillMode.getValue() != FillMode.NONE);

    private ImmediateRenderer outlineRenderer;
    private ImmediateRendererBufferSource outlineBufferSource;
    private ImmediateRenderer fillRenderer;
    private ImmediateRendererBufferSource fillBufferSource;

    public ESP2Module() {
        super("ESP 2", "Lets you see blocks or entities trough blocks.", Category.RENDER);
    }

    @Override
    protected void onActivate() {
        if (this.outlineRenderer == null) {
            this.outlineRenderer = new ImmediateRenderer(Buffers.getImmediateBufferPool());
            this.outlineBufferSource = new ImmediateRendererBufferSource(this.outlineRenderer);
        }
        if (this.fillRenderer == null) {
            this.fillRenderer = new ImmediateRenderer(Buffers.getImmediateBufferPool());
            this.fillBufferSource = new ImmediateRendererBufferSource(this.fillRenderer);
        }
    }

    public boolean shouldOutlineEntity(Entity entity) {
        if (this.includeSelf.getValue() && entity == mc.player) return true;
        else if (entity == mc.player) return false;
        return this.entities.isSelected(entity.getType());
    }

    public Color getColorForEntity(Entity entity) {
        return entity == mc.player ? Color.ORANGE : Color.WHITE; // TODO: Add setting for this
    }

    public boolean isFillEnabled() {
        return this.fillMode.getValue() != FillMode.NONE;
    }

    public boolean isFillTexture() {
        return switch (this.fillShader.getValue()) {
            case TEXTURE, INVERTEDTEXTURE -> true;
            default -> false;
        };
    }

    public VertexConsumerProvider getOutlineBufferSource() {
        return this.outlineBufferSource;
    }

    public VertexConsumerProvider getFillBufferSource() {
        return this.fillBufferSource;
    }

    public OutlineEffect getOutlineEffect() {
        return switch (this.mode.getValue()) {
            case OUTLINE -> Shaders.getOuterOutlineEffect();
            case FASTOUTLINE -> Shaders.getFastOuterOutlineEffect();
            case INNEROUTLINE -> Shaders.getInnerOutlineEffect();
            case GLOW -> Shaders.getGlowOutlineEffect();
        };
    }

    public void prepareRenderingFor(Entity entity) {
        this.prepareRendering();
    }

    public void prepareRendering() {
        final OutlineEffect effect = this.getOutlineEffect();
        effect.setOutlineWidth(this.outlineWidth.getValue());
        effect.setOutlineAccuracy(this.outlineAccuracy.getValue());
        if (effect instanceof final GlowOutlineEffect glow) {
            glow.setExponent(this.glowExponent.getValue());
        }
    }

    public void renderOutline() {
        this.outlineBufferSource.draw();
        this.outlineRenderer.draw();
    }

    public void renderFill() {
        if (!this.isFillEnabled()) return;
        final FillEffect effect = switch (this.fillShader.getValue()) {
            case WHITE -> {
                Shaders.getColorFillEffect().setColor(new Color(1.0f, 1.0f, 1.0f, this.fillOpacity.getValue()));
                yield Shaders.getColorFillEffect();
            }
            case RAINBOW -> {
                Shaders.getRainbowFillEffect().setOpacity(this.fillOpacity.getValue());
                yield Shaders.getRainbowFillEffect();
            }
            case TEXTURE -> {
                Shaders.getTextureFillEffect().setFlipY(false);
                Shaders.getTextureFillEffect().setTextureId(Shaders.getTextureFillEffect().maskFramebuffer().get().getColorAttachment());
                Shaders.getTextureFillEffect().setOpacity(this.fillOpacity.getValue());
                yield Shaders.getTextureFillEffect();
            }
            case INVERTEDTEXTURE -> {
                Shaders.getInvertedTextureFillEffect().setFlipY(false);
                Shaders.getInvertedTextureFillEffect().setTextureId(Shaders.getInvertedTextureFillEffect().maskFramebuffer().get().getColorAttachment());
                Shaders.getInvertedTextureFillEffect().setOpacity(this.fillOpacity.getValue());
                yield Shaders.getInvertedTextureFillEffect();
            }
        };
        final boolean onlyInvisibleParts = this.fillMode.getValue() == FillMode.INVISIBLEPARTS;
        final Framebuffer targetBuffer = onlyInvisibleParts ? Shaders.getDepthFilterEffect().maskFramebuffer().get() : effect.maskFramebuffer().get();
        Shaders.runWithOutlineBufferSwapped(targetBuffer, () -> {
            DebugHelper.pushMarker("ESP Fill");
            if (onlyInvisibleParts) {
                Shaders.getDepthFilterEffect().setDepthTextureId(mc.getFramebuffer().getDepthAttachment());
                Shaders.getDepthFilterEffect().bindMask();
            } else {
                RenderSystem.disableDepthTest();
                effect.bindMask();
            }
            this.fillBufferSource.draw();
            this.fillRenderer.draw();
            if (onlyInvisibleParts) {
                DebugHelper.pushMarker("ESP Depth Filter");
                Shaders.getDepthFilterEffect().renderFullscreen(effect.maskFramebuffer().get(), false);
                DebugHelper.popMarker();
            }
            RenderSystem.disableDepthTest();
            effect.renderFullscreen(mc.getFramebuffer(), false);
            RenderSystem.enableDepthTest();
            DebugHelper.popMarker();
        });
    }

    public void renderESP() {
        Shaders.runWithOutlineBufferSwapped(this.getOutlineEffect().maskFramebuffer().get(), () -> {
            RenderSystem.disableDepthTest();
            final boolean rainbow = this.rainbowOutline.getValue();
            this.prepareRendering();
            this.getOutlineEffect().bindMask();
            this.renderOutline();
            if (rainbow) {
                Shaders.getRainbowFillEffect().setOpacity(1.0f);
                this.getOutlineEffect().renderFullscreen(Shaders.getRainbowFillEffect().maskFramebuffer().get(), false);
                Shaders.getRainbowFillEffect().renderFullscreen(mc.getFramebuffer(), false);
            } else {
                this.getOutlineEffect().renderFullscreen(mc.getFramebuffer(), false);
            }
            RenderSystem.enableDepthTest();
        });
    }

    @RequiredArgsConstructor
    public enum ESPMode implements IName {
        OUTLINE("Outline"),
        FASTOUTLINE("Fast-Outline"),
        INNEROUTLINE("Inner-Outline"),
        GLOW("Glow");

        @Getter
        private final String name;
    }

    @RequiredArgsConstructor
    public enum FillMode implements IName {
        NONE("None"),
        ALWAYS("Always"),
        INVISIBLEPARTS("InvisibleParts");

        @Getter
        private final String name;
    }

    @RequiredArgsConstructor
    public enum FillShader implements IName {
        WHITE("White"),
        RAINBOW("Rainbow"),
        TEXTURE("Texture"),
        INVERTEDTEXTURE("InvertedTexture");

        @Getter
        private final String name;
    }
}
