package com.qhy040404.libraryonetap.recycleview;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

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

import com.drakeet.multitype.MultiTypeAdapter;
import com.google.android.material.appbar.AppBarLayout;
import com.qhy040404.libraryonetap.R;
import com.qhy040404.libraryonetap.annotation.InsetsParams;
import com.qhy040404.libraryonetap.compat.WICompat;
import com.qhy040404.libraryonetap.recycleview.simplepage.Card;
import com.qhy040404.libraryonetap.recycleview.simplepage.CardViewBinder;
import com.qhy040404.libraryonetap.recycleview.simplepage.Category;
import com.qhy040404.libraryonetap.recycleview.simplepage.CategoryViewBinder;
import com.qhy040404.libraryonetap.recycleview.simplepage.ClickableItem;
import com.qhy040404.libraryonetap.recycleview.simplepage.ClickableItemViewBinder;
import com.qhy040404.libraryonetap.recycleview.simplepage.OnClickableItemClickedListener;

import java.util.ArrayList;
import java.util.List;

import rikka.material.app.MaterialActivity;

/**
 * MUST CALL syncRecycleView()
 * to show items in RecycleView.
 * <p>
 * MUST DEFINE innerThread
 * in class.
 */

public abstract class SimplePageActivity extends MaterialActivity {
    protected Thread innerThread;

    private Toolbar toolbar;

    private MultiTypeAdapter adapter;
    private RecyclerView recyclerView;
    private boolean initialized = false;

    private @Nullable
    OnClickableItemClickedListener onClickableItemClickedListener;

    private boolean givenInsetsToDecorView = false;

    private final Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                if (innerThread == null) {
                    throw new IllegalArgumentException("innerThread is null. Did you assign a value to it?");
                }
                innerThread.start();
                innerThread.join();
                if (!initialized) {
                    throw new NullPointerException("You must call syncRecycleView() #onCreate");
                }
            } catch (InterruptedException ignored) {
            }
        }
    });

    protected abstract void initializeView();

    protected abstract void initializeViewPref();

    protected abstract void onItemsCreated(@NonNull List<Object> items);

    @LayoutRes
    protected int layoutRes() {
        return R.layout.simplepage_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initializeViewPref();
        super.onCreate(savedInstanceState);
        setContentView(layoutRes());
        toolbar = findViewById(R.id.simple_toolbar);
        ProgressBar progressBar = findViewById(R.id.simple_progressbar);

        progressBar.setVisibility(View.INVISIBLE);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        onApplyPresetAttrs();
        recyclerView = findViewById(R.id.simple_list);
        applyEdgeToEdge();
        initializeView();

        mThread.start();
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

            insetLeft = WICompat.INSTANCE.getInsetsParam(windowInsets, WICompat.INSTANCE.getSystemBars(), InsetsParams.LEFT);
            insetRight = WICompat.INSTANCE.getInsetsParam(windowInsets, WICompat.INSTANCE.getSystemBars(), InsetsParams.RIGHT);
            insetTop = WICompat.INSTANCE.getInsetsParam(windowInsets, WICompat.INSTANCE.getSystemBars(), InsetsParams.TOP);

            decorView.setPadding(insetLeft, decorView.getPaddingTop(), insetRight, decorView.getPaddingBottom());
            appBarLayout.setPadding(appBarLayout.getPaddingLeft(), insetTop, appBarLayout.getPaddingRight(), appBarLayout.getPaddingBottom());
            recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingRight(), originalRecyclerViewPaddingBottom + navigationBarsInsets.bottom);
            return windowInsets;
        });
    }

    public void syncRecycleView() {
        adapter = new MultiTypeAdapter();
        adapter.register(Card.class, new CardViewBinder());
        adapter.register(Category.class, new CategoryViewBinder());
        adapter.register(ClickableItem.class, new ClickableItemViewBinder(this));
        List<Object> items = new ArrayList<>();
        onItemsCreated(items);
        adapter.setItems(items);
        adapter.setHasStableIds(true);
        recyclerView.post(() -> recyclerView.setAdapter(adapter));
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
    OnClickableItemClickedListener getOnClickableItemClickedListener() {
        return onClickableItemClickedListener;
    }

    @SuppressWarnings("unused")
    public void setOnClickableItemClickedListener(@Nullable OnClickableItemClickedListener listener) {
        this.onClickableItemClickedListener = listener;
    }
}
