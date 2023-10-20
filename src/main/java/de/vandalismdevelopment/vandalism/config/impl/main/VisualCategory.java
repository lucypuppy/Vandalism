package de.vandalismdevelopment.vandalism.config.impl.main;

import de.vandalismdevelopment.vandalism.config.impl.MainConfig;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.ValueCategory;
import de.vandalismdevelopment.vandalism.value.values.BooleanValue;
import de.vandalismdevelopment.vandalism.value.values.ListValue;
import de.vandalismdevelopment.vandalism.value.values.number.slider.SliderFloatValue;
import org.apache.commons.lang3.RandomStringUtils;

public class VisualCategory extends ValueCategory {

    public VisualCategory(final MainConfig parent) {
        super("Visual", "Visual related settings.", parent);
    }

    private final ValueCategory blockHitCategory = new ValueCategory(
            "BlockHit",
            "BlockHit settings (<=1.8.x)",
            this
    );

    public final ListValue blockHitAnimations = new ListValue(
            "BlockHit Animations",
            "Change the BlockHit Animation.",
            this.blockHitCategory,
            "None",
            "Own",
            "Suicide"
    );

    public final Value<Boolean> blockHitAnimation = new BooleanValue(
            "BlockHit Animation",
            "Enable/Disable BlockHit Animation.",
            this.blockHitCategory,
            true
    );

    public final Value<Float> blockItemSize = new SliderFloatValue(
            "Item Size",
            "Change the size of items.",
            this.blockHitCategory,
            1.0f,
            0.1f,
            2.0f,
            "%.2f"
    );

    public final Value<Boolean> customBobView = new BooleanValue(
            "Custom Bob View",
            "If enabled allows you to customize the bob view camera effect.",
            this,
            false
    );

    public final Value<Float> customBobViewValue = new SliderFloatValue(
            "Custom Bob View Value",
            "Here you can change the custom bob view value.-",
            this,
            5.0f,
            0.0f,
            50.0f,
            "%.2f"
    ).visibleConsumer(this.customBobView::getValue);

    public final static String SIGN_HIDE_SECRET = RandomStringUtils.randomAlphanumeric(4);

    public final Value<Boolean> hideSignText = new BooleanValue(
            "Hide Sign Text",
            "Hides text of signs when creating a new one.",
            this,
            false
    );

    public final Value<Float> shieldAlpha = new SliderFloatValue(
            "Shield Alpha",
            "Change the alpha of a shield.",
            this,
            1.0f,
            0.1f,
            1.0f,
            "%.2f"
    );

    public final Value<Float> fireOverlayOffset = new SliderFloatValue(
            "Fire Overlay Offset",
            "Change the Fire Overlay Offset.",
            this,
            0.0f,
            0.0f,
            0.4f,
            "%.2f"
    );

    public final Value<Boolean> portalScreen = new BooleanValue(
            "Portal Screen",
            "If this option is enabled you are allowed to use screens in portals.",
            this,
            true
    );

    public final Value<Boolean> waterOverlay = new BooleanValue(
            "Water Overlay",
            "Enable/Disable Water Overlay.",
            this,
            true
    );

    public final Value<Boolean> inWallOverlay = new BooleanValue(
            "In Wall Overlay",
            "Enable/Disable In Wall Overlay.",
            this,
            true
    );

    public final Value<Boolean> freezeOverlay = new BooleanValue(
            "Freeze Overlay",
            "Enable/Disable Freeze Overlay.",
            this,
            true
    );

    public final Value<Boolean> pumpkinOverlay = new BooleanValue(
            "Pumpkin Overlay",
            "Enable/Disable Pumpkin Overlay.",
            this,
            true
    );

    public final Value<Boolean> spyGlassOverlay = new BooleanValue(
            "Spyglass Overlay",
            "Enable/Disable Spyglass Overlay.",
            this,
            true
    );

    public final Value<Boolean> nauseaOverlay = new BooleanValue(
            "Nausea Overlay",
            "Enable/Disable Nausea Overlay.",
            this,
            true
    );

    public final Value<Boolean> blindnessEffect = new BooleanValue(
            "Blindness Effect",
            "Enable/Disable Blindness Effect.",
            this,
            true
    );
    public final Value<Boolean> hurtCam = new BooleanValue(
            "Hurt Cam",
            "Enable/Disable Hurt Cam.",
            this,
            true
    );

    public final Value<Boolean> fullBright = new BooleanValue(
            "Full Bright",
            "Enable/Disable Full Bright.",
            this,
            false
    );
    
}
