#version 330 core

in vec4 v_Color;

out vec4 FragColor;

void main() {
    FragColor = v_Color;
    if (FragColor.a == 0.0) discard;
}
