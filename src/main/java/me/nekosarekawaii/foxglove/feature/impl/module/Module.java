package me.nekosarekawaii.foxglove.feature.impl.module;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.Feature;
import me.nekosarekawaii.foxglove.feature.FeatureType;
import me.nekosarekawaii.foxglove.util.ChatUtils;
import me.nekosarekawaii.foxglove.value.Value;
//import net.raphimc.vialoader.util.VersionEnum;
//import net.raphimc.vialoader.util.VersionRange;

import java.lang.reflect.Field;

/**
 * The Module class is an abstract class that serves as a base for creating modules.
 * Modules are individual features within the Foxglove mod that can be enabled or disabled
 * and provide additional configuration options for customization.
 */
public abstract class Module extends Feature {

    //private VersionRange supportedVersions = null;

    private boolean enabled;

    private final ObjectArrayList<Value<?>> values;

    /**
     * Initializes a new instance of the Module class.
     * Sets the module's name, description, type, category, and experimental status based on the ModuleInfo annotation.
     * Sets the initial state of the module based on the isDefaultEnabled flag in the annotation.
     * The showMenu property is set to false by default.
     */
    public Module() {
        final ModuleInfo moduleInfo = this.getClass().getAnnotation(ModuleInfo.class);
        this.setName(moduleInfo.name());
        this.setDescription(moduleInfo.description());
        this.setType(FeatureType.MODULE);
        this.setCategory(moduleInfo.category());
        this.setExperimental(moduleInfo.isExperimental());
        this.setState(moduleInfo.isDefaultEnabled() && Foxglove.getInstance().isFirstStart());
        this.values = new ObjectArrayList<>();
        for (final Field field : this.getClass().getDeclaredFields()) {
            if (Value.class.isAssignableFrom(field.getType())) {
                try {
                    this.values.add((Value<?>) field.get(this));
                } catch (final IllegalAccessException e) {
                    Foxglove.getInstance().getLogger().error("Failed to add field '" + field.getName() + "' as value inside Module '" + this.getName() + "'", e);
                }
            }
        }
    }


    /**
     * Called when the module is enabled.
     * Override this method in derived classes to implement custom behavior when the module is enabled.
     */
    protected void onEnable() {
    }

    /**
     * Called when the module is disabled.
     * Override this method in derived classes to implement custom behavior when the module is disabled.
     */
    protected void onDisable() {
    }

    /**
     * Enables the module.
     * Sets the module's state to enabled and invokes the onEnable() method.
     */
    public void enable() {
        this.setState(true);
    }

    /**
     * Disables the module.
     * Sets the module's state to disabled and invokes the onDisable() method.
     */
    public void disable() {
        this.setState(false);
    }

    /**
     * Toggles the module's state.
     * If the module is currently enabled, it will be disabled, and vice versa.
     */
    public void toggle() {
        this.setState(!this.enabled);
    }

    /**
     * Sets the state of the module.
     * If the state changes, it invokes the corresponding onEnable() or onDisable() method.
     * It also displays the current state of the module in the chat.
     *
     * @param state The new state of the module (true for enabled, false for disabled).
     */
    public void setState(final boolean state) {
        if (this.enabled != state) {
            this.enabled = state;
            ChatUtils.infoChatMessage(this.getName() + " has been " + (state ? "enabled" : "disabled") + ".");
            if (state) {
                this.onEnable();
            } else {
                this.onDisable();
            }
        }
    }

    /**
     * Checks if the module is currently enabled.
     *
     * @return true if the module is enabled, false otherwise.
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Returns the list of values associated with the module.
     *
     * @return the list of values
     */
    public ObjectArrayList<Value<?>> getValues() {
        return this.values;
    }

    /**
     * Retrieves a Value object by its name from the module's values list.
     *
     * @param name the name of the Value to retrieve
     * @return the Value object if found, null otherwise
     */
    public Value<?> getValue(final String name) {
        for (final Value<?> value : this.values) {
            if (value.getHashIdent().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

    /*public void setSupportedVersions(final VersionRange versionRange) {
        this.supportedVersions = versionRange;
    }

    public boolean isSupported(final VersionEnum version) {
        if (this.supportedVersions == null) return true;
        return this.supportedVersions.contains(version);
    }*/

    @Override
    public String toString() {
        return "{" +
                "name=" + this.getName() +
                ", category=" + this.getCategory() +
                ", enabled=" + this.enabled +
                ", experimental=" + this.isExperimental() +
                '}';
    }

}
