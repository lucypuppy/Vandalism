/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.feature.module;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.pattern.storage.named.NamedStorage;
import de.nekosarekawaii.vandalism.base.FabricBootstrap;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.clientwindow.ClientWindowManager;
import de.nekosarekawaii.vandalism.event.normal.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.event.normal.game.MouseInputListener;
import de.nekosarekawaii.vandalism.event.normal.game.ShutdownProcessListener;
import de.nekosarekawaii.vandalism.event.normal.network.DisconnectListener;
import de.nekosarekawaii.vandalism.event.normal.network.WorldListener;
import de.nekosarekawaii.vandalism.event.normal.player.HealthUpdateListener;
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.Feature;
import de.nekosarekawaii.vandalism.feature.module.config.ModulesConfig;
import de.nekosarekawaii.vandalism.feature.module.gui.ModulesClientWindow;
import de.nekosarekawaii.vandalism.feature.module.impl.combat.*;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.*;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.consolespammer.ConsoleSpammerModule;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.disabler.DisablerModule;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.exploitfixer.ExploitFixerModule;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.godmode.GodModeModule;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.servercrasher.ServerCrasherModule;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.*;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.*;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.elytraflight.ElytraFlightModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.FlightModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.jesus.JesusModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.NoFallModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.phase.PhaseModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.speed.SpeedModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.velocity.VelocityModule;
import de.nekosarekawaii.vandalism.feature.module.impl.render.*;
import de.nekosarekawaii.vandalism.util.game.MinecraftWrapper;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;

