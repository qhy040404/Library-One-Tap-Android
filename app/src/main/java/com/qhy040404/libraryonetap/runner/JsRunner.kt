package com.qhy040404.libraryonetap.runner

import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.Scriptable

@Suppress("ObjectPropertyName")
object JsRunner {
    private val _lock = Any()

    private var _rhino: Context? = null
    private var _scope: Scriptable? = null

    private fun init() {
        if (_rhino == null) {
            synchronized(_lock) {
                if (_rhino == null) {
                    _rhino = Context.enter()
                    _rhino!!.optimizationLevel = -1
                    _scope = _rhino?.initStandardObjects()
                }
            }
        }
    }

    fun initScript(js: String?): Boolean {
        init()
        return runCatching {
            _rhino!!.evaluateString(_scope, js, null, 1, null)
            true
        }.getOrDefault(false)
    }

    fun callFunc(funcName: String?, vararg funcParams: Any?): Any? {
        return runCatching {
            (_scope!![funcName, _scope] as Function).call(_rhino, _scope, _scope, funcParams)
        }.getOrNull()
    }
}
