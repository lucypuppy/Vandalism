/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
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

package de.nekosarekawaii.vandalism.clientwindow.impl;

import de.nekosarekawaii.vandalism.clientwindow.base.ClientWindow;
import imgui.ImGui;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.minecraft.client.gui.DrawContext;

public class AboutClientWindow extends ClientWindow {

    private final ImString text = new ImString(10000);

    public AboutClientWindow() {
        super("About", null, 1060f, 550f);
        final StringBuilder text = new StringBuilder();
        text.append("- Ein funktionierendes Haus mit einer funktionierenden Internetverbindung und funktionierendem Strom\n");
        text.append("- Einen PC, Keyboard, Mouse und Display (mindestens 100p, damit man noch gerade so Code lesen kann), der die oben genannten Ressourcen perfekt ausnutzt\n");
        text.append("- Ausreichend Wasser und Essen, damit du beim Vandalismus-Coden nicht wegstirbst\n");
        text.append("- PC-Kenntnisse: Anschalten (Ausschalten ist nicht so wichtig, da du sowieso 24/7 coden sollst), Keyboard-Benutzung, Maus-Bedienung,\n");
        text.append("\tAsync Keyboard- und Mouse-Handling gleichzeitig (advanced), Display anschauen (nicht danebengucken, sonst kannst du nicht richtig 24/7 coden), Display anschalten\n");
        text.append("- Nicht blind oder taub sein, da TTS-User etc. ineffizient sind\n");
        text.append("- Kein ADHS, weil kein Bock darauf, genervt zu werden\n");
        text.append("- Einen IQ über 80 (falls nicht bekannt, bitte messen gehen!!)\n");
        text.append("- Englische und Deutsche Sprache beherrschen (Englisch ist nicht ganz so wichtig, weil mori fixt eh alles,\n");
        text.append("\taber Deutsch musst du schon so die Basics wie \"MACH WEITER\" verstehen)\n");
        text.append("- Java-Coding-Erfahrung (besser als SidCool + Graf - ikik schwer)\n");
        text.append("- Internetbrowser verwenden zum Skidden (Skidden = Abschreiben von Code etc.)\n");
        text.append("- Git verwenden (so ein kollaboratives Tool - musst du dir dann mal so angucken)\n");
        text.append("- Genug IQ haben, um dir dein Git-Passwort zu merken/dir eins auszudenken\n");
        text.append(" sonst musst du halt einen Password-Generator + Manager verwenden oder manchmal einen neuen Git-Account machen, so wie Oliver Hoerner)\n");
        text.append("- Skills in Sachen Discord: Schreiben (+ Senden drücken), Reden (auch Mund dabei aufmachen und Voice Call joinen können), Screenshare (advanced users only)\n");
        text.append("- Und du musst gut vandalisieren können\n");
        text.append("- Wenn Thomas mit dir im Channel ist (Discord + Voice Call s.o.), soll dein Puls nicht hochgehen.\n");
        text.append("\tBitte mit Pulsmessgerät nachweisen zur Eignungsprüfung! Bei Lennox trifft dieser Fall nicht zu, da ist es intended, dass dein Puls hochgeht.\n");
        text.append("\tWenn nicht, dann kacke, weil Lennox dich nerven will können, du Hurensohn\n");
        text.append("- Mindestens 30 Stunden am Stück wachbleiben können und dabei ordentlich am Vandalismus coden können. Falls nicht möglich,\n");
        text.append("\tmuss auf Aufputschmittel oder Koffein zurückgegriffen werden (also genug Koffeintabletten / anderes bereithalten;\n");
        text.append("\tKaffee verboten, weil ineffizient, bitte Koffeintabletten verwenden! Ritalin kann auch verwendet werden und ist empfohlen!!!)\n");
        text.append("- Keine Fehler im Code, sonst optimiere ich deinen PC\n");
        text.append("- Bei Herzrhythmusstörungen bitte einen Arzt nach Hause rufen, weil wenn du zum Arzt gehst, dann arbeitest du nicht lange genug am Tag und musst nacharbeiten\n");
        text.append("- Jegliche ärztliche Untersuchungen am besten komplett vergessen oder aufschieben und wenn dann nur zuhause beim Coden\n");
        text.append("- Funktionierendes Herz, sonst Herzschrittmacher (muss aber auch richtig durchgehend klappen, weil sonst bist du zu ineffizient)\n");
        this.text.set(text.toString());
    }

    @Override
    protected void onRender(final DrawContext context, final int mouseX, final int mouseY, final float delta) {
        final String id = "##" + this.getName();
        ImGui.text("Anforderungen");
        ImGui.beginChild(id + "child", ImGui.getColumnWidth(), -1, true, ImGuiWindowFlags.HorizontalScrollbar);
        ImGui.inputTextMultiline(id + "text", this.text, ImGui.getColumnWidth(), -1, ImGuiInputTextFlags.ReadOnly);
        ImGui.endChild();
    }

}
