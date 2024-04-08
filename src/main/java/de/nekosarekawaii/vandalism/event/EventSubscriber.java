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

package de.nekosarekawaii.vandalism.event;

import de.florianmichael.dietrichevents2.AbstractEvent;
import de.florianmichael.dietrichevents2.Priorities;
import de.nekosarekawaii.vandalism.Vandalism;
import net.minecraft.util.Pair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class EventSubscriber {

    private final List<Pair<Integer, Integer>> eventIds;

    public EventSubscriber() {
        this.eventIds = new ArrayList<>();

        final Class<?> clazz = this.getClass();
        final HashMap<Integer, Integer> priorityMap = new HashMap<>();

        if (clazz.isAnnotationPresent(EventPriority.class)) {
            final EventPriority eventPriority = clazz.getAnnotation(EventPriority.class);

            if (eventPriority.id().length != eventPriority.priority().length) {
                throw new IllegalArgumentException("EventPriority id and priority arrays must have the same length");
            }

            for (int i = 0; i < eventPriority.id().length; i++) {
                priorityMap.put(eventPriority.id()[i], eventPriority.priority()[i]);
            }
        }

        for (final Class<?> inter : clazz.getInterfaces()) {
            for (final Class<?> eventClass : inter.getClasses()) {
                if (!AbstractEvent.class.isAssignableFrom(eventClass)) {
                    continue;
                }

                try {
                    final Field field = eventClass.getField("ID");
                    final int id = field.getInt(null);
                    this.eventIds.add(new Pair<>(id, priorityMap.getOrDefault(id, Priorities.NORMAL)));
                } catch (final NoSuchFieldException e) {
                    Vandalism.getInstance().getLogger().error("Failed to get event ID for {} in {}", eventClass.getName(), clazz.getName());
                } catch (final IllegalAccessException e) {
                    Vandalism.getInstance().getLogger().error("Access error while trying to init {} in {}\n({})", eventClass.getName(), clazz.getName(), e);
                }
            }
        }
    }

    public void registerEvents() {
        for (final Pair<Integer, Integer> id : this.eventIds) {
            Vandalism.getInstance().getEventSystem().subscribe(id.getLeft(), this, id.getRight());
        }
    }

    public void unregisterEvents() {
        for (final Pair<Integer, Integer> id : this.eventIds) {
            Vandalism.getInstance().getEventSystem().unsubscribe(id.getLeft(), this);
        }
    }

}
