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
package de.lennox.rainbowify;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * The rainbowify resource factory used to get files out of the mod file
 *
 * @since 1.0.0
 * @author Lennox
 */
public class RainbowifyResourceFactory implements ResourceFactory {
  public Optional<Resource> getResource(Identifier id) {
    //noinspection unused
    return Optional.of(
        new Resource(
            "",
            () ->
                getClass()
                    .getResourceAsStream(
                        "/assets" + "/" + id.getNamespace() + "/" + id.getPath())) {
          @Nullable private InputStream stream;

          public void close() throws IOException {
            if (stream != null) {
              stream.close();
            }
          }

          public Identifier getId() {
            return id;
          }

          public InputStream getInputStream() {
            stream =
                getClass()
                    .getResourceAsStream("/assets" + "/" + id.getNamespace() + "/" + id.getPath());
            return stream;
          }

          public boolean hasMetadata() {
            return false;
          }

          @Nullable
          public <T> T getMetadata(ResourceMetadataReader<T> metaReader) {
            return null;
          }

          public String getResourcePackName() {
            return id.toString();
          }
        });
  }
}
