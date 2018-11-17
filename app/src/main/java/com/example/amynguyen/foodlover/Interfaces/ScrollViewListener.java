package com.example.amynguyen.foodlover.Interfaces;

import com.example.amynguyen.foodlover.CustomView.ScrollViewExt;

public interface ScrollViewListener {
    void onScrollChanged(ScrollViewExt scrollView,
                         int x, int y, int oldx, int oldy);
}
