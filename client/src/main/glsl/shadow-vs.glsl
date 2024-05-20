#version 300 es

in vec4 vertexPosition;
in vec4 vertexTexCoord;

uniform struct {
  mat4 modelMatrix;
} gameObject;

uniform struct {
  mat4 viewProjMatrix; 
} camera;

uniform struct {
  mat4 shadowMatrix;
  float time;
} scene;

out vec4 texCoord;

void main(void) {
  vec4 shadowPos = vertexPosition * gameObject.modelMatrix;
  shadowPos = shadowPos * scene.shadowMatrix;

  gl_Position = shadowPos * camera.viewProjMatrix;

  texCoord = vertexTexCoord;
}