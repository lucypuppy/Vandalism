package de.vandalismdevelopment.vandalism.gui.impl.menu;

import java.util.List;

public class ImGuiMenuCategory {

    private final String name;
    private final List<ImGuiMenu> imGuiMenus;

    public ImGuiMenuCategory(final String name, final ImGuiMenu... imGuiMenus) {
        this.name = name;
        this.imGuiMenus = List.of(imGuiMenus);
    }

    public String getName() {
        return this.name;
    }

    public List<ImGuiMenu> getImGuiMenus() {
        return this.imGuiMenus;
    }

    @Override
    public String toString() {
        return '{' + "name=" + this.getName() + '}';
    }

}
