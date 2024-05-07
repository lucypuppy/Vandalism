#version 330 core

in vec2 FragCoord;
in vec2 RlTexCoord;
in vec2 AbsTexCoord;
flat in vec2 RlPixelSize;

out vec4 FragColor;

uniform sampler2D tex;
uniform vec2 u_Resolution;
uniform vec2 u_ScaleFactor;
uniform float u_Time;
uniform vec4 u_FillColor;

void main() {
    vec4 texColor = texture(tex, RlTexCoord);
    if (texColor.a == 0.0) {
        discard;
        return;
    }
    FragColor = vec4(u_FillColor.rgb, u_FillColor.a * texColor.a);
}
