package com.qhy040404.libraryonetap.ui.about;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.drakeet.about.Card;
import com.drakeet.about.CardViewBinder;
import com.drakeet.about.Category;
import com.drakeet.about.CategoryViewBinder;
import com.drakeet.about.Contributor;
import com.drakeet.about.License;
import com.drakeet.about.LicenseViewBinder;
import com.drakeet.about.OnContributorClickedListener;
import com.drakeet.about.R;
import com.drakeet.multitype.MultiTypeAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.qhy040404.libraryonetap.annotation.InsetsParams;
import com.qhy040404.libraryonetap.utils.WindowInsetsUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import rikka.material.app.MaterialActivity;

/**
 * @author drakeet
 * @author qhy040404
 */
public abstract class AbsAboutActivityProxy extends MaterialActivity {
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private LinearLayout headerContentLayout;

    private MultiTypeAdapter adapter;
    private TextView slogan, version;
    private RecyclerView recyclerView;
    private @Nullable
    OnContributorClickedListener onContributorClickedListener;
    private boolean givenInsetsToDecorView = false;

    protected abstract void onCreateHeader(@NonNull ImageView icon, @NonNull TextView slogan, @NonNull TextView version);

    protected abstract void onItemsCreated(@NonNull List<Object> items);

    protected void onTitleViewCreated() {
    }

    @LayoutRes
    protected int layoutRes() {
        return R.layout.about_page_main_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutRes());
        toolbar = findViewById(R.id.toolbar);
        ImageView icon = findViewById(R.id.icon);
        slogan = findViewById(R.id.slogan);
        version = findViewById(R.id.version);
        collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        headerContentLayout = findViewById(R.id.header_content_layout);
        onTitleViewCreated();
        onCreateHeader(icon, slogan, version);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        onApplyPresetAttrs();
        recyclerView = findViewById(R.id.list);
        applyEdgeToEdge();
    }

    private void applyEdgeToEdge() {
        Window window = getWindow();
        int navigationBarColor = ContextCompat.getColor(this, R.color.about_page_navigationBarColor);
        window.setNavigationBarColor(navigationBarColor);

        final AppBarLayout appBarLayout = findViewById(R.id.header_layout);
        final View decorView = window.getDecorView();
        final int originalRecyclerViewPaddingBottom = recyclerView.getPaddingBottom();

        givenInsetsToDecorView = false;
        WindowCompat.setDecorFitsSystemWindows(window, false);
        ViewCompat.setOnApplyWindowInsetsListener(decorView, (v, windowInsets) -> {
            int insetLeft, insetRight, insetTop;

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

            insetLeft = WindowInsetsUtils.INSTANCE.getInsetsParam(windowInsets, WindowInsetsUtils.INSTANCE.getSystemBars(), InsetsParams.LEFT);
            insetRight = WindowInsetsUtils.INSTANCE.getInsetsParam(windowInsets, WindowInsetsUtils.INSTANCE.getSystemBars(), InsetsParams.RIGHT);
            insetTop = WindowInsetsUtils.INSTANCE.getInsetsParam(windowInsets, WindowInsetsUtils.INSTANCE.getSystemBars(), InsetsParams.TOP);

            decorView.setPadding(insetLeft, decorView.getPaddingTop(), insetRight, decorView.getPaddingBottom());
            appBarLayout.setPadding(appBarLayout.getPaddingLeft(), insetTop, appBarLayout.getPaddingRight(), appBarLayout.getPaddingBottom());
            recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), originalRecyclerViewPaddingBottom + navigationBarsInsets.bottom);
            return windowInsets;
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        adapter = new MultiTypeAdapter();
        adapter.register(Category.class, new CategoryViewBinder());
        adapter.register(Card.class, new CardViewBinder());
        adapter.register(Contributor.class, new ContributorViewBinder(this));
        adapter.register(License.class, new LicenseViewBinder());
        List<Object> items = new ArrayList<>();
        onItemsCreated(items);
        adapter.setItems(items);
        adapter.setHasStableIds(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(adapter));
        recyclerView.setAdapter(adapter);
    }

    private void onApplyPresetAttrs() {
        final TypedArray a = obtainStyledAttributes(R.styleable.AbsAboutActivity);
        Drawable headerBackground = a.getDrawable(R.styleable.AbsAboutActivity_aboutPageHeaderBackground);
        if (headerBackground != null) {
            setHeaderBackground(headerBackground);
        }
        Drawable headerContentScrim = a.getDrawable(R.styleable.AbsAboutActivity_aboutPageHeaderContentScrim);
        if (headerContentScrim != null) {
            setHeaderContentScrim(headerContentScrim);
        }
        @ColorInt
        int headerTextColor = a.getColor(R.styleable.AbsAboutActivity_aboutPageHeaderTextColor, -1);
        if (headerTextColor != -1) {
            setHeaderTextColor(headerTextColor);
        }
        Drawable navigationIcon = a.getDrawable(R.styleable.AbsAboutActivity_aboutPageNavigationIcon);
        if (navigationIcon != null) {
            setNavigationIcon(navigationIcon);
        }
        a.recycle();
    }

    /**
     * Use {@link #setHeaderBackground(int)} instead.
     *
     * @param resId The resource id of header background
     */
    @Deprecated
    public void setHeaderBackgroundResource(@DrawableRes int resId) {
        setHeaderBackground(resId);
    }

    public void setHeaderBackground(@DrawableRes int resId) {
        setHeaderBackground(Objects.requireNonNull(ContextCompat.getDrawable(this, resId)));
    }

    public void setHeaderBackground(@NonNull Drawable drawable) {
        ViewCompat.setBackground(headerContentLayout, drawable);
    }

    /**
     * Set the drawable to use for the content scrim from resources. Providing null will disable
     * the scrim functionality.
     *
     * @param drawable the drawable to display
     */
    public void setHeaderContentScrim(@NonNull Drawable drawable) {
        collapsingToolbar.setContentScrim(drawable);
    }

    public void setHeaderTextColor(@ColorInt int color) {
        collapsingToolbar.setCollapsedTitleTextColor(color);
        slogan.setTextColor(color);
        version.setTextColor(color);
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

    public Toolbar getToolbar() {
        return toolbar;
    }

    public MultiTypeAdapter getAdapter() {
        return adapter;
    }

    public @Nullable
    OnContributorClickedListener getOnContributorClickedListener() {
        return onContributorClickedListener;
    }

    @SuppressWarnings("unused")
    public void setOnContributorClickedListener(@Nullable OnContributorClickedListener listener) {
        this.onContributorClickedListener = listener;
    }
}
