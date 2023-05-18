package net.helium24.fractal

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent

class FractalSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: FractalRenderer

    init {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        // Create the fractal renderer
        renderer = FractalRenderer()
        setRenderer(renderer)
        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    fun updateFractalType(fractalType: FractalType) {
        renderer.updateFractalType(fractalType)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x: Float = e.x
        val y: Float = e.y

        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                // Scale the touch positions to -1, 1 on both x and y.
                renderer.updateTouchPosition(2.0f * x / width - 1.0f, 2.0f * y / height - 1.0f)
            }
        }
        return true
    }
}