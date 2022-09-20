package com.qhy040404.libraryonetap.ui.about;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author drakeet
 * @author qhy040404
 */
public class ClickableViewHolder extends RecyclerView.ViewHolder {
    private @Nullable
    String url;

    public ClickableViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(v -> {
            if (url != null) {
                try {
                    new CustomTabsIntent.Builder().build()
                        .launchUrl(v.getContext(), Uri.parse(url));
                } catch (ActivityNotFoundException ignored1) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    try {
                        v.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException ignored2) {
                    }
                }
            }
        });
    }

    public void setURL(@Nullable String url) {
        this.url = url;
    }
}
