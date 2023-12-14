package de.nekosarekawaii.vandalism.feature.script.parse.command;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.feature.command.AbstractCommand;
import de.nekosarekawaii.vandalism.feature.script.parse.ScriptParser;
import de.nekosarekawaii.vandalism.feature.script.parse.ScriptVariable;
import de.nekosarekawaii.vandalism.util.minecraft.ChatUtil;
import de.nekosarekawaii.vandalism.util.minecraft.MovementUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public enum ScriptCommand {

    RUN((scriptName, lineNumber, code, execute) -> {
        final String[] args = code.split("( )+");
        if (args.length < 1) throw new RuntimeException("Run command needs at least one argument");
        if (args[0].equals("script") && args.length > 2 && args[1].equals("execute") && args[2].equals(scriptName)) {
            throw new RuntimeException("This script can't run itself because this would cause a stack overflow");
        }
        final var dispatcher = Vandalism.getInstance().getCommandManager().getCommandDispatcher();
        if (execute) {
            dispatcher.execute(ScriptVariable.applyReplacements(code), AbstractCommand.COMMAND_SOURCE);
        } else {
            final ParseResults<CommandSource> parse = dispatcher.parse(code, AbstractCommand.COMMAND_SOURCE);
            if (parse.getReader().canRead()) {
                if (parse.getExceptions().size() == 1) {
                    throw parse.getExceptions().values().iterator().next();
                } else if (parse.getContext().getRange().isEmpty()) {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parse.getReader());
                } else {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parse.getReader());
                }
            } else if (parse.getContext().getCommand() == null) {
                throw new RuntimeException("The command needs at least one argument");
            }
        }
    }, exampleBuilder -> {
        exampleBuilder.append("This command can run the mods own command system like this \"run <mod_command>\".");
        exampleBuilder.append('\n');
        exampleBuilder.append("For example \"run say Hello World!\" which would say \"Hello World!\" in the chat.");
        return exampleBuilder;
    }), ADD_CHAT_MESSAGE((scriptName, lineNumber, code, execute) -> {
        final String[] args = code.split("( )+");
        if (args.length < 1) throw new RuntimeException("AddChatMessage command needs at least one argument");
        if (execute) {
            final MutableText text = Text.empty();
            text.setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
            text.append("[");
            text.append(Text.literal(scriptName));
            text.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(Color.CYAN.getRGB())));
            text.append("] ");
            text.append(Text.literal(ScriptVariable.applyReplacements(code)));
            ChatUtil.chatMessage(text);
        }
    }, exampleBuilder -> {
        exampleBuilder.append("This command can add a chat message to the chat like this \"add_chat_message <message>\".");
        exampleBuilder.append('\n');
        exampleBuilder.append("For example \"add_chat_message Hello World!\" which would add \"Hello World!\" to the chat.");
        return exampleBuilder;
    }), JUMP((scriptName, lineNumber, code, execute) -> {
        if (execute) {
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.jump();
            }
        }
        if (code.isBlank()) return;
        if (code.split("( )+").length >= 1) {
            final String commandWithCode = ScriptParser.CODE_CHAR + code, command = commandWithCode.replaceFirst(ScriptParser.CODE_CHAR, "");
            final Pair<ScriptCommand, Pair<Integer, String>> parsedCodeObject = ScriptParser.parseCodeFromScriptLine(scriptName, commandWithCode, lineNumber, execute);
            if (parsedCodeObject != null) {
                final String parsedCode = parsedCodeObject.getRight().getRight();
                final ScriptCommand scriptCommand = parsedCodeObject.getLeft();
                if (execute) scriptCommand.execute(scriptName, lineNumber, parsedCode);
                else scriptCommand.check(scriptName, lineNumber, parsedCode);
            } else throw new RuntimeException("Unknown script command after jump command '" + command + "'");
        }
    }, exampleBuilder -> {
        exampleBuilder.append("This command can make the player jump like this \"jump\".");
        exampleBuilder.append('\n');
        exampleBuilder.append("You can also run multiple commands after the jump command like this \"jump <command>\".");
        exampleBuilder.append('\n');
        exampleBuilder.append("For example \"jump set_speed 0.5\" which would make the player jump and set the speed of the player to 0.5.");
        return exampleBuilder;
    }), SET_SPEED((scriptName, lineNumber, code, execute) -> {
        final String[] args = code.split("( )+");
        if (args.length < 1) throw new RuntimeException("SetSpeed command needs at least one argument");
        final String commandWithCode = ScriptParser.CODE_CHAR + code.substring(code.indexOf(" ") + 1), command = commandWithCode.replaceFirst(ScriptParser.CODE_CHAR, "");
        final double speed;
        try {
            speed = Double.parseDouble(args[0].trim());
        } catch (final NumberFormatException e) {
            throw new RuntimeException("Invalid double value '" + args[0] + "' for speed argument after SetSpeed command");
        }
        if (execute) {
            if (MinecraftClient.getInstance().player != null) {
                MovementUtil.setSpeed(speed);
            }
        }
        if (args.length > 1) {
            final Pair<ScriptCommand, Pair<Integer, String>> parsedCodeObject = ScriptParser.parseCodeFromScriptLine(scriptName, commandWithCode, lineNumber, execute);
            if (parsedCodeObject != null) {
                final String parsedCode = parsedCodeObject.getRight().getRight();
                final ScriptCommand scriptCommand = parsedCodeObject.getLeft();
                if (execute) scriptCommand.execute(scriptName, lineNumber, parsedCode);
                else scriptCommand.check(scriptName, lineNumber, parsedCode);
            } else throw new RuntimeException("Unknown script command after set speed command '" + command + "'");
        }
    }, exampleBuilder -> {
        exampleBuilder.append("This command can set the speed of the player like this \"set_speed <speed>\".");
        exampleBuilder.append('\n');
        exampleBuilder.append("For example \"set_speed 0.5\" which would set the speed to 0.5.");
        exampleBuilder.append('\n');
        exampleBuilder.append("You can also run multiple commands after the set speed command like this \"set_speed <speed> <command>\".");
        exampleBuilder.append('\n');
        exampleBuilder.append("For example \"set_speed 0.5 set_yaw 100\" which would set the speed to 0.5 and set the yaw of player to 100.");
        return exampleBuilder;
    }), SET_YAW((scriptName, lineNumber, code, execute) -> {
        final String[] args = code.split("( )+");
        if (args.length < 1) throw new RuntimeException("SetVelocityYaw command needs at least one argument");
        final String commandWithCode = ScriptParser.CODE_CHAR + code.substring(code.indexOf(" ") + 1), command = commandWithCode.replaceFirst(ScriptParser.CODE_CHAR, "");
        final float yaw;
        try {
            yaw = Float.parseFloat(args[0].trim());
        } catch (final NumberFormatException e) {
            throw new RuntimeException("Invalid float value '" + args[0] + "' for yaw argument after SetYaw command");
        }
        if (execute) {
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.setYaw(yaw);
            }
        }
        if (args.length > 1) {
            final Pair<ScriptCommand, Pair<Integer, String>> parsedCodeObject = ScriptParser.parseCodeFromScriptLine(scriptName, commandWithCode, lineNumber, execute);
            if (parsedCodeObject != null) {
                final String parsedCode = parsedCodeObject.getRight().getRight();
                final ScriptCommand scriptCommand = parsedCodeObject.getLeft();
                if (execute) scriptCommand.execute(scriptName, lineNumber, parsedCode);
                else scriptCommand.check(scriptName, lineNumber, parsedCode);
            } else throw new RuntimeException("Unknown script command after set yaw command '" + command + "'");
        }
    }, exampleBuilder -> {
        exampleBuilder.append("This command can set the yaw of the player like this \"set_yaw <yaw>\".");
        exampleBuilder.append('\n');
        exampleBuilder.append("For example \"set_yaw 100\" which would set the yaw of the player to 100.");
        exampleBuilder.append('\n');
        exampleBuilder.append("You can also run multiple commands after the set yaw command like this \"set_yaw <yaw> <command>\".");
        exampleBuilder.append('\n');
        exampleBuilder.append("For example \"set_yaw 100 set_pitch 50\" which would set the yaw" + " of the player to 100 and set the pitch of the player to 50.");
        return exampleBuilder;
    }), SET_PITCH((scriptName, lineNumber, code, execute) -> {
        final String[] args = code.split("( )+");
        if (args.length < 1) throw new RuntimeException("SetVelocityPitch command needs at least one argument");
        final String commandWithCode = ScriptParser.CODE_CHAR + code.substring(code.indexOf(" ") + 1), command = commandWithCode.replaceFirst(ScriptParser.CODE_CHAR, "");
        final float pitch;
        try {
            pitch = Float.parseFloat(args[0].trim());
        } catch (final NumberFormatException e) {
            throw new RuntimeException("Invalid float value '" + args[0] + "' for pitch argument after SetYaw command");
        }
        if (execute) {
            if (MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.setPitch(pitch);
            }
        }
        if (args.length > 1) {
            final Pair<ScriptCommand, Pair<Integer, String>> parsedCodeObject = ScriptParser.parseCodeFromScriptLine(scriptName, commandWithCode, lineNumber, execute);
            if (parsedCodeObject != null) {
                final String parsedCode = parsedCodeObject.getRight().getRight();
                final ScriptCommand scriptCommand = parsedCodeObject.getLeft();
                if (execute) scriptCommand.execute(scriptName, lineNumber, parsedCode);
                else scriptCommand.check(scriptName, lineNumber, parsedCode);
            } else throw new RuntimeException("Unknown script command after set pitch command '" + command + "'");
        }
    }, exampleBuilder -> {
        exampleBuilder.append("This command can set the pitch of the player like this \"set_pitch <pitch>\".");
        exampleBuilder.append('\n');
        exampleBuilder.append("For example \"set_pitch 50\" which would set the pitch of the player to 50.");
        exampleBuilder.append('\n');
        exampleBuilder.append("You can also run multiple commands after the set pitch command like this \"set_pitch <pitch> <command>\".");
        exampleBuilder.append('\n');
        exampleBuilder.append("For example \"set_pitch 50 set_yaw 100\" which would set the pitch of the" + " player to 50 and set the yaw of the player to 100.");
        return exampleBuilder;
    }), SET_VELOCITY_X((scriptName, lineNumber, code, execute) -> {
        final String[] args = code.split("( )+");
        if (args.length < 1) throw new RuntimeException("SetVelocityX command needs at least one argument");
        final String commandWithCode = ScriptParser.CODE_CHAR + code.substring(code.indexOf(" ") + 1), command = commandWithCode.replaceFirst(ScriptParser.CODE_CHAR, "");
        final double xVelocity;
        try {
            xVelocity = Double.parseDouble(args[0].trim());
        } catch (final NumberFormatException e) {
            throw new RuntimeException("Invalid double value '" + args[0] + "' for xVelocity argument after SetVelocityX command");
        }
        if (execute) {
            if (MinecraftClient.getInstance().player != null) {
                final Vec3d velocity = MinecraftClient.getInstance().player.getVelocity();
                MinecraftClient.getInstance().player.setVelocity(xVelocity, velocity.y, velocity.z);
            }
        }
        if (args.length > 1) {
            final Pair<ScriptCommand, Pair<Integer, String>> parsedCodeObject = ScriptParser.parseCodeFromScriptLine(scriptName, commandWithCode, lineNumber, execute);
            if (parsedCodeObject != null) {
                final String parsedCode = parsedCodeObject.getRight().getRight();
                final ScriptCommand scriptCommand = parsedCodeObject.getLeft();
                if (execute) scriptCommand.execute(scriptName, lineNumber, parsedCode);
                else scriptCommand.check(scriptName, lineNumber, parsedCode);
            } else throw new RuntimeException("Unknown script command after set velocity x command '" + command + "'");
        }
    }, exampleBuilder -> {
        exampleBuilder.append("This command can set the x velocity of the player like this \"set_velocity_x <x_velocity>\".");
        exampleBuilder.append('\n');
        exampleBuilder.append("For example \"set_velocity_x 0.5\" which would set the x velocity of the player to 0.5.");
        exampleBuilder.append('\n');
        exampleBuilder.append("You can also run multiple commands after the set velocity x command like this \"set_velocity_x <x_velocity> <command>\".");
        exampleBuilder.append('\n');
        exampleBuilder.append("For example \"set_velocity_x 0.5 set_velocity_y 0.5\" which would set" + " the x velocity of the player to 0.5 and set the y velocity of the player to 0.5.");
        return exampleBuilder;
    }), SET_VELOCITY_Y((scriptName, lineNumber, code, execute) -> {
        final String[] args = code.split("( )+");
        if (args.length < 1) throw new RuntimeException("SetVelocityY command needs at least one argument");
        final String commandWithCode = ScriptParser.CODE_CHAR + code.substring(code.indexOf(" ") + 1), command = commandWithCode.replaceFirst(ScriptParser.CODE_CHAR, "");
        final double yVelocity;
        try {
            yVelocity = Double.parseDouble(args[0].trim());
        } catch (final NumberFormatException e) {
            throw new RuntimeException("Invalid double value '" + args[0] + "' for yVelocity argument after SetVelocityY command");
        }
        if (execute) {
            if (MinecraftClient.getInstance().player != null) {
                final Vec3d velocity = MinecraftClient.getInstance().player.getVelocity();
                MinecraftClient.getInstance().player.setVelocity(velocity.x, yVelocity, velocity.z);
            }
        }
        if (args.length > 1) {
            final Pair<ScriptCommand, Pair<Integer, String>> parsedCodeObject = ScriptParser.parseCodeFromScriptLine(scriptName, commandWithCode, lineNumber, execute);
            if (parsedCodeObject != null) {
                final String parsedCode = parsedCodeObject.getRight().getRight();
                final ScriptCommand scriptCommand = parsedCodeObject.getLeft();
                if (execute) scriptCommand.execute(scriptName, lineNumber, parsedCode);
                else scriptCommand.check(scriptName, lineNumber, parsedCode);
            } else throw new RuntimeException("Unknown script command after set velocity y command '" + command + "'");
        }
    }, exampleBuilder -> {
        exampleBuilder.append("This command can set the y velocity of the player like this \"set_velocity_y <y_velocity>\".");
        exampleBuilder.append('\n');
        exampleBuilder.append("For example \"set_velocity_y 0.5\" which would set the y velocity of the player to 0.5.");
        exampleBuilder.append('\n');
        exampleBuilder.append("You can also run multiple commands after the set velocity y command like this \"set_velocity_y <y_velocity> <command>\".");
        exampleBuilder.append('\n');
        exampleBuilder.append("For example \"set_velocity_y 0.5 set_velocity_z 0.5\" which would set the y velocity" + " of the player to 0.5 and set the z velocity of the player to 0.5.");
        return exampleBuilder;
    }), SET_VELOCITY_Z((scriptName, lineNumber, code, execute) -> {
        final String[] args = code.split("( )+");
        if (args.length < 1) throw new RuntimeException("SetVelocityZ command needs at least one argument");
        final String commandWithCode = ScriptParser.CODE_CHAR + code.substring(code.indexOf(" ") + 1), command = commandWithCode.replaceFirst(ScriptParser.CODE_CHAR, "");
        final double zVelocity;
        try {
            zVelocity = Double.parseDouble(args[0].trim());
        } catch (final NumberFormatException e) {
            throw new RuntimeException("Invalid double value '" + args[0] + "' for zVelocity argument after SetVelocityZ command");
        }
        if (execute) {
            if (MinecraftClient.getInstance().player != null) {
                final Vec3d velocity = MinecraftClient.getInstance().player.getVelocity();
                MinecraftClient.getInstance().player.setVelocity(velocity.x, velocity.y, zVelocity);
            }
        }
        if (args.length > 1) {
            final Pair<ScriptCommand, Pair<Integer, String>> parsedCodeObject = ScriptParser.parseCodeFromScriptLine(scriptName, commandWithCode, lineNumber, execute);
            if (parsedCodeObject != null) {
                final String parsedCode = parsedCodeObject.getRight().getRight();
                final ScriptCommand scriptCommand = parsedCodeObject.getLeft();
                if (execute) scriptCommand.execute(scriptName, lineNumber, parsedCode);
                else scriptCommand.check(scriptName, lineNumber, parsedCode);
            } else throw new RuntimeException("Unknown script command after set velocity z command '" + command + "'");
        }
    }, exampleBuilder -> {
        exampleBuilder.append("This command can set the z velocity of the player like this \"set_velocity_z <z_velocity>\".");
        exampleBuilder.append('\n');
        exampleBuilder.append("For example \"set_velocity_z 0.5\" which would set the z velocity of the player to 0.5.");
        exampleBuilder.append('\n');
        exampleBuilder.append("You can also run multiple commands after the set velocity z command like this \"set_velocity_z <z_velocity> <command>\".");
        exampleBuilder.append('\n');
        exampleBuilder.append("For example \"set_velocity_z 0.5 set_velocity_x 0.5\" which would set the z velocity" + " of the player to 0.5 and set the x velocity of the player to 0.5.");
        return exampleBuilder;
    }), WAIT((scriptName, lineNumber, code, execute) -> {
        final String[] args = code.split("( )+");
        if (args.length < 1) throw new RuntimeException("Wait command needs at least one argument");
        final String commandWithCode = ScriptParser.CODE_CHAR + code.substring(code.indexOf(" ") + 1), command = commandWithCode.replaceFirst(ScriptParser.CODE_CHAR, "");
        final long delay;
        try {
            delay = Long.parseLong(args[0].trim());
        } catch (final NumberFormatException e) {
            throw new RuntimeException("Invalid long value '" + args[0] + "' for delay argument after wait command");
        }
        if (delay < 1) {
            throw new RuntimeException("Delay '" + args[0] + "' after wait command must be greater than 0");
        }
        if (execute) {
            try {
                Thread.sleep(delay);
            } catch (final InterruptedException ignored) {
            }
        }
        if (args.length > 1) {
            final Pair<ScriptCommand, Pair<Integer, String>> parsedCodeObject = ScriptParser.parseCodeFromScriptLine(scriptName, commandWithCode, lineNumber, execute);
            if (parsedCodeObject != null) {
                final String parsedCode = parsedCodeObject.getRight().getRight();
                final ScriptCommand scriptCommand = parsedCodeObject.getLeft();
                if (execute) scriptCommand.execute(scriptName, lineNumber, parsedCode);
                else scriptCommand.check(scriptName, lineNumber, parsedCode);
            } else throw new RuntimeException("Unknown script command after wait command '" + command + "'");
        }
    }, exampleBuilder -> {
        exampleBuilder.append("This command lets the script wait for a specific amount of time like this \"wait <delay>\".");
        exampleBuilder.append('\n');
        exampleBuilder.append("For example \"wait 1000\" which would let the script wait for 1000 milliseconds.");
        exampleBuilder.append('\n');
        exampleBuilder.append("You can also run multiple commands after the wait command like this \"wait <delay> <command>\".");
        exampleBuilder.append('\n');
        exampleBuilder.append("For example \"wait 1000 jump\" which would let the script wait for 1000 milliseconds and make the player jump.");
        return exampleBuilder;
    }), TIMES((scriptName, lineNumber, code, execute) -> {
        final String[] args = code.split("( )+");
        if (args.length < 1) throw new RuntimeException("Times command needs at least one argument");
        final String commandWithCode = ScriptParser.CODE_CHAR + code.substring(code.indexOf(" ") + 1), command = commandWithCode.replaceFirst(ScriptParser.CODE_CHAR, "");
        int amount;
        try {
            amount = Integer.parseInt(args[0].trim());
        } catch (final NumberFormatException e) {
            throw new RuntimeException("Invalid integer value '" + args[0] + "' for amount argument after times command");
        }
        if (amount < 1) {
            throw new RuntimeException("Amount '" + args[0] + "' after times command must be greater than 0");
        }
        if (args.length < 2) {
            throw new RuntimeException("Times command needs at least two arguments '" + args[0] + "'");
        }
        for (int i = 0; i < amount; i++) {
            final Pair<ScriptCommand, Pair<Integer, String>> parsedCodeObject = ScriptParser.parseCodeFromScriptLine(scriptName, commandWithCode, lineNumber, execute);
            if (parsedCodeObject != null) {
                final String parsedCode = parsedCodeObject.getRight().getRight();
                final ScriptCommand scriptCommand = parsedCodeObject.getLeft();
                if (execute) scriptCommand.execute(scriptName, lineNumber, parsedCode);
                else scriptCommand.check(scriptName, lineNumber, parsedCode);
            } else throw new RuntimeException("Unknown script command after times command '" + command + "'");
        }
    }, exampleBuilder -> {
        exampleBuilder.append("This command lets the script run a command a specific amount of time like this \"times <amount> <command>\".");
        exampleBuilder.append('\n');
        exampleBuilder.append("For example \"times 5 jump\" which would make the player jump 5 times.");
        exampleBuilder.append('\n');
        exampleBuilder.append("You can also run multiple commands after the times command like this \"times <amount> <command> <command>\".");
        return exampleBuilder;
    });

    private final ScriptCommandExecution scriptCommandExecution;
    private final ScriptCommandExample scriptCommandExample;

    ScriptCommand(final ScriptCommandExecution scriptCommandExecution, final ScriptCommandExample scriptCommandExample) {
        this.scriptCommandExecution = scriptCommandExecution;
        this.scriptCommandExample = scriptCommandExample;
    }

    public void check(final String scriptName, final int lineNumber, final String code) throws Exception {
        this.scriptCommandExecution.execute(scriptName, lineNumber, code, false);
    }

    public void execute(final String scriptName, final int lineNumber, final String code) throws Exception {
        this.scriptCommandExecution.execute(scriptName, lineNumber, code, true);
    }

    public String getExample() {
        return this.scriptCommandExample.getExample(new StringBuilder()).toString();
    }

}
