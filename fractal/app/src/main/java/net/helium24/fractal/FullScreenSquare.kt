package net.helium24.fractal

import android.opengl.GLES20
import android.opengl.GLES20.glGetProgramInfoLog
import java.lang.Exception
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

// https://developer.android.com/develop/ui/views/graphics/opengl/draw
// number of coordinates per vertex in this array
const val COORDS_PER_VERTEX = 3
var squareCoords = floatArrayOf(
    -0.5f,  0.5f, 0.0f,      // top left
    -0.5f, -0.5f, 0.0f,      // bottom left
    0.5f, -0.5f, 0.0f,      // bottom right
    0.5f,  0.5f, 0.0f       // top right
)

class FullScreenSquare {
    private var mProgram: Int

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        varying vec2 fs_pos;

        void main() {
          vec4 transformedPos = uMVPMatrix * vPosition;
          gl_Position = transformedPos;
          fs_pos = vPosition.xy + vec2(0.5, 0.5); // transformedPos.xy;
        }
    """

    // TODO anything interesting for fractals will be here!
    private val fragmentShaderCode = """
        precision mediump float;
        varying vec2 fs_pos;
        uniform vec4 vColor;
        uniform sampler2D fractalGradient;
   
        void main() {
          gl_FragColor = vec4(texture2D(fractalGradient, vec2(fs_pos.x, 0)).xyz, 1.0); // , abs(fs_pos.x), 1.0);
        }
    """

    private var fractalGradientTexture: Int = 0

    init {
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0)

        if (linkStatus[0] == 0) {
            GLES20.glDeleteProgram(mProgram)
            var logInfo = "Linking of program failed. ${GLES20.glGetProgramInfoLog(mProgram)}";
            throw Exception(logInfo)
        }

        // Create gradient texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        val generatedTexture = IntArray(1)
        GLES20.glGenTextures(1, generatedTexture, 0)
        fractalGradientTexture = generatedTexture[0]

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fractalGradientTexture)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

        // Greyscale test (for now)
        var gradientData = mutableListOf<Byte>()
        for (i in 0 until 256) {
            gradientData.add(i.toUByte().toByte())
            gradientData.add(i.toUByte().toByte())
            gradientData.add(i.toUByte().toByte())
        }

        val gradientBuffer: ByteBuffer =
            // (# of colors * RGB * 1 bytes per byte
            ByteBuffer.allocateDirect(256 * 3 * 1).run {
                order(ByteOrder.nativeOrder())
                .apply {
                    put(gradientData.toByteArray())
                    position(0)
                }
            }

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB,
            256, 1,
            0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, gradientBuffer)
    }

    private val drawOrder = shortArrayOf(0, 1, 2, 0, 2, 3) // order to draw vertices

    // initialize vertex byte buffer for shape coordinates
    private val vertexBuffer: FloatBuffer =
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer.allocateDirect(squareCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(squareCoords)
                position(0)
            }
        }

    // initialize byte buffer for the draw list
    private val drawListBuffer: ShortBuffer =
        // (# of coordinate values * 2 bytes per short)
        ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }

    fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    private var positionHandle: Int = 0
    private var mColorHandle: Int = 0
    private var vPMatrixHandle: Int = 0
    private var fractalGradientLoc: Int = 0

    private val vertexCount: Int = squareCoords.size / COORDS_PER_VERTEX
    private val vertexStride: Int = COORDS_PER_VERTEX * 4 // 4 bytes per vertex

    fun draw(mvpMatrix: FloatArray) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram)

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition").also {

            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(it)

            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(
                it,
                COORDS_PER_VERTEX,
                GLES20.GL_FLOAT,
                false,
                vertexStride,
                vertexBuffer
            )

            // get handle to fragment shader's vColor member
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor").also { colorHandle ->
                val color = floatArrayOf(1f, 1f, 0f, 1.0f)

                // Set color for drawing the triangle
                GLES20.glUniform4fv(colorHandle, 1, color, 0)
            }

            fractalGradientLoc = GLES20.glGetUniformLocation(mProgram, "fractalGradient").also { gradientHandle ->
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fractalGradientTexture)
                GLES20.glUniform1i(gradientHandle, 0)
            }

            vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix").also { matrixHandle ->
                // Pass the projection and view transformation to the shader
                GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mvpMatrix, 0)
            }

            // Draw the triangle
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.size,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

            // Disable vertex array
            GLES20.glDisableVertexAttribArray(it)
        }
    }
}