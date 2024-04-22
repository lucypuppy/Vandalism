#version 330 core

in vec2 FragCoord;
in vec2 RlTexCoord;
in vec2 AbsTexCoord;
flat in vec2 RlPixelSize;

out vec4 FragColor;

uniform sampler2D tex;

void main() {
    FragColor = texture(tex, RlTexCoord);
}