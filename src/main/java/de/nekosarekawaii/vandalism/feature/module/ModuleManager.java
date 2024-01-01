package de.nekosarekawaii.vandalism.feature.module;

import de.florianmichael.rclasses.pattern.storage.named.NamedStorage;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.base.config.template.ConfigWithValues;
import de.nekosarekawaii.vandalism.base.event.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.base.event.game.ShutdownProcessListener;
import de.nekosarekawaii.vandalism.base.event.network.DisconnectListener;
import de.nekosarekawaii.vandalism.clientmenu.ClientMenuManager;
import de.nekosarekawaii.vandalism.feature.Feature;
import de.nekosarekawaii.vandalism.feature.module.gui.ModulesClientMenuWindow;
import de.nekosarekawaii.vandalism.feature.module.impl.combat.*;
import de.nekosarekawaii.vandalism.feature.module.impl.development.PacketManagerModule;
import de.nekosarekawaii.vandalism.feature.module.impl.development.TestModule;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.*;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.*;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.*;
import de.nekosarekawaii.vandalism.feature.module.impl.render.*;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;

public class ModuleManager extends NamedStorage<AbstractModule> implements KeyboardInputListener, ShutdownProcessListener, DisconnectListener, MinecraftWrapper {

    private final ConfigManager configManager;

    private ModPacketBlockerModule modPacketBlockerModule;
    private ExploitFixerModule exploitFixerModule;
    private TargetSelectorModule targetSelectorModule;
    private AutoBlockModule autoBlockModule;
    private KillAuraModule killAuraModule;
    private TrueSightModule trueSightModule;
    private VisualThrottleModule visualThrottleModule;
    private BetterTabListModule betterTabListModule;
    private FastUseModule fastUseModule;
    private IllegalBlockPlaceModule illegalBlockPlaceModule;
    private ESPModule espModule;

    public ModuleManager(final ConfigManager configManager, final ClientMenuManager clientMenuManager) {
        this.configManager = configManager;
        clientMenuManager.add(new ModulesClientMenuWindow());
        Vandalism.getInstance().getEventSystem().subscribe(KeyboardInputEvent.ID, this);
        Vandalism.getInstance().getEventSystem().subscribe(ShutdownProcessEvent.ID, this);
        Vandalism.getInstance().getEventSystem().subscribe(DisconnectEvent.ID, this);
    }

    @Override
    public void init() {
        this.add(
                this.modPacketBlockerModule = new ModPacketBlockerModule(),
                this.exploitFixerModule = new ExploitFixerModule(),
                this.targetSelectorModule = new TargetSelectorModule(),
                this.autoBlockModule = new AutoBlockModule(),
                this.killAuraModule = new KillAuraModule(),
                this.trueSightModule = new TrueSightModule(),
                this.visualThrottleModule = new VisualThrottleModule(),
                this.betterTabListModule = new BetterTabListModule(),
                this.fastUseModule = new FastUseModule(),
                this.illegalBlockPlaceModule = new IllegalBlockPlaceModule(),
                this.espModule = new ESPModule(),
                //Don't touch the order of the list above this comment because it breaks anything.
                //The order of the list below this comment doesn't matter.
                new TestModule(),
                new PacketManagerModule(),
                new ServerCrasherModule(),
                new BungeeCordSpooferModule(),
                new GodModeModule(),
                new BowSpammerModule(),
                new ConsoleSpammerModule(),
                new CraftCarryModule(),
                new JoinLeaveModule(),
                new AutoFishModule(),
                new AutoRespawnModule(),
                new InteractionSpammerModule(),
                new ItemStackLoggerModule(),
                new MessageEncryptorModule(),
                new BlockDensityModule(),
                new ElytraFlightModule(),
                new FlightModule(),
                new FOVFuckerModule(),
                new NoFallModule(),
                new PhaseModule(),
                new PushVelocityModule(),
                new SpeedModule(),
                new TimerModule(),
                new SprintModule(),
                new StepModule(),
                new VelocityModule(),
                new AmbienceModule(),
                new BetterTooltipsModule(),
                new CameraNoClipModule(),
                new DeutschMacherModule(),
                new ProtectorModule(),
                new VehicleOneHitModule(),
                new LongJumpModule(),
                new TrashTalkModule(),
                new BoatFlightModule(),
                new EcholocationModule(),
                new AutoClickerModule(),
                new SuicideModule(),
                new WTabModule()
        );

        configManager.add(new ConfigWithValues("modules", getList()));
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        //Cancel if the key is unknown to prevent the module from being toggled multiple times.
        if (action != GLFW.GLFW_PRESS || key == GLFW.GLFW_KEY_UNKNOWN || this.mc.player == null || this.mc.currentScreen != null) {
            return;
        }
        for (final AbstractModule module : Vandalism.getInstance().getModuleManager().getList()) {
            if (module.getKeyBind().getValue() == key) {
                module.toggle();
            }
        }
    }

    @Override
    public void onShutdownProcess() {
        for (final AbstractModule module : getList()) {
            if (module.isActive() && module.isDeactivateOnShutdown()) {
                module.deactivate();
            }
        }
    }

    @Override
    public void onDisconnect(final ClientConnection clientConnection, final Text disconnectReason) {
        // There is a thing called pinging a server
        if (this.mc.getNetworkHandler() != null && Objects.equals(clientConnection, mc.getNetworkHandler().getConnection())) {
            for (AbstractModule module : getList()) {
                if (module.isActive() && module.isDeactivateOnQuit()) {
                    module.deactivate();
                }
            }
        }
    }

    public List<AbstractModule> getByCategory(final Feature.Category category) {
        return this.getList().stream().filter(abstractModule -> abstractModule.getCategory() == category).toList();
    }

    public ModPacketBlockerModule getModPacketBlockerModule() {
        return modPacketBlockerModule;
    }

    public ExploitFixerModule getExploitFixerModule() {
        return exploitFixerModule;
    }

    public TargetSelectorModule getTargetSelectorModule() {
        return targetSelectorModule;
    }

    public AutoBlockModule getAutoBlockModule() {
        return autoBlockModule;
    }

    public KillAuraModule getKillAuraModule() {
        return killAuraModule;
    }

    public TrueSightModule getTrueSightModule() {
        return trueSightModule;
    }

    public VisualThrottleModule getVisualThrottleModule() {
        return visualThrottleModule;
    }

    public BetterTabListModule getBetterTabListModule() {
        return betterTabListModule;
    }

    public IllegalBlockPlaceModule getIllegalBlockPlaceModule() {
        return illegalBlockPlaceModule;
    }

    public FastUseModule getFastUseModule() {
        return fastUseModule;
    }

    public ESPModule getEspModule() {
        return espModule;
    }

}
