package de.nekosarekawaii.foxglove.util.minecraft;

import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class StringVisitorAccessor implements CharacterVisitor {

    private final MutableText mutableText = Text.literal("");

    @Override
    public boolean accept(final int index, final Style style, final int codePoint) {
        this.mutableText.append(Text.literal(String.valueOf((char) codePoint)).setStyle(style));
        return true;
    }

    public MutableText getMutableText() {
        return this.mutableText;
    }

}