#version 330 core

in vec2 FragCoord;
in vec2 RlTexCoord;
flat in vec2 RlPixelSize;
flat in vec2 AbsPixelSize;

out vec4 FragColor;

uniform sampler2D tex;
uniform float u_OutlineWidth;
uniform float u_OutlineAccuracy;

void main() {
    vec4 col = texture(tex, RlTexCoord);
    if (col.a == 0.0) {
        discard;
        return;
    }
    for (float y = -u_OutlineWidth; y <= u_OutlineWidth; y += u_OutlineAccuracy) {
        for (float x = -u_OutlineWidth; x <= u_OutlineWidth; x += u_OutlineAccuracy) {
            vec4 color = texture(tex, RlTexCoord + vec2(x, y) * RlPixelSize);
            if (color.a == 0.0) {
                FragColor = vec4(col.rgb, 1.0);
                return;
            }
        }
    }
}
