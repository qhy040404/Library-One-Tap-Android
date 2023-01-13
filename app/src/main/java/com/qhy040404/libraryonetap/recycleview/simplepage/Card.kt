package com.qhy040404.libraryonetap.recycleview.simplepage;

import androidx.annotation.NonNull;

public class Card {
    public @NonNull
    final CharSequence content;

    public final int lineSpacingExtra;

    public Card(@NonNull CharSequence content) {
        this(content, 0);
    }

    public Card(@NonNull CharSequence content, int lineSpacingExtra) {
        this.content = content;
        this.lineSpacingExtra = lineSpacingExtra;
    }
}
