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
package de.lennox.rainbowify.config.screen.widget;

import de.lennox.rainbowify.config.screen.RenderedCategory;
import de.lennox.rainbowify.mixin.modifications.accessor.EntryListWidgetAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Custom ElementListWidget to render a scrollable category collection
 *
 * @author Lennox
 * @since 2.0.0
 */
public class CategoryListWidget extends ElementListWidget<CategoryListEntry> {
    private int height;

    public CategoryListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
    }

    /**
     * Adds a given list of RenderedCategories to the list of entries
     *
     * @param categories The categories
     * @since 2.0.0
     */
    public void addAll(List<RenderedCategory> categories) {
        for (RenderedCategory entry : categories) {
            // Add the rendered category as a CategoryListEntry
            addEntry(new CategoryListEntry(entry));
        }
        // Reset the height
        this.height = 0;
        // Calculate the new height
        for (CategoryListEntry child : children()) {
            height += child.renderedCategory().height();
        }
    }

    @Override
    protected void renderList(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        //noi nspection rawtypes
        EntryListWidgetAccessor accessor = (EntryListWidgetAccessor) this;
        // Draw the list
        int entryCount = this.getEntryCount();
        for (int i = 0; i < entryCount; ++i) {
            CategoryListEntry entry = this.getEntry(i);
            int entryHeight = entry.renderedCategory().height();
            int rowTop = this.getRowTop(i);
            int rowBottom = this.getRowTop(i) + entryHeight;
            // Check if this entry needs to be rendered or if it's out of bounds
            if (rowBottom >= this.top && rowTop <= this.bottom) {
                int itemHeight = entryHeight - 4;
                int rowWidth = this.getRowWidth();
                int rowLeft = this.getRowLeft();
                // Render the entry
                entry.render(
                        matrices,
                        i,
                        rowTop,
                        rowLeft,
                        rowWidth,
                        itemHeight,
                        mouseX,
                        mouseY,
                        Objects.equals(accessor.getHoveredEntry(), entry),
                        delta);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        //noinspection rawtypes
        EntryListWidgetAccessor accessor = (EntryListWidgetAccessor) this;
        // Update the scrolling state
        this.updateScrollingState(mouseX, mouseY, button);
        // Check if the mouse is in bounds
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            CategoryListEntry entry = this.getModifiedEntryAtPosition(mouseX, mouseY);
            // Check if there is any entry at this position
            if (entry != null) {
                // Click on the entry
                if (entry.mouseClicked(mouseX, mouseY, button)) {
                    this.setFocused(entry);
                    this.setDragging(true);
                    return true;
                }
            } else if (button == 0) {
                // Click the header
                this.clickedHeader(
                        (int) (mouseX - (double) (this.left + this.width / 2 - this.getRowWidth() / 2)),
                        (int) (mouseY - (double) this.top) + (int) this.getScrollAmount() - 4);
                return true;
            }
            // Return if the action is scrolling
            return accessor.getScrolling();
        }
    }

    @Nullable
    protected CategoryListEntry getModifiedEntryAtPosition(double x, double y) {
        int halfWidth = this.getRowWidth() / 2;
        int middle = this.left + this.width / 2;
        int minX = middle - halfWidth;
        int maxX = middle + halfWidth;
        // Calculate the actual mouse position
        int actualMousePosY =
                MathHelper.floor(y - (double) this.top)
                        - this.headerHeight
                        + (int) this.getScrollAmount()
                        - 4;
        // Calculate which child is in that y position
        int index = -1;
        int offset = 0;
        for (int i = 0; i < children().size(); i++) {
            CategoryListEntry child = children().get(i);
            int height = child.renderedCategory().height();
            // Check if the interaction was in bounds of the child
            if (actualMousePosY > offset && actualMousePosY < offset + height) {
                index = i;
                break;
            }
            // Append the children's height
            offset += height;
        }
        // Return the results
        return x < (double) this.getScrollbarPositionX()
                && index >= 0
                && actualMousePosY >= 0
                ? this.children().get(index)
                : null;
    }

    @Override
    protected int getRowTop(int index) {
        int offset = 16;
        // Calculate the offset
        for (int i = 0; i < index; i++) {
            offset += children().get(i).renderedCategory().height();
        }
        // Combine position, scrolling and offset information
        return this.top + 4 - (int) this.getScrollAmount() + offset;
    }

    @Override
    protected int getScrollbarPositionX() {
        // Adjust the scrollbar position to our needs and limit it
        return Math.min(this.width / 2 + 174, this.width - 6);
    }

    @Override
    protected int getMaxPosition() {
        // Append our custom height field to the max position, this way we won't have to deal with
        // Minecraft enterprise code
        return this.headerHeight + height;
    }
}
