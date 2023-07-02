package me.nekosarekawaii.foxglove.feature.impl.command.impl.misc;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.nekosarekawaii.foxglove.Foxglove;
import me.nekosarekawaii.foxglove.event.KeyboardListener;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.command.Command;
import me.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import me.nekosarekawaii.foxglove.util.ChatUtils;
import me.nekosarekawaii.foxglove.util.RunnableScheduleUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicInteger;

@CommandInfo(name = "Macro", aliases = {"macro"}, description = "This Command lets you add, remove and list Chat Macros.", category = FeatureCategory.MISC)
public class MacroCommand extends Command implements KeyboardListener {

    private String message = "";

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder
                .then(literal("list")
                        .executes(context -> {
                            final Object2ObjectOpenHashMap<String, Integer> macros = Foxglove.getInstance().getConfigManager().getMainConfig().getChatMacros();
                            if (macros.isEmpty()) {
                                ChatUtils.errorChatMessage("No Macros are registered!");
                                return SINGLE_SUCCESS;
                            }
                            final StringBuilder moduleStringBuilder = new StringBuilder()
                                    .append(Formatting.DARK_AQUA)
                                    .append(Formatting.UNDERLINE)
                                    .append("Macros")
                                    .append(Formatting.RESET)
                                    .append('\n');
                            final AtomicInteger number = new AtomicInteger(1);
                            macros.object2ObjectEntrySet().fastForEach(macro -> {
                                moduleStringBuilder.append('\n')
                                        .append(Formatting.GRAY)
                                        .append(number.get() < 10 ? "0" : "")
                                        .append(number.get())
                                        .append(Formatting.DARK_GRAY)
                                        .append(". ")
                                        .append(Formatting.RESET)
                                        .append("'")
                                        .append(macro.getKey())
                                        .append("'")
                                        .append(" > ")
                                        .append(Formatting.WHITE)
                                        .append(macro.getValue());
                                number.getAndIncrement();
                            });
                            ChatUtils.emptyChatMessage();
                            ChatUtils.infoChatMessage(moduleStringBuilder.toString());
                            return SINGLE_SUCCESS;
                        })
                )
                .then(literal("add")
                        .then(argument("message", StringArgumentType.greedyString())
                                .executes(context -> {
                                    this.message = context.getArgument("message", String.class);
                                    RunnableScheduleUtils.scheduleRunnableWithDelayForMinecraftThread(() -> {
                                        DietrichEvents2.global().subscribe(KeyboardEvent.ID, this);
                                        Foxglove.getInstance().blockKeyEvent = true;
                                        ChatUtils.infoChatMessage("Listening for Key Input...");
                                    }, 100);
                                    return SINGLE_SUCCESS;
                                }))
                )
                .then(literal("remove")
                        .then(argument("removeNumber", IntegerArgumentType.integer(1))
                                .executes(context -> {
                                    final Object2ObjectOpenHashMap<String, Integer> macros = Foxglove.getInstance().getConfigManager().getMainConfig().getChatMacros();
                                    if (macros.isEmpty()) {
                                        ChatUtils.errorChatMessage("No Macros are registered!");
                                        return SINGLE_SUCCESS;
                                    }
                                    final int removeNumber = context.getArgument("removeNumber", Integer.class);
                                    if (removeNumber < macros.size() + 1) {
                                        final AtomicInteger number = new AtomicInteger(0);
                                        macros.object2ObjectEntrySet().fastForEach(macro -> {
                                            number.getAndIncrement();
                                            if (number.get() == removeNumber) {
                                                macros.remove(macro.getKey());
                                                ChatUtils.infoChatMessage("Removed Macro '" + macro.getKey() + " > " + macro.getValue() + "'.");
                                            }
                                        });
                                        if (number.get() < 1)
                                            ChatUtils.errorChatMessage("No Macro with the Number " + removeNumber + " has been found!");
                                    } else
                                        ChatUtils.errorChatMessage("No Macro with the Number " + removeNumber + " has been found!");
                                    return SINGLE_SUCCESS;
                                }))
                );
    }

    @Override
    public void onKey(final long window, final int key, final int scanCode, final int action, final int modifiers) {
        if (action != GLFW.GLFW_PRESS) return;
        DietrichEvents2.global().unsubscribe(KeyboardEvent.ID, this);
        if (key == GLFW.GLFW_KEY_ESCAPE || key == GLFW.GLFW_KEY_BACKSPACE) {
            ChatUtils.infoChatMessage("Aborted Chat Macro binding!");
            this.message = "";
            Foxglove.getInstance().blockKeyEvent = false;
            return;
        }
        if (this.message.isEmpty()) ChatUtils.errorChatMessage("Invalid Macro Message!");
        else {
            Foxglove.getInstance().getConfigManager().getMainConfig().getChatMacros().put(this.message, key);
            ChatUtils.infoChatMessage("'" + this.message + "' has been bound to the Key " + GLFW.glfwGetKeyName(key, scanCode) + ".");
            this.message = "";
            Foxglove.getInstance().blockKeyEvent = false;
        }
    }

}
