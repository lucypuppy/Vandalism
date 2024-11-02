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

package de.nekosarekawaii.vandalism.feature.module.impl.movement.step.impl;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.FloatValue;
import de.nekosarekawaii.vandalism.event.player.StepListener;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.step.StepModule;
import de.nekosarekawaii.vandalism.feature.module.template.module.ModuleMulti;

public class InstantModuleMode extends ModuleMulti<StepModule> implements StepListener {

    private final FloatValue stepHeight = new FloatValue(
            this,
            "Step Height",
            "Allows you to customize the step height.",
            5.0f,
            0.7f,
            10.0f
    );

    public InstantModuleMode() {
        super("Instant");
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(StepEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(StepEvent.ID, this);
    }

    @Override
    public void onStep(final StepEvent event) {
        event.stepHeight = this.stepHeight.getValue();
    }

}
