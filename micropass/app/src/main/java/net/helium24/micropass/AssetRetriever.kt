package net.helium24.micropass

import android.content.Context
import android.content.res.AssetManager




// Although this theoretically could interface with the Google Drive API, this is substantially simpler.
class AssetRetriever {
    fun ListAssets(context: Context): List<String> {
        return context.assets.list("")!!.asList()
    }

    fun GetAsset(context: Context, name: String): String {
        return context.assets.open(name).bufferedReader().use { it.readText() }
    }
}