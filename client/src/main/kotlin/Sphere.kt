import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.max
import vision.gears.webglmath.Vec3
import vision.gears.webglmath.Vec4

class Sphere(vararg meshes: Mesh) : GameObject(*meshes) {
    val collisionRadius = 3f
    var velocity = Vec3() // Current velocity of the sphere
    var deceleration = Vec3()
    val initialSpeed = 100f
    val rotationFactor = 40f

    fun startRolling(impactDirection: Vec3){
        velocity = impactDirection.normalize()
        // Set a fixed deceleration value that will be applied each frame to slow down the sphere
        deceleration = velocity.normalize()
    }

    init {
        needsShadow = true
        move = object : Motion() {
            override fun invoke(dt: Float, t: Float, keysPressed: Set<String>, gameObjects: List<GameObject>): Boolean {
                position += velocity * initialSpeed * dt
        
                // Reduce the speed
                if(velocity.length() > deceleration.length() * dt) {
                    velocity = velocity - deceleration * dt
                    roll += velocity.z * rotationFactor * dt
                    xroll += velocity.x * rotationFactor * dt
                } else {
                    velocity = Vec3(0f, 0f, 0f) // Stop the sphere if deceleration would reverse the direction
                }


                return true
            }
        }
    }
}
