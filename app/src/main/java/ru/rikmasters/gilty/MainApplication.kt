package ru.rikmasters.gilty

import android.app.Application
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