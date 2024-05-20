import org.w3c.dom.HTMLCanvasElement
import org.khronos.webgl.WebGLRenderingContext as GL //# GL# we need this for the constants declared ˙HUN˙ a constansok miatt kell
import kotlin.js.Date
import vision.gears.webglmath.UniformProvider
import vision.gears.webglmath.Vec1
import vision.gears.webglmath.Vec2
import vision.gears.webglmath.Vec3
import vision.gears.webglmath.Vec4
import vision.gears.webglmath.Mat4
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.PI

class Scene (
  val gl : WebGL2RenderingContext)  : UniformProvider("scene") {

  val vsTextured = Shader(gl, GL.VERTEX_SHADER, "textured-vs.glsl")
  val fsTextured = Shader(gl, GL.FRAGMENT_SHADER, "textured-fs.glsl")
  val texturedProgram = Program(gl, vsTextured, fsTextured)

  val vsNew = Shader(gl, GL.VERTEX_SHADER, "uj-vs.glsl")
  val fsNew = Shader(gl, GL.FRAGMENT_SHADER, "uj-fs.glsl")
  val newProgram = Program(gl, vsNew, fsNew)
  val lightDir = Vec4(1f, -1f, 1f, 1f)
  val shadowMatrix by Mat4(
    1.0f,  0f,  0.0f,  0.0f,
    lightDir.x/lightDir.y,  0.0f,  lightDir.z/lightDir.y,  0.0f,
    0.0f,  0f,  1.0f,  0.0f,
    5.0f, -4.99f, 0.0f,  1.0f)
  val vsShadow = Shader(gl, GL.VERTEX_SHADER, "shadow-vs.glsl")
  val fsShadow = Shader(gl, GL.FRAGMENT_SHADER, "shadow-fs.glsl")
  val shadowProgram = Program(gl, vsShadow, fsShadow)
  val shadowMaterial = Material(shadowProgram)
  
  val texturedQuadGeometry = TexturedQuadGeometry(gl)
  val planeGeometry = PlaneGeometry(gl)

  val jsonLoader = JsonLoader()
  val chevyMesh = jsonLoader.loadMeshes(gl,
  "media/json/chevy/chassis.json",
  Material(texturedProgram).apply{
    this["colorTexture"]?.set(
        Texture2D(gl, "media/json/chevy/chevy.png"))
  }
)

  val wheelMesh = jsonLoader.loadMeshes(gl,
  "media/json/chevy/wheel.json",
  Material(texturedProgram).apply{
    this["colorTexture"]?.set(
        Texture2D(gl, "media/json/chevy/chevy.png"))
  }
)
  
  val envTexture = TextureCube(gl, 
    "media/posx512.jpg",
    "media/negx512.jpg",
    "media/posy512.jpg",
    "media/negy512.jpg",
    "media/posz512.jpg",
    "media/negz512.jpg"
    )

  val backGroundMesh = Mesh(Material(newProgram).apply {
    this["envTexture"]?.set(envTexture)
  }, texturedQuadGeometry)

  val planeMesh = Mesh(Material(texturedProgram).apply{
    this["colorTexture"]?.set(
      Texture2D(gl, "media/map.png"))
    }, planeGeometry)

  val sphereMesh = jsonLoader.loadMeshes(gl,
      "media/json/sphere.json",
      Material(texturedProgram).apply{
        this["colorTexture"]?.set(
            Texture2D(gl, "media/json/tree.png"))
      }
    )
  

  val gameObjects = ArrayList<GameObject>()
  val car = Car(chevyMesh[0])
  val map = GameObject(planeMesh).apply{position.set(Vec3(0f, -5f, 0f))}
  val sphere = Sphere(sphereMesh[0]).apply{position.set(Vec3(20f, 0f, 40f)); scale.set(Vec3(6f, 6f, 6f))}
  val spheres = ArrayList<Sphere>()

  

  init {
    car.addWheel(Wheel(wheelMesh[0], initLocalPos=Vec3(-8f, -1.7f, -11f)).apply{scale.set(Vec3(0.8f, 1.1f, 1.1f))})
    car.addWheel(Wheel(wheelMesh[0], initLocalPos=Vec3(-8f, -1.7f, 14f)).apply{scale.set(Vec3(0.8f, 1.1f, 1.1f))})
    car.addWheel(Wheel(wheelMesh[0], initLocalPos=Vec3(8f, -1.7f, -11f)).apply{scale.set(Vec3(0.8f, 1.1f, 1.1f))})
    car.addWheel(Wheel(wheelMesh[0], initLocalPos=Vec3(8f, -1.7f, 14f)).apply{scale.set(Vec3(0.8f, 1.1f, 1.1f))})
    gameObjects += sphere
    gameObjects += car
    gameObjects += car.wheels
    gameObjects += map
    gameObjects += GameObject(backGroundMesh)
    spheres += sphere
  }

  
  val camera = PerspectiveCamera(*Program.all)

  fun resize(canvas : HTMLCanvasElement) {
    gl.viewport(0, 0, canvas.width, canvas.height)//#viewport# tell the rasterizer which part of the canvas to draw to ˙HUN˙ a raszterizáló ide rajzoljon
    camera.setAspectRatio(canvas.width.toFloat()/canvas.height)
  }

  val timeAtFirstFrame = Date().getTime()
  var timeAtLastFrame =  timeAtFirstFrame

  init{
    gl.enable(GL.BLEND)
    addComponentsAndGatherUniforms(*Program.all)
  }

  @Suppress("UNUSED_PARAMETER")
  fun update(keysPressed : Set<String>) {
    gl.enable(GL.DEPTH_TEST)
    val timeAtThisFrame = Date().getTime() 
    val dt = (timeAtThisFrame - timeAtLastFrame).toFloat() / 1000.0f
    val t = (timeAtThisFrame - timeAtFirstFrame).toFloat() / 1000.0f
    timeAtLastFrame = timeAtThisFrame

    camera.move(dt, keysPressed)
    
    gl.clearColor(0.3f, 0.0f, 0.3f, 1.0f)//## red, green, blue, alpha in [0, 1]
    gl.clearDepth(1.0f)//## will be useful in 3D ˙HUN˙ 3D-ben lesz hasznos
    gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)//#or# bitwise OR of flags

    
    gl.blendFunc(
      GL.SRC_ALPHA,
      GL.ONE_MINUS_SRC_ALPHA)
    

    gameObjects.forEach{ it.move(dt, t, keysPressed, gameObjects) }
    car.checkCollisions(spheres)
    gameObjects.forEach{ it.update() }
    gameObjects.forEach{ it.draw(this, camera); 
                          if(it.needsShadow){ // ground, background need no shadow
                            it.using(shadowMaterial).draw(this, camera);
                          }
    }
  }
}
