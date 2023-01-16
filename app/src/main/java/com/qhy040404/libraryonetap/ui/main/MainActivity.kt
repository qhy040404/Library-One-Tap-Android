package com.qhy040404.libraryonetap.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.BuildConfig
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.base.BaseActivity
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.OnceTag
import com.qhy040404.libraryonetap.databinding.ActivityMainBottomBinding
import com.qhy040404.libraryonetap.ui.fragment.library.SingleFragment
import com.qhy040404.libraryonetap.ui.fragment.library.YanxiujianFragment
import com.qhy040404.libraryonetap.ui.fragment.settings.SettingsFragment
import com.qhy040404.libraryonetap.ui.fragment.tools.ToolsInitFragment
import com.qhy040404.libraryonetap.ui.interfaces.IAppBarContainer
import com.qhy040404.libraryonetap.ui.tools.BathReserveActivity
import com.qhy040404.libraryonetap.ui.tools.ExamsActivity
import com.qhy040404.libraryonetap.ui.tools.VCardActivity
import com.qhy040404.libraryonetap.utils.CacheUtils
import com.qhy040404.libraryonetap.utils.SPUtils
import com.qhy040404.libraryonetap.utils.UpdateUtils
import com.qhy040404.libraryonetap.utils.extensions.IntExtensions.getString
import com.qhy040404.libraryonetap.utils.extensions.ViewExtensions.setCurrentItem
import jonathanfinerty.once.Once
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : BaseActivity<ActivityMainBottomBinding>(),
    IAppBarContainer {
    private val navViewBehavior by lazy { HideBottomViewOnScrollBehavior<BottomNavigationView>() }

    override fun init() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = R.string.app_name.getString()
        if (!GlobalValues.md3) {
            binding.toolbar.setTitleTextColor(getColor(R.color.white))
        }

        if (!Once.beenDone(Once.THIS_APP_VERSION, OnceTag.CLEAR_AFTER_UPDATE)) {
            if (!BuildConfig.DEBUG) {
                CacheUtils.trimCaches()
            }
            if (
                UpdateUtils.getVersionCode(
                    GlobalValues.latestApkName, true
                ) <= UpdateUtils.getVersionCode(
                    BuildConfig.VERSION_NAME, false
                )
            ) {
                SPUtils.sp.edit { remove(Constants.LATEST_APK_NAME) }
                val file = File(dataDir, Constants.CHANGELOG_INACTIVE)
                if (file.exists()) {
                    val newFile = File(dataDir, Constants.CHANGELOG_ACTIVE)
                    if (newFile.exists()) {
                        newFile.delete()
                    }
                    file.renameTo(newFile)
                }
            }
            Once.markDone(OnceTag.CLEAR_AFTER_UPDATE)
        }

        binding.apply {
            root.bringChildToFront(binding.appbar)
            viewpager.apply {
                adapter = object : FragmentStateAdapter(this@MainActivity) {
                    override fun getItemCount(): Int {
                        return 4
                    }

                    override fun createFragment(position: Int): Fragment {
                        return when (position) {
                            0 -> SingleFragment()
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
                ViewCompat.setOnApplyWindowInsetsListener(this) { _, windowInsets ->
                    val naviBarsInsets = ViewCompat.getRootWindowInsets(this)!!
                        .getInsets(WindowInsetsCompat.Type.navigationBars())
                    this.updatePadding(bottom = naviBarsInsets.bottom)
                    windowInsets
                }
            }
        }
        handleIntentFromShortcuts(intent)
        showWelcomeDialog()

        lifecycleScope.launch(Dispatchers.IO) {
            UpdateUtils.checkUpdate(this@MainActivity)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntentFromShortcuts(intent)
    }

    override fun scheduleAppbarLiftingStatus(isLifted: Boolean) {
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
            Constants.SHORTCUT_EXAMS -> startActivity(Intent(this, ExamsActivity::class.java))
            Intent.ACTION_APPLICATION_PREFERENCES -> binding.viewpager.setCurrentItem(3, false)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        LibraryOneTapApp.instance?.dismissFragment()
        if (requestCode == 100) {
            for (i in permissions.indices) {
                when (permissions[i]) {
                    Manifest.permission.ACCESS_FINE_LOCATION -> if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        startActivity(Intent(this, BathReserveActivity::class.java))
                    } else {
                        MaterialAlertDialogBuilder(this)
                            .setMessage(R.string.br_permission_fail)
                            .setTitle(R.string.glb_error)
                            .setPositiveButton(R.string.glb_ok) { _, _ ->
                                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = "package:$packageName".toUri()
                                })
                            }
                            .setCancelable(false)
                            .create()
                            .show()
                    }
                }
            }
        }
    }

    private fun showWelcomeDialog() {
        if (!Once.beenDone(Once.THIS_APP_INSTALL, OnceTag.FIRST_DATA_INPUT)) {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.in_welcome)
                .setMessage(R.string.in_message)
                .setPositiveButton(R.string.glb_ok) { _, _ ->
                    this.binding.viewpager.setCurrentItem(3, true)
                }
                .setNegativeButton(R.string.glb_no) { _, _ -> }
                .setCancelable(false)
                .create()
                .show()
            Once.markDone(OnceTag.FIRST_DATA_INPUT)
        }
    }
}
