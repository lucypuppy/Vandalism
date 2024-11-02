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

package de.nekosarekawaii.vandalism.feature.hud.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.misc.ColorValue;
import de.nekosarekawaii.vandalism.base.value.impl.misc.EasingTypeValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.internal.ModuleToggleListener;
import de.nekosarekawaii.vandalism.feature.hud.HUDElement;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.interfaces.Easing;
import de.nekosarekawaii.vandalism.util.render.Buffers;
import de.nekosarekawaii.vandalism.util.render.Shaders;
import de.nekosarekawaii.vandalism.util.render.gl.render.AttribConsumerProvider;
import de.nekosarekawaii.vandalism.util.render.gl.render.ImmediateRenderer;
import de.nekosarekawaii.vandalism.util.render.util.AlignmentX;
import de.nekosarekawaii.vandalism.util.render.util.AlignmentY;
import de.nekosarekawaii.vandalism.util.render.util.ColorUtils;
import de.nekosarekawaii.vandalism.util.render.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector2f;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ModuleListHUDElement extends HUDElement implements ModuleToggleListener {

    private final List<String> activatedModules = new CopyOnWriteArrayList<>();
    private final List<String> externalModules = new CopyOnWriteArrayList<>();
    private final HashMap<String, AnimationState> animationStates = new HashMap<>();

    private boolean sort;

    private final BooleanValue showExternalClientName = new BooleanValue(
            this,
            "Show External Client Name",
            "Whether or not to show the name of an external client.",
            true
    ).onValueChange((oldValue, newValue) -> this.sort = true);

    private final BooleanValue shadow = new BooleanValue(
            this,
            "Shadow",
            "Whether or not the text should have a shadow.",
            true
    );

    private final IntegerValue heightOffset = new IntegerValue(
            this,
            "Height Offset",
            "The height offset of the text.",
            0,
            -1,
            5
    );

    private final ColorValue color = new ColorValue(
            this,
            "Color",
            "The color of the text."
    );

    private final BooleanValue glowOutline = new BooleanValue(
            this,
            "Glow Outline",
            "Activates/Deactivates the glow outline.",
            false
    );

    private final ColorValue glowOutlineColor = new ColorValue(
            this,
            "Glow Outline Color",
            "The color of the glow outline.",
            Color.lightGray
    ).visibleCondition(this.glowOutline::getValue);

    private final FloatValue glowOutlineWidth = new FloatValue(
            this,
            "Glow Outline Width",
            "The width of the glow outline.",
            6.0f,
            1.0f,
            20.0f
    ).visibleCondition(this.glowOutline::getValue);

    private final FloatValue glowOutlineAccuracy = new FloatValue(
            this,
            "Glow Outline Accuracy",
            "The accuracy of the glow outline.",
            1.0f,
            1.0f,
            8.0f
    ).visibleCondition(this.glowOutline::getValue);

    private final FloatValue glowOutlineExponent = new FloatValue(
            this,
            "Glow Outline Exponent",
            "The exponent of the glow outline.",
            0.22f,
            0.01f,
            4.0f
    ).visibleCondition(this.glowOutline::getValue);

    public final EasingTypeValue animationIn = new EasingTypeValue(this, "Zoom-in Animation", "The easing animation to use when zooming in", Easing.LINEAR);
    public final EasingTypeValue animationOut = new EasingTypeValue(this, "Zoom-out Animation", "The easing animation to use when zooming out", Easing.LINEAR);
    private final FloatValue animationSpeed = new FloatValue(
            this,
            "Animation Speed",
            "Speed of the modlist sliding animation.",
            1f,
            0.1f,
            2.0f
    );

    public ModuleListHUDElement() {
        super("Module List", true, AlignmentX.RIGHT, AlignmentY.TOP);
        this.alignmentX.onValueChange((oldValue, newValue) -> this.sort = true);
        this.alignmentY.onValueChange((oldValue, newValue) -> this.sort = true);
        Vandalism.getInstance().getEventSystem().subscribe(ModuleToggleEvent.ID, this);
    }

    @Override
    public void onModuleToggle(final ModuleToggleEvent event) {
        this.sort = true;
    }

    @Override
    protected void onRender(final DrawContext context, final float delta, final boolean inGame) {
        this.sort();
        if (this.glowOutline.getValue()) {
            this.draw(context, delta, true);
        }
        this.draw(context, delta, false);
    }

    private void draw(final DrawContext context, final float delta, final boolean isPostProcessing) {
        try (final ImmediateRenderer renderer = new ImmediateRenderer(Buffers.getImmediateBufferPool())) {
            final float outlineWidth = this.glowOutlineWidth.getValue();
            final float outlineAccuracy = this.glowOutlineAccuracy.getValue();
            final float outlineExponent = this.glowOutlineExponent.getValue();
            final Color glowOutlineColor = this.glowOutlineColor.getColor();
            final Vector2f sizeVec = new Vector2f();
            if (isPostProcessing) {
                Shaders.getGlowOutlineEffect().configure(outlineWidth, outlineAccuracy, outlineExponent);
                Shaders.getGlowOutlineEffect().bindMask();
            }
            int yOffset = 0;
            this.width = 0;
            for (final String activatedModule : this.activatedModules) {
                final AnimationState animationState = this.animationStates.get(activatedModule);

                if (!isPostProcessing)
                    animationState.updateAnimation(activatedModule, delta);

                this.getTextSize(activatedModule, sizeVec);
                final float textWidth = (int) sizeVec.x * animationState.xAnimation;
                final float textHeight = (int) sizeVec.y;
                switch (this.alignmentX.getValue()) {
                    case MIDDLE ->
                            this.drawText(renderer, context, animationState, activatedModule, this.getX() - textWidth / 2.f, this.getY() + yOffset + this.heightOffset.getValue(), isPostProcessing);
                    case RIGHT ->
                            this.drawText(renderer, context, animationState, activatedModule, this.getX() - textWidth, this.getY() + yOffset + this.heightOffset.getValue(), isPostProcessing);
                    default ->
                            this.drawText(renderer, context, animationState, activatedModule, this.getX(), this.getY() + yOffset + this.heightOffset.getValue(), isPostProcessing);
                }

                this.width = (int) Math.max(this.width, textWidth);
                yOffset += (int) (textHeight * animationState.yAnimation + this.heightOffset.getValue());
            }
            this.height = yOffset;
            renderer.draw();
            if (isPostProcessing) {
                Shaders.getGlowOutlineEffect().renderFullscreen(Shaders.getColorFillEffect().maskFramebuffer().get(), false);
                Shaders.getColorFillEffect().setColor(glowOutlineColor);
                Shaders.getColorFillEffect().renderFullscreen(mc.getFramebuffer(), false);
            }
        }
    }

    private void drawText(AttribConsumerProvider batch, final DrawContext context, AnimationState animationState, final String text, final float x, final float y, final boolean isPostProcessing) {
        final int alpha = MathHelper.clamp((int) (255 * animationState.xAnimation), 0, 255);

        if (this.glowOutline.getValue()) {
            RenderUtil.fill(context,
                    x - 2,
                    y,
                    x + this.getTextWidth(text) + 2,
                    y + heightOffset.getValue() + this.getFontHeight(),
                    1677721600);
        }

        if (!isPostProcessing) {
            final Color textColor = ColorUtils.withAlpha(this.color.getColor((int) (-y * 20)), alpha);
            this.drawText(batch, text, context, x, y, this.glowOutline.getValue() || this.shadow.getValue(), textColor.getRGB());
        }
    }

    private void sort() {
        if (this.sort) {
            this.sort = false;

            for (final Module module : Vandalism.getInstance().getModuleManager().getList()) {
                final String name = module.getName();

                if (module.isActive() && module.isShowInHUD()) {
                    if (!this.animationStates.containsKey(name)) {
                        this.activatedModules.add(name);

                        final AnimationState animationState = new AnimationState();
                        animationState.yAnimation = 0;
                        animationState.xAnimation = 0;
                        animationState.showModule = true;
                        this.animationStates.put(name, animationState);
                    }
                } else if (this.animationStates.containsKey(name)) {
                    this.animationStates.get(name).showModule = false;
                }
            }

            for (String activatedModule : this.externalModules) {
                if (!this.animationStates.containsKey(activatedModule)) {
                    final AnimationState animationState = new AnimationState();
                    animationState.yAnimation = 0;
                    animationState.xAnimation = 0;
                    animationState.showModule = true;
                    this.animationStates.put(activatedModule, animationState);

                    if (!this.showExternalClientName.getValue()) {
                        activatedModule = activatedModule.split("\\s", 2)[1];
                    }

                    this.activatedModules.add(activatedModule);
                }
            }

            this.activatedModules.sort((s1, s2) -> switch (this.alignmentY.getValue()) {
                case TOP, MIDDLE -> this.getTextWidth(s2) - this.getTextWidth(s1);
                case BOTTOM -> this.getTextWidth(s1) - this.getTextWidth(s2);
            });
        }
    }

    public void addExternalModule(final String source, final String name) {
        final String module = source + " " + name;
        if (this.externalModules.contains(module)) {
            return;
        }
        this.externalModules.add(module);
        this.sort = true;
    }

    public void removeExternalModule(final String source, final String name) {
        final String realName = source + " " + name;
        this.externalModules.remove(realName);
        this.sort = true;

        final AnimationState state = this.animationStates.get(realName);
        if (state != null) state.showModule = false;
    }

    public void markForSorting() {
        sort = true;
    }

    private class AnimationState {
        public float yAnimation;
        public float xAnimation;
        public boolean showModule;

        private float xProgress = 0f;
        private float yProgress = 0f;

        public void updateAnimation(final String activatedModule, final float delta) {
            final float duration = 1.0f; // Duration in seconds for the easing function
            final float startValue = 0f; // Starting animation value
            final float endValue = 1.0f; // Ending animation value
            final float increment = (animationSpeed.getValue() / 10f); // Animation increment per frame (adjust as needed)

            if (showModule) {
                // yAnimation is animated first until it reaches the end value
                if (yAnimation < 1) {
                    yProgress += increment / duration; // Increase progress
                    yProgress = Math.min(yProgress, 1); // Clamp progress to max 1
                    yAnimation = animationIn.getValue().easePercent(yProgress, startValue, endValue, duration);
                } else {
                    // Once yAnimation is complete, start animating xAnimation
                    if (xAnimation < 1) {
                        xProgress += increment / duration; // Increase progress
                        xProgress = Math.min(xProgress, 1); // Clamp progress to max 1
                        xAnimation = animationIn.getValue().easePercent(xProgress, startValue, endValue, duration);
                    }
                }
            } else {
                // Reverse animation for xAnimation first
                if (xAnimation > 0) {
                    xProgress -= increment / duration; // Decrease progress
                    xProgress = Math.max(xProgress, 0); // Clamp progress to min 0
                    xAnimation = animationOut.getValue().easePercent(xProgress, startValue, endValue, duration);
                } else if (yAnimation > 0) {
                    // Once xAnimation is reversed, start reversing yAnimation
                    yProgress -= increment / duration; // Decrease progress
                    yProgress = Math.max(yProgress, 0); // Clamp progress to min 0
                    yAnimation = animationOut.getValue().easePercent(yProgress, startValue, endValue, duration);

                    if (yProgress <= 0) { // Reset and remove the module when the animation is completev
                        animationStates.remove(activatedModule);
                        activatedModules.remove(activatedModule);
                    }
                }
            }
        }
    }

}
