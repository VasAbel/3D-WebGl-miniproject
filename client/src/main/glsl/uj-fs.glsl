#version 300 es

precision highp float;

// Ray direction from the vertex shader
in vec3 rayDir;

// Environment cube map texture
uniform struct { samplerCube envTexture; } material;


// Output color of the pixel
out vec4 fragmentColor;

void main(void) {
  // Use the ray direction to sample the cube map texture
  fragmentColor = texture(material.envTexture, rayDir);
}