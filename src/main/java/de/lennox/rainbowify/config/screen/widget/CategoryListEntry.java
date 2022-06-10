package de.lennox.rainbowify.config.screen.widget;

import de.lennox.rainbowify.config.screen.RenderedCategory;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom ElementListWidget Entry to render our custom option categories with ease
 *
 * @since 2.0.0
 * @author Lennox
 */
public class CategoryListEntry extends ElementListWidget.Entry<CategoryListEntry> {
  private final RenderedCategory renderedCategory;

  public CategoryListEntry(RenderedCategory renderedCategory) {
    this.renderedCategory = renderedCategory;
  }

  @Override
  public List<? extends Selectable> selectableChildren() {
    return collectClickableWidgets();
  }

  @Override
  public List<? extends Element> children() {
    return collectClickableWidgets();
  }

  /**
   * Collects all clickable widgets from the category renderer
   *
   * @return The clickable widgets
   * @since 2.0.0
   */
  @SuppressWarnings("rawtypes")
  private List<ClickableWidget> collectClickableWidgets() {
    List<SimpleOption> options = renderedCategory.options();
    List<ClickableWidget> selectables = new ArrayList<>();
    // Get all buttons / clickable widgets for the options
    for (SimpleOption option : options) {
      selectables.add(renderedCategory.listWidget().getButtonFor(option));
    }
    return selectables;
  }

  @Override
  public void render(
      MatrixStack matrices,
      int index,
      int y,
      int x,
      int entryWidth,
      int entryHeight,
      int mouseX,
      int mouseY,
      boolean hovered,
      float tickDelta) {
    // Render the category
    renderedCategory.render(matrices, y, mouseX, mouseY, tickDelta);
  }

  public RenderedCategory renderedCategory() {
    return renderedCategory;
  }

  /**
   * Creates a new category list entry
   *
   * @param category The category renderer
   * @return The category list entry
   */
  public static CategoryListEntry of(RenderedCategory category) {
    return new CategoryListEntry(category);
  }
}
