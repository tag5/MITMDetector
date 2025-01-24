package app.security.mitmdetector.services

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseService @Inject constructor() {
    // TODO: Store data in a sqlite Database
    private val db = mutableMapOf<String, Boolean>()

    fun isCheckEnabled(checkId: String) : Boolean {
        return db.getOrDefault(checkId, true)
    }

    fun setCheckEnabled(checkId: String, isEnabled: Boolean) {
        db[checkId] = isEnabled
    }
}