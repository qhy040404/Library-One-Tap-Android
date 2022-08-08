package com.qhy040404.libraryonetap.recycleview.simplepage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        holder.actionIcon.setImageDrawable(category.actionIcon);
        holder.actionIcon.setContentDescription(category.actionIconContentDescription);
        if (category.actionIcon != null) {
            holder.actionIcon.setVisibility(View.VISIBLE);
        } else {
            holder.actionIcon.setVisibility(View.GONE);
        }
        holder.actionIcon.setOnClickListener(category.getOnActionClickListener());
    }

    @Override
    public long getItemId(@NonNull Category item) {
        return item.hashCode();
    }

    public static class CGViewHolder extends RecyclerView.ViewHolder {

        public final TextView category;
        public final ImageButton actionIcon;

        public CGViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
            actionIcon = itemView.findViewById(R.id.actionIcon);
        }
    }
}