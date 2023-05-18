package net.helium24.fractal;

import android.os.SystemClock

class FractalOptions {
    var fractalType: FractalType = FractalType.Julia
    var aspectRatio: Float = 1.0f
    var currentTime: Float = 1.0f

    var animate: Boolean = true

    // Both of these are updated from FractalSurfaceView and must be @Volatile
    @Volatile
    var touchX: Float = 0.0f

    @Volatile
    var touchY: Float = 0.0f

    fun updateTime() {
        currentTime = ((SystemClock.elapsedRealtime() % 100000).toFloat() / 1000.0f)
    }
}
