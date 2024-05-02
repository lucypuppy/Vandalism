#version 330 core

in vec2 v_TexCoord;
in vec4 v_Color;

out vec4 FragColor;

uniform sampler2D texture0;

void main() {
    float alpha = texture(texture0, v_TexCoord).r;
    if (alpha == 0.0) {
        discard;
        return;
    }
    FragColor = vec4(1.0, 1.0, 1.0, alpha) * v_Color;
    if (FragColor.a == 0.0) discard;
}