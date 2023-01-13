package com.qhy040404.libraryonetap.recycleview.simplepage;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drakeet.multitype.MultiTypeAdapter;

import java.util.Arrays;
import java.util.List;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private final @NonNull
    MultiTypeAdapter adapter;
    private final Class<?>[] dividerClasses = {Card.class, ClickableItem.class};

    public DividerItemDecoration(@NonNull MultiTypeAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (adapter.getItemCount() == 0) {
            outRect.set(0, 0, 0, 0);
            return;
        }
        List<?> items = adapter.getItems();
        int position = parent.getChildAdapterPosition(view);
        boolean should = false;
        for (int i = 0; !should && i < dividerClasses.length; i++) {
            should = position + 1 < items.size()
                && Arrays.asList(dividerClasses).contains(items.get(position).getClass())
                && Arrays.asList(dividerClasses).contains(items.get(position).getClass());
        }
        if (should) {
            outRect.set(0, 0, 0, 1);
        } else {
            outRect.set(0, 0, 0, 0);
        }
    }
}
