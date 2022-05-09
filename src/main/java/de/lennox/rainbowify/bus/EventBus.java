/*
 * Copyright (c) 2021-2022 Lennox
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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EventBus<T> {
  private final Map<Type, Set<Subscription<T>>> subscriptions = new HashMap<>();

  public void createSubscription(Object obj) {
    Class<?> type = obj.getClass();
    for (Field field : type.getDeclaredFields()) {
      if (field.getType() == Subscription.class) {
        Subscription<T> fieldSubscription = subscriptionOf(field, obj);
        Type subscriptionType =
            ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        if (fieldSubscription == null) {
          System.err.println(
              "ERROR: Could not create a subscription for the object "
                  + obj.getClass().getSimpleName()
                  + " as a subscription field didn't have proper type arguments.");
          return;
        }
        put(subscriptionType, fieldSubscription);
      }
    }
  }

  public void removeSubscription(Object obj) {
    Class<?> type = obj.getClass();
    for (Field field : type.getDeclaredFields()) {
      if (field.getType() == Subscription.class) {
        Subscription<T> fieldSubscription = subscriptionOf(field, obj);
        Type subscriptionType =
            ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        if (fieldSubscription == null) {
          System.err.println(
              "ERROR: Could not remove a subscription for the object "
                  + obj.getClass().getSimpleName()
                  + " as a subscription field didn't have proper type arguments");
          return;
        }
        if (subscriptions.containsKey(subscriptionType)) {
          subscriptions.get(subscriptionType).remove(fieldSubscription);
        }
      }
    }
  }

  public void publish(T event) {
    Type eventType = event.getClass();
    Set<Subscription<T>> subscriptionOfEvent = subscriptions.get(eventType);
    if (subscriptionOfEvent == null) return;
    for (Subscription<T> subscription : subscriptionOfEvent) {
      subscription.call(event);
    }
  }

  private Subscription<T> subscriptionOf(Field field, Object obj) {
    boolean accessible = field.canAccess(obj);
    if (!accessible) {
      field.setAccessible(true);
    }
    Subscription<T> fieldSubscription = null;
    try {
      //noinspection unchecked
      fieldSubscription = (Subscription<T>) field.get(obj);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    field.setAccessible(accessible);
    return fieldSubscription;
  }

  private void put(Type type, Subscription<T> subscription) {
    if (subscriptions.containsKey(type)) {
      subscriptions.get(type).add(subscription);
    } else {
      HashSet<Subscription<T>> subscriptionSet = new HashSet<>();
      subscriptionSet.add(subscription);
      subscriptions.put(type, subscriptionSet);
    }
  }
}
