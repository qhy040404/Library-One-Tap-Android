package com.qhy040404.libraryonetap.recyclerview

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.google.android.material.appbar.AppBarLayout
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.compat.WICompat.getInsetsParam
import com.qhy040404.libraryonetap.compat.WICompat.getSystemBars
import com.qhy040404.libraryonetap.constant.enums.InsetsParams
import com.qhy040404.libraryonetap.recyclerview.simplepage.Card
import com.qhy040404.libraryonetap.recyclerview.simplepage.CardViewBinder
import com.qhy040404.libraryonetap.recyclerview.simplepage.Category
import com.qhy040404.libraryonetap.recyclerview.simplepage.CategoryViewBinder
import com.qhy040404.libraryonetap.recyclerview.simplepage.Clickable
import com.qhy040404.libraryonetap.recyclerview.simplepage.ClickableViewBinder
import com.qhy040404.libraryonetap.recyclerview.simplepage.DividerItemDecoration
import com.qhy040404.libraryonetap.utils.extensions.getColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rikka.material.app.MaterialActivity

abstract class SimplePageActivity : MaterialActivity() {
    private var givenInsetsToDecorView = false
    private lateinit var recyclerView: RecyclerView
    lateinit var toolbar: Toolbar

    protected abstract fun initializeView()
    protected abstract fun initializeViewPref()
    protected open fun onItemsCreated(items: MutableList<Any>) {
        items.clear()
    }

    protected abstract suspend fun setData()

    override fun onCreate(savedInstanceState: Bundle?) {
        initializeViewPref()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.simplepage_activity)
        toolbar = findViewById(R.id.simple_toolbar)
        findViewById<ProgressBar>(R.id.simple_progressbar).isVisible = false
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        recyclerView = findViewById(R.id.simple_list)
        applyEdgeToEdge()
        initializeView()
        onBackPressedDispatcher.addCallback(this, true) {
            finish()
        }
        lifecycleScope.launch(Dispatchers.IO) {
            setData()
        }
    }

    private fun applyEdgeToEdge() {
        window.apply {
            navigationBarColor =
                R.color.simple_page_navigationBarColor.getColor(this@SimplePageActivity)
            val appBarLayout = findViewById<AppBarLayout>(R.id.header_layout)
            val originalRecyclerViewPaddingBottom = recyclerView.paddingBottom
            givenInsetsToDecorView = false
            WindowCompat.setDecorFitsSystemWindows(this, false)
            ViewCompat.setOnApplyWindowInsetsListener(decorView) { _: View?, windowInsets: WindowInsetsCompat ->
                val navigationBarsInsets =
                    windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
                val isGestureNavigation =
                    navigationBarsInsets.bottom <= 20 * resources.displayMetrics.density
                if (!isGestureNavigation) {
                    ViewCompat.onApplyWindowInsets(decorView, windowInsets)
                    givenInsetsToDecorView = true
                } else if (givenInsetsToDecorView) {
                    ViewCompat.onApplyWindowInsets(
                        decorView,
                        WindowInsetsCompat.Builder()
                            .setInsets(
                                WindowInsetsCompat.Type.navigationBars(),
                                Insets.of(
                                    navigationBarsInsets.left,
                                    navigationBarsInsets.top,
                                    navigationBarsInsets.right,
                                    0
                                )
                            )
                            .build()
                    )
                }
                val insetLeft = getInsetsParam(windowInsets, getSystemBars(), InsetsParams.LEFT)
                val insetRight = getInsetsParam(windowInsets, getSystemBars(), InsetsParams.RIGHT)
                val insetTop = getInsetsParam(windowInsets, getSystemBars(), InsetsParams.TOP)
                decorView.setPadding(
                    insetLeft,
                    decorView.paddingTop,
                    insetRight,
                    decorView.paddingBottom
                )
                appBarLayout.setPadding(
                    appBarLayout.paddingLeft,
                    insetTop,
                    appBarLayout.paddingRight,
                    appBarLayout.paddingBottom
                )
                recyclerView.setPadding(
                    recyclerView.paddingLeft,
                    recyclerView.paddingTop,
                    recyclerView.paddingRight,
                    originalRecyclerViewPaddingBottom + navigationBarsInsets.bottom
                )
                windowInsets
            }
        }
    }

    fun syncRecycleView() {
        runOnUiThread {
            findViewById<ProgressBar>(R.id.simple_progressbar).isVisible = true
        }
        MultiTypeAdapter().apply {
            register(Card::class.java, CardViewBinder())
            register(Category::class.java, CategoryViewBinder())
            register(Clickable::class.java, ClickableViewBinder())
            val items = mutableListOf<Any>()
            onItemsCreated(items)
            this.items = items
            setHasStableIds(true)
        }.also {
            recyclerView.post {
                recyclerView.addItemDecoration(
                    DividerItemDecoration(it)
                )
                recyclerView.adapter = it
            }
        }
        runOnUiThread {
            findViewById<ProgressBar>(R.id.simple_progressbar).isVisible = false
        }
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(menuItem)
    }
}
