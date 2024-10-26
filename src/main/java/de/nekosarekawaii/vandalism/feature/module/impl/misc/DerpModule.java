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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.integration.rotation.PrioritizedRotation;
import de.nekosarekawaii.vandalism.integration.rotation.RotationUtil;
import de.nekosarekawaii.vandalism.integration.rotation.enums.RotationPriority;
import de.nekosarekawaii.vandalism.util.StringUtils;
import de.nekosarekawaii.vandalism.util.interfaces.IName;

import java.util.concurrent.ThreadLocalRandom;

public class DerpModule extends Module implements PlayerUpdateListener {

    private final EnumModeValue<Mode> mode = new EnumModeValue<>(
            this,
            "Mode",
            "The derp mode.",
            Mode.RANDOM,
            Mode.values()
    );

    private final EnumModeValue<SpinDirection> spinModeDirection = new EnumModeValue<>(
            this,
            "Spin Mode Direction",
            "The direction of the spin.",
            SpinDirection.LEFT,
            SpinDirection.values()
    ).visibleCondition(() -> this.mode.getValue() == Mode.SPIN);

    private final FloatValue spinValue = new FloatValue(
            this,
            "Spin Value",
            "The value of how much to spin.",
            100f,
            50f,
            150f
    ).visibleCondition(() -> this.mode.getValue() == Mode.SPIN);

    private final IntegerValue spinDelay = new IntegerValue(
            this,
            "Spin Delay",
            "The delay in ticks of how long to wait before spinning again.",
            10,
            5,
            100
    ).visibleCondition(() -> this.mode.getValue() == Mode.SPIN);

    private final IntegerValue rotateSpeed = new IntegerValue(
            this,
            "Rotate Speed",
            "The speed to rotate to the target.",
            180,
            1,
            180
    );

    private final BooleanValue useMoveFix = new BooleanValue(
            this,
            "Use Move Fix",
            "Whether or not to use the move fix.",
            false
    );

    public DerpModule() {
        super(
                "Derp",
                "Messes around with your server side rotation.",
                Category.MISC
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getRotationManager().resetRotation();
        Vandalism.getInstance().getEventSystem().subscribe(PlayerUpdateEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(PlayerUpdateEvent.ID, this);
        Vandalism.getInstance().getRotationManager().resetRotation();
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        final PrioritizedRotation currentRotation = Vandalism.getInstance().getRotationManager().getServerRotation();
        if (currentRotation == null) {
            return;
        }
        float yaw = currentRotation.getYaw();
        float pitch = currentRotation.getPitch();
        if (this.mode.getValue() == Mode.RANDOM) {
            yaw += ThreadLocalRandom.current().nextFloat() * 360f - 180f;
            pitch = ThreadLocalRandom.current().nextFloat() * 180f - 90f;
        } else {
            final int spinDelay = this.spinDelay.getValue();
            final float spinValue = this.spinValue.getValue();
            yaw = mc.player.age % spinDelay == 0 ? yaw + (this.spinModeDirection.getValue() == SpinDirection.LEFT ? -spinValue : spinValue) : yaw;
            pitch = (float) (Math.sin((double) mc.player.age / spinDelay) * 90.0f + pitch);
        }
        Vandalism.getInstance().getRotationManager().setRotation(
                new PrioritizedRotation(yaw, pitch, RotationPriority.NORMAL),
                this.useMoveFix.getValue(),
                (targetRotation, serverRotation, deltaTime, hasClientRotation) ->
                        RotationUtil.rotateMouse(targetRotation, serverRotation, this.rotateSpeed.getValue(), deltaTime, hasClientRotation)
        );
    }

    private enum Mode implements IName {

        RANDOM,
        SPIN;

        private final String name;

        Mode() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }

        @Override
        public String getName() {
            return this.name;
        }

    }

    private enum SpinDirection implements IName {

        LEFT,
        RIGHT;

        private final String name;

        SpinDirection() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }

        @Override
        public String getName() {
            return this.name;
        }

    }

}
