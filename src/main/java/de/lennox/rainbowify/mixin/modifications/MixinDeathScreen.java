package de.lennox.rainbowify.mixin.modifications;

import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.bus.events.ScreenBackgroundDrawEvent;
import de.lennox.rainbowify.config.Config;
import de.lennox.rainbowify.gl.GLUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.Objects;

import static net.minecraft.client.gui.DrawableHelper.drawCenteredText;

@Mixin(DeathScreen.class)
public abstract class MixinDeathScreen extends MixinScreen {

    @Shadow
    @Nullable
    protected abstract Style getTextComponentUnderMouse(int mouseX);

    @Shadow
    private Text scoreText;

    @Shadow
    @Final
    private Text message;

    /**
     * @author Lennox
     * @reason Remove the gradient in the background of the death screen (bad solution to fix this, need to find something better)
     */
    @Overwrite
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (Config.ENABLED.value) {
            if (MinecraftClient.getInstance().world != null) {
                RainbowifyMod.instance().eventBus().dispatch(new ScreenBackgroundDrawEvent(matrices));
            }
        } else {
            GLUtil.fillGradient(matrices, 0, 0, this.width, this.height, 1615855616, -1602211792);
        }

        matrices.push();
        matrices.scale(2.0F, 2.0F, 2.0F);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2 / 2, 30, 16777215);
        matrices.pop();
        if (this.message != null) {
            drawCenteredText(matrices, this.textRenderer, this.message, this.width / 2, 85, 16777215);
        }

        drawCenteredText(matrices, this.textRenderer, this.scoreText, this.width / 2, 100, 16777215);
        if (this.message != null && mouseY > 85) {
            Objects.requireNonNull(this.textRenderer);
            if (mouseY < 85 + 9) {
                Style style = this.getTextComponentUnderMouse(mouseX);
                this.renderTextHoverEffect(matrices, style, mouseX, mouseY);
            }
        }

        for (Drawable drawable : screenDrawables()) {
            drawable.render(matrices, mouseX, mouseY, delta);
        }
    }

}
