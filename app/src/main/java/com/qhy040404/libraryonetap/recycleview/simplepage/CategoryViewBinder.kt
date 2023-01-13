package com.qhy040404.libraryonetap.recycleview.simplepage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.drakeet.multitype.ItemViewBinder;
import com.qhy040404.libraryonetap.R;

public class CategoryViewBinder extends ItemViewBinder<Category, CategoryViewBinder.CGViewHolder> {
    @Override
    @NonNull
    public CGViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new CGViewHolder(inflater.inflate(R.layout.simplepage_item_category, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CGViewHolder holder, @NonNull Category category) {
        holder.category.setText(category.title);
    }

    @Override
    public long getItemId(@NonNull Category item) {
        return item.hashCode();
    }

    public static class CGViewHolder extends RecyclerView.ViewHolder {
        public final AppCompatTextView category;

        public CGViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
        }
    }
}
