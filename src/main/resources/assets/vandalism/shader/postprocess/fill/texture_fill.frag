#version 330 core

in vec2 FragCoord;
in vec2 RlTexCoord;
in vec2 AbsTexCoord;
flat in vec2 RlPixelSize;

out vec4 FragColor;

uniform sampler2D mask;
uniform sampler2D tex;
uniform vec2 u_Resolution;
uniform vec2 u_ScaleFactor;
uniform float u_Time;
uniform float u_Opacity;
uniform bool u_FlipY;

void main() {
    if (texture(mask, RlTexCoord).a == 0.0) {
        discard;
        return;
    }
    vec2 uv = RlTexCoord;
    if (u_FlipY) {
        uv.y = 1.0 - uv.y;
    }
    vec4 textureColor = texture(tex, uv);
    textureColor.a *= u_Opacity;
    FragColor = textureColor;
}