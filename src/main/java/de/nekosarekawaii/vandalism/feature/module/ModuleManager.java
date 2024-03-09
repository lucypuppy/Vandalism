/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO and contributors
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
import de.nekosarekawaii.vandalism.clientmenu.ClientMenuManager;
import de.nekosarekawaii.vandalism.event.normal.game.KeyboardInputListener;
import de.nekosarekawaii.vandalism.event.normal.game.ShutdownProcessListener;
import de.nekosarekawaii.vandalism.event.normal.network.DisconnectListener;
import de.nekosarekawaii.vandalism.event.normal.network.WorldListener;
import de.nekosarekawaii.vandalism.event.normal.player.HealthUpdateListener;
import de.nekosarekawaii.vandalism.event.normal.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.Feature;
import de.nekosarekawaii.vandalism.feature.module.config.ModulesConfig;
import de.nekosarekawaii.vandalism.feature.module.gui.ModulesClientMenuWindow;
import de.nekosarekawaii.vandalism.feature.module.impl.combat.*;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.*;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.consolespammer.ConsoleSpammerModule;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.disabler.DisablerModule;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.godmode.GodModeModule;
import de.nekosarekawaii.vandalism.feature.module.impl.exploit.servercrasher.ServerCrasherModule;
import de.nekosarekawaii.vandalism.feature.module.impl.misc.*;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.*;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.elytraflight.ElytraFlightModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.flight.FlightModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.nofall.NoFallModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.phase.PhaseModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.speed.SpeedModule;
import de.nekosarekawaii.vandalism.feature.module.impl.movement.velocity.VelocityModule;
import de.nekosarekawaii.vandalism.feature.module.impl.render.*;
import de.nekosarekawaii.vandalism.util.wrapper.MinecraftWrapper;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;

public class ModuleManager extends NamedStorage<AbstractModule> implements
        KeyboardInputListener, ShutdownProcessListener,
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
    private CreativePackagerModule creativePackagerModule;

    public ModuleManager(final DietrichEvents2 eventSystem, final ConfigManager configManager, final ClientMenuManager clientMenuManager) {
        this.configManager = configManager;
        clientMenuManager.add(new ModulesClientMenuWindow());
        eventSystem.subscribe(
                this,
                KeyboardInputEvent.ID, ShutdownProcessEvent.ID,
                DisconnectEvent.ID, WorldLoadEvent.ID,
                PlayerUpdateEvent.ID,
                HealthUpdateEvent.ID
        );
    }

    @Override
    public void init() {
        if (FabricBootstrap.IS_DEV_ENVIRONMENT) {
            this.add(
                    new TestModule()
            );
        }
        this.add(
                this.modPacketBlockerModule = new ModPacketBlockerModule(),
                this.exploitFixerModule = new ExploitFixerModule(),
                this.trueSightModule = new TrueSightModule(),
                this.betterTabListModule = new BetterTabListModule(),
                this.fastPlaceModule = new FastPlaceModule(),
                this.illegalInteractionModule = new IllegalInteractionModule(),
                this.espModule = new ESPModule(),
                this.fullBrightModule = new FullBrightModule(),
                this.killAuraModule = new KillAuraModule(),
                this.tickBaseModule = new TickBaseModule(this.killAuraModule),
                this.vehicleControlModule = new VehicleControlModule(),
                this.creativePackagerModule = new CreativePackagerModule(),
                new FakeLagModule(this.killAuraModule),
                new CraftCarryModule(),
                new BackTrackModule(),
                new NoSlowModule(),
                new PacketManagerModule(),
                new ServerCrasherModule(),
                new BungeeCordSpooferModule(),
                new GodModeModule(),
                new BowSpammerModule(),
                new ConsoleSpammerModule(),
                new JoinLeaveModule(),
                new AutoFishModule(),
                new AutoRespawnModule(),
                new InteractionSpammerModule(),
                new ItemStackLoggerModule(),
                new MessageEncryptorModule(),
                new BlockNormalizerModule(),
                new ElytraFlightModule(),
                new FlightModule(),
                new FOVFuckerModule(),
                new NoFallModule(),
                new PhaseModule(),
                new PushVelocityModule(),
                new SpeedModule(),
                new TimerModule(),
                new AutoSprintModule(),
                new StepModule(),
                new VelocityModule(),
                new AmbienceModule(),
                new BetterTooltipsModule(),
                new CameraNoClipModule(),
                new DeutschMacherModule(),
                new ProtectorModule(),
                new VehicleOneHitModule(),
                new LongJumpModule(),
                new ChatReactionModule(),
                new EcholocationModule(),
                new AutoClickerModule(),
                new WTapModule(),
                new DisablerModule(),
                new TeleportModule(),
                new SignExploitsModule(),
                new ScaffoldModule(),
                new NoteBotModule(),
                new MiddleClickFriendsModule(),
                new ResourcePackSpooferModule(),
                new AutoShieldModule()
        );
        this.configManager.add(new ModulesConfig(this));
    }

    @Override
    public void onKeyInput(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action == GLFW.GLFW_PRESS || key != GLFW.GLFW_KEY_UNKNOWN) {
            this.getList().stream().filter(m -> m.getKeyBind().isPressed(key)).forEach(AbstractModule::toggle);
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

    public CreativePackagerModule getCreativePackagerModule() {
        return creativePackagerModule;
    }

}
