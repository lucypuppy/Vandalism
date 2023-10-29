package de.vandalismdevelopment.vandalism.feature.impl.module;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.event.KeyboardListener;
import de.vandalismdevelopment.vandalism.feature.FeatureList;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.development.DebugModule;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.development.TestModule;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.exploit.*;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.misc.*;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.movement.*;
import de.vandalismdevelopment.vandalism.feature.impl.module.impl.render.*;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class ModuleRegistry implements KeyboardListener {

    private HeadUpDisplayModule headUpDisplayModule;

    public HeadUpDisplayModule getHeadUpDisplayModule() {
        return this.headUpDisplayModule;
    }

    private TestModule testModule;

    public TestModule getTestModule() {
        return this.testModule;
    }

    private DebugModule debugModule;

    public DebugModule getDebugModule() {
        return this.debugModule;
    }

    private ExploitFixerModule exploitFixerModule;

    public ExploitFixerModule getExploitFixerModule() {
        return this.exploitFixerModule;
    }

    private ClientBrandChangerModule clientBrandChangerModule;

    public ClientBrandChangerModule getClientBrandChangerModule() {
        return this.clientBrandChangerModule;
    }

    private TrueSightModule trueSightModule;

    public TrueSightModule getTrueSightModule() {
        return this.trueSightModule;
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

    private BungeeCordSpooferModule bungeeCordSpooferModule;

    public BungeeCordSpooferModule getBungeeCordSpooferModule() {
        return this.bungeeCordSpooferModule;
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

    private FastUseModule fastUseModule;

    public FastUseModule getFastUseModule() {
        return this.fastUseModule;
    }

    private PushVelocityModule pushVelocityModule;

    public PushVelocityModule getPushVelocityModule() {
        return this.pushVelocityModule;
    }

    private StepModule stepModule;

    public StepModule getStepModule() {
        return this.stepModule;
    }

    private NoFallModule noFallModule;

    public NoFallModule getNoFallModule() {
        return this.noFallModule;
    }

    private CameraNoClipModule cameraNoClipModule;

    public CameraNoClipModule getCameraNoClipModule() {
        return this.cameraNoClipModule;
    }

    private JoinLeaveModule joinLeaveModule;

    public JoinLeaveModule getJoinLeaveModule() {
        return this.joinLeaveModule;
    }

    private ESPModule espModule;

    public ESPModule getEspModule() {
        return this.espModule;
    }

    private BetterTooltipModule betterTooltipModule;

    public BetterTooltipModule getBetterTooltipModule() {
        return this.betterTooltipModule;
    }

    private PhaseModule phaseModule;

    public PhaseModule getPhaseModule() {
        return this.phaseModule;
    }

    private VelocityModule velocityModule;

    public VelocityModule getVelocityModule() {
        return this.velocityModule;
    }

    private AutoFishModule autoFishModule;

    public AutoFishModule getAutoFishModule() {
        return this.autoFishModule;
    }

    private BlockDensityModule blockDensityModule;

    public BlockDensityModule getBlockDensityModule() {
        return this.blockDensityModule;
    }

    private VisualThrottleModule visualThrottleModule;

    public VisualThrottleModule getVisualThrottleModule() {
        return this.visualThrottleModule;
    }

    private ElytraFlightModule elytraFlightModule;

    public ElytraFlightModule getElytraFlightModule() {
        return this.elytraFlightModule;
    }

    private AmbienceModule ambienceModule;

    public AmbienceModule getAmbienceModule() {
        return this.ambienceModule;
    }

    private MessageEncryptorModule messageEncryptorModule;

    public MessageEncryptorModule getMessageEncryptorModule() {
        return this.messageEncryptorModule;
    }

    private IllegalBlockPlaceModule illegalBlockPlaceModule;

    public IllegalBlockPlaceModule getIllegalBlockPlaceModule() {
        return this.illegalBlockPlaceModule;
    }

    private AutoRespawnModule autoRespawnModule;

    public AutoRespawnModule getAutoRespawnModule() {
        return this.autoRespawnModule;
    }

    private AntiFOVModule antiFOVModule;

    public AntiFOVModule getAntiFOVModule() {
        return this.antiFOVModule;
    }

    private boolean done;

    private final FeatureList<Module> modules;

    public ModuleRegistry() {
        this.done = false;
        this.modules = new FeatureList<>();
        this.register();
        DietrichEvents2.global().subscribe(KeyboardEvent.ID, this);
        this.done = true;
    }

    private void register() {
        this.registerModules(
                this.testModule = new TestModule(),
                this.exploitFixerModule = new ExploitFixerModule(),
                this.clientBrandChangerModule = new ClientBrandChangerModule(),
                this.debugModule = new DebugModule(),
                this.trueSightModule = new TrueSightModule(),
                this.betterTabListModule = new BetterTabListModule(),
                this.packetLoggerModule = new PacketLoggerModule(),
                this.modPacketBlockerModule = new ModPacketBlockerModule(),
                this.flightModule = new FlightModule(),
                this.bungeeCordSpooferModule = new BungeeCordSpooferModule(),
                this.godModeModule = new GodModeModule(),
                this.speedModule = new SpeedModule(),
                this.serverCrasherModule = new ServerCrasherModule(),
                this.craftCarryModule = new CraftCarryModule(),
                this.consoleSpammerModule = new ConsoleSpammerModule(),
                this.itemStackLoggerModule = new ItemStackLoggerModule(),
                this.fastUseModule = new FastUseModule(),
                this.pushVelocityModule = new PushVelocityModule(),
                this.stepModule = new StepModule(),
                this.noFallModule = new NoFallModule(),
                this.cameraNoClipModule = new CameraNoClipModule(),
                this.joinLeaveModule = new JoinLeaveModule(),
                this.espModule = new ESPModule(),
                this.betterTooltipModule = new BetterTooltipModule(),
                this.phaseModule = new PhaseModule(),
                this.velocityModule = new VelocityModule(),
                this.autoFishModule = new AutoFishModule(),
                this.blockDensityModule = new BlockDensityModule(),
                this.visualThrottleModule = new VisualThrottleModule(),
                this.elytraFlightModule = new ElytraFlightModule(),
                this.ambienceModule = new AmbienceModule(),
                this.messageEncryptorModule = new MessageEncryptorModule(),
                this.illegalBlockPlaceModule = new IllegalBlockPlaceModule(),
                this.autoRespawnModule = new AutoRespawnModule(),
                this.antiFOVModule = new AntiFOVModule(),
                this.headUpDisplayModule = new HeadUpDisplayModule()
        );
    }

    private void registerModules(final Module... modules) {
        Vandalism.getInstance().getLogger().info("Registering modules...");
        for (final Module module : modules) {
            if (!this.modules.contains(module)) {
                this.modules.add(module);
                Vandalism.getInstance().getLogger().info("Module '" + module.toString() + "' has been registered.");
            } else {
                Vandalism.getInstance().getLogger().error("Duplicated module found: " + module.toString());
            }
        }
        final int moduleListSize = this.modules.size();
        if (moduleListSize < 1) Vandalism.getInstance().getLogger().info("No modules found!");
        else Vandalism.getInstance().getLogger().info("Registered " + moduleListSize + " module/s.");
    }

    public FeatureList<Module> getModules() {
        return this.modules;
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action != GLFW.GLFW_PRESS || key == GLFW.GLFW_KEY_UNKNOWN || MinecraftClient.getInstance().player == null || MinecraftClient.getInstance().currentScreen != null)
            return;
        for (final Module module : Vandalism.getInstance().getModuleRegistry().getModules()) {
            if (module.getKeyBind().getKeyCode() == key) {
                module.toggle();
            }
        }
    }

    public boolean isDone() {
        return this.done;
    }

}
