package com.github.browsermovies.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpStreamFetcher
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.github.browsermovies.libs.ApiService
import com.github.browsermovies.libs.SSLSocketFactoryCompat
import okhttp3.OkHttpClient
import java.io.InputStream


@GlideModule
class OkHttpLibraryGlideModule : com.bumptech.glide.module.GlideModule {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val trustAllCerts = ApiService().provideTrustManager()
        val okHttpClient = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .sslSocketFactory(SSLSocketFactoryCompat(trustAllCerts),trustAllCerts)
            .build()
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(okHttpClient))

    }
}