# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html
#
# Starting with version 2.2 of the Android plugin for Gradle, this file is distributed together with
# the plugin and unpacked at build-time. The files in $ANDROID_HOME are no longer maintained and
# will be ignored by new version of the Android plugin for Gradle.

#-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
# -optimizationpasses 5
# -optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable,!class/unboxing/enum
-repackageclasses com.qhy040404.libraryonetap

-dontpreverify

# Preserve some attributes that may be required for reflection.
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

-keep class * extends androidx.fragment.app.Fragment{}
-dontnote com.android.vending.licensing.ILicensingService
-dontnote com.google.vending.licensing.ILicensingService
-dontnote com.google.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep setters in Views so that animations can still work.
-keepclassmembers public class * extends android.view.View {
    void set*(***);
    *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick.
# -keepclassmembers class * extends android.app.Activity {
#     public void *(android.view.View);
# }

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# Preserve annotated Javascript interface methods.
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# The support libraries contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version. We know about them, and they are safe.
-dontnote android.support.**
-dontwarn android.support.**

-dontwarn javax.annotation.**

# Understand the @Keep support annotation.
-keep class androidx.annotation.Keep
-keep @androidx.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
    static void checkExpressionValueIsNotNull(java.lang.Object, java.lang.String);
    static void checkNotNullExpressionValue(java.lang.Object, java.lang.String);
    static void checkReturnedValueIsNotNull(java.lang.Object, java.lang.String, java.lang.String);
    static void checkReturnedValueIsNotNull(java.lang.Object, java.lang.String);
    static void checkFieldIsNotNull(java.lang.Object, java.lang.String, java.lang.String);
    static void checkFieldIsNotNull(java.lang.Object, java.lang.String);
    static void checkNotNull(java.lang.Object, java.lang.String);
    static void checkNotNullParameter(java.lang.Object, java.lang.String);
}

-dontwarn org.xmlpull.v1.XmlPullParser
-dontwarn org.xmlpull.v1.XmlSerializer
-keep class org.xmlpull.v1.* {*;}

## Android architecture components: Lifecycle
# LifecycleObserver's empty constructor is considered to be unused by proguard
-keepclassmembers class * implements androidx.lifecycle.LifecycleObserver {
    <init>(...);
}
# ViewModel's empty constructor is considered to be unused by proguard
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
# keep methods annotated with @OnLifecycleEvent even if they seem to be unused
# (Mostly for LiveData.LifecycleBoundObserver.onStateChange(), but who knows)
-keepclassmembers class * {
    @androidx.lifecycle.OnLifecycleEvent *;
}

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature,InnerClasses
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

-dontwarn java.lang.ClassValue

# OkHttp
-dontwarn org.conscrypt.**
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn okhttp3.internal.platform.ConscryptPlatform

-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider

-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
-dontwarn org.slf4j.impl.StaticLoggerBinder

# ViewBinding
-keep public class * extends androidx.viewbinding.ViewBinding {*;}
-keep,allowoptimization public class * extends androidx.viewbinding.ViewBinding {
  public static * inflate(android.view.LayoutInflater);
}

# BottomSheetBehavior
-keepclassmembers public class com.google.android.material.bottomsheet.BottomSheetBehavior {
  void setStateInternal(int);
}

# MPAndroidChart
-keep public class com.github.mikephil.charting.animation.* {
    public protected *;
}

