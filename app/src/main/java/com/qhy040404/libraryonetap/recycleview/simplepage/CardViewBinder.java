package com.qhy040404.libraryonetap.recycleview.simplepage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.drakeet.multitype.ItemViewBinder;
import com.qhy040404.libraryonetap.R;

public class CardViewBinder extends ItemViewBinder<Card, CardViewBinder.CViewHolder> {
    @Override
    @NonNull
    public CViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new CViewHolder(inflater.inflate(R.layout.simplepage_item_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CViewHolder holder, @NonNull Card card) {
        holder.content.setLineSpacing(card.lineSpacingExtra, holder.content.getLineSpacingMultiplier());
        holder.content.setText(card.content);
    }

    @Override
    public long getItemId(@NonNull Card item) {
        return item.hashCode();
    }

    public static class CViewHolder extends RecyclerView.ViewHolder {
        public final AppCompatTextView content;

        public CViewHolder(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.simple_content);
        }
    }
}
