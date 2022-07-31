package com.qhy040404.libraryonetap.recycleview.simplepage;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

public class Card {
    public @NonNull
    final CharSequence content;

    public final int lineSpacingExtra;

    @SuppressLint("NewApi")
    public Card(@NonNull CharSequence content) {
        this(content, 0);
    }

    public Card(@NonNull CharSequence content, int lineSpacingExtra) {
        this.content = content;
        this.lineSpacingExtra = lineSpacingExtra;
    }
}