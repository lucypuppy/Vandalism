package de.nekosarekawaii.vandalism.integration.hud;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.florianmichael.rclasses.pattern.storage.Storage;
import de.nekosarekawaii.vandalism.base.config.ConfigManager;
import de.nekosarekawaii.vandalism.base.event.game.ScreenListener;
import de.nekosarekawaii.vandalism.base.event.render.Render2DListener;
import de.nekosarekawaii.vandalism.clientmenu.ClientMenuManager;
import de.nekosarekawaii.vandalism.integration.hud.config.HUDConfig;
import de.nekosarekawaii.vandalism.integration.hud.gui.HUDClientMenuWindow;
import de.nekosarekawaii.vandalism.integration.hud.impl.DebugElement;
import de.nekosarekawaii.vandalism.integration.hud.impl.InfoHUDElement;
import de.nekosarekawaii.vandalism.integration.hud.impl.ModuleListHUDElement;
import de.nekosarekawaii.vandalism.integration.hud.impl.WatermarkHUDElement;
import de.nekosarekawaii.vandalism.util.MinecraftWrapper;
import net.minecraft.client.gui.DrawContext;

public class HUDManager extends Storage<HUDElement> implements Render2DListener, ScreenListener, MinecraftWrapper {

    public ModuleListHUDElement moduleListHUDElement;

    public HUDManager(final ConfigManager configManager, final ClientMenuManager clientMenuManager) {
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
        DietrichEvents2.global().subscribe(ScreenEvent.ID, this);

        configManager.add(new HUDConfig(this));
        clientMenuManager.add(new HUDClientMenuWindow(this));
    }

    @Override
    public void init() {
        this.add(
                new WatermarkHUDElement(),
                this.moduleListHUDElement = new ModuleListHUDElement(),
                new InfoHUDElement(),
                new DebugElement()
        );
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta) {
        for (final HUDElement hudElement : this.getList()) {
            if (!hudElement.isEnabled()) {
                continue;
            }
            hudElement.onRender(context, delta);
        }
    }

    @Override
    public void onResizeScreen(final ScreenEvent event) {
        for (final HUDElement hudElement : this.getList()) {
            hudElement.calculateAlignment();
            hudElement.calculatePosition();
        }
    }
}