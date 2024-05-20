import vision.gears.webglmath.*
import kotlin.math.exp
import kotlin.math.PI
import kotlin.math.floor

open class GameObject(
  vararg val meshes : Mesh
   ) : UniformProvider("gameObject") {

  val position = Vec3()
  var roll = 0.0f
  val scale = Vec3(1.0f, 1.0f, 1.0f)
  var yaw = 0.0f
  var xroll = 0.0f
  var needsShadow : Boolean = false

  val modelMatrix by Mat4()

  var parent : GameObject? = null

  init { 
    addComponentsAndGatherUniforms(*meshes)
  }

  fun update() {
    modelMatrix.set().
      scale(scale).
      rotate(roll).
      rotate(yaw, Vec3(0f, 1f, 0f)).
      rotate(xroll, Vec3(1f, 0f, 0f)).
      translate(position)
    parent?.let{ parent -> 
      modelMatrix *= parent.modelMatrix
    }
  }

  open class Motion {
    open operator fun invoke(
        dt : Float = 0.016666f,
        t : Float = 0.0f,
        keysPressed : Set<String> = emptySet<String>(),
        gameObjects : List<GameObject> = emptyList<GameObject>()
        ) : Boolean {
      return true;
    }
  }
  var move = Motion()

}
