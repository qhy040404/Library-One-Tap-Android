package com.qhy040404.libraryonetap.utils.extensions

import android.view.LayoutInflater
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

@Suppress("unused")
object CompatExtensions {
    @Suppress("UNCHECKED_CAST")
    fun <T : ViewBinding> LifecycleOwner.inflateBinding(inflater: LayoutInflater): T =
        (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
            .filterIsInstance<Class<T>>()
            .first()
            .getDeclaredMethod("inflate", LayoutInflater::class.java)
            .invoke(null, inflater) as T
}
