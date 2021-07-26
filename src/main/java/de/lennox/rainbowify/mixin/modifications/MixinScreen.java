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
import de.lennox.rainbowify.bus.events.ScreenBackgroundDrawEvent;
import de.lennox.rainbowify.bus.events.ScreenInitEvent;
import de.lennox.rainbowify.config.Config;
import de.lennox.rainbowify.gl.GLUtil;
import de.lennox.rainbowify.mixin.interfaces.RainbowifyScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Screen.class)
public abstract class MixinScreen implements RainbowifyScreen {

    @Shadow
    public int width;
    @Shadow
    public int height;
    @Shadow
    @Nullable
    protected MinecraftClient client;

    @Shadow
    public abstract void renderBackgroundTexture(int vOffset);

    @Shadow @Final protected Text title;

    @Shadow protected TextRenderer textRenderer;

    @Shadow protected abstract void renderTextHoverEffect(MatrixStack matrices, @Nullable Style style, int x, int y);

    @Shadow @Final
    private List<Drawable> drawables;

    @Inject(method = "init()V", at = @At("HEAD"))
    public void init(CallbackInfo info) {
        RainbowifyMod.instance().eventBus().dispatch(new ScreenInitEvent());
    }

    /**
     * @author Lennox
     * @reason Draw our custom effects instead of the minecraft gradient
     * <p>
     * TODO: Add an option to toggle off the rainbow and blur effects
     */
    @Overwrite
    public void renderBackground(MatrixStack matrices, int vOffset) {
        if (this.client.world != null) {
            if (Config.ENABLED.value) {
                RainbowifyMod.instance().eventBus().dispatch(new ScreenBackgroundDrawEvent(matrices));
            } else {
                GLUtil.fillGradient(matrices, 0, 0, this.width, this.height, -1072689136, -804253680);
            }
        } else {
            this.renderBackgroundTexture(vOffset);
        }
    }

    @Override
    public List<Drawable> screenDrawables() {
        return drawables;
    }
}
