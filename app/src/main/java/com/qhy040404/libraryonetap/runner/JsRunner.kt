package com.qhy040404.libraryonetap.runner

import com.qhy040404.libraryonetap.utils.encrypt.AESEncryptUtils
import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.Scriptable

@Suppress("ObjectPropertyName")
object JsRunner {
    private val _lock = Any()

    private var _rhino: Context? = null
    private var _scope: Scriptable? = null

    private fun init() {
        if (Context.getCurrentContext() == null) reset()
        if (_rhino == null) {
            synchronized(_lock) {
                if (_rhino == null) {
                    _rhino = Context.enter()
                    _rhino!!.languageVersion = Context.VERSION_ES6
                    _rhino!!.optimizationLevel = -1
                    _scope = _rhino?.initStandardObjects()
                }
            }
        }
    }

    fun initScript(js: String?) {
        init()
        _rhino!!.evaluateString(_scope, js, null, 1, null)
    }

    fun callFunc(funcName: String?, vararg funcParams: Any?): Any? {
        return (_scope!![funcName, _scope] as Function).call(_rhino, _scope, _scope, funcParams)
    }

    fun reset() {
        _rhino = null
        _scope = null
        AESEncryptUtils.initialized = false
    }
}
