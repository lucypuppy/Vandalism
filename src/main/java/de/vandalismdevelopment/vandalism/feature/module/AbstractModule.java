package de.vandalismdevelopment.vandalism.feature.module;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.base.value.IValue;
import de.vandalismdevelopment.vandalism.base.value.Value;
import de.vandalismdevelopment.vandalism.base.value.ValueCategory;
import de.vandalismdevelopment.vandalism.base.value.impl.BooleanValue;
import de.vandalismdevelopment.vandalism.base.value.impl.KeyInputValue;
import de.vandalismdevelopment.vandalism.base.value.impl.list.ModuleModeValue;
import de.vandalismdevelopment.vandalism.feature.Feature;
import de.vandalismdevelopment.vandalism.util.ChatUtil;
import de.vandalismdevelopment.vandalism.util.GlfwKeyName;
import net.raphimc.vialoader.util.VersionRange;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractModule extends Feature implements IValue {

    private final List<Value<?>> values = new ArrayList<>();

    private final BooleanValue active;
    private final BooleanValue favorite;
    private final BooleanValue showInHUD;
    private final KeyInputValue keyBind;

    private boolean disableOnQuit;
    private boolean disableOnShutdown;

    public AbstractModule(String name, String description, Category category) {
        this(name, description, category, null); // Java is just so much fun
    }

    public AbstractModule(String name, String description, Category category, VersionRange supportedVersions) {
        super(name, description, category, supportedVersions);

        this.active = new BooleanValue("Active", "Whether this module is active.", this, false).
                valueChangeConsumer((oldValue, newValue) -> {
            if (Vandalism.getInstance().getClientSettings().getMenuSettings().moduleStateLogging.getValue()) {
                if (this.mc.player != null) {
                    ChatUtil.infoChatMessage(this.getName() + " has been " + (newValue ? "enabled" : "disabled") + ".");
                }
            }
            if (newValue) {
                this.onEnable();
            } else {
                this.onDisable();
            }
            recursiveUpdateActiveState(newValue, this.values);
        });
        this.favorite = new BooleanValue("Favorite", "Whether this module is a favorite.", this, false);
        this.showInHUD = new BooleanValue("Show in HUD", "Whether this module should be shown in the HUD.", this, true);
        this.keyBind = new KeyInputValue("Key Bind", "The key bind of this module.", this, GlfwKeyName.UNKNOWN);
    }

    public void enableDefault() {
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

    protected void onEnable() {
    }

    protected void onDisable() {
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

    public GlfwKeyName getKeyBind() {
        return this.keyBind.getValue();
    }

    public void setKeyBind(final GlfwKeyName keyBind) {
        this.keyBind.setValue(keyBind);
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
            if (value instanceof final ValueCategory valueCategory) {
                recursiveUpdateActiveState(active, valueCategory.getValues());
            } else if (value instanceof final ModuleModeValue<?> moduleModeValue) {
                if (active) {
                    moduleModeValue.getSelectedMode().onEnable();
                } else {
                    moduleModeValue.getSelectedMode().onDisable();
                }
            }
        }
    }

    @Override
    public String getValueName() {
        return this.getName();
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

}
