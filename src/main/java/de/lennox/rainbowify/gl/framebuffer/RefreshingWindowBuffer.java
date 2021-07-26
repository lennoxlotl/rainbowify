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
package de.lennox.rainbowify.gl.framebuffer;

import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.bus.Subscriber;
import de.lennox.rainbowify.bus.events.ScreenResolutionChangeEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.WindowFramebuffer;

public class RefreshingWindowBuffer extends WindowFramebuffer {

    private static final MinecraftClient MC = MinecraftClient.getInstance();
    public final Subscriber<ScreenResolutionChangeEvent> screenResolutionSubscriber = event -> resize(MC.getWindow().getFramebufferWidth(), MC.getWindow().getFramebufferHeight(), false);

    public RefreshingWindowBuffer(int width, int height) {
        super(width, height);
        RainbowifyMod.instance().eventBus().subscribe(this);
    }

}
