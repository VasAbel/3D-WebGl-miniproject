import kotlin.math.sin
import kotlin.math.cos
import vision.gears.webglmath.Vec3
import vision.gears.webglmath.Vec4

class Car(vararg meshes: Mesh) : GameObject(*meshes) {
    val wheels = mutableListOf<Wheel>()
    private val movementSpeed = 50f  // Adjust as necessary
    private val rotationSpeed = 3f  // Adjust as necessary
    private val wheelRotationFactor = 80f
    private val wheelYaw = 10f
    val collisionRadius = 25f
    fun addWheel(wheel: Wheel) {
        wheels.add(wheel)
        wheel.parent = this        
    }

    fun checkCollisions(spheres: List<Sphere>): Boolean {
        for (sphere in spheres) {
            val dx = this.position.x - sphere.position.x
            val dy = this.position.y - sphere.position.y
            val dz = this.position.z - sphere.position.z
            val distanceSquared = dx * dx + dy * dy + dz * dz
            val radiiSum = this.collisionRadius + sphere.collisionRadius
            val radiiSumSquared = radiiSum * radiiSum

            if (distanceSquared <= radiiSumSquared) {
                // Collision detected

                // Calculate the normalized direction from the car to the sphere
                val collisionDirection = Vec3(-dx, 0f, -dz).normalize() // Assume y component is 0 for a flat ground plane
            
                // Call startRolling on the sphere
                sphere.startRolling(collisionDirection)

                return true
            }
        }
        return false
    }

    init {
        needsShadow = true
        move = object : Motion() {
            override fun invoke(dt: Float, t: Float, keysPressed: Set<String>, gameObjects: List<GameObject>): Boolean {
                for(wheel in wheels)wheel.yaw = 0f
                if (keysPressed.contains("w")) {
                    position.x += sin(yaw) * movementSpeed * dt
                    position.z += cos(yaw) * movementSpeed * dt
                    if (!keysPressed.contains("a") && !keysPressed.contains("d"))
                        for(wheel in wheels)
                            wheel.xroll -= wheelRotationFactor * dt
                    else{wheels[0].xroll -= wheelRotationFactor * dt
                            wheels[2].xroll -= wheelRotationFactor * dt}        
                }
                if (keysPressed.contains("s")) {
                    position.x -= sin(yaw) * movementSpeed * dt
                    position.z -= cos(yaw) * movementSpeed * dt
                    if (!keysPressed.contains("a") && !keysPressed.contains("d"))
                        for(wheel in wheels)
                            wheel.xroll += wheelRotationFactor * dt
                    else{wheels[0].xroll += wheelRotationFactor * dt
                            wheels[2].xroll += wheelRotationFactor * dt} 
                }
                if (keysPressed.contains("a")) {
                    yaw += rotationSpeed * dt
                    wheels[1].xroll = 0f
                    wheels[3].xroll = 0f
                    wheels[1].yaw = wheelYaw
                    wheels[3].yaw = wheelYaw
                }
               

                if (keysPressed.contains("d")) {
                    yaw -= rotationSpeed * dt
                    wheels[1].xroll = 0f
                    wheels[3].xroll = 0f
                    wheels[1].yaw = -wheelYaw
                    wheels[3].yaw = -wheelYaw
                }
                
               
                return true
            }
        }
    }
}