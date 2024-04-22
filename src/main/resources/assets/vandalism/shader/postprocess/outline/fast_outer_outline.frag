#version 330 core

in vec2 FragCoord;
in vec2 RlTexCoord;
flat in vec2 RlPixelSize;
flat in vec2 AbsPixelSize;

out vec4 FragColor;

uniform sampler2D mask;
uniform sampler2D tex;
uniform float u_OutlineWidth;
uniform float u_OutlineAccuracy;
uniform int u_Pass;

void main() {
    float maxDist = u_OutlineWidth * u_OutlineWidth;
    if (u_Pass == 0) {
        for (float y = -u_OutlineWidth; y <= u_OutlineWidth; y += u_OutlineAccuracy) {
            vec4 color = texture(tex, RlTexCoord + vec2(0.0, y) * RlPixelSize);
            if (color.a != 0.0) {
                FragColor = vec4(color.rgb, 1.0);
                return;
            }
        }
    } else {
        vec4 col = texture(mask, RlTexCoord);
        if (col.a != 0.0) {
            discard;
            return;
        }
        for (float x = -u_OutlineWidth; x <= u_OutlineWidth; x += u_OutlineAccuracy) {
            vec4 color = texture(tex, RlTexCoord + vec2(x, 0.0) * RlPixelSize);
            if (color.a != 0.0) {
                FragColor = vec4(color.rgb, 1.0);
                return;
            }
        }
    }
    discard;
}
