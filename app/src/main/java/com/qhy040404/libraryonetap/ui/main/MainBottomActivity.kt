package com.qhy040404.libraryonetap.ui.main

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.base.BaseActivity
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.databinding.ActivityMainBottomBinding
import com.qhy040404.libraryonetap.fragment.IAppBarContainer
import com.qhy040404.libraryonetap.fragment.SettingsFragment
import com.qhy040404.libraryonetap.fragment.library.DetailFragment
import com.qhy040404.libraryonetap.fragment.library.YanxiujianFragment
import com.qhy040404.libraryonetap.fragment.tools.ToolsInitFragment
import com.qhy040404.libraryonetap.ui.tools.VCardActivity
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.extensions.ViewExtensions.setCurrentItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainBottomActivity : BaseActivity<ActivityMainBottomBinding>(), INavViewContainer,
    IAppBarContainer {
    private val navViewBehavior by lazy { HideBottomViewOnScrollBehavior<BottomNavigationView>() }

    override fun init() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = AppUtils.setTitle(this)

        binding.apply {
            root.bringChildToFront(binding.appbar)
            viewpager.apply {
                adapter = object : FragmentStateAdapter(this@MainBottomActivity) {
                    override fun getItemCount(): Int {
                        return 4
                    }

                    override fun createFragment(position: Int): Fragment {
                        return when (position) {
                            0 -> DetailFragment()
                            1 -> YanxiujianFragment()
                            2 -> ToolsInitFragment()
                            else -> SettingsFragment()
                        }
                    }
                }

                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        binding.navView.menu.getItem(position).isChecked = true
                    }
                })

                isUserInputEnabled = false
                offscreenPageLimit = 2
            }

            navView.apply {
                (layoutParams as CoordinatorLayout.LayoutParams).also {
                    it.behavior = navViewBehavior
                }
                requestLayout()
                setOnItemSelectedListener {
                    fun performClickNavigationItem(index: Int) {
                        if (binding.viewpager.currentItem != index) {
                            if (!binding.viewpager.isFakeDragging) {
                                binding.viewpager.setCurrentItem(index, 300L)
                            }
                        } else {
                            val clickFlag =
                                binding.viewpager.getTag(R.id.viewpager_tab_click) as? Boolean
                                    ?: false
                            if (!clickFlag) {
                                binding.viewpager.setTag(R.id.viewpager_tab_click, true)

                                lifecycleScope.launch {
                                    delay(200)
                                    binding.viewpager.setTag(R.id.viewpager_tab_click, false)
                                }
                            }
                        }
                    }

                    when (it.itemId) {
                        R.id.navigation_qr -> performClickNavigationItem(0)
                        R.id.navigation_yxj -> performClickNavigationItem(1)
                        R.id.navigation_tools -> performClickNavigationItem(2)
                        R.id.navigation_settings -> performClickNavigationItem(3)
                    }
                    true
                }
            }
        }
        handleIntentFromShortcuts(intent)
        showWelcomeDialog()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntentFromShortcuts(intent)
    }

    override fun showNavigationView() {
        navViewBehavior.slideUp(binding.navView)
    }

    override fun hideNavigationView() {
        navViewBehavior.slideDown(binding.navView)
    }

    override fun showProgressBar() {
        binding.progressHorizontal.show()
    }

    override fun hideProgressBar() {
        binding.progressHorizontal.hide()
    }

    override fun scheduleAppbarLiftingStatus(isLifted: Boolean, from: String) {
        binding.appbar.isLifted = isLifted
    }

    override fun setLiftOnScrollTargetView(targetView: View) {
        binding.appbar.setLiftOnScrollTargetView(targetView)
    }

    private fun handleIntentFromShortcuts(intent: Intent) {
        when (intent.action) {
            Constants.SHORTCUT_DETAIL -> binding.viewpager.setCurrentItem(0, false)
            Constants.SHORTCUT_TOOLS -> binding.viewpager.setCurrentItem(2, false)
            Constants.SHORTCUT_VCARD -> startActivity(Intent(this, VCardActivity::class.java))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    MaterialAlertDialogBuilder(this)
                        .setMessage(R.string.gotPermission)
                        .setTitle(R.string.bath_title)
                        .setPositiveButton(R.string.ok) { _, _ -> }
                        .setCancelable(true)
                        .create()
                        .show()
                } else {
                    MaterialAlertDialogBuilder(this)
                        .setMessage(R.string.failPermission)
                        .setTitle(R.string.error)
                        .setPositiveButton(R.string.ok) { _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                        .setCancelable(false)
                        .create()
                        .show()
                }
            }
        }
    }

    private fun showWelcomeDialog() {
        if (GlobalValues.initialized) {
            return
        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.welcome)
                .setMessage(R.string.welcome_message)
                .setPositiveButton(R.string.ok) { _, _ ->
                    this.binding.viewpager.setCurrentItem(3, true)
                }
                .setNegativeButton(R.string.no) { _, _ -> }
                .setCancelable(false)
                .create()
                .show()
            GlobalValues.initialized = true
        }
    }
}