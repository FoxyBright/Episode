package ru.rikmasters.gilty

import android.app.Application
import android.content.Context
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import com.yandex.mapkit.MapKitFactory
import ru.rikmasters.gilty.core.initApplication

class MainApplication: Application(), ImageLoaderFactory {
    
    override fun onCreate() {
        super.onCreate()
        initApplication(this, AppModule)
        MapKitFactory.setApiKey("6eb87a4e-7668-4cf6-a691-36051b71e2e5")
        MapKitFactory.initialize(this)
        // Reset saved positions in screens
        this.getSharedPreferences("prefs", Context.MODE_PRIVATE)
            .edit()
            .putInt("scroll_position", 0)
            .putInt("scroll_offset", 0)
            .apply()
    }
    
    override fun newImageLoader() = ImageLoader
        .Builder(this)
        .diskCache {
            DiskCache.Builder()
                .directory(this.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.02)
                .build()
        }
        .build()
}