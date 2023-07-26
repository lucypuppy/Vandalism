package me.nekosarekawaii.foxglove.feature.impl.module;

import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.feature.FeatureList;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.development.DebugModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.development.TestModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.exploit.*;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.misc.*;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.movement.*;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.render.BetterTabListModule;
import me.nekosarekawaii.foxglove.feature.impl.module.impl.render.HeadUpDisplayModule;
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

    private HeadUpDisplayModule headUpDisplayModule;

    public HeadUpDisplayModule getHeadUpDisplayModule() {
        return this.headUpDisplayModule;
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

    private FlightModule flightModule;

    public FlightModule getFlightModule() {
        return this.flightModule;
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

    private CraftCarryModule craftCarryModule;

    public CraftCarryModule getCraftCarryModule() {
        return this.craftCarryModule;
    }

    private ConsoleSpammerModule consoleSpammerModule;

    public ConsoleSpammerModule getConsoleSpammerModule() {
        return this.consoleSpammerModule;
    }

    private ItemStackLoggerModule itemStackLoggerModule;

    public ItemStackLoggerModule getItemStackLoggerModule() {
        return this.itemStackLoggerModule;
    }

    private CustomItemUseCooldownModule customItemUseCooldownModule;

    public CustomItemUseCooldownModule getCustomItemUseCooldownModule() {
        return this.customItemUseCooldownModule;
    }

    private CustomPushVelocityModule customPushVelocityModule;

    public CustomPushVelocityModule getCustomPushVelocityModule() {
        return this.customPushVelocityModule;
    }

    private StepModule stepModule;

    public StepModule getStepModule() {
        return this.stepModule;
    }

    private NofallModule nofallModule;

    public NofallModule getNofallModule() {
        return this.nofallModule;
    }

    private CameraNoClipModule cameraNoClipModule;

    public CameraNoClipModule getCameraNoClipModule() {
        return this.cameraNoClipModule;
    }

    private JoinLeaveModule joinLeaveModule;

    public JoinLeaveModule getJoinLeaveModule() {
        return this.joinLeaveModule;
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
                this.exploitFixerModule = new ExploitFixerModule(),
                this.brandChangerModule = new BrandChangerModule(),
                this.debugModule = new DebugModule(),
                this.trueSightModule = new TrueSightModule(),
                this.headUpDisplayModule = new HeadUpDisplayModule(),
                this.betterTabListModule = new BetterTabListModule(),
                this.packetLoggerModule = new PacketLoggerModule(),
                this.modPacketBlockerModule = new ModPacketBlockerModule(),
                this.flightModule = new FlightModule(),
                this.bungeeCordSpoofModule = new BungeeCordSpoofModule(),
                this.godModeModule = new GodModeModule(),
                this.speedModule = new SpeedModule(),
                this.serverCrasherModule = new ServerCrasherModule(),
                this.craftCarryModule = new CraftCarryModule(),
                this.consoleSpammerModule = new ConsoleSpammerModule(),
                this.itemStackLoggerModule = new ItemStackLoggerModule(),
                this.customItemUseCooldownModule = new CustomItemUseCooldownModule(),
                this.customPushVelocityModule = new CustomPushVelocityModule(),
                this.stepModule = new StepModule(),
                this.nofallModule = new NofallModule(),
                this.cameraNoClipModule = new CameraNoClipModule(),
                this.joinLeaveModule = new JoinLeaveModule()
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
