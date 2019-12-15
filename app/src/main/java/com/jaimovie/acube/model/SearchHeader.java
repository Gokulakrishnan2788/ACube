package com.jaimovie.acube.model;

import android.support.annotation.NonNull;

import com.jaimovie.acube.adapter.viewholder.Section;

import java.util.List;

public class SearchHeader implements Section<Child> , Comparable<SearchHeader> {
    public List<Child> childList;
    public String sectionText;
    int index;

    public SearchHeader(List<Child> childList, String sectionText, int index) {
        this.childList = childList;
        this.sectionText = sectionText;
        this.index = index;
    }

    @Override
    public List<Child> getChildItems() {
        return childList;
    }

    public String getSectionText() {
        return sectionText;
    }

    @Override
    public int compareTo(@NonNull SearchHeader another) {
        if (this.index > another.index) {
            return -1;
        } else {
            return 1;
        }
    }
}
