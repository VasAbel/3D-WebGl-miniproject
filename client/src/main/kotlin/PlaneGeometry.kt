import org.khronos.webgl.WebGLRenderingContext as GL
import org.khronos.webgl.Float32Array
import org.khronos.webgl.Uint16Array
import vision.gears.webglmath.Geometry

class PlaneGeometry(val gl : WebGL2RenderingContext) : Geometry() {
	
	val vertexBuffer = gl.createBuffer()
	init {
	    gl.bindBuffer(GL.ARRAY_BUFFER, vertexBuffer)
	    gl.bufferData(GL.ARRAY_BUFFER,
	        Float32Array(arrayOf<Float>(
	            // Origin
	            0f, 0f, 0f, 1f,
	            // Ideal points along the x-axis and z-axis
	            1f, 0f, 0f, 0f,  // +X direction
	            0f, 0f, 1f, 0f,  // +Z direction
	            -1f, 0f, 0f, 0f  // -X direction
	        )),
	        GL.STATIC_DRAW)
	}

  val vertexTexCoordBuffer = gl.createBuffer()
	init {
	    gl.bindBuffer(GL.ARRAY_BUFFER, vertexTexCoordBuffer)
	    gl.bufferData(GL.ARRAY_BUFFER,
	        Float32Array(arrayOf<Float>(
	            // Texture coordinates for origin
	            0f, 0f, 0f, 1f,
	            // Texture coordinates for ideal points
	            1f, 0f, 0f, 0f,  // +X direction
	            0f, 1f, 0f, 0f,  // +Z direction
	            -1f, 0f, 0f, 0f  // -X direction
	        )),
	        GL.STATIC_DRAW)
	}

	val vertexNormalBuffer = gl.createBuffer()
	init {
	    gl.bindBuffer(GL.ARRAY_BUFFER, vertexNormalBuffer)
	    gl.bufferData(GL.ARRAY_BUFFER,
	        Float32Array(arrayOf<Float>(
	            // Normal vectors for each vertex (assuming a horizontal plane)
	            0f, 1f, 0f, // Upward normal for origin
	            0f, 1f, 0f, // Upward normal for ideal point 1
	            0f, 1f, 0f, // Upward normal for ideal point 2
	            0f, 1f, 0f  // Upward normal for ideal point 3
	        )),
	        GL.STATIC_DRAW)
	}

  val indexBuffer = gl.createBuffer()
	init {
	    gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indexBuffer)
	    gl.bufferData(GL.ELEMENT_ARRAY_BUFFER,
	        Uint16Array(arrayOf<Short>(
	            0, 1, 2,  // First triangle
	            0, 2, 3,   // Second triangle
	            0, 3, 1
	        )),
	        GL.STATIC_DRAW)
	}

	val inputLayout = gl.createVertexArray() 
    init {
        gl.bindVertexArray(inputLayout)

        gl.bindBuffer(GL.ARRAY_BUFFER, vertexBuffer)
        gl.enableVertexAttribArray(0)
        gl.vertexAttribPointer(
            0, // Index of the vertex attribute
            4, // The number of components per vertex attribute (x, y, z, w)
            GL.FLOAT, // The data type of each component in the array
            false, // Whether integer data values should be normalized
            0, // Byte offset between consecutive attributes
            0 // Byte offset of the first component in the data store
        )

        gl.bindBuffer(GL.ARRAY_BUFFER, vertexTexCoordBuffer)
		    gl.enableVertexAttribArray(1) // Attribute location 1 for texture coordinates
		    gl.vertexAttribPointer(1,
		      4, // 4 components per texture coordinate attribute (s, t, p, q)
		      GL.FLOAT,
		      false,
		      0, // stride (0 = move forward size * sizeof(type) each iteration to get the next texture coord)
		      0  // offset (0 = start at the beginning of the buffer)
		    )

		    gl.bindBuffer(GL.ARRAY_BUFFER, vertexNormalBuffer)
		    gl.enableVertexAttribArray(2) // Use location 2 for normals
		    gl.vertexAttribPointer(2,
		        3, GL.FLOAT, //< three pieces of float
		        false,
		        0, //< tightly packed
		        0 //< data starts at array start
		    )

        gl.bindVertexArray(null)
    }

	override fun draw() {
    gl.bindVertexArray(inputLayout)
    gl.bindBuffer(GL.ELEMENT_ARRAY_BUFFER, indexBuffer)
    gl.drawElements(GL.TRIANGLES, 9, GL.UNSIGNED_SHORT, 0) // 9 indices for 3 triangles
}
}