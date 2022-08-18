# RecycleView Models

* [SimplePage](#SimplePage)

### SimplePage

* *List*
* *Category*

**One must call function**

```kotlin
/**
 * Sync RecycleView items
 * Call in innerThread is better
 */
syncRecycleView()
```

**Three abstract functions**

```kotlin
/**
 * Before super.onCreate() to set theme or etc.
 */
initializeViewPref()

/**
 *  After super.onCreate()
 *  Just like normal
 */
initializeView()
```

**One late-init Thread**

```kotlin

import java.lang.Runnable
import java.lang.Thread

/**
 * Set a inner Runnable
 * to innerThread
 */
innerThread = Thread {
    //TODO
}

innerThread = Thread(AThread())
private inner class AThread : Runnable {
    override fun run() {
        //TODO
    }
}
```
