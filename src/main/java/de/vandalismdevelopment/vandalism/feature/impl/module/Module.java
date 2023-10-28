package de.vandalismdevelopment.vandalism.feature.impl.module;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.config.Config;
import de.vandalismdevelopment.vandalism.feature.Feature;
import de.vandalismdevelopment.vandalism.feature.FeatureCategory;
import de.vandalismdevelopment.vandalism.feature.FeatureType;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.render.HeadUpDisplayModule;
import de.vandalismdevelopment.vandalism.util.ChatUtils;
import de.vandalismdevelopment.vandalism.util.GlfwKeyName;
import de.vandalismdevelopment.vandalism.value.IValue;
import de.vandalismdevelopment.vandalism.value.Value;
import de.vandalismdevelopment.vandalism.value.ValueCategory;
import de.vandalismdevelopment.vandalism.value.values.BooleanValue;
import de.vandalismdevelopment.vandalism.value.values.KeyInputValue;
import de.vandalismdevelopment.vandalism.value.values.list.ModuleModeValue;

import java.util.ArrayList;
import java.util.List;

public abstract class Module extends Feature implements IValue {

    private final List<Value<?>> values;
    private final BooleanValue enabled, showInModuleList;
    private final KeyInputValue keyBind;

    public Module(final String name, final String description, final FeatureCategory category, final boolean isExperimental, final boolean isDefaultEnabled) {
        this(name, description, category, isExperimental, isDefaultEnabled, GlfwKeyName.UNKNOWN);
    }

    public Module(final String name, final String description, final FeatureCategory category, final boolean isExperimental, final boolean isDefaultEnabled, final GlfwKeyName keyBind) {
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
        ).valueChangedConsumer(this::setState);
        this.showInModuleList = new BooleanValue(
                "Show in Module List",
                "Whether this module should be shown in the module list.",
                this,
                !(this instanceof HeadUpDisplayModule)
        );
        this.keyBind = new KeyInputValue(
                "Key Bind",
                "The key bind of this module.",
                this,
                keyBind
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
            if (player() != null) {
                ChatUtils.infoChatMessage(this.getName() + " has been " + (state ? "enabled" : "disabled") + ".");
            }
            final ModuleRegistry moduleRegistry = Vandalism.getInstance().getModuleRegistry();
            if (moduleRegistry != null && moduleRegistry.isDone()) {
                final HeadUpDisplayModule headUpDisplayModule = moduleRegistry.getHeadUpDisplayModule();
                if (headUpDisplayModule != null) {
                    headUpDisplayModule.sortEnabledModules();
                }
            }
            if (state) this.onEnable();
            else this.onDisable();
            this.recursiveModeEnable(state, this.values);
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

    public boolean isShowInModuleList() {
        return this.showInModuleList.getValue();
    }

    public void setKeyBind(final GlfwKeyName glfwKeyName) {
        this.keyBind.setValue(glfwKeyName);
    }

    public GlfwKeyName getKeyBind() {
        return this.keyBind.getValue();
    }

    @Override
    public String toString() {
        return '{' +
                "name=" + this.getName() +
                ", category=" + this.getCategory().normalName() +
                ", enabled=" + this.enabled.getValue() +
                ", experimental=" + this.isExperimental() +
                ", keyBind=" + this.keyBind.getValue().normalName() +
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
