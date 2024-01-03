/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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

import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.ParticleTracker;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;

public class VisualThrottleModule extends AbstractModule {

    public final IntegerValue minSodiumEntityAverageSideLength = new IntegerValue(
            this,
            "Min sodium entity average side length",
            "How long should a average side from an entity be to get always rendered to prevent sodium crash.",
            10,
            10,
            1000
    ).visibleCondition(() -> FabricLoader.getInstance().isModLoaded("sodium"));

    public final HashMap<String, ParticleTracker> particleTrackerMap = new HashMap<>();

    public final BooleanValue blockTooManyParticles = new BooleanValue(
            this,
            "Block too many particles",
            "Blocks particles when their count is too high.",
            false
    );

    public final IntegerValue countToBlockParticles = new IntegerValue(
            this,
            "Count to block particles",
            "Set the count to block the particles (per particle type).",
            10,
            2,
            1000
    ).visibleCondition(this.blockTooManyParticles::getValue);

    public final IntegerValue particleBlockingCountResetDelay = new IntegerValue(
            this,
            "Particle blocking reset delay",
            "Set the delay to reset the particle blocking count (per particle type).",
            100,
            1,
            1000
    ).visibleCondition(this.blockTooManyParticles::getValue);

    public final BooleanValue modifyDisplayNameLength = new BooleanValue(
            this,
            "Modify display name length",
            "If activated allows you to modify the max display name length.",
            true
    );

    public final IntegerValue maxDisplayNameLength = new IntegerValue(
            this,
            "Max display name length",
            "How long can a display name be.",
            250,
            3,
            500
    ).visibleCondition(this.modifyDisplayNameLength::getValue);

    public final BooleanValue blockStructureBlockBoundingBoxes = new BooleanValue(
            this,
            "Block structure block bounding boxes",
            "Blocks the bounding boxes from structure blocks.",
            true
    );

    public final BooleanValue blockStructureBlockAirBoxes = new BooleanValue(
            this,
            "Block structure block air boxes",
            "Blocks the air boxes from structure blocks.",
            true
    );

    public VisualThrottleModule() {
        super(
                "Visual Throttle",
                "Limits the game rendering to enhance performance or even prevent crashes.",
                Category.RENDER
        );
        this.markExperimental();
    }

}
