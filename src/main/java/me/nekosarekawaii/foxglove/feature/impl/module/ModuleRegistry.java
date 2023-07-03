package me.nekosarekawaii.foxglove.feature.impl.module;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.FeatureList;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.development.TestModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.exploit.AntiChatContextModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.exploit.AntiServerBlockListModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.exploit.ExploitFixerModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.misc.AntiTelemetryModule;

public class ModuleRegistry {

    private TestModule testModule;

    public TestModule getTestModule() {
        return this.testModule;
    }

    private AntiChatContextModule antiChatContextModule;

    public AntiChatContextModule getAntiChatContextModule() {
        return this.antiChatContextModule;
    }

    private AntiServerBlockListModule antiServerBlockListModule;

    public AntiServerBlockListModule getAntiServerBlockListModule() {
        return this.antiServerBlockListModule;
    }

    private AntiTelemetryModule antiTelemetryModule;

    public AntiTelemetryModule getAntiTelemetryModule() {
        return this.antiTelemetryModule;
    }

    private ExploitFixerModule exploitFixerModule;

    public ExploitFixerModule getExploitFixerModule() {
        return this.exploitFixerModule;
    }

    private final FeatureList<Module> modules;

    public ModuleRegistry() {
        this.modules = new FeatureList<>();
        this.register();
    }

    public void reload() {
        final ObjectArrayList<String> enabledModules = new ObjectArrayList<>();
        for (final Module module : this.modules) {
            if (module.isEnabled()) {
                module.disable();
                enabledModules.add(module.getName());
            }
        }
        this.modules.clear();
        this.register();
        for (final String enabledModule : enabledModules) {
            final Module module = this.getModules().get(enabledModule);
            if (module.isEnabled()) {
                module.enable();
            }
        }
    }

    private void register() {
        this.registerModules(
                this.testModule = new TestModule(),
                this.antiChatContextModule = new AntiChatContextModule(),
                this.antiServerBlockListModule = new AntiServerBlockListModule(),
                this.exploitFixerModule = new ExploitFixerModule(),
                this.antiTelemetryModule = new AntiTelemetryModule()
        );
    }

    private void registerModules(final Module... modules) {
        for (final Module module : modules) {
            if (module.getClass().isAnnotationPresent(ModuleInfo.class)) {
                if (!this.modules.contains(module)) {
                    this.modules.add(module);
                    Foxglove.getInstance().getLogger().info("Module '" + module + "' has been registered.");
                } else {
                    Foxglove.getInstance().getLogger().error("Duplicated Module found: " + module);
                }
            } else {
                Foxglove.getInstance().getLogger().error("Module '" + module + "' is not annotated with Module Info!");
            }
        }
        final int moduleListSize = this.modules.size();
        if (moduleListSize < 1) Foxglove.getInstance().getLogger().info("No Modules found!");
        else Foxglove.getInstance().getLogger().info("Registered " + moduleListSize + " Module/s.");
    }

    public FeatureList<Module> getModules() {
        return this.modules;
    }

}
