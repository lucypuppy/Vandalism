/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO, Recyz and contributors
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

import com.google.common.collect.Multimap;
import com.mojang.authlib.properties.PropertyMap;
import de.florianmichael.asmfabricloader.AsmFabricLoader;
import de.florianmichael.asmfabricloader.api.MapperBase;
import de.florianmichael.dietrichevents2.Priorities;
import de.florianmichael.rclasses.common.array.ArrayUtils;
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.MultiModeValue;
import de.nekosarekawaii.vandalism.event.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.ChatUtil;
import net.lenni0451.reflect.stream.RStream;
import net.lenni0451.reflect.stream.field.FieldWrapper;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.*;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3i;

import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class PacketManagerModule extends AbstractModule implements IncomingPacketListener, OutgoingPacketListener {

    private final BooleanValue log = new BooleanValue(
            this,
            "Log",
            "Log packets.",
            true
    );

    private final BooleanValue logUnhandledPackets = new BooleanValue(
            this,
            "Log Unhandled Packets",
            "Log packets that are not handled by this module.",
            false
    ).visibleCondition(this.log::getValue);

    private final BooleanValue cancel = new BooleanValue(
            this,
            "Cancel",
            "Cancel packets.",
            false
    );
    private final IntegerValue recursionDepthLimit = new IntegerValue(
            this,
            "Recursion depth limit",
            "Limit of recursion depth in packet data.",
            4,
            0,
            127
    );

    private static final MapperBase MAPPER_BASE = AsmFabricLoader.getLoader().getMappingsResolver().named();

    private final MultiModeValue serverPackets, clientPackets;

    private final HashMap<Identifier, String> packetNames = new HashMap<>();

    private void addPacketName(final String networkPhase, final FieldWrapper fieldWrapper, final List<String> serverPackets, final List<String> clientPackets) {
        if (fieldWrapper.get() instanceof final PacketType<?> packetType) {
            final Identifier id = packetType.id();
            final String name = id.getNamespace() + ":" + networkPhase + "_" + id.getPath();
            this.packetNames.put(id, name);
            if (packetType.side() == NetworkSide.CLIENTBOUND) {
                serverPackets.add(name);
            } else {
                clientPackets.add(name);
            }
        }
    }

    public PacketManagerModule() {
        super("Packet Manager", "Allows to log and cancel packets.", Category.MISC);
        final List<String> serverPackets = new ArrayList<>();
        final List<String> clientPackets = new ArrayList<>();
        RStream.of(CommonPackets.class).fields().forEach(field -> this.addPacketName("common", field, serverPackets, clientPackets));
        RStream.of(ConfigPackets.class).fields().forEach(field -> this.addPacketName("config", field, serverPackets, clientPackets));
        RStream.of(CookiePackets.class).fields().forEach(field -> this.addPacketName("cookie", field, serverPackets, clientPackets));
        RStream.of(HandshakePackets.class).fields().forEach(field -> this.addPacketName("handshake", field, serverPackets, clientPackets));
        RStream.of(LoginPackets.class).fields().forEach(field -> this.addPacketName("login", field, serverPackets, clientPackets));
        RStream.of(PingPackets.class).fields().forEach(field -> this.addPacketName("ping", field, serverPackets, clientPackets));
        RStream.of(PlayPackets.class).fields().forEach(field -> this.addPacketName("play", field, serverPackets, clientPackets));
        RStream.of(StatusPackets.class).fields().forEach(field -> this.addPacketName("status", field, serverPackets, clientPackets));
        this.serverPackets = new MultiModeValue(
                this,
                "Server Packets",
                "The incoming packets.",
                serverPackets.toArray(new String[0])
        );
        this.clientPackets = new MultiModeValue(
                this,
                "Client Packets",
                "The outgoing packets.",
                clientPackets.toArray(new String[0])
        );
    }

    @Override
    public void onActivate() {
        Vandalism.getInstance().getEventSystem().subscribe(IncomingPacketEvent.ID, this, Priorities.HIGH);
        Vandalism.getInstance().getEventSystem().subscribe(OutgoingPacketEvent.ID, this, Priorities.HIGH);
    }

    @Override
    public void onDeactivate() {
        Vandalism.getInstance().getEventSystem().unsubscribe(IncomingPacketEvent.ID, this);
        Vandalism.getInstance().getEventSystem().unsubscribe(OutgoingPacketEvent.ID, this);
    }

    private void log(final StringBuilder builder, final boolean error) {
        final String text = builder.toString();
        if (this.mc.inGameHud != null) {
            if (error) ChatUtil.errorChatMessage(text);
            else ChatUtil.infoChatMessage(text);
        } else {
            if (error) Vandalism.getInstance().getLogger().error(text);
            else Vandalism.getInstance().getLogger().info(text);
        }
    }

    private boolean handlePacket(final boolean outgoing, final Packet<?> packet) {
        final Identifier id = packet.getPacketId().id();
        final boolean packetNamesContains = this.packetNames.containsKey(id);
        final StringBuilder text = new StringBuilder();
        text.append(outgoing ? "Outgoing: " : "Incoming: ");
        final String name = packetNamesContains ? this.packetNames.get(id) : "Unhandled Packet -> " + getSimpleName(packet.getClass());
        final boolean isSelected = outgoing && this.clientPackets.isSelected(name) || !outgoing && this.serverPackets.isSelected(name);
        if (this.log.getValue()) {
            if (packetNamesContains) {
                if (isSelected) {
                    text.append(id).append(" | ").append(PacketManagerModule.dump(packet, 0, this.recursionDepthLimit.getValue()));
                    this.log(text, false);
                }
            } else if (this.logUnhandledPackets.getValue()) {
                text.append(id).append(" | ").append(name);
                this.log(text, true);
            }
        }
        return this.cancel.getValue() && packetNamesContains && isSelected;
    }

    private static String dump(Object object, final int depth, final int depthLimit) {
        if (object == null)
            return "null";

        if (depth > depthLimit) { /* max stack depth */
            return object.toString();
        }

        { /* unwrapping */
            if (object instanceof Optional<?> optional) {
                if (optional.isEmpty())
                    return "<empty>";

                object = optional.get();
            }
            if (object instanceof PropertyMap map) {
                object = map.asMap();
            }
        }

        { /* primitive arrays */
            switch (object) {
                case int[] array -> {
                    return Arrays.toString(array);
                }
                case byte[] array -> {
                    return Arrays.toString(array);
                }
                case char[] array -> {
                    return Arrays.toString(array);
                }
                case short[] array -> {
                    return Arrays.toString(array);
                }
                case long[] array -> {
                    return Arrays.toString(array);
                }
                case float[] array -> {
                    return Arrays.toString(array);
                }
                case boolean[] array -> {
                    return Arrays.toString(array);
                }
                case double[] array -> {
                    return Arrays.toString(array);
                }
                default -> {
                }
            }
        }

        final Class<?> clazz = object.getClass();

        /* Arrays */
        if (clazz.isArray()) {
            final Object[] array = (Object[]) object;
            return "[".concat(Arrays.stream(array).map(index -> PacketManagerModule.dump(index, depth + 1, depthLimit)).collect(Collectors.joining(", "))).concat("]");
        }

        { /* Formattable */
            if (object instanceof Text text) {
                return Text.Serialization.toJsonString(text, DynamicRegistryManager.EMPTY);
            }
            if (object instanceof NbtElement element) {
                return NbtHelper.toFormattedString(element);
            }
        }

        /* Enums */
        if (clazz.isEnum() || object instanceof Enum<?>) {
            return ((Enum<?>) object).name();
        }

        { /* ADTs */
            switch (object) {
                case Collection<?> collection -> {
                    return "[".concat(collection.stream().map(index -> PacketManagerModule.dump(index, depth + 1, depthLimit)).collect(Collectors.joining(", "))).concat("]");
                }
                case Map<?, ?> map -> {
                    return "[".concat(map.entrySet().stream().map(entry -> "(".concat(PacketManagerModule.dump(entry.getKey(), depth + 1, depthLimit)).concat(", ").concat(PacketManagerModule.dump(entry.getValue(), depth + 1, depthLimit)).concat(")")).collect(Collectors.joining(", "))).concat("]");
                }
                case Multimap<?, ?> map -> {
                    return "[".concat(map.entries().stream().map(entry -> "(".concat(PacketManagerModule.dump(entry.getKey(), depth + 1, depthLimit)).concat(", ").concat(PacketManagerModule.dump(entry.getValue(), depth + 1, depthLimit)).concat(")")).collect(Collectors.joining(", "))).concat("]");
                }
                case Pair<?, ?> pair -> {
                    return "(".concat(PacketManagerModule.dump(pair.getLeft(), depth + 1, depthLimit)).concat(", ").concat(PacketManagerModule.dump(pair.getRight(), depth + 1, depthLimit)).concat(")");
                }
                case Map.Entry<?, ?> entry -> {
                    return "(".concat(PacketManagerModule.dump(entry.getKey(), depth + 1, depthLimit)).concat(", ").concat(PacketManagerModule.dump(entry.getValue(), depth + 1, depthLimit)).concat(")");
                }
                case Vec3i vec -> {
                    return "(".concat(String.valueOf(vec.getX())).concat("|").concat(String.valueOf(vec.getY())).concat(String.valueOf(vec.getZ())).concat(")");
                }
                case Identifier identifier -> {
                    return identifier.toString();
                }
                default -> {
                }
            }
        }

        /* Java data types */
        if (object instanceof UUID || object instanceof Number || object instanceof Character || object instanceof String || object instanceof Boolean) {
            return object.toString();
        }

        /* Other java.* classes */
        if (clazz.getName().startsWith("java."))
            return object.toString();

        final Object finalObject = object;
        return PacketManagerModule.getSimpleName(clazz).concat("{").concat(Arrays.stream(clazz.getDeclaredFields()).map(field -> {
            try {
                field.setAccessible(true);
                if (Modifier.isStatic(field.getModifiers()) || (field.getModifiers() & 0x00001000 /* SYNTHETIC */) != 0) {
                    return null;
                }
                final Object value = field.get(finalObject);
                return String.format("%s=%s", PacketManagerModule.MAPPER_BASE == null ? field.getName() : PacketManagerModule.MAPPER_BASE.getFieldName(field.getDeclaringClass(), field.getName()), field.getType().isPrimitive() ? value : PacketManagerModule.dump(value, depth + 1, depthLimit));
            } catch (IllegalAccessException | InaccessibleObjectException ignored) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.joining(";"))).concat("}");
    }

    private static String getSimpleName(final Class<?> clazz) {
        return PacketManagerModule.MAPPER_BASE == null ? clazz.getSimpleName() : ArrayUtils.last(PacketManagerModule.MAPPER_BASE.getClassName(clazz.getName().replace(".", "/")).split("/"));
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        if (this.handlePacket(false, event.packet)) {
            event.cancel();
        }
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        if (this.handlePacket(true, event.packet)) {
            event.cancel();
        }
    }

}
