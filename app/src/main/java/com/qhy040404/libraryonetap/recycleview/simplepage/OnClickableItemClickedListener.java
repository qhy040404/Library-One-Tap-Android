package com.qhy040404.libraryonetap.recycleview.simplepage;

import android.view.View;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

public interface OnClickableItemClickedListener {

    @CheckResult
    boolean onClickableItemClicked(@NonNull View itemView, @NonNull ClickableItem contributor);
}