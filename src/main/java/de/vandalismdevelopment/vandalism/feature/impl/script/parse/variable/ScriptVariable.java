package de.vandalismdevelopment.vandalism.feature.impl.script.parse.variable;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.ScriptParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public enum ScriptVariable {

    USERNAME(() -> MinecraftClient.getInstance().getSession().getUsername(), descriptionBuilder -> {
        descriptionBuilder.append("Your current Username.");
        return descriptionBuilder;
    }),
    UUID(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return player.getUuidAsString();
    }, descriptionBuilder -> {
        descriptionBuilder.append("Your current UUID.");
        return descriptionBuilder;
    }),
    RANDOM(() -> String.valueOf(ThreadLocalRandom.current().nextInt(1000, 9999)), descriptionBuilder -> {
        descriptionBuilder.append("A random number between 1000 and 9999.");
        return descriptionBuilder;
    }),
    X(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getX());
    }, descriptionBuilder -> {
        descriptionBuilder.append("Your current X position.");
        return descriptionBuilder;
    }),
    Y(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getY());
    }, descriptionBuilder -> {
        descriptionBuilder.append("Your current Y position.");
        return descriptionBuilder;
    }),
    Z(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getZ());
    }, descriptionBuilder -> {
        descriptionBuilder.append("Your current Z position.");
        return descriptionBuilder;
    }),
    YAW(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getYaw());
    }, descriptionBuilder -> {
        descriptionBuilder.append("Your current Yaw.");
        return descriptionBuilder;
    }),
    PITCH(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getPitch());
    }, descriptionBuilder -> {
        descriptionBuilder.append("Your current Pitch.");
        return descriptionBuilder;
    }),
    DAY(() -> String.valueOf(new Date().getDay()), descriptionBuilder -> {
        descriptionBuilder.append("The current day of the month.");
        return descriptionBuilder;
    }),
    MONTH(() -> String.valueOf(new Date().getMonth()), descriptionBuilder -> {
        descriptionBuilder.append("The current month.");
        return descriptionBuilder;
    }),
    YEAR(() -> String.valueOf(new Date().getYear()), descriptionBuilder -> {
        descriptionBuilder.append("The current year.");
        return descriptionBuilder;
    }),
    HOURS(() -> String.valueOf(new Date().getHours()), descriptionBuilder -> {
        descriptionBuilder.append("The current hour.");
        return descriptionBuilder;
    }),
    MINUTES(() -> String.valueOf(new Date().getMinutes()), descriptionBuilder -> {
        descriptionBuilder.append("The current minute.");
        return descriptionBuilder;
    }),
    SECONDS(() -> String.valueOf(new Date().getSeconds()), descriptionBuilder -> {
        descriptionBuilder.append("The current second.");
        return descriptionBuilder;
    }),
    MILLISECONDS(() -> String.valueOf(new Date().getTime()), descriptionBuilder -> {
        descriptionBuilder.append("The current millisecond.");
        return descriptionBuilder;
    }),
    MOD_NAME(() -> Vandalism.getInstance().getName(), descriptionBuilder -> {
        descriptionBuilder.append("The name of this mod.");
        return descriptionBuilder;
    }),
    MOD_VERSION(() -> Vandalism.getInstance().getVersion(), descriptionBuilder -> {
        descriptionBuilder.append("The version of this mod.");
        return descriptionBuilder;
    }),
    MOD_AUTHOR(() -> Vandalism.getInstance().getAuthor(), descriptionBuilder -> {
        descriptionBuilder.append("The author of this mod.");
        return descriptionBuilder;
    });

    private final ScriptCodeReplacement scriptCodeReplacement;
    private final ScriptVariableDescription scriptVariableDescription;

    ScriptVariable(final ScriptCodeReplacement scriptCodeReplacement, final ScriptVariableDescription scriptVariableDescription) {
        this.scriptCodeReplacement = scriptCodeReplacement;
        this.scriptVariableDescription = scriptVariableDescription;
    }

    public String replaceCode(String code) {
        final String variable = ScriptParser.VARIABLE_CHAR + this.name().toLowerCase() + ScriptParser.VARIABLE_CHAR;
        while (code.contains(variable)) code = code.replaceFirst(variable, this.scriptCodeReplacement.replacement());
        return code;
    }

    public String getDescription() {
        return this.scriptVariableDescription.getDescription(new StringBuilder()).toString();
    }

}