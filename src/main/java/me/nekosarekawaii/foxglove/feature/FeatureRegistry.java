package me.nekosarekawaii.foxglove.feature;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.impl.command.Command;
import me.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import me.nekosarekawaii.foxglove.feature.impl.command.impl.development.ReloadCommand;
import me.nekosarekawaii.foxglove.feature.impl.command.impl.development.TestCommand;
import me.nekosarekawaii.foxglove.feature.impl.command.impl.misc.ChatClearCommand;
import me.nekosarekawaii.foxglove.feature.impl.command.impl.misc.FeaturesCommand;
import me.nekosarekawaii.foxglove.feature.impl.command.impl.misc.MacroCommand;
import me.nekosarekawaii.foxglove.feature.impl.command.impl.misc.ToggleModuleCommand;
import me.nekosarekawaii.foxglove.feature.impl.module.Module;
import me.nekosarekawaii.foxglove.feature.impl.module.ModuleInfo;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.development.TestModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.exploit.AntiChatContextModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.exploit.AntiServerBlockListModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.exploit.AntiTextureDDoSModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.misc.AntiTelemetryModule;

import java.util.concurrent.atomic.AtomicInteger;

public class FeatureRegistry {

    private final Object2ObjectOpenHashMap<FeatureType, FeatureList<? extends Feature>> features;

    public FeatureRegistry() {
        this.features = new Object2ObjectOpenHashMap<>();
        this.register();
    }

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

    private AntiTextureDDoSModule antiTextureDDoSModule;

    public AntiTextureDDoSModule getAntiTextureDDoSModule() {
        return this.antiTextureDDoSModule;
    }

    private AntiTelemetryModule antiTelemetryModule;

    public AntiTelemetryModule getAntiTelemetryModule() {
        return this.antiTelemetryModule;
    }

    private void register() {
        this.features.put(FeatureType.MODULE, new FeatureList<Module>());
        this.features.put(FeatureType.COMMAND, new FeatureList<Command>());
        this.registerModules(
                this.testModule = new TestModule(),
                this.antiChatContextModule = new AntiChatContextModule(),
                this.antiServerBlockListModule = new AntiServerBlockListModule(),
                this.antiTextureDDoSModule = new AntiTextureDDoSModule(),
                this.antiTelemetryModule = new AntiTelemetryModule()
        );
        this.registerCommands(
                new TestCommand(),
                new ReloadCommand(),
                new FeaturesCommand(),
                new ChatClearCommand(),
                new MacroCommand(),
                new ToggleModuleCommand()
        );
    }

    public void reload() {
        final ObjectArrayList<String> enabledModules = new ObjectArrayList<>();
        for (final Module module : this.getModules()) {
            if (module.isEnabled()) {
                module.disable();
                enabledModules.add(module.getName());
            }
        }
        this.features.clear();
        this.register();
        for (final String enabledModule : enabledModules) {
            final Module module = this.getModules().get(enabledModule);
            if (module.isEnabled()) {
                module.enable();
            }
        }
        Foxglove.getInstance().getCommandHandler().register();
    }

    private void registerModules(final Module... modules) {
        for (final Module module : modules) {
            if (module.getClass().isAnnotationPresent(ModuleInfo.class)) {
                if (!this.getModules().contains(module)) {
                    this.getModules().add(module);
                    Foxglove.getInstance().getLogger().info("Module '" + module + "' has been registered.");
                } else {
                    Foxglove.getInstance().getLogger().error("Duplicated Module found: " + module);
                }
            } else {
                Foxglove.getInstance().getLogger().error("Module '" + module + "' is not annotated with Module Info!");
            }
        }

        final int moduleListSize = this.getModules().size();
        if (moduleListSize < 1) {
            Foxglove.getInstance().getLogger().info("No Modules found!");
        } else {
            Foxglove.getInstance().getLogger().info("Registered " + moduleListSize + " Module/s.");
        }
    }

    private void registerCommands(final Command... commands) {
        for (final Command command : commands) {
            if (command.getClass().isAnnotationPresent(CommandInfo.class)) {
                if (!this.getCommands().contains(command)) {
                    this.getCommands().add(command);
                    Foxglove.getInstance().getLogger().info("Command '" + command + "' has been registered.");
                } else {
                    Foxglove.getInstance().getLogger().error("Duplicated Command found: " + command);
                }
            } else {
                Foxglove.getInstance().getLogger().error("Command '" + command + "' is not annotated with Command Info!");
            }
        }

        final int commandListSize = this.getCommands().size();
        if (commandListSize < 1) {
            Foxglove.getInstance().getLogger().info("No Commands found!");
        } else {
            Foxglove.getInstance().getLogger().info("Registered " + commandListSize + " Command/s.");
        }
    }

    public FeatureList<Command> getCommands() {
        return (FeatureList<Command>) this.features.get(FeatureType.COMMAND);
    }

    public FeatureList<Module> getModules() {
        return (FeatureList<Module>) this.features.get(FeatureType.MODULE);
    }

    public boolean isEmpty() {
        return this.getCount() < 1;
    }

    public int getCount() {
        final AtomicInteger count = new AtomicInteger(0);
        this.features.forEach((type, featureList) -> count.addAndGet(featureList.size()));
        return count.get();
    }

}
