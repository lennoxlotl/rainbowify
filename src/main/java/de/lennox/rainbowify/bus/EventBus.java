/*
 * Copyright (c) 2021 Lennox
 *
 * This file is part of rainbowify.
 *
 * rainbowify is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rainbowify is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with rainbowify.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.lennox.rainbowify.bus;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class EventBus<T> {

    private final Map<Type, List<SubscriberContainer<T>>> containerMap = new HashMap<>();
    private final Map<Type, List<Subscriber<T>>> subscriberMap = new HashMap<>();

    public void subscribe(Object obj) {
        Arrays.stream(obj.getClass().getDeclaredFields()).filter(field -> field.getType() == Subscriber.class)
            .forEach(field -> {
                try {
                    var accessible = field.canAccess(obj);
                    field.setAccessible(true);
                    // Fetch the event being subscribed to
                    var eventType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    var subscriber = (Subscriber<T>) field.get(obj);
                    field.setAccessible(accessible);
                    // If there already is a container list for an event type, add it in that list otherwise create a new one
                    if (containerMap.containsKey(eventType)) {
                        List<SubscriberContainer<T>> repositories = containerMap.get(eventType);
                        repositories.add(new SubscriberContainer<>(obj, subscriber));
                    } else {
                        containerMap.put(eventType, List.of(new SubscriberContainer<>(obj, subscriber)));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        refreshSubscriptions();
    }

    public void unsubscribe(Object obj) {
        containerMap.values().forEach(subscriberContainers -> subscriberContainers.removeIf(tSubscriberContainer -> tSubscriberContainer.obj == obj));
        refreshSubscriptions();
    }

    private void refreshSubscriptions() {
        // Update the subscription cache
        containerMap.keySet().forEach(type -> {
            List<Subscriber<T>> subscribers = new ArrayList<>();
            List<SubscriberContainer<T>> containers = containerMap.get(type);
            containers.forEach(container -> subscribers.add(container.subscriber));
            subscriberMap.put(type, subscribers);
        });
    }

    public void dispatch(T event) {
        // Search for subscribers that subscribed to the dispatched event, then execute an event call
        List<Subscriber<T>> subscribers = subscriberMap.get(event.getClass());
        if (subscribers != null) {
            subscribers.forEach(subscriber -> subscriber.call(event));
        }
    }

    record SubscriberContainer<T>(Object obj, Subscriber<T> subscriber) {
    }
}
