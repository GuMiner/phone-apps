package net.helium24.fractal

import android.content.Context
import android.opengl.GLSurfaceView

class FractalSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: FractalRenderer

    init {

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        renderer = FractalRenderer()

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
    }
}