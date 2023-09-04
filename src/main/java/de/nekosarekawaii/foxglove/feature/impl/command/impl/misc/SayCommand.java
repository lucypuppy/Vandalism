package de.nekosarekawaii.foxglove.feature.impl.command.impl.misc;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.nekosarekawaii.foxglove.feature.FeatureCategory;
import de.nekosarekawaii.foxglove.feature.impl.command.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.message.LastSeenMessagesCollector;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;

import java.time.Instant;

public class SayCommand extends Command {

    public SayCommand() {
        super(
                "Say",
                "Allows you to send every message into the chat by skipping the command system of this mod.",
                FeatureCategory.MISC,
                false,
                "say"
        );
    }

    @Override
    public void build(final LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("message", StringArgumentType.greedyString()).executes(context -> {
            final String message = context.getArgument("message", String.class);
            final Instant instant = Instant.now();
            final long l = NetworkEncryptionUtils.SecureRandomUtil.nextLong();
            if (networkHandler() != null) {
                final LastSeenMessagesCollector.LastSeenMessages lastSeenMessages = networkHandler().lastSeenMessagesCollector.collect();
                final MessageSignatureData messageSignatureData = networkHandler().messagePacker.pack(
                        new MessageBody(message, instant, l, lastSeenMessages.lastSeen())
                );
                networkHandler().sendPacket(new ChatMessageC2SPacket(message, instant, l, messageSignatureData, lastSeenMessages.update()));
            }
            return singleSuccess;
        }));
    }

}
