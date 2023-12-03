package de.vandalismdevelopment.vandalism.gui.ingame;

import de.florianmichael.dietrichevents2.DietrichEvents2;
import de.vandalismdevelopment.vandalism.event.RenderListener;
import de.vandalismdevelopment.vandalism.event.ScreenListener;
import de.vandalismdevelopment.vandalism.gui.ingame.elements.InfoElement;
import de.vandalismdevelopment.vandalism.gui.ingame.elements.ModuleListElement;
import de.vandalismdevelopment.vandalism.gui.ingame.elements.WatermarkElement;
import de.vandalismdevelopment.vandalism.util.minecraft.MinecraftWrapper;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomHUDSystem implements RenderListener, ScreenListener, MinecraftWrapper {

    private final List<Element> elements, addedElements;
    private final ModuleListElement moduleListElement;

    public CustomHUDSystem() {
        DietrichEvents2.global().subscribe(Render2DEvent.ID, this);
        DietrichEvents2.global().subscribe(ScreenEvent.ID, this);
        this.elements = new ArrayList<>();
        this.addedElements = new ArrayList<>();
        this.registerElements(
                new WatermarkElement(),
                this.moduleListElement = new ModuleListElement(),
                new InfoElement()
        );
    }

    private void registerElements(final Element... elements) {
        this.elements.addAll(Arrays.asList(elements));
    }

    @Override
    public void onRender2DInGame(final DrawContext context, final float delta) {
        for (final Element element : this.addedElements) {
            element.render(context, delta);
        }
    }

    @Override
    public void onResizeScreen(final ScreenEvent event) {
        for (final Element element : this.addedElements) {
            element.calculatePosition();
        }
    }

    public List<Element> getAddedElements() {
        return this.addedElements;
    }
    public List<Element> getElements() {
        return this.elements;
    }

    public ModuleListElement getModuleListElement() {
        return this.moduleListElement;
    }

}
