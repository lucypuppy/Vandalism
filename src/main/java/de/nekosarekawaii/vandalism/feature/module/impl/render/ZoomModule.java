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

package de.nekosarekawaii.vandalism.feature.module.impl.render;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.misc.EasingTypeValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.event.game.MouseInputListener;
import de.nekosarekawaii.vandalism.event.game.SmoothCameraRotationsListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.interfaces.Easing;

public class ZoomModule extends Module implements MouseInputListener, SmoothCameraRotationsListener {

    public final DoubleValue zoomFov = new DoubleValue(this, "Zoom FOV", "The FOV to use when zooming in", 30.0, 1.0, 70.0);
    public final EasingTypeValue animationIn = new EasingTypeValue(this, "Zoom-in Animation", "The easing animation to use when zooming in", Easing.LINEAR);
    public final EasingTypeValue animationOut = new EasingTypeValue(this, "Zoom-out Animation", "The easing animation to use when zooming out", Easing.LINEAR);
    public final IntegerValue animationDuration = new IntegerValue(this, "Animation Duration", "The duration of the zoom animation in milliseconds", 100, 0, 1000);
    public final BooleanValue scroll = new BooleanValue(this, "Scroll", "If enabled, you can zoom in by scrolling the mouse wheel", false);
    public final BooleanValue smoothRotations = new BooleanValue(this, "Smooth Rotations", "If enabled, the camera will rotate smoothly when zooming in", false);

    private long animTime;
    private double scrollAmount;

    public ZoomModule() {
        super("Zoom", "Allows you to zoom in.", Category.RENDER);
        this.deactivateOnReleaseDefault();
    }

    @Override
    public void onActivate() {
        this.scrollAmount = 0.0;
        Vandalism.getInstance().getEventSystem().subscribe(this, MouseEvent.ID);
        Vandalism.getInstance().getEventSystem().subscribe(this, SmoothCameraRotationsEvent.ID);
        final long now = System.currentTimeMillis();
        final long animEnd = this.animTime + this.animationDuration.getValue();
        if (animEnd < now) {
            this.animTime = System.currentTimeMillis();
        } else {
            final float progress = (now - this.animTime) / (float) this.animationDuration.getValue();
            this.setAnimProgress(now, 1.0f - progress);
        }
    }

    private void setAnimProgress(final long now, final float progress) {
        this.animTime = now - (long) (progress * this.animationDuration.getValue());
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, MouseEvent.ID);
        Vandalism.getInstance().getEventSystem().unsubscribe(this, SmoothCameraRotationsEvent.ID);
        final long now = System.currentTimeMillis();
        final long animEnd = this.animTime + this.animationDuration.getValue();
        if (animEnd < now) {
            this.animTime = System.currentTimeMillis();
        } else {
            final float progress = (now - this.animTime) / (float) this.animationDuration.getValue();
            this.setAnimProgress(now, 1.0f - progress);
        }
    }

    @Override
    public void onMouse(final MouseEvent event) {
        if (event.type == Type.SCROLL) {
            if (!this.scroll.getValue()) return;
            this.scrollAmount += event.vertical * 2.0;
            if (this.scrollAmount < 0) this.scrollAmount = 0;
            event.setCancelled(true);
        }
    }

    @Override
    public void onSmoothCameraRotations(final SmoothCameraRotationsEvent event) {
        if (this.smoothRotations.getValue()) {
            event.smoothCamera = true;
        }
    }

    /**
     * Not using CameraFOVEvent because it's not called when zooming out, because the event is only called when the module is enabled
     */
    public double getFov(final double fov) {
        if (this.zoomFov.getValue() - this.scrollAmount < this.zoomFov.getMinValue()) {
            this.scrollAmount = this.zoomFov.getValue() - this.zoomFov.getMinValue();
        }
        final double targetFOV = Math.max(1, Math.min(this.zoomFov.getValue() - this.scrollAmount, this.zoomFov.getMaxValue()));
        if (this.animationDuration.getValue() < 1) {
            return this.isActive() ? targetFOV : fov;
        }
        final long now = System.currentTimeMillis();
        if (this.isActive()) {
            final long startTime = this.animTime + (long) this.animationDuration.getValue();
            if (now >= startTime) {
                return targetFOV;
            }
            // return this.interpolate(targetFOV, fov, (startTime - now) / (double) this.animationDuration.getInt());
            return this.animationIn.getValue().easePercent((startTime - now) / 1000.0f, (float) targetFOV, (float) fov, this.animationDuration.getValue() / 1000.0f);
        }
        final long stopTime = this.animTime + (long) this.animationDuration.getValue();
        if (now >= stopTime) {
            return fov;
        }
        // return this.interpolate(fov, targetFOV, (stopTime - now) / (double) this.animationDuration.getInt());
        return this.animationOut.getValue().easePercent((stopTime - now) / 1000.0f, (float) fov, (float) targetFOV, this.animationDuration.getValue() / 1000.0f);
    }

}
