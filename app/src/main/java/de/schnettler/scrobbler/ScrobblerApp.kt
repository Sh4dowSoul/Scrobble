package de.schnettler.scrobbler

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import de.schnettler.scrobble.work.ScrobbleDelegationWorkerFactory
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class ScrobblerApp: Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: ScrobbleDelegationWorkerFactory

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        AndroidThreeTen.init(this);
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()
    }
}