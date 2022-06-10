package de.lennox.rainbowify.config.screen;

import de.lennox.rainbowify.mixin.modifications.accessor.EntryListWidgetAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.List;

/**
 * Renders a generated category
 *
 * @since 2.0.0
 * @author Lennox
 */
@SuppressWarnings("rawtypes")
public class RenderedCategory {
  private final Text title;

  private final List<SimpleOption> options;

  private final ButtonListWidget listWidget;
  private final int height;

  public RenderedCategory(
      Text title, List<SimpleOption> options, ButtonListWidget listWidget, int height) {
    this.title = title;
    this.options = options;
    this.listWidget = listWidget;
    this.height = height;
  }

  /**
   * Renders the category
   *
   * @param matrices The rendering matrix
   * @param y The y-position of the category
   * @param mouseX The mouse x
   * @param mouseY The mouse y
   * @param delta The render tick delta
   * @since 2.0.0
   */
  public void render(MatrixStack matrices, int y, int mouseX, int mouseY, float delta) {
    // Render the title of the category
    MinecraftClient client = MinecraftClient.getInstance();
    float width = client.getWindow().getScaledWidth();
    client.textRenderer.drawWithShadow(
        matrices, title, width / 2f - client.textRenderer.getWidth(title) / 2f, y - 13, -1);
    // Render the list widget
    //noinspection rawtypes
    EntryListWidgetAccessor accessor = (EntryListWidgetAccessor) listWidget;
    accessor.setTop(y);
    accessor.setBottom(y + height - 20);
    listWidget.render(matrices, mouseX, mouseY, delta);
  }

  public ButtonListWidget listWidget() {
    return listWidget;
  }

  public List<SimpleOption> options() {
    return options;
  }

  public int height() {
    return height;
  }

  /**
   * Creates a new rendered category used for creating a ConfigScreen
   *
   * @param title The title
   * @param options The options
   * @param listWidget The option list widget
   * @param height The height
   * @return The created render category
   * @since 2.0.0
   */
  public static RenderedCategory of(
      Text title, List<SimpleOption> options, ButtonListWidget listWidget, int height) {
    return new RenderedCategory(title, options, listWidget, height);
  }
}
