/*
 * Copyright (c) 2021-2023 Lennox
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
package de.lennox.rainbowify.config;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Custom record TypeAdapterFactory to automatically load records with gson
 *
 * <p>This code won't receive extra documentation as it is not made by rainbowify
 *
 * @author https://github.com/sceutre (https://github.com/google/gson/issues/1794)
 * @since 2.0.0
 */
public class RecordTypeAdapterFactory implements TypeAdapterFactory {

  /**
   * Creates the type adapter
   *
   * @param gson The gson instance
   * @param type The type token
   * @param <T>  The type of the adapter
   * @return The type adapter
   */
  @Override
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    @SuppressWarnings("unchecked")
    Class<T> clazz = (Class<T>) type.getRawType();
    if (!clazz.isRecord()) {
      return null;
    }
    TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

    return new TypeAdapter<T>() {
      @Override
      public void write(JsonWriter out, T value) throws IOException {
        delegate.write(out, value);
      }

      @Override
      public T read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
          reader.nextNull();
          return null;
        } else {
          var recordComponents = clazz.getRecordComponents();
          var typeMap = new HashMap<String, TypeToken<?>>();
          for (int i = 0; i < recordComponents.length; i++) {
            typeMap.put(
                recordComponents[i].getName(), TypeToken.get(recordComponents[i].getGenericType()));
          }
          var argsMap = new HashMap<String, Object>();
          reader.beginObject();
          while (reader.hasNext()) {
            String name = reader.nextName();
            argsMap.put(name, gson.getAdapter(typeMap.get(name)).read(reader));
          }
          reader.endObject();

          var argTypes = new Class<?>[recordComponents.length];
          var args = new Object[recordComponents.length];
          for (int i = 0; i < recordComponents.length; i++) {
            argTypes[i] = recordComponents[i].getType();
            args[i] = argsMap.get(recordComponents[i].getName());
          }
          Constructor<T> constructor;
          try {
            constructor = clazz.getDeclaredConstructor(argTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
          } catch (NoSuchMethodException
                   | InstantiationException
                   | SecurityException
                   | IllegalAccessException
                   | IllegalArgumentException
                   | InvocationTargetException e) {
            throw new RuntimeException(e);
          }
        }
      }
    };
  }
}
