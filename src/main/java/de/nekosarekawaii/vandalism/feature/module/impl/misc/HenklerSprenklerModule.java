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

package de.nekosarekawaii.vandalism.feature.module.impl.misc;

import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.DoubleValue;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.StringValue;
import de.nekosarekawaii.vandalism.base.value.impl.rendering.ButtonValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.EnumModeValue;
import de.nekosarekawaii.vandalism.event.player.PlayerUpdateListener;
import de.nekosarekawaii.vandalism.feature.module.Module;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import de.nekosarekawaii.vandalism.util.MSTimer;
import de.nekosarekawaii.vandalism.util.StringUtils;
import de.nekosarekawaii.vandalism.util.interfaces.IName;
import de.nekosarekawaii.vandalism.util.math.RandomUtils;
import net.minecraft.util.Formatting;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HenklerSprenklerModule extends Module implements PlayerUpdateListener {

    private static final int SAMPLE_RATE = 48_000;
    private static final double TONE_DURATION = 0.02;
    private static final int NUM_SAMPLES = /*(int) (SAMPLE_RATE * TONE_DURATION)*/ 960;
    private static final int MAX_GROUP_NAME_LENGTH = 24;

    private final EnumModeValue<HenklerSprenklerModule.Mode> mode = new EnumModeValue<>(
            this,
            "Mode",
            "The mode of the module.",
            Mode.VOICE,
            Mode.values()
    );

    private final EnumModeValue<HenklerSprenklerModule.VoiceMode> voiceMode = new EnumModeValue<>(
            this,
            "Voice Mode",
            "The voice input mode.",
            VoiceMode.STATIC_NOISE,
            VoiceMode.values()
    ).visibleCondition(() -> this.mode.getValue() == Mode.VOICE);

    private final DoubleValue frequency = new DoubleValue(
            this,
            "Frequency",
            "Sound frequency in Hertz.",
            50.0,
            20.0,
            20_000.0
    ).visibleCondition(() -> this.mode.getValue() == Mode.VOICE && this.voiceMode.getValue() == VoiceMode.HERTZ);

    private final StringValue groupName = new StringValue(
            this,
            "Group name",
            "The name of the groups.",
            ""
    ).visibleCondition(() -> this.mode.getValue() == Mode.GROUP);

    private final IntegerValue randomAudioSamplesCount = new IntegerValue(
            this,
            "Random Audio Samples Count",
            "Count of pre-generated random audio samples.",
            256,
            1,
            512
    ).visibleCondition(() -> this.mode.getValue() == Mode.VOICE);

    private final ButtonValue resetRandomAudioSamples = new ButtonValue(
            this,
            "Reset Random Audio Samples",
            "Resets the pre-generated random audio samples.",
            value -> this.randomAudioSamples = null
    ).visibleCondition(() -> this.mode.getValue() == Mode.VOICE);

    private final IntegerValue bufferSize = new IntegerValue(
            this,
            "Buffer Size",
            "The size of the audio buffer.",
            32,
            1,
            32
    ).visibleCondition(() -> this.mode.getValue() == Mode.VOICE);

    private final IntegerValue delay = new IntegerValue(
            this,
            "Delay",
            "The delay between executions.",
            0,
            0,
            10_000
    );

    private final MSTimer timer;
    private boolean done;
    private boolean unsupported;
    private Class<?> clientManagerClass;
    private Method getClientMethod;
    private Class<?> clientVoiceChatClass;
    private Method getMicThreadMethod;
    private Class<?> micThreadClass;
    private Field connectionField;
    private Field sequenceNumberField;
    private Field encoderField;
    private Class<?> opusEncoderClass;
    private Method resetStateMethod;
    private Method encodeMethod;
    private Class<?> networkMessageClass;
    private Constructor<?> networkMessageConstructor;
    private Class<?> clientVoiceChatConnectionClass;
    private Method clientVoiceChatConnectionSendToServerMethod;
    private Class<?> micPacketClass;
    private Constructor<?> micPacketConstructor;
    private short[][] randomAudioSamples;
    private Class<?> netManagerClass;
    private Method netManagerSendToServerMethod;
    private Constructor<?> createGroupPacketConstructor;
    private Object groupTypeOpen;

    private static short[] pcm(final double frequency, final int num, final int sampleRate) {
        final short[] pcm = new short[num];
        for (int i = 0; i < num; i++) {

            final double time = i / (double) sampleRate;

            final double angle = 2.0 * Math.PI * frequency * time;
            final double sineValue = Math.sin(angle);

            pcm[i] = (short) (sineValue * Short.MAX_VALUE);
        }
        return pcm;
    }

    public HenklerSprenklerModule() {
        super(
                "Henkler Sprenkler",
                "Applies a henkel to simple voice chat, causing sudden ear-piercing blasts that\n" +
                        "will leave other players scrambling for the volume button.\n" +
                        "Also allows you to create massive amounts of empty voice groups.\n\n" +
                        "Use responsibly... or don't!",
                Category.MISC
        );
        this.deactivateAfterSessionDefault();
        this.timer = new MSTimer();
    }

    @Override
    protected void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(this, PlayerUpdateEvent.ID);

        final ModPacketBlockerModule module = Vandalism.getInstance().getModuleManager().getModPacketBlockerModule();
        if (module.isActive()) {
            ChatUtil.errorChatMessage("Please deactivate the " + module.getName() + " module before using this " + this.getName() + " module.");
            this.deactivate();
            return;
        }

        if (this.unsupported) {
            this.deactivate();
            return;
        }

        if (!this.done) {
            try {
                this.clientManagerClass = Class.forName("de.maxhenkel.voicechat.voice.client.ClientManager");
                this.getClientMethod = this.clientManagerClass.getDeclaredMethod("getClient");
                this.clientVoiceChatClass = Class.forName("de.maxhenkel.voicechat.voice.client.ClientVoicechat");
                this.getMicThreadMethod = this.clientVoiceChatClass.getDeclaredMethod("getMicThread");
                this.micThreadClass = Class.forName("de.maxhenkel.voicechat.voice.client.MicThread");
                this.connectionField = this.micThreadClass.getDeclaredField("connection");
                this.connectionField.setAccessible(true);
                this.sequenceNumberField = this.micThreadClass.getDeclaredField("sequenceNumber");
                this.sequenceNumberField.setAccessible(true);
                this.encoderField = this.micThreadClass.getDeclaredField("encoder");
                this.encoderField.setAccessible(true);
                this.opusEncoderClass = Class.forName("de.maxhenkel.voicechat.api.opus.OpusEncoder");
                this.resetStateMethod = this.opusEncoderClass.getDeclaredMethod("resetState");
                this.encodeMethod = this.opusEncoderClass.getDeclaredMethod("encode", short[].class);
                this.networkMessageClass = Class.forName("de.maxhenkel.voicechat.voice.common.NetworkMessage");
                this.networkMessageConstructor = this.networkMessageClass.getDeclaredConstructor(Class.forName("de.maxhenkel.voicechat.voice.common.Packet"));
                this.clientVoiceChatConnectionClass = Class.forName("de.maxhenkel.voicechat.voice.client.ClientVoicechatConnection");
                this.clientVoiceChatConnectionSendToServerMethod = this.clientVoiceChatConnectionClass.getDeclaredMethod("sendToServer", this.networkMessageClass);
                this.micPacketClass = Class.forName("de.maxhenkel.voicechat.voice.common.MicPacket");
                this.micPacketConstructor = this.micPacketClass.getDeclaredConstructor(byte[].class, boolean.class, long.class);
                this.netManagerClass = Class.forName("de.maxhenkel.voicechat.net.NetManager");
                this.netManagerSendToServerMethod = this.netManagerClass.getDeclaredMethod("sendToServer", Class.forName("de.maxhenkel.voicechat.net.Packet"));
                final Class<?> groupTypeClass = Class.forName("de.maxhenkel.voicechat.api.Group$Type");
                this.createGroupPacketConstructor = Class.forName("de.maxhenkel.voicechat.net.CreateGroupPacket").getDeclaredConstructor(String.class, String.class, groupTypeClass);
                this.groupTypeOpen = groupTypeClass.getDeclaredField("OPEN").get(null);
            } catch (final ClassNotFoundException | NoSuchMethodException | NoSuchFieldException |
                           IllegalAccessException exception) {
                this.unsupported = true;
                this.deactivate();
                ChatUtil.errorChatMessage("Error while initializing " + this.getName() + " module, please make sure Simple Voice Chat is installed.");
                exception.printStackTrace();
                return;
            }

            this.done = true;
        }

        this.regenerateRandomAudioSamplesIfNeeded();
    }

    @Override
    protected void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(this, PlayerUpdateEvent.ID);
        this.randomAudioSamples = null;
    }

    private short[][] generateRandomAudioSamples(final int count) {
        final short[][] randomAudioSamples = new short[count][];
        for (int i = 0; i < count; i++) {
            final short[] buffer = new short[HenklerSprenklerModule.NUM_SAMPLES];
            for (int j = 0; j < HenklerSprenklerModule.NUM_SAMPLES; j++) {
                buffer[j] = (short) ThreadLocalRandom.current().nextInt(Short.MIN_VALUE, Short.MAX_VALUE);
            }
            randomAudioSamples[i] = buffer;
        }
        return randomAudioSamples;
    }

    private void regenerateRandomAudioSamples() {
        this.randomAudioSamples = this.generateRandomAudioSamples(this.randomAudioSamplesCount.getDefaultValue());
    }

    private void regenerateRandomAudioSamplesIfNeeded() {
        final int count = this.randomAudioSamplesCount.getValue();
        if (this.randomAudioSamples == null || this.randomAudioSamples.length != count) {
            this.regenerateRandomAudioSamples();
        }
    }

    @Override
    public void onPrePlayerUpdate(final PlayerUpdateEvent event) {
        if (!this.timer.hasReached(this.delay.getValue(), true)) {
            return;
        }

        try {
            switch (this.mode.getValue()) {
                case GROUP -> {
                    final String value = this.groupName.getValue();
                    final int length = value.length();
                    if (length > HenklerSprenklerModule.MAX_GROUP_NAME_LENGTH) {
                        this.deactivate();
                        ChatUtil.errorChatMessage("Group name too long. (max 24)");
                        return;
                    }
                    final int x = (HenklerSprenklerModule.MAX_GROUP_NAME_LENGTH - length) / 2;
                    if (x <= 0) {
                        this.deactivate();
                        ChatUtil.errorChatMessage("No space for random formatting codes after group name.");
                        return;
                    }
                    this.netManagerSendToServerMethod.invoke(null, this.createGroupPacketConstructor.newInstance(
                            !value.isEmpty() ? value + IntStream.range(0, x).mapToObj(y -> Formatting.values()[ThreadLocalRandom.current().nextInt(Formatting.values().length)].toString()).collect(Collectors.joining()) :
                                    Formatting.values()[ThreadLocalRandom.current().nextInt(1, Formatting.values().length)].toString() + Formatting.OBFUSCATED +
                                            RandomUtils.randomString(20, true, true, true, false),
                            null,
                            this.groupTypeOpen
                    ));
                }
                case VOICE -> {
                    this.regenerateRandomAudioSamplesIfNeeded();
                    final Object client = this.getClientMethod.invoke(null);
                    final Object micThread = this.getMicThreadMethod.invoke(client);
                    if (micThread == null) {
                        ChatUtil.errorChatMessage("No present microphone input interface.");
                        this.deactivate();
                        return;
                    }
                    final Object connection = this.connectionField.get(micThread);
                    final AtomicLong sequenceNumber = (AtomicLong) this.sequenceNumberField.get(micThread);
                    final Object encoder = this.encoderField.get(micThread);
                    for (int i = 0; i < this.bufferSize.getValue(); i++) {
                        this.clientVoiceChatConnectionSendToServerMethod.invoke(
                                connection,
                                this.networkMessageConstructor.newInstance(
                                        this.micPacketConstructor.newInstance(
                                                this.encodeMethod.invoke(
                                                        encoder,
                                                        switch (this.voiceMode.getValue()) {
                                                            case STATIC_NOISE -> this.randomAudioSamples[ThreadLocalRandom.current().nextInt(this.randomAudioSamples.length)];
                                                            case HERTZ -> HenklerSprenklerModule.pcm(this.frequency.getValue(), HenklerSprenklerModule.NUM_SAMPLES, HenklerSprenklerModule.SAMPLE_RATE);
                                                        }
                                                ),
                                                false,
                                                sequenceNumber.getAndIncrement()
                                        )
                                )
                        );
                    }
                    this.resetStateMethod.invoke(encoder);
                    this.clientVoiceChatConnectionSendToServerMethod.invoke(
                            connection,
                            this.networkMessageConstructor.newInstance(
                                    this.micPacketConstructor.newInstance(
                                            new byte[0], false, sequenceNumber.getAndIncrement()
                                    )
                            )
                    );
                }
            }
        } catch (final IllegalAccessException | InvocationTargetException | InstantiationException exception) {
            exception.printStackTrace();
            ChatUtil.errorChatMessage("Error at update in " + this.getName() + " module.");
            this.deactivate();
        }
    }

    private enum Mode implements IName {

        VOICE, GROUP;

        private final String name;

        Mode() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }

        @Override
        public final String getName() {
            return this.name;
        }

    }

    private enum VoiceMode implements IName {

        STATIC_NOISE, HERTZ;

        private final String name;

        VoiceMode() {
            this.name = StringUtils.normalizeEnumName(this.name());
        }

        @Override
        public final String getName() {
            return this.name;
        }

    }

}
