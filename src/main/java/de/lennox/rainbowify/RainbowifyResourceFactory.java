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
package de.lennox.rainbowify;

import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public class RainbowifyResourceFactory implements ResourceFactory {

    private InputStream open(Identifier id) throws IOException {
        return getClass().getResourceAsStream("/assets" + "/" + id.getNamespace() + "/" + id.getPath());
    }

    public Resource getResource(final Identifier id) {
        return new Resource() {
            @Nullable
            InputStream stream;

            public void close() throws IOException {
                if (this.stream != null) {
                    this.stream.close();
                }

            }

            public Identifier getId() {
                return id;
            }

            public InputStream getInputStream() {
                try {
                    this.stream = open(id);
                } catch (IOException var2) {
                    throw new UncheckedIOException("Could not get client resource from vanilla pack", var2);
                }
                return this.stream;
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
        };
    }

}
