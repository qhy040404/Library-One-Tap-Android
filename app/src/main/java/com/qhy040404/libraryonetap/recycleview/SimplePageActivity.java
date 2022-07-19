package com.qhy040404.libraryonetap.recycleview;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.drakeet.multitype.MultiTypeAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.qhy040404.libraryonetap.R;
import com.qhy040404.libraryonetap.recycleview.simplepage.Category;
import com.qhy040404.libraryonetap.recycleview.simplepage.CategoryViewBinder;
import com.qhy040404.libraryonetap.recycleview.simplepage.ClickableItem;
import com.qhy040404.libraryonetap.recycleview.simplepage.ClickableItemViewBinder;
import com.qhy040404.libraryonetap.recycleview.simplepage.ImageLoader;
import com.qhy040404.libraryonetap.recycleview.simplepage.OnClickableItemClickedListener;

import java.util.ArrayList;
import java.util.List;

import rikka.material.app.MaterialActivity;

public abstract class SimplePageActivity extends MaterialActivity {

    private Toolbar toolbar;

    private List<Object> items;
    private MultiTypeAdapter adapter;
    private RecyclerView recyclerView;
    private @Nullable
    ImageLoader imageLoader;
    private boolean initialized;
    private @Nullable
    OnClickableItemClickedListener onClickableItemClickedListener;
    private boolean givenInsetsToDecorView = false;

    protected abstract void onItemsCreated(@NonNull List<Object> items);

    public @Nullable
    ImageLoader getImageLoader() {
        return imageLoader;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setImageLoader(@NonNull ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
        if (initialized) {
            adapter.notifyDataSetChanged();
        }
    }

    @LayoutRes
    protected int layoutRes() {
        return R.layout.simplepage_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutRes());
        toolbar = findViewById(R.id.simple_toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        onApplyPresetAttrs();
        recyclerView = findViewById(R.id.simple_list);
        applyEdgeToEdge();
    }

    private void applyEdgeToEdge() {
        Window window = getWindow();
        int navigationBarColor = ContextCompat.getColor(this, R.color.simple_page_navigationBarColor);
        window.setNavigationBarColor(navigationBarColor);

        final AppBarLayout appBarLayout = findViewById(R.id.header_layout);
        final View decorView = window.getDecorView();
        final int originalRecyclerViewPaddingBottom = recyclerView.getPaddingBottom();

        givenInsetsToDecorView = false;
        WindowCompat.setDecorFitsSystemWindows(window, false);
        ViewCompat.setOnApplyWindowInsetsListener(decorView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat windowInsets) {
                Insets navigationBarsInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars());
                boolean isGestureNavigation = navigationBarsInsets.bottom <= 20 * getResources().getDisplayMetrics().density;

                if (!isGestureNavigation) {
                    ViewCompat.onApplyWindowInsets(decorView, windowInsets);
                    givenInsetsToDecorView = true;
                } else if (givenInsetsToDecorView) {
                    ViewCompat.onApplyWindowInsets(
                            decorView,
                            new WindowInsetsCompat.Builder()
                                    .setInsets(
                                            WindowInsetsCompat.Type.navigationBars(),
                                            Insets.of(navigationBarsInsets.left, navigationBarsInsets.top, navigationBarsInsets.right, 0)
                                    )
                                    .build()
                    );
                }
                decorView.setPadding(windowInsets.getSystemWindowInsetLeft(), decorView.getPaddingTop(), windowInsets.getSystemWindowInsetRight(), decorView.getPaddingBottom());
                appBarLayout.setPadding(appBarLayout.getPaddingLeft(), windowInsets.getSystemWindowInsetTop(), appBarLayout.getPaddingRight(), appBarLayout.getPaddingBottom());
                recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), originalRecyclerViewPaddingBottom + navigationBarsInsets.bottom);
                return windowInsets;
            }
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        adapter = new MultiTypeAdapter();
        adapter.register(Category.class, new CategoryViewBinder());
        adapter.register(ClickableItem.class, new ClickableItemViewBinder(this));
        items = new ArrayList<>();
        onItemsCreated(items);
        adapter.setItems(items);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        initialized = true;
    }

    private void onApplyPresetAttrs() {
        final TypedArray a = obtainStyledAttributes(R.styleable.SimplePageActivity);
        Drawable navigationIcon = a.getDrawable(R.styleable.SimplePageActivity_simplePageNavigationIcon);
        if (navigationIcon != null) {
            setNavigationIcon(navigationIcon);
        }
        a.recycle();
    }

    /**
     * Set the icon to use for the toolbar's navigation button.
     *
     * @param resId Resource ID of a drawable to set
     */
    public void setNavigationIcon(@DrawableRes int resId) {
        toolbar.setNavigationIcon(resId);
    }

    public void setNavigationIcon(@NonNull Drawable drawable) {
        toolbar.setNavigationIcon(drawable);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void setTitle(@NonNull CharSequence title) {
        toolbar.setTitle(title);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public List<Object> getItems() {
        return items;
    }

    public MultiTypeAdapter getAdapter() {
        return adapter;
    }

    public @Nullable
    OnClickableItemClickedListener getOnClickableItemClickedListener() {
        return onClickableItemClickedListener;
    }

    public void setOnClickableItemClickedListener(@Nullable OnClickableItemClickedListener listener) {
        this.onClickableItemClickedListener = listener;
    }
}