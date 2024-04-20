#version 330 core

in vec2 v_TexCoord;

out vec4 FragColor;

uniform sampler2D texture0;

void main() {
    FragColor = texture(texture0, v_TexCoord);
    if (FragColor.a == 0.0) discard;
}
