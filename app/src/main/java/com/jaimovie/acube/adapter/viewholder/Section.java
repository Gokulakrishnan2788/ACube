package com.jaimovie.acube.adapter.viewholder;

import java.util.List;

/**
 * Interface for implementing required methods in a section.
 */
public interface Section<C> {
    /**
     * Getter for the list of this parent's child items.
     * <p>
     * If list is empty, the parent has no children.
     *
     * @return A {@link List} of the children of this {@link Section}
     */
    List<C> getChildItems();
}