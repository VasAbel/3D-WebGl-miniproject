#version 300 es

// Full-screen quad positions in NDC are passed in directly, so no need for model/view/projection matrices
in vec4 vertexPosition;

// Pass the ray direction to the fragment shader
out vec3 rayDir;

// Uniform to transform NDC to world space ray direction
uniform struct{
  mat4 rayDirMatrix; 
} camera;

void main(void) {
  // Set the full-screen quad vertex position
  gl_Position = vec4(vertexPosition.xy, 0.99999, 1.0);

  // Transform the NDC coordinate to a world space ray using the ray direction matrix
  // Perspective division is not needed because we use a w of 1.0 for a direction vector
  rayDir = (gl_Position * camera.rayDirMatrix).xyz;
}