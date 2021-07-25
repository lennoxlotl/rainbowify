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
package de.lennox.rainbowify.mixin.modifications;

import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.RainbowifyResourceFactory;
import de.lennox.rainbowify.bus.events.ScreenDrawEvent;
import de.lennox.rainbowify.bus.events.ScreenInitEvent;
import de.lennox.rainbowify.effect.effects.RainbowEffect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow public abstract void renderBackgroundTexture(int vOffset);

    @Shadow @Nullable protected MinecraftClient client;

    @Inject(method = "init()V", at = @At("HEAD"))
    public void init(CallbackInfo info) {
        RainbowifyMod.instance().eventBus().dispatch(new ScreenInitEvent());
    }

    /**
     * @author Lennox
     */
    @Overwrite
    public void renderBackground(MatrixStack matrices, int vOffset) {
        if (this.client.world != null) {
            RainbowifyMod.instance().eventBus().dispatch(new ScreenDrawEvent(matrices));
        } else {
            this.renderBackgroundTexture(vOffset);
        }
    }


}
