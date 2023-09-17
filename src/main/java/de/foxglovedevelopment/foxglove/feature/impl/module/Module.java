package de.foxglovedevelopment.foxglove.feature.impl.module;

import de.foxglovedevelopment.foxglove.Foxglove;
import de.foxglovedevelopment.foxglove.config.Config;
import de.foxglovedevelopment.foxglove.feature.Feature;
import de.foxglovedevelopment.foxglove.feature.FeatureCategory;
import de.foxglovedevelopment.foxglove.feature.FeatureType;
import de.foxglovedevelopment.foxglove.feature.impl.module.impl.render.HeadUpDisplayModule;
import de.foxglovedevelopment.foxglove.util.ChatUtils;
import de.foxglovedevelopment.foxglove.value.IValue;
import de.foxglovedevelopment.foxglove.value.Value;
import de.foxglovedevelopment.foxglove.value.ValueCategory;
import de.foxglovedevelopment.foxglove.value.values.list.ModuleModeValue;

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

            final ModuleRegistry moduleRegistry = Foxglove.getInstance().getModuleRegistry();
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
        return Foxglove.getInstance().getConfigManager().getModulesConfig();
    }

}
