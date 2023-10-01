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
import de.vandalismdevelopment.vandalism.value.values.list.ModuleModeValue;

import java.util.ArrayList;
import java.util.List;

public abstract class Module extends Feature implements IValue {

    private boolean enabled, showInModuleList;

    private final List<Value<?>> values;

    public Module(final String name, final String description, final FeatureCategory category, final boolean isExperimental, final boolean isDefaultEnabled) {
        this.setName(name);
        this.setDescription(description);
        this.setType(FeatureType.MODULE);
        this.setCategory(category);
        this.setExperimental(isExperimental);
        this.setState(isDefaultEnabled);
        this.showInModuleList = !(this instanceof HeadUpDisplayModule);
        this.values = new ArrayList<>();
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
        this.setState(!this.enabled);
    }

    public void setState(final boolean state) {
        if (this.enabled != state) {
            this.enabled = state;
            ChatUtils.infoChatMessage(this.getName() + " has been " + (state ? "enabled" : "disabled") + ".");

            final ModuleRegistry moduleRegistry = Vandalism.getInstance().getModuleRegistry();
            if (moduleRegistry != null && moduleRegistry.isDone()) {
                final HeadUpDisplayModule headUpDisplayModule = moduleRegistry.getHeadUpDisplayModule();
                if (headUpDisplayModule != null) headUpDisplayModule.sortEnabledModules();
            }

            if (state) {
                this.onEnable();
            } else {
                this.onDisable();
            }

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
        return this.enabled;
    }

    public boolean isShowInModuleList() {
        return this.showInModuleList;
    }

    public void setShowInModuleList(final boolean showInModuleList) {
        this.showInModuleList = showInModuleList;
    }

    @Override
    public List<Value<?>> getValues() {
        return this.values;
    }

    @Override
    public String toString() {
        return '{' + "name=" + this.getName() + ", category=" + this.getCategory() + ", enabled=" + this.enabled + ", experimental=" + this.isExperimental() + '}';
    }

    @Override
    public Config getConfig() {
        return Vandalism.getInstance().getConfigManager().getModulesConfig();
    }

    @Override
    public String iName() {
        return this.getName();
    }

}
