package net.helium24.micropass

import android.content.Context
import android.content.res.AssetManager




// Although this theoretically could interface with the Google Drive API, this is substantially simpler.
class AssetRetriever {
    fun ListAssets(context: Context): List<String> {
        return context.assets.list("")!!.asList().sortedBy { it.lowercase() }
    }

    fun GetAsset(context: Context, name: String): String {
        return context.assets.open(name).bufferedReader(Charsets.UTF_8).use { it.readText() }
            .replace("\uFEFF", "") // Strip the UTF8 BOM if it still makes it through
    }
}