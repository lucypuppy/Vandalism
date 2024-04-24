/*
 * This file is part of Vandalism - https://github.com/VandalismDevelopment/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, FooFieOwO and contributors
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
import de.nekosarekawaii.vandalism.Vandalism;
import de.nekosarekawaii.vandalism.base.value.impl.number.IntegerValue;
import de.nekosarekawaii.vandalism.base.value.impl.primitive.BooleanValue;
import de.nekosarekawaii.vandalism.base.value.impl.selection.MultiModeValue;
import de.nekosarekawaii.vandalism.event.cancellable.network.IncomingPacketListener;
import de.nekosarekawaii.vandalism.event.cancellable.network.OutgoingPacketListener;
import de.nekosarekawaii.vandalism.feature.module.AbstractModule;
import de.nekosarekawaii.vandalism.util.common.ArrayUtil;
import de.nekosarekawaii.vandalism.util.common.StringUtils;
import de.nekosarekawaii.vandalism.util.game.ChatUtil;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.Packet;
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

    private final MultiModeValue serverPackets, clientPackets;

    private static final MapperBase MAPPER_BASE = AsmFabricLoader.getLoader().getMappingsResolver().named();

    public PacketManagerModule() {
        super("Packet Manager", "Allows to log and cancel packets.", Category.MISC);
        final List<String> serverPackets = new ArrayList<>();
        final List<String> clientPackets = new ArrayList<>();
        for (final NetworkState networkState : NetworkState.values()) {
            final String networkStateName = StringUtils.normalizeEnumName(networkState.name());
            for (final Class<? extends Packet<?>> serverPacketClass : networkState.getPacketIdToPacketMap(NetworkSide.CLIENTBOUND).values()) {
                serverPackets.add(networkStateName + PacketManagerModule.getSimpleName(serverPacketClass));
            }
            for (final Class<? extends Packet<?>> clientPacketClass : networkState.getPacketIdToPacketMap(NetworkSide.SERVERBOUND).values()) {
                clientPackets.add(networkStateName + PacketManagerModule.getSimpleName(clientPacketClass));
            }
        }
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

    private boolean handlePacket(final boolean outgoing, final Packet<?> packet, final NetworkState networkState) {
        final String networkStateNormalized = StringUtils.normalizeEnumName(networkState.name());
        final String name = networkStateNormalized + PacketManagerModule.getSimpleName(packet.getClass());
        final boolean contains = this.clientPackets.isSelected(name) || this.serverPackets.isSelected(name);
        if (this.log.getValue() && contains) {
            final StringBuilder text = new StringBuilder();
            text.append(outgoing ? "Outgoing: " : "Incoming: ");
            text.append(networkStateNormalized);
            text.append(PacketManagerModule.dump(packet, 0, this.recursionDepthLimit.getValue()));
            if (this.mc.inGameHud != null) {
                ChatUtil.infoChatMessage(text.toString());
            } else {
                Vandalism.getInstance().getLogger().info(text.toString());
            }
        }
        return this.cancel.getValue() && contains;
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
            if (object instanceof int[] array) {
                return Arrays.toString(array);
            }
            if (object instanceof byte[] array) {
                return Arrays.toString(array);
            }
            if (object instanceof char[] array) {
                return Arrays.toString(array);
            }
            if (object instanceof short[] array) {
                return Arrays.toString(array);
            }
            if (object instanceof long[] array) {
                return Arrays.toString(array);
            }
            if (object instanceof float[] array) {
                return Arrays.toString(array);
            }
            if (object instanceof boolean[] array) {
                return Arrays.toString(array);
            }
            if (object instanceof double[] array) {
                return Arrays.toString(array);
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
                return Text.Serialization.toJsonString(text);
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
            if (object instanceof Collection<?> collection) {
                return "[".concat(collection.stream().map(index -> PacketManagerModule.dump(index, depth + 1, depthLimit)).collect(Collectors.joining(", "))).concat("]");
            }
            if (object instanceof Map<?, ?> map) {
                return "[".concat(map.entrySet().stream().map(entry -> "(".concat(PacketManagerModule.dump(entry.getKey(), depth + 1, depthLimit)).concat(", ").concat(PacketManagerModule.dump(entry.getValue(), depth + 1, depthLimit)).concat(")")).collect(Collectors.joining(", "))).concat("]");
            }
            if (object instanceof Multimap<?, ?> map) {
                return "[".concat(map.entries().stream().map(entry -> "(".concat(PacketManagerModule.dump(entry.getKey(), depth + 1, depthLimit)).concat(", ").concat(PacketManagerModule.dump(entry.getValue(), depth + 1, depthLimit)).concat(")")).collect(Collectors.joining(", "))).concat("]");
            }
            if (object instanceof Pair<?, ?> pair) {
                return "(".concat(PacketManagerModule.dump(pair.getLeft(), depth + 1, depthLimit)).concat(", ").concat(PacketManagerModule.dump(pair.getRight(), depth + 1, depthLimit)).concat(")");
            }
            if (object instanceof Map.Entry<?, ?> entry) {
                return "(".concat(PacketManagerModule.dump(entry.getKey(), depth + 1, depthLimit)).concat(", ").concat(PacketManagerModule.dump(entry.getValue(), depth + 1, depthLimit)).concat(")");
            }
            if (object instanceof Vec3i vec) {
                return "(".concat(String.valueOf(vec.getX())).concat("|").concat(String.valueOf(vec.getY())).concat(String.valueOf(vec.getZ())).concat(")");
            }
            if (object instanceof Identifier identifier) {
                return identifier.toString();
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
        return PacketManagerModule.MAPPER_BASE == null ? clazz.getSimpleName() : ArrayUtil.last(PacketManagerModule.MAPPER_BASE.getClassName(clazz.getName().replace(".", "/")).split("/"));
    }

    @Override
    public void onIncomingPacket(final IncomingPacketEvent event) {
        if (this.handlePacket(false, event.packet, event.networkState)) {
            event.cancel();
        }
    }

    @Override
    public void onOutgoingPacket(final OutgoingPacketEvent event) {
        if (this.handlePacket(true, event.packet, event.networkState)) {
            event.cancel();
        }
    }

}
