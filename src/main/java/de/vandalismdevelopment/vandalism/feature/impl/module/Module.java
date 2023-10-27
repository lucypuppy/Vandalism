package de.vandalismdevelopment.vandalism.feature.impl.module;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.Config;
import de.vandalismdevelopment.vandalism.feature.Feature;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.FeatureType;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.render.HeadUpDisplayModule;
import de.vandalismdevelopment.vandalism.util.ChatUtils;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.ValueCategory;
import de.vandalismdevelopment.vandalism.value.values.BooleanValue;
import de.vandalismdevelopment.vandalism.value.values.KeyInputValue;
import de.vandalismdevelopment.vandalism.value.values.list.ModuleModeValue;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public abstract class Module extends Feature implements IValue {

    private final List<Value<?>> values;
    private final BooleanValue enabled, showInModuleList;
    private final KeyInputValue keyCode;

    public Module(final String name, final String description, final FeatureCategory category, final boolean isExperimental, final boolean isDefaultEnabled) {
        this(name, description, category, isExperimental, isDefaultEnabled, GLFW.GLFW_KEY_UNKNOWN);
    }

    public Module(final String name, final String description, final FeatureCategory category, final boolean isExperimental, final boolean isDefaultEnabled, final int keyCode) {
        this.setName(name);
        this.setDescription(description);
        this.setType(FeatureType.MODULE);
        this.setCategory(category);
        this.setExperimental(isExperimental);
        this.values = new ArrayList<>();
        this.enabled = new BooleanValue(
                "Enabled",
                "Whether this module is enabled.",
                this,
                isDefaultEnabled
        ).valueChangedConsumer(value -> {
            if (player() != null) {
                ChatUtils.infoChatMessage(this.getName() + " has been " + (value ? "enabled" : "disabled") + ".");
            }
            final ModuleRegistry moduleRegistry = Vandalism.getInstance().getModuleRegistry();
            if (moduleRegistry != null && moduleRegistry.isDone()) {
                final HeadUpDisplayModule headUpDisplayModule = moduleRegistry.getHeadUpDisplayModule();
                if (headUpDisplayModule != null) headUpDisplayModule.sortEnabledModules();
            }
            if (value) this.onEnable();
            else this.onDisable();
            this.recursiveModeEnable(value, this.values);
        });
        this.showInModuleList = new BooleanValue(
                "Show in Module List",
                "Whether this module should be shown in the module list.",
                this,
                !(this instanceof HeadUpDisplayModule)
        );
        this.keyCode = new KeyInputValue(
                "Keybind",
                "The keybind of this module.",
                this,
                keyCode,
                "unknown"
        );
        this.setState(isDefaultEnabled);
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    public void enable() {
        this.setState(true);
    }

    public void disable() {
        this.setState(false);
    }

    public void toggle() {
        this.setState(!this.enabled.getValue());
    }

    public void setState(final boolean state) {
        if (this.enabled.getValue() != state) {
            this.enabled.setValue(state);
        }
    }

    private void recursiveModeEnable(final boolean state, final List<Value<?>> values) {
        if (values == null) return;
        for (final Value<?> value : values) {
            if (value instanceof final ValueCategory valueCategory) {
                recursiveModeEnable(state, valueCategory.getValues());
            } else if (value instanceof final ModuleModeValue<?> moduleModeValue) {
                if (state) {
                    moduleModeValue.getSelectedMode().onEnable();
                } else {
                    moduleModeValue.getSelectedMode().onDisable();
                }
            }
        }
    }

    public boolean isEnabled() {
        return this.enabled.getValue();
    }

    public void setShowInModuleList(final boolean showInModuleList) {
        this.showInModuleList.setValue(showInModuleList);
    }

    public boolean isShowInModuleList() {
        return this.showInModuleList.getValue();
    }

    public void setKeyCode(final int keyCode) {
        this.keyCode.setKeyCode(keyCode);
    }

    public int getKeyCode() {
        return this.keyCode.getValue().getLeft();
    }

    @Override
    public String toString() {
        return '{' +
                "name=" + this.getName() +
                ", category=" + this.getCategory() +
                ", enabled=" + this.enabled +
                ", experimental=" + this.isExperimental() +
                ", keyCode=" + this.keyCode.getValue().getRight() +
                '}';
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

    @Override
    public Config getConfig() {
        return Vandalism.getInstance().getConfigManager().getModulesConfig();
    }

    @Override
    public String getValueName() {
        return this.getName();
    }

}
