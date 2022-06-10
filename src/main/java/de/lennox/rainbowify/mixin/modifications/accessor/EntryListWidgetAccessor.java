package de.lennox.rainbowify.mixin.modifications.accessor;

import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntryListWidget.class)
public interface EntryListWidgetAccessor<E extends EntryListWidget.Entry<E>> {
  @Accessor
  void setTop(int top);

  @Accessor
  void setBottom(int bottom);

  @Accessor
  E getHoveredEntry();

  @Accessor
  boolean getScrolling();
}
