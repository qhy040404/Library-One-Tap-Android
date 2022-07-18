package com.qhy040404.libraryonetap.recycleview.simplepage;

import android.widget.ImageView;

import androidx.annotation.NonNull;

public interface ImageLoader {

    void load(@NonNull ImageView imageView, @NonNull String url);
}