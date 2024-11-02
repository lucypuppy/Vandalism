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

package de.nekosarekawaii.vandalism.feature.module.impl.render;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.event.render.TextDrawListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import net.minecraft.util.Formatting;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class DeutschMacherModule extends Module implements TextDrawListener {

    private static final String[] SUFFIX = new String[]{
            "", "tausend",
            "millionen", "milliarden", "billionen",
            "billiarden", "trillionen", "trilliarden",
            "quadrillionen", "quadrilliarden", "quintillionen",
            "quintilliarden", "sextillionen", "sextilliarden",
            "septillionen", "septilliarden", "oktillionen",
            "oktilliarden", "nonillionen", "nonilliarden",
            "dezillionen", "dezilliarden", "undezillionen",
            "undezilliarden", "dodezillionen", "dodezilliarden",
            "tredezillionen", "tredezilliarden", "quattuordezillionen",
            "quattuordezilliarden", "quindezillionen", "quindezilliarden",
            "sedezillionen", "sedezilliarden", "septendezillionen",
            "septendezilliarden", "dodevigintillionen", "dodevigintilliarden",
            "undevigintillionen", "undevigintilliarden", "vigintillionen",
            "vigintilliarden", "unvigintillionen", "unvigintilliarden",
            "dovigintillionen", "dovigintilliarden", "tresvigintillionen",
            "tresvigintilliarden", "quattuorvigintillionen", "quattuorvigintilliarden",
            "quinvigintillionen", "quinvigintilliarden", "sevigintillionen",
            "sevigintilliarden", "septenvigintillionen", "septenvigintilliarden",
            "dodetrigintillionen", "dodetrigintillarden", "undetrigintillionen",
            "undetrigintilliarden", "trigintillionen", "trigintilliarden",
            "untrigintillionen", "untrigintilliarden", "dotrigintillionen",
            "dotrigintilliarden", "tretrigintillionen", "tretrigintilliarden",
            "quattuortrigintillionen", "quattuortrigintilliarden", "quintrigintillionen",
            "quintrigintilliarden", "setrigintillionen", "setrigintilliarden",
            "septentrigintillionen", "septentrigintilliarden", "oktotrigintillionen",
            "oktotrigintilliarden", "novemtrigintillionen", "novemtrigintilliarden",
            "quadragintillionen", "quadragintilliarden", "unquadragintillionen",
            "unquadragintilliarden", "doquadragintillionen", "doquadragintilliarden",
            "trequadragintillionen", "trequadragintilliarden", "quattuorquadragintillionen",
            "quattuorquadragintilliarden", "quinquadragintillionen", "quinquadragintilliarden",
            "sequadragintillionen", "sequadragintilliarden", "septenquadragintillionen",
            "septenquadragintilliarden", "oktoquadragintillionen", "oktoquadragintilliarden",
            "novemquadragintillionen", "novemquadragintilliarden", "quinquagintillionen",
            "quinquagintilliarden", "zentillionen", "zentilliarden",
            "quinquagintizentillionen", "quinquagintizentilliarden", "duzentillionen",
            "duzentilliarden"
    };

    public DeutschMacherModule() {
        super("Deutsch Macher", "Dieses Modul verwandelt die Modifikation in eine arische Modifikation.", Category.RENDER);
        this.markExperimental();
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(TextDrawEvent.ID, this);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(TextDrawEvent.ID, this);
    }

    private String toDeutsch(final byte num) {
        return toDeutsch(BigInteger.valueOf(num));
    }

    private String toDeutsch(final short num) {
        return toDeutsch(BigInteger.valueOf(num));
    }

    private String toDeutsch(final int num) {
        return toDeutsch(BigInteger.valueOf(num));
    }

    private String toDeutsch(final long num) {
        return toDeutsch(BigInteger.valueOf(num));
    }

    private String toDeutsch(BigInteger num) {
        final boolean minus = num.compareTo(BigInteger.ZERO) < 0;
        num = num.abs();
        final String s = num.toString();
        if (s.length() == 1) {
            final StringBuilder sb = new StringBuilder();
            if (minus) {
                sb.append("minus");
            }
            if (num.equals(BigInteger.ONE)) {
                sb.append("eins");
            } else {
                sb.append(getName(s.charAt(0)));
            }
            return sb.toString();
        } else if (num.compareTo(BigInteger.valueOf(20)) < 0) {
            final StringBuilder sb = new StringBuilder();
            if (minus) {
                sb.append("minus");
            }
            sb.append(getName2(s));
            return sb.toString();
        }
        final List<String> list = new ArrayList<>();
        final List<String> suffixList = new ArrayList<>();
        int suf = 0;
        for (int i = s.length() - 1; i >= 0; i -= 3) {
            final StringBuilder sb = new StringBuilder();
            final char c1 = 0 > i - 2 ? 0 : s.charAt(i - 2),
                    c2 = 0 > i - 1 ? 0 : s.charAt(i - 1),
                    c3 = s.charAt(i);
            if (c1 == '0' && c2 == '0' && c3 == '0') {
                ++suf;
                continue;
            }
            String s2 = "";
            if (c2 == '0' || c2 == 0) {
                if ((c1 == 0 || c1 == '0') && c3 == '1' && suf > 1) {
                    s2 += "eine";
                } else if (c3 == '1' && suf == 0) {
                    s2 += "eins";
                } else if (c3 != '0') {
                    s2 += getName(c3);
                }
            } else {
                int val = Integer.parseInt(c2 + "" + c3);
                if (val > 19) {
                    String s3;
                    if (c2 == '2') {
                        s3 = "zwan";
                    } else if (c2 == '6') {
                        s3 = "sech";
                    } else if (c2 == '7') {
                        s3 = "sieb";
                    } else {
                        s3 = getName(c2);
                    }
                    s3 += "zig";
                    if (val % 10 == 0) {
                        s2 += s3;
                    } else {
                        s2 += getName(c3) + "und" + s3;
                    }
                } else if (val != 0) {
                    s2 += getName2(c2 + "" + c3);
                }
            }
            if (c1 != 0 && c1 != '0') {
                sb.append(getName(c1));
                sb.append("hundert");
                sb.append(s2);
            } else {
                sb.append(s2);
            }
            if (suf >= SUFFIX.length) {
                throw new ArithmeticException("Number too big: " + suf + "/" + (SUFFIX.length - 1));
            }
            list.add(sb.toString());
            suffixList.add(SUFFIX[suf]);
            ++suf;
        }
        final StringBuilder sb = new StringBuilder();
        if (minus) {
            sb.append("minus");
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            sb.append(list.get(i)).append(suffixList.get(i));
        }
        return sb.toString();
    }

    private String getName(final char num) {
        return switch (num) {
            case '0' -> "null";
            case '1' -> "ein";
            case '2' -> "zwei";
            case '3' -> "drei";
            case '4' -> "vier";
            case '5' -> "fünf";
            case '6' -> "sechs";
            case '7' -> "sieben";
            case '8' -> "acht";
            case '9' -> "neun";
            default -> throw new IllegalArgumentException("Illegal character: '" + num + "'.");
        };
    }

    private String getName2(final String num) {
        return switch (num) {
            case "10" -> "zehn";
            case "11" -> "elf";
            case "12" -> "zwölf";
            case "13" -> "dreizehn";
            case "14" -> "vierzehn";
            case "15" -> "fünfzehn";
            case "16" -> "sechzehn";
            case "17" -> "siebzehn";
            case "18" -> "achtzehn";
            case "19" -> "neunzehn";
            default -> throw new IllegalArgumentException("Illegal character: '" + num + "'.");
        };
    }

    private String toDeutscheZahlen(final String string) {
        final String[] parts = string.split("(?<!" + Formatting.FORMATTING_CODE_PREFIX + ")(?<=\\D|\\s)(?=\\d)|(?<=\\d)(?=\\D|\\s)");
        final StringBuilder result = new StringBuilder();
        for (final String part : parts) {
            try {
                result.append(this.toDeutsch(new BigDecimal(part).toBigInteger()));
                continue;
            } catch (NumberFormatException ignored) {
            }
            result.append(part);
        }
        return result.toString().trim();
    }

    @Override
    public void onTextDraw(final TextDrawEvent event) {
        event.text = this.toDeutscheZahlen(event.text).replace(",", "komma");
    }

}
