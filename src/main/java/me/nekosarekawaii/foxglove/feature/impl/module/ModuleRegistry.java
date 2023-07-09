package me.nekosarekawaii.foxglove.feature.impl.module;

import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.FeatureList;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.development.DebugModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.development.TestModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.exploit.*;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.misc.AntiTelemetryModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.misc.DontClearChatHistoryModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.misc.ModPacketBlockerModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.misc.PacketLoggerModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.movement.FlyModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.movement.SpeedModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.render.BetterTabListModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.render.HUDModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.render.TrueSightModule;

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

    private DebugModule debugModule;

    public DebugModule getDebugModule() {
        return this.debugModule;
    }

    private BrandChangerModule brandChangerModule;

    public BrandChangerModule getBrandChangerModule() {
        return this.brandChangerModule;
    }

    private TrueSightModule trueSightModule;

    public TrueSightModule getTrueSightModule() {
        return this.trueSightModule;
    }

    private HUDModule hudModule;

    public HUDModule getHudModule() {
        return this.hudModule;
    }

    private BetterTabListModule betterTabListModule;

    public BetterTabListModule getBetterTabListModule() {
        return this.betterTabListModule;
    }

    private PacketLoggerModule packetLoggerModule;

    public PacketLoggerModule getPacketLoggerModule() {
        return this.packetLoggerModule;
    }

    private ModPacketBlockerModule modPacketBlockerModule;

    public ModPacketBlockerModule getModPacketBlockerModule() {
        return this.modPacketBlockerModule;
    }

    private DontClearChatHistoryModule dontClearChatHistoryModule;

    public DontClearChatHistoryModule getDontClearChatHistoryModule() {
        return this.dontClearChatHistoryModule;
    }

    private FlyModule flyModule;

    public FlyModule getFlyModule() {
        return this.flyModule;
    }

    private AntiTimeoutKickModule antiTimeoutKickModule;

    public AntiTimeoutKickModule getAntiTimeoutKickModule() {
        return this.antiTimeoutKickModule;
    }

    private BungeeCordSpoofModule bungeeCordSpoofModule;

    public BungeeCordSpoofModule getBungeeCordSpoofModule() {
        return this.bungeeCordSpoofModule;
    }

    private GodModeModule godModeModule;

    public GodModeModule getGodModeModule() {
        return this.godModeModule;
    }

    private SpeedModule speedModule;

    public SpeedModule getSpeedModule() {
        return this.speedModule;
    }

    private ServerCrasherModule serverCrasherModule;

    public ServerCrasherModule getServerCrasherModule() {
        return this.serverCrasherModule;
    }

    private CraftCarry craftCarry;

    public CraftCarry getCraftCarryModule() {
        return this.craftCarry;
    }

    private final FeatureList<Module> modules;

    public ModuleRegistry() {
        this.modules = new FeatureList<>();
        this.register();
    }

    private void register() {
        this.registerModules(
                this.testModule = new TestModule(),
                this.antiChatContextModule = new AntiChatContextModule(),
                this.antiServerBlockListModule = new AntiServerBlockListModule(),
                this.antiTelemetryModule = new AntiTelemetryModule(),
                this.exploitFixerModule = new ExploitFixerModule(),
                this.brandChangerModule = new BrandChangerModule(),
                this.debugModule = new DebugModule(),
                this.trueSightModule = new TrueSightModule(),
                this.hudModule = new HUDModule(),
                this.betterTabListModule = new BetterTabListModule(),
                this.packetLoggerModule = new PacketLoggerModule(),
                this.modPacketBlockerModule = new ModPacketBlockerModule(),
                this.dontClearChatHistoryModule = new DontClearChatHistoryModule(),
                this.flyModule = new FlyModule(),
                this.antiTimeoutKickModule = new AntiTimeoutKickModule(),
                this.bungeeCordSpoofModule = new BungeeCordSpoofModule(),
                this.godModeModule = new GodModeModule(),
                this.speedModule = new SpeedModule(),
                this.serverCrasherModule = new ServerCrasherModule(),
                this.craftCarry = new CraftCarry()
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
