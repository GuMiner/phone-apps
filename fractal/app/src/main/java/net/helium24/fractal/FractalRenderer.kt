package net.helium24.fractal

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock

class FractalRenderer : GLSurfaceView.Renderer {
    private lateinit var square: FullScreenSquare

    private var fractalOptions: FractalOptions = FractalOptions()

    fun updateFractalType(fractalType: FractalType) {
        fractalOptions.fractalType = fractalType
    }

    fun updateTouchPosition(touchX: Float, touchY: Float) {
        fractalOptions.touchX = touchX
        fractalOptions.touchY = touchY
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        square = FullScreenSquare()

        // Should never be visible, but set anyways
        GLES20.glClearColor(0.0f, 0.125f, 0.0f, 1.0f)
    }

    override fun onDrawFrame(unused: GL10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        fractalOptions.updateTime()
        square.draw(this.fractalOptions)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        fractalOptions.aspectRatio = width.toFloat() / height.toFloat()
    }
}