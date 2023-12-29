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
import de.nekosarekawaii.vandalism.feature.module.impl.combat.AutoBlockModule;
import de.nekosarekawaii.vandalism.feature.module.impl.combat.AutoClickerModule;
import de.nekosarekawaii.vandalism.feature.module.impl.combat.BowSpammerModule;
import de.nekosarekawaii.vandalism.feature.module.impl.combat.KillAuraModule;
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

    private BetterTabListModule betterTabListModule;
    private ESPModule espModule;
    private ExploitFixerModule exploitFixerModule;
    private FastUseModule fastUseModule;
    private IllegalBlockPlaceModule illegalBlockPlaceModule;
    private MessageEncryptorModule messageEncryptorModule;
    private ModPacketBlockerModule modPacketBlockerModule;
    private TrueSightModule trueSightModule;
    private VisualThrottleModule visualThrottleModule;
    private KillAuraModule killauraModule;
    private AutoBlockModule autoBlockModule;

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
                new BowSpammerModule(),
                new PacketManagerModule(),
                new TestModule(),
                new BungeeCordSpooferModule(),
                new ConsoleSpammerModule(),
                new CraftCarryModule(),
                this.exploitFixerModule = new ExploitFixerModule(),
                new GodModeModule(),
                new JoinLeaveModule(),
                new ServerCrasherModule(),
                new AutoFishModule(),
                new AutoRespawnModule(),
                this.fastUseModule = new FastUseModule(),
                this.illegalBlockPlaceModule = new IllegalBlockPlaceModule(),
                new InteractionSpammerModule(),
                new ItemStackLoggerModule(),
                this.messageEncryptorModule = new MessageEncryptorModule(),
                this.modPacketBlockerModule = new ModPacketBlockerModule(),
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
                this.betterTabListModule = new BetterTabListModule(),
                new BetterTooltipsModule(),
                new CameraNoClipModule(),
                new DeutschMacherModule(),
                this.espModule = new ESPModule(),
                new ProtectorModule(),
                this.trueSightModule = new TrueSightModule(),
                this.visualThrottleModule = new VisualThrottleModule(),
                new VehicleOneHitModule(),
                new LongJumpModule(),
                new TrashTalkModule(),
                this.autoBlockModule = new AutoBlockModule(),
                this.killauraModule = new KillAuraModule(),
                new BoatFlightModule(),
                new ParalyzeModule(),
                new EcholocationModule(),
                new AutoClickerModule()
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
        for (AbstractModule module : getList()) {
            if (module.isActive() && module.isDeactivateOnShutdown()) {
                module.deactivate();
            }
        }
    }

    @Override
    public void onDisconnect(ClientConnection clientConnection, Text disconnectReason) {
        if (mc.getNetworkHandler() != null && Objects.equals(clientConnection, mc.getNetworkHandler().getConnection())) { // There is a thing called pinging a server
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

    public BetterTabListModule getBetterTabListModule() {
        return betterTabListModule;
    }

    public ESPModule getEspModule() {
        return espModule;
    }

    public ExploitFixerModule getExploitFixerModule() {
        return exploitFixerModule;
    }

    public FastUseModule getFastUseModule() {
        return fastUseModule;
    }

    public IllegalBlockPlaceModule getIllegalBlockPlaceModule() {
        return illegalBlockPlaceModule;
    }

    public MessageEncryptorModule getMessageEncryptorModule() {
        return messageEncryptorModule;
    }

    public ModPacketBlockerModule getModPacketBlockerModule() {
        return modPacketBlockerModule;
    }

    public TrueSightModule getTrueSightModule() {
        return trueSightModule;
    }

    public VisualThrottleModule getVisualThrottleModule() {
        return visualThrottleModule;
    }

    public KillAuraModule getKillauraModule() {
        return killauraModule;
    }

    public AutoBlockModule getAutoBlockModule() {
        return autoBlockModule;
    }

}
