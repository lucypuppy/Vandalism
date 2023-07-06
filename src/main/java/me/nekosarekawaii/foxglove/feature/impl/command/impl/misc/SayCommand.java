package me.nekosarekawaii.foxglove.feature.impl.command.impl.misc;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.nekosarekawaii.foxglove.feature.FeatureCategory;
import me.nekosarekawaii.foxglove.feature.impl.command.Command;
import me.nekosarekawaii.foxglove.feature.impl.command.CommandInfo;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.message.LastSeenMessagesCollector;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

import java.time.Instant;

@CommandInfo(name = "Say", description = "Sends messages in chat, can be used to bypass clientside command restrictions.", aliases = {"say", "message", "msg"}, category = FeatureCategory.MISC)
public class SayCommand extends Command {

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("message", StringArgumentType.greedyString()).executes(context -> {
            final String message = context.getArgument("message", String.class);
            final Instant instant = Instant.now();
            final long l = NetworkEncryptionUtils.SecureRandomUtil.nextLong();
            ClientPlayNetworkHandler handler = mc().getNetworkHandler();
            if (handler != null) {
                final LastSeenMessagesCollector.LastSeenMessages lastSeenMessages = handler.lastSeenMessagesCollector.collect();
                final MessageSignatureData messageSignatureData = handler.messagePacker
                        .pack(new MessageBody(message, instant, l, lastSeenMessages.lastSeen()));
                handler.sendPacket(new ChatMessageC2SPacket(message, instant, l, messageSignatureData, lastSeenMessages.update()));
            }
            return SINGLE_SUCCESS;
        }));
    }

}
