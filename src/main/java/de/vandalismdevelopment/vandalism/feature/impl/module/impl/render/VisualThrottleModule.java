package de.vandalismdevelopment.vandalism.feature.impl.module.impl.render;

import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.impl.module.Module;
import de.vandalismdevelopment.vandalism.util.ParticleTracker;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.impl.BooleanValue;
import de.vandalismdevelopment.vandalism.value.impl.number.slider.SliderIntegerValue;
import net.fabricmc.loader.api.FabricLoader;

import java.util.HashMap;

public class VisualThrottleModule extends Module {

    public final Value<Integer> minSodiumEntityAverageSideLength = new SliderIntegerValue(
            "Min sodium entity average side length",
            "How long should a average side from an entity be to get always rendered to prevent sodium crash.",
            this,
            10,
            10,
            1000
    ).visibleConsumer(() -> FabricLoader.getInstance().isModLoaded("sodium"));

    public final HashMap<String, ParticleTracker> particleTrackerMap = new HashMap<>();

    public final Value<Boolean> blockTooManyParticles = new BooleanValue(
            "Block too many particles",
            "Blocks particles when their count is too high.",
            this,
            false
    );

    public final Value<Integer> countToBlockParticles = new SliderIntegerValue(
            "Count to block particles",
            "Set the count to block the particles (per particle type).",
            this,
            10,
            2,
            1000
    ).visibleConsumer(this.blockTooManyParticles::getValue);

    public final Value<Integer> particleBlockingCountResetDelay = new SliderIntegerValue(
            "Particle blocking reset delay",
            "Set the delay to reset the particle blocking count (per particle type).",
            this,
            100,
            1,
            1000
    ).visibleConsumer(this.blockTooManyParticles::getValue);

    public final Value<Boolean> modifyDisplayNameLength = new BooleanValue(
            "Modify display name length",
            "If enabled allows you to modify the max display name length.",
            this,
            true
    );

    public final Value<Integer> maxDisplayNameLength = new SliderIntegerValue(
            "Max display name length",
            "How long can a display name be.",
            this,
            250,
            3,
            500
    ).visibleConsumer(this.modifyDisplayNameLength::getValue);

    public VisualThrottleModule() {
        super(
                "Visual Throttle",
                "Limits the game rendering to enhance performance or even prevent crashes.",
                FeatureCategory.RENDER,
                true,
                false
        );
    }

}
