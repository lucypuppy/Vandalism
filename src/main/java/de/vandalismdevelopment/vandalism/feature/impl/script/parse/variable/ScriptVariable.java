package de.vandalismdevelopment.vandalism.feature.impl.script.parse.variable;

import de.vandalismdevelopment.vandalism.Vandalism;
import de.vandalismdevelopment.vandalism.feature.impl.script.parse.ScriptParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public enum ScriptVariable {

    USERNAME(() -> MinecraftClient.getInstance().getSession().getUsername()),
    UUID(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return player.getUuidAsString();
    }),
    RANDOM(() -> String.valueOf(ThreadLocalRandom.current().nextInt(1000, 9999))),
    X(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getX());
    }),
    Y(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getY());
    }),
    Z(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getZ());
    }),
    YAW(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getYaw());
    }),
    PITCH(() -> {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return "null";
        return String.valueOf(player.getPitch());
    }),
    DAY(() -> String.valueOf(new Date().getDay())),
    MONTH(() -> String.valueOf(new Date().getMonth())),
    YEAR(() -> String.valueOf(new Date().getYear())),
    HOURS(() -> String.valueOf(new Date().getHours())),
    MINUTES(() -> String.valueOf(new Date().getMinutes())),
    SECONDS(() -> String.valueOf(new Date().getSeconds())),
    MILLISECONDS(() -> String.valueOf(new Date().getTime())),
    MOD_NAME(() -> Vandalism.getInstance().getName()),
    MOD_VERSION(() -> Vandalism.getInstance().getVersion()),
    MOD_AUTHOR(() -> Vandalism.getInstance().getAuthor());

    private final ScriptCodeReplacement scriptCodeReplacement;

    ScriptVariable(final ScriptCodeReplacement scriptCodeReplacement) {
        this.scriptCodeReplacement = scriptCodeReplacement;
    }

    public String replaceCode(String code) {
        final String variable = ScriptParser.VARIABLE_CHAR + this.name().toLowerCase() + ScriptParser.VARIABLE_CHAR;
        while (code.contains(variable)) code = code.replaceFirst(variable, this.scriptCodeReplacement.replacement());
        return code;
    }

}