import kotlin.math.sin
import kotlin.math.cos
import vision.gears.webglmath.Vec3
import vision.gears.webglmath.Vec4

class Wheel(vararg meshes: Mesh, initLocalPos: Vec3) : GameObject(*meshes) {
    
    val localPos = initLocalPos

    init {
        needsShadow = true
        position.set(localPos + (parent?.position ?: Vec3(0f,0f, 0f)))
        move = object : Motion() {
            override fun invoke(dt: Float, t: Float, keysPressed: Set<String>, gameObjects: List<GameObject>): Boolean {
                return true
            }
        }
    }
}
