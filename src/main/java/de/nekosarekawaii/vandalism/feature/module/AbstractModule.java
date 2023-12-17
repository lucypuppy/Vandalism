package de.nekosarekawaii.vandalism.feature.module;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.event.internal.ModuleToggleListener;
import de.nekosarekawaii.vandalism.base.value.Value;
import de.nekosarekawaii.vandalism.base.value.ValueParent;
import de.nekosarekawaii.vandalism.base.value.impl.awt.KeyBindValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.template.ValueGroup;
import de.nekosarekawaii.vandalism.feature.Feature;
import de.nekosarekawaii.vandalism.feature.module.template.ModuleModeValue;
import de.nekosarekawaii.vandalism.util.minecraft.ChatUtil;
import net.raphimc.vialoader.util.VersionRange;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractModule extends Feature implements ValueParent {

    private final List<Value<?>> values = new ArrayList<>();

    private final BooleanValue active;
    private final BooleanValue favorite;
    private final BooleanValue showInHUD;
    private final KeyBindValue keyBind;

    private boolean disableOnQuit;
    private boolean disableOnShutdown;

    public AbstractModule(String name, String description, Category category) {
        this(name, description, category, null); // Java is just so much fun
    }

    public AbstractModule(String name, String description, Category category, VersionRange supportedVersions) {
        super(name, description, category, supportedVersions);
        this.active = new BooleanValue(
                this,
                "Active",
                "Whether this module is active.",
                false
        ).onValueChange((oldValue, newValue) -> {
            final var event = new ModuleToggleListener.ModuleToggleEvent(this, newValue);
            Vandalism.getEventSystem().postInternal(ModuleToggleListener.ModuleToggleEvent.ID, event);

            // Allows the event to change the active state of the module
            // It's important that people don't use the setActive method from the module itself in the event
            // because that would cause an infinite loop
            newValue = event.active;

            if (oldValue != newValue) {
                if (newValue) {
                    this.onEnable();
                } else {
                    this.onDisable();
                }
                if (Vandalism.getInstance().getClientSettings().getMenuSettings().moduleStateLogging.getValue() && this.mc.player != null) {
                    ChatUtil.infoChatMessage(this.getName() + " has been " + (newValue ? "activated" : "deactivated") + ".");
                }
                this.recursiveUpdateActiveState(newValue, this.values);
            }
        });
        this.favorite = new BooleanValue(
                this,
                "Favorite",
                "Whether this module is a favorite.",
                false
        );
        this.showInHUD = new BooleanValue(
                this,
                "Show in HUD",
                "Whether this module should be shown in the HUD.",
                true
        );
        this.keyBind = new KeyBindValue(
                this,
                "Key Bind",
                "The key bind of this module."
        );
    }

    public void activateDefault() {
        this.active.setValue(true);
    }

    public void disableAfterSession() {
        this.disableOnQuit = true;
        this.disableOnShutdown = true;
    }

    public void disableOnQuit() {
        this.disableOnQuit = true;
    }

    public void disableOnShutdown() {
        this.disableOnShutdown = true;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public boolean isActive() {
        return this.active.getValue();
    }

    public void toggle() {
        this.active.setValue(!this.active.getValue());
    }

    public boolean isFavorite() {
        return this.favorite.getValue();
    }

    public void setFavorite(final boolean favorite) {
        this.favorite.setValue(favorite);
    }

    public boolean isShowInHUD() {
        return this.showInHUD.getValue();
    }

    public void setShowInHUD(final boolean showInHUD) {
        this.showInHUD.setValue(showInHUD);
    }

    public KeyBindValue getKeyBind() {
        return keyBind;
    }

    public boolean isDisableOnQuit() {
        return disableOnQuit;
    }

    public boolean isDisableOnShutdown() {
        return disableOnShutdown;
    }

    /**
     * Recursively updates the active state of all module modes and their values.
     *
     * @param active Whether the module should be active.
     * @param values The values to update.
     */
    private void recursiveUpdateActiveState(final boolean active, final List<Value<?>> values) {
        if (values == null) return;
        for (final Value<?> value : values) {
            if (value instanceof final ValueGroup valueGroup) {
                recursiveUpdateActiveState(active, valueGroup.getValues());
            } else if (value instanceof final ModuleModeValue<?> moduleModeValue) {
                if (active) {
                    moduleModeValue.getValue().onEnable();
                } else {
                    moduleModeValue.getValue().onDisable();
                }
            }
        }
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

}
