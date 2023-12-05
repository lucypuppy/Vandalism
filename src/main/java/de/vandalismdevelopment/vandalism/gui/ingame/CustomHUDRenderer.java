package de.vandalismdevelopment.vandalism.gui.ingame;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.RenderListener;
import de.vandalismdevelopment.vandalism.event.ScreenListener;
import de.vandalismdevelopment.vandalism.gui.ingame.hudelements.InfoHUDElement;
import de.vandalismdevelopment.vandalism.gui.ingame.hudelements.ModuleListHUDElement;
import de.vandalismdevelopment.vandalism.gui.ingame.hudelements.WatermarkHUDElement;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
import net.minecraft.client.gui.DrawContext;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CustomHUDRenderer implements RenderListener, ScreenListener, MinecraftWrapper {

    private final List<HUDElement> hudElements;
    private final ModuleListHUDElement moduleListHUDElement;

    public CustomHUDRenderer() {
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
        DietrichEvents2.global().subscribe(ScreenEvent.ID, this);
        this.hudElements = new CopyOnWriteArrayList<>();
        this.hudElements.addAll(Arrays.asList(
                new WatermarkHUDElement(),
                this.moduleListHUDElement = new ModuleListHUDElement(),
                new InfoHUDElement()
        ));
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta) {
        for (final HUDElement HUDElement : this.hudElements) {
            if (!HUDElement.isEnabled()) continue;
            HUDElement.render(context, delta);
        }
    }

    @Override
    public void onResizeScreen(final ScreenEvent event) {
        for (final HUDElement HUDElement : this.hudElements) {
            if (!HUDElement.isEnabled()) continue;
            HUDElement.calculatePosition();
        }
    }

    public List<HUDElement> getHudElements() {
        return this.hudElements;
    }

    public ModuleListHUDElement getModuleListHUDElement() {
        return this.moduleListHUDElement;
    }

}
