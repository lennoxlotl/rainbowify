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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventBus<T> {

    private final Map<Type, List<SubscriptionContainer<T>>> containerMap = new HashMap<>();
    private final Map<Type, List<Subscription<T>>> subscriptionMap = new HashMap<>();

    public void subscribe(Object obj) {
        Arrays.stream(obj.getClass().getDeclaredFields()).filter(field -> field.getType() == Subscription.class)
            .forEach(field -> {
                try {
                    var accessible = field.canAccess(obj);
                    field.setAccessible(true);
                    var eventType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    //noinspection unchecked
                    var subscription = (Subscription<T>) field.get(obj);
                    field.setAccessible(accessible);
                    if (containerMap.containsKey(eventType)) {
                        List<SubscriptionContainer<T>> repositories = containerMap.get(eventType);
                        repositories.add(new SubscriptionContainer<>(obj, subscription));
                    } else {
                        containerMap.put(eventType, List.of(new SubscriptionContainer<>(obj, subscription)));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        refresh();
    }

    private void refresh() {
        containerMap.forEach((type, subscriberContainers) -> subscriptionMap.put(type, subscriberContainers.stream().map(SubscriptionContainer::subscriber).collect(Collectors.toList())));
    }

    public void dispatch(T event) {
        if (!subscriptionMap.containsKey(event.getClass())) return;
        List<Subscription<T>> subscriptions = subscriptionMap.get(event.getClass());
        subscriptions.forEach(tSubscription -> tSubscription.call(event));
    }

    record SubscriptionContainer<T>(Object obj, Subscription<T> subscriber) {
    }
}
