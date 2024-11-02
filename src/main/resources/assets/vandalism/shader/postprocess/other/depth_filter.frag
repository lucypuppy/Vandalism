#version 330 core

in vec2 FragCoord;
in vec2 RlTexCoord;
in vec2 AbsTexCoord;
flat in vec2 RlPixelSize;

out vec4 FragColor;

uniform sampler2D tex;
uniform sampler2D depth_tex1;
uniform sampler2D depth_tex2;
uniform vec2 u_Resolution;
uniform vec2 u_ScaleFactor;
uniform float u_Time;

void main() {
    vec4 textureColor = texture(tex, RlTexCoord);
    if (textureColor.a == 0.0) {
        discard;
        return;
    }
    float depth1 = texture(depth_tex1, RlTexCoord).r;
    float depth2 = texture(depth_tex2, RlTexCoord).r;
    if (depth1 > depth2) {
        FragColor = textureColor;
        return;
    }
    discard;
}
