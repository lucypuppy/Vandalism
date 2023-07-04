package me.nekosarekawaii.foxglove.feature.impl.module;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.config.Config;
import me.nekosarekawaii.foxglove.feature.Feature;
import me.nekosarekawaii.foxglove.feature.FeatureType;
import me.nekosarekawaii.foxglove.util.ChatUtils;
import me.nekosarekawaii.foxglove.value.IValue;
import me.nekosarekawaii.foxglove.value.Value;

public abstract class Module extends Feature implements IValue {

    private boolean enabled;

    private final ObjectArrayList<Value<?>> values;

    public Module() {
        final ModuleInfo moduleInfo = this.getClass().getAnnotation(ModuleInfo.class);
        this.setName(moduleInfo.name());
        this.setDescription(moduleInfo.description());
        this.setType(FeatureType.MODULE);
        this.setCategory(moduleInfo.category());
        this.setExperimental(moduleInfo.isExperimental());
        this.setState(moduleInfo.isDefaultEnabled() && Foxglove.getInstance().isFirstStart());
        this.values = new ObjectArrayList<>();
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
            if (state) {
                this.onEnable();
            } else {
                this.onDisable();
            }
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public ObjectArrayList<Value<?>> getValues() {
        return this.values;
    }

    @Override
    public String toString() {
        return '{' +
                "name=" + this.getName() +
                ", category=" + this.getCategory() +
                ", enabled=" + this.enabled +
                ", experimental=" + this.isExperimental() +
                '}';
    }

    @Override
    public Config getConfig() {
        return Foxglove.getInstance().getConfigManager().getModulesConfig();
    }

}
