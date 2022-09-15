package com.qhy040404.libraryonetap.ui.about;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.drakeet.about.License;
import com.drakeet.about.R;
import com.drakeet.multitype.ItemViewBinder;

/**
 * @author drakeet
 * @author qhy040404
 */
public class LicenseViewBinder extends ItemViewBinder<License, LicenseViewBinder.LViewHolder> {
    @Override
    @NonNull
    public LViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new LViewHolder(inflater.inflate(R.layout.about_page_item_license, parent, false));
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull LViewHolder holder, @NonNull License data) {
        holder.content.setText(data.name + " - " + data.author);
        holder.hint.setText(data.url + "\n" + data.type);
        holder.setURL(data.url);
    }

    @Override
    public long getItemId(@NonNull License item) {
        return item.hashCode();
    }

    public static class LViewHolder extends ClickableViewHolder {
        public TextView content;
        public TextView hint;

        public LViewHolder(View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.content);
            hint = itemView.findViewById(R.id.hint);
        }
    }
}
