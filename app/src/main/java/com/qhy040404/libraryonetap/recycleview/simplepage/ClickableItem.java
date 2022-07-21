package com.qhy040404.libraryonetap.recycleview.simplepage;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qhy040404.libraryonetap.R;

public class ClickableItem {

    public @DrawableRes
    final int avatarResId;
    public @NonNull
    final String name;
    public @NonNull
    final String desc;
    public @Nullable
    String url;

    public ClickableItem(@NonNull String name, @NonNull String desc) {
        this(R.color.white, name, desc, null);
    }

    public ClickableItem(@DrawableRes int avatarResId, @NonNull String name, @NonNull String desc) {
        this(avatarResId, name, desc, null);
    }

    public ClickableItem(
            @DrawableRes int avatarResId,
            @NonNull String name,
            @NonNull String desc,
            @Nullable String url) {

        this.avatarResId = avatarResId;
        this.name = name;
        this.desc = desc;
        this.url = url;
    }
}