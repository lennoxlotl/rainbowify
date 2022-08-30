package de.lennox.rainbowify.config.screen.widget;

import de.lennox.rainbowify.mixin.modifications.accessor.EntryListWidgetAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.util.math.MatrixStack;

public class CategoryButtonListWidget extends ButtonListWidget {

  public CategoryButtonListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
    super(minecraftClient, i, j, k, l, m);
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    //noinspection unchecked
    ((EntryListWidgetAccessor) this)
        .setHoveredEntry(this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null);

    this.renderList(matrices, mouseX, mouseY, delta);
    this.renderDecorations(matrices, mouseX, mouseY);
  }
}
