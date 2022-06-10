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
package de.lennox.rainbowify.event;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The eventbus used to publish to events and subscribe to them
 *
 * @param <T> The event base class type
 * @since 1.0.0
 * @author Lennox
 */
public class EventBus<T> {
  private final Map<Type, Set<Subscription<T>>> subscriptions = new HashMap<>();

  /**
   * Fetches and creates all subscriptions in an object / instance of a class
   *
   * @param obj The instance of the class
   * @since 1.0.0
   */
  public void createSubscription(Object obj) {
    Class<?> type = obj.getClass();
    for (Field field : type.getDeclaredFields()) {
      // Check if the field is a subscription
      if (field.getType() == Subscription.class) {
        // Get the subscription interface
        Subscription<T> fieldSubscription = subscriptionOf(field, obj);
        // Get the type arguments (event type which is going to be subscribed to)
        Type subscriptionType =
            ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        if (fieldSubscription == null) {
          System.err.println(
              "ERROR: Could not create a subscription for the object "
                  + obj.getClass().getSimpleName()
                  + " as a subscription field didn't have proper type arguments.");
          return;
        }
        // Insert the subscription
        put(subscriptionType, fieldSubscription);
      }
    }
  }

  /**
   * Publishes an event to all types of that event
   *
   * @param event The event to be published
   * @since 1.0.0
   */
  public void publish(T event) {
    Type eventType = event.getClass();
    Set<Subscription<T>> subscriptionOfEvent = subscriptions.get(eventType);
    if (subscriptionOfEvent == null) return;
    // Publish to all subscribers
    for (Subscription<T> subscription : subscriptionOfEvent) {
      subscription.call(event);
    }
  }

  /**
   * Returns the given field as a subscription if possible
   *
   * @param field The field
   * @param obj The object / instance the field is in
   * @return The subscription
   * @since 1.0.0
   */
  private Subscription<T> subscriptionOf(Field field, Object obj) {
    boolean accessible = field.canAccess(obj);
    // Make the field accessible
    if (!accessible) {
      field.setAccessible(true);
    }
    // Get the subscription field
    Subscription<T> fieldSubscription = null;
    try {
      //noinspection unchecked
      fieldSubscription = (Subscription<T>) field.get(obj);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    // Revert the accessible state
    field.setAccessible(accessible);
    return fieldSubscription;
  }

  /**
   * Handles adding of a subscription by automatically handling HashSet creation
   *
   * @param type The event type
   * @param subscription The subscription
   * @since 1.0.0
   */
  private void put(Type type, Subscription<T> subscription) {
    if (subscriptions.containsKey(type)) {
      // Insert the subscription
      subscriptions.get(type).add(subscription);
    } else {
      // Create a new subscription map
      HashSet<Subscription<T>> subscriptionSet = new HashSet<>();
      subscriptionSet.add(subscription);
      subscriptions.put(type, subscriptionSet);
    }
  }
}
