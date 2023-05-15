package net.helium24.fractal

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock

class FractalRenderer : GLSurfaceView.Renderer {

    private lateinit var mSquare: FullScreenSquare

    private var fractalType: FractalType = FractalType.Julia;
    fun updateFractalType(fractalType: FractalType) {
        this.fractalType = fractalType
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        mSquare = FullScreenSquare()
    }
    private val rotationMatrix = FloatArray(16)

    @Volatile
    var angleMultiplier: Float = 1f
    override fun onDrawFrame(unused: GL10) {
        // Redraw background color
        if (this.fractalType == FractalType.Julia) {
            // Set the background frame color
            GLES20.glClearColor(0.0f, 0.125f, 0.0f, 1.0f)
        }
        else {
            // Set the background frame color
            GLES20.glClearColor(1.0f, 0.125f, 0.0f, 1.0f)
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Create a rotation transformation
        val time = SystemClock.uptimeMillis() % 4000L
        val angle = 0.090f * time.toInt()
        Matrix.setRotateM(rotationMatrix, 0, angleMultiplier, 0f, 0f, -1.0f) // angle for timed motion

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        val scratch = FloatArray(16)
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrix, 0)

        mSquare.draw(scratch)
    }

    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }
}