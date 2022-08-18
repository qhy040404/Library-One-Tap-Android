package com.qhy040404.libraryonetap.recycleview.simplepage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ClickableItem {
    public @NonNull
    final String name;
    public @NonNull
    final String desc;
    public @Nullable
    final String url;

    public ClickableItem(@NonNull String name, @NonNull String desc) {
        this(name, desc, null);
    }

    public ClickableItem(
        @NonNull String name,
        @NonNull String desc,
        @Nullable String url) {
        this.name = name;
        this.desc = desc;
        this.url = url;
    }
}
