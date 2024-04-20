#version 330 core

in vec2 v_TexCoord;
in vec4 v_Color;

out vec4 FragColor;

uniform sampler2D texture0;

void main() {
    FragColor = texture(texture0, v_TexCoord) * v_Color;
    if (FragColor.a == 0.0) discard;
}
