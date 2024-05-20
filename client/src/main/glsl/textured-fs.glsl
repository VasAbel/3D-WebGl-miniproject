#version 300 es

precision highp float;

in vec4 texCoord;

uniform struct {
  sampler2D colorTexture; 
} material;

out vec4 fragmentColor;

void main(void) {
  vec2 uv = texCoord.xy / texCoord.w;
  fragmentColor = texture(material.colorTexture, uv); 
}