# Rhino
-keep class org.mozilla.javascript.** { *; }
-dontwarn java.awt.AWTEvent
-dontwarn java.awt.ActiveEvent
-dontwarn java.awt.BorderLayout
-dontwarn java.awt.Color
-dontwarn java.awt.Component
-dontwarn java.awt.Container
-dontwarn java.awt.Dimension
-dontwarn java.awt.EventQueue
-dontwarn java.awt.Font
-dontwarn java.awt.FontMetrics
-dontwarn java.awt.Frame
-dontwarn java.awt.Graphics
-dontwarn java.awt.GridBagConstraints
-dontwarn java.awt.GridBagLayout
-dontwarn java.awt.GridLayout
-dontwarn java.awt.Insets
-dontwarn java.awt.LayoutManager
-dontwarn java.awt.MenuComponent
-dontwarn java.awt.Point
-dontwarn java.awt.Polygon
-dontwarn java.awt.Rectangle
-dontwarn java.awt.Toolkit
-dontwarn java.awt.event.ActionEvent
-dontwarn java.awt.event.ActionListener
-dontwarn java.awt.event.ComponentEvent
-dontwarn java.awt.event.ComponentListener
-dontwarn java.awt.event.ContainerEvent
-dontwarn java.awt.event.ContainerListener
-dontwarn java.awt.event.KeyAdapter
-dontwarn java.awt.event.KeyEvent
-dontwarn java.awt.event.KeyListener
-dontwarn java.awt.event.MouseAdapter
-dontwarn java.awt.event.MouseEvent
-dontwarn java.awt.event.MouseListener
-dontwarn java.awt.event.WindowAdapter
-dontwarn java.awt.event.WindowEvent
-dontwarn java.awt.event.WindowListener
-dontwarn java.beans.BeanDescriptor
-dontwarn java.beans.BeanInfo
-dontwarn java.beans.IntrospectionException
-dontwarn java.beans.Introspector
-dontwarn java.beans.PropertyDescriptor
-dontwarn javax.lang.model.SourceVersion
-dontwarn javax.swing.AbstractButton
-dontwarn javax.swing.BorderFactory
-dontwarn javax.swing.Box
-dontwarn javax.swing.BoxLayout
-dontwarn javax.swing.ButtonGroup
-dontwarn javax.swing.CellEditor
-dontwarn javax.swing.DefaultListModel
-dontwarn javax.swing.DefaultListSelectionModel
-dontwarn javax.swing.DesktopManager
-dontwarn javax.swing.Icon
-dontwarn javax.swing.JButton
-dontwarn javax.swing.JCheckBoxMenuItem
-dontwarn javax.swing.JComboBox
-dontwarn javax.swing.JComponent
-dontwarn javax.swing.JDesktopPane
-dontwarn javax.swing.JDialog
-dontwarn javax.swing.JFileChooser
-dontwarn javax.swing.JFrame
-dontwarn javax.swing.JInternalFrame
-dontwarn javax.swing.JLabel
-dontwarn javax.swing.JList
-dontwarn javax.swing.JMenu
-dontwarn javax.swing.JMenuBar
-dontwarn javax.swing.JMenuItem
-dontwarn javax.swing.JOptionPane
-dontwarn javax.swing.JPanel
-dontwarn javax.swing.JPopupMenu
-dontwarn javax.swing.JRadioButtonMenuItem
-dontwarn javax.swing.JRootPane
-dontwarn javax.swing.JScrollPane
-dontwarn javax.swing.JSplitPane
-dontwarn javax.swing.JTabbedPane
-dontwarn javax.swing.JTable
-dontwarn javax.swing.JTextArea
-dontwarn javax.swing.JToolBar
-dontwarn javax.swing.JTree
-dontwarn javax.swing.JViewport
-dontwarn javax.swing.KeyStroke
-dontwarn javax.swing.ListModel
-dontwarn javax.swing.ListSelectionModel
-dontwarn javax.swing.LookAndFeel
-dontwarn javax.swing.SwingUtilities
-dontwarn javax.swing.UIManager
-dontwarn javax.swing.border.Border
-dontwarn javax.swing.event.CellEditorListener
-dontwarn javax.swing.event.ChangeEvent
-dontwarn javax.swing.event.DocumentEvent
-dontwarn javax.swing.event.DocumentListener
-dontwarn javax.swing.event.EventListenerList
-dontwarn javax.swing.event.InternalFrameAdapter
-dontwarn javax.swing.event.InternalFrameEvent
-dontwarn javax.swing.event.InternalFrameListener
-dontwarn javax.swing.event.ListSelectionEvent
-dontwarn javax.swing.event.ListSelectionListener
-dontwarn javax.swing.event.PopupMenuEvent
-dontwarn javax.swing.event.PopupMenuListener
-dontwarn javax.swing.event.TreeExpansionEvent
-dontwarn javax.swing.event.TreeExpansionListener
-dontwarn javax.swing.event.TreeModelEvent
-dontwarn javax.swing.event.TreeModelListener
-dontwarn javax.swing.filechooser.FileFilter
-dontwarn javax.swing.table.AbstractTableModel
-dontwarn javax.swing.table.TableCellEditor
-dontwarn javax.swing.table.TableCellRenderer
-dontwarn javax.swing.table.TableModel
-dontwarn javax.swing.text.BadLocationException
-dontwarn javax.swing.text.Caret
-dontwarn javax.swing.text.Document
-dontwarn javax.swing.text.Segment
-dontwarn javax.swing.tree.DefaultTreeCellRenderer
-dontwarn javax.swing.tree.DefaultTreeSelectionModel
-dontwarn javax.swing.tree.TreeCellRenderer
-dontwarn javax.swing.tree.TreeModel
-dontwarn javax.swing.tree.TreePath
-dontwarn javax.swing.tree.TreeSelectionModel
-dontwarn kotlin.internal.jdk7.JDK7PlatformImplementations

# LOTA
-keep public class com.qhy040404.libraryonetap.annotation.**{*;}
-keep public class com.qhy040404.libraryonetap.base.**{*;}
-keep public class com.qhy040404.libraryonetap.constant.GlobalManager{*;}
-keep public class com.qhy040404.libraryonetap.data.**{*;}
-keep public class com.qhy040404.libraryonetap.ui.fragment.settings.SettingsFragment{*;}
-keep public class com.qhy040404.libraryonetap.utils.**{*;}
-keep public class rikka.material.app.LocaleDelegate{*;}
