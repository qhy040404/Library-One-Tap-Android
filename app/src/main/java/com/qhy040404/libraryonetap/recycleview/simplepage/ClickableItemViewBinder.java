package com.qhy040404.libraryonetap.recycleview.simplepage;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drakeet.multitype.ItemViewBinder;
import com.qhy040404.libraryonetap.R;
import com.qhy040404.libraryonetap.recycleview.SimplePageActivity;

public class ClickableItemViewBinder extends ItemViewBinder<ClickableItem, ClickableItemViewBinder.ViewHolder> {

    private @NonNull
    final SimplePageActivity activity;

    public ClickableItemViewBinder(@NonNull SimplePageActivity activity) {
        this.activity = activity;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.simplepage_item_clickable, parent, false), activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @NonNull ClickableItem clickableItem) {
        holder.name.setText(clickableItem.name);
        holder.desc.setText(clickableItem.desc);
        holder.data = clickableItem;
    }

    @Override
    public long getItemId(@NonNull ClickableItem item) {
        return item.hashCode();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected @NonNull
        final SimplePageActivity activity;
        public final TextView name;
        public final TextView desc;
        public ClickableItem data;

        public ViewHolder(View itemView, @NonNull SimplePageActivity activity) {
            super(itemView);
            this.activity = activity;
            name = itemView.findViewById(R.id.name);
            desc = itemView.findViewById(R.id.desc);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            OnClickableItemClickedListener listener = activity.getOnClickableItemClickedListener();
            if (listener != null && listener.onClickableItemClicked(v, data)) {
                return;
            }
            if (data.url != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(data.url));
                try {
                    v.getContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}