public class ModuleManager extends NamedStorage<AbstractModule> implements
        KeyboardInputListener, MouseInputListener, ShutdownProcessListener,
        DisconnectListener, MinecraftWrapper,
        WorldListener, PlayerUpdateListener, HealthUpdateListener {

    private final ConfigManager configManager;

    private ModPacketBlockerModule modPacketBlockerModule;
    private ExploitFixerModule exploitFixerModule;
    private TrueSightModule trueSightModule;
    private BetterTabListModule betterTabListModule;
    private FastPlaceModule fastPlaceModule;
    private IllegalInteractionModule illegalInteractionModule;
    private ESPModule espModule;
    private KillAuraModule killAuraModule;
    private TickBaseModule tickBaseModule;
    private FullBrightModule fullBrightModule;
    private VehicleControlModule vehicleControlModule;
    private ConsoleSpammerModule consoleSpammerModule;
    private FastBreakModule fastBreakModule;
    private ZoomModule zoomModule;

    public ModuleManager(final DietrichEvents2 eventSystem, final ConfigManager configManager, final ClientWindowManager clientWindowManager) {
        this.configManager = configManager;
        clientWindowManager.add(new ModulesClientWindow());
        eventSystem.subscribe(
                this,
                KeyboardInputEvent.ID, MouseEvent.ID,
                ShutdownProcessEvent.ID, DisconnectEvent.ID,
                WorldLoadEvent.ID, PlayerUpdateEvent.ID,
                HealthUpdateEvent.ID
        );
    }

    @Override
    public void init() {
        if (FabricBootstrap.IS_DEV_ENVIRONMENT) {
            this.add(new TestModule());
        }
        this.add(
                // used by others
                this.killAuraModule = new KillAuraModule(),
                this.consoleSpammerModule = new ConsoleSpammerModule(),
                this.exploitFixerModule = new ExploitFixerModule(),
                this.tickBaseModule = new TickBaseModule(this.killAuraModule),
                this.fastBreakModule = new FastBreakModule(),
                this.fastPlaceModule = new FastPlaceModule(),
                this.illegalInteractionModule = new IllegalInteractionModule(),
                this.modPacketBlockerModule = new ModPacketBlockerModule(),
                this.vehicleControlModule = new VehicleControlModule(),
                this.betterTabListModule = new BetterTabListModule(),
                this.trueSightModule = new TrueSightModule(),
                this.espModule = new ESPModule(),
                this.fullBrightModule = new FullBrightModule(),
                this.zoomModule = new ZoomModule(),

                // combat
                new AutoClickerModule(),
                new AutoShieldModule(),
                new BackTrackModule(),
                new BowSpammerModule(),
                new WTapModule(),

                // exploit
                new DisablerModule(),
                new GodModeModule(),
                new ServerCrasherModule(),
                new BungeeCordSpooferModule(),
                new CraftCarryModule(),
                new EcholocationModule(),
                new FakeLagModule(this.killAuraModule),
                new SignExploitsModule(),
                new VehicleOneHitModule(),

                // misc
                new AutoFishModule(),
                new AutoRespawnModule(),
                new BlockBreakerModule(),
                new ChatReactionModule(),
                new EthanolModule(),
                new FakeGameModeModule(),
                new HandFuckerModule(),
                new InteractionSpammerModule(),
                new ItemStackLoggerModule(),
                new JoinLeaveModule(),
                new MessageEncryptorModule(),
                new MiddleClickFriendsModule(),
                new NoteBotModule(),
                new PacketManagerModule(),
                new ResourcePackSpooferModule(),

                // movement
                new AirJumpModule(),
                new BlinkModule(),
                new ElytraFlightModule(),
                new FlightModule(),
                new JesusModule(),
                new NoFallModule(),
                new PhaseModule(),
                new SpeedModule(),
                new VelocityModule(),
                new AutoSprintModule(),
                new BlockNormalizerModule(),
                new FOVFuckerModule(),
                new LongJumpModule(),
                new NoSlowModule(),
                new PushVelocityModule(),
                new ScaffoldModule(),
                new StepModule(),
                new StrafeModule(),
                new TeleportModule(),
                new TimerModule(),

                // render
                new AmbienceModule(),
                new BetterTooltipsModule(),
                new CameraNoClipModule(),
                new DeutschMacherModule(),
                new ProtectorModule()
        );
        this.configManager.add(new ModulesConfig(this));
    }

    @Override
    public void onKeyInput(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        this.handleInput(action, key);
    }

    @Override
    public void onMouseButton(final int button, final int action, final int mods) {
        this.handleInput(action, button);
    }

    private void handleInput(final int action, final int code) {
        // Cancel if the key is unknown to prevent the script from being executed multiple times.
        if (action == GLFW.GLFW_REPEAT || code == GLFW.GLFW_KEY_UNKNOWN) {
            return;
        }

        for (final AbstractModule module : this.getList()) {
            if (module.getKeyBind().getValue() != code || (module.getKeyBind().isOnlyInGame() && (mc.player == null || mc.currentScreen != null)))
                continue;

            if (action == GLFW.GLFW_PRESS) {
                module.toggle();
            } else if (action == GLFW.GLFW_RELEASE && module.isDeactivateOnRelease()) {
                module.deactivate();
            }
        }
    }

    @Override
    public void onShutdownProcess() {
        this.getList().stream().filter(module -> module.isActive() && module.isDeactivateOnShutdown()).forEach(AbstractModule::deactivate);
    }

    @Override
    public void onDisconnect(final ClientConnection clientConnection, final Text disconnectReason) {
        // There is a thing called pinging a server
        if (this.mc.getNetworkHandler() != null && Objects.equals(clientConnection, this.mc.getNetworkHandler().getConnection())) {
            this.getList().stream().filter(module -> module.isActive() && module.isDeactivateOnQuit()).forEach(AbstractModule::deactivate);
        }
    }

    @Override
    public void onPreWorldLoad() {
        this.getList().stream().filter(module -> module.isActive() && module.isDeactivateOnWorldLoad()).forEach(AbstractModule::deactivate);
    }

    @Override
    public void onHealthUpdate(final HealthUpdateEvent event) {
        if (event.health <= 0.0F) {
            this.getList().stream().filter(module -> module.isActive() && module.isDeactivateOnDeath()).forEach(AbstractModule::deactivate);
        }
    }

    public List<AbstractModule> getByCategory(final Feature.Category category) {
        return this.getList().stream().filter(module -> module.getCategory() == category).toList();
    }

    public ModPacketBlockerModule getModPacketBlockerModule() {
        return modPacketBlockerModule;
    }

    public TrueSightModule getTrueSightModule() {
        return trueSightModule;
    }

    public ExploitFixerModule getExploitFixerModule() {
        return exploitFixerModule;
    }

    public BetterTabListModule getBetterTabListModule() {
        return betterTabListModule;
    }

    public IllegalInteractionModule getIllegalInteractionModule() {
        return illegalInteractionModule;
    }

    public FastPlaceModule getFastPlaceModule() {
        return fastPlaceModule;
    }

    public ESPModule getEspModule() {
        return espModule;
    }

    public TickBaseModule getTickBaseModule() {
        return tickBaseModule;
    }

    public FullBrightModule getFullBrightModule() {
        return fullBrightModule;
    }

    public VehicleControlModule getVehicleControlModule() {
        return vehicleControlModule;
    }

    public ConsoleSpammerModule getConsoleSpammerModule() {
        return consoleSpammerModule;
    }

    public FastBreakModule getFastBreakModule() {
        return fastBreakModule;
    }

    public ZoomModule getZoomModule() {
        return zoomModule;
    }
}
