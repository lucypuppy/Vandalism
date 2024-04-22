#version 330 core

in vec2 FragCoord;
in vec2 RlTexCoord;
flat in vec2 RlPixelSize;
flat in vec2 AbsPixelSize;

out vec4 FragColor;

uniform sampler2D tex;
uniform float u_OutlineWidth;
uniform float u_OutlineAccuracy;
uniform float u_Exponent;

void main() {
    vec4 col = texture(tex, RlTexCoord);
    if (col.a != 0.0) {
        discard;
        return;
    }
    float maxDist = pow(u_OutlineWidth * u_OutlineWidth, u_Exponent);
    vec2 nearest = vec2(0.0, 0.0);
    vec4 nearestColor = vec4(0.0);
    float nearestDist = maxDist + 1.0;
    for (float y = -u_OutlineWidth; y <= u_OutlineWidth; y += u_OutlineAccuracy) {
        for (float x = -u_OutlineWidth; x <= u_OutlineWidth; x += u_OutlineAccuracy) {
            vec4 color = texture(tex, RlTexCoord + vec2(x, y) * RlPixelSize);
            if (color.a >= 0.01) {
                float dist = pow(x * x + y * y, u_Exponent);
                if (dist < nearestDist) {
                    nearest = vec2(x, y);
                    nearestColor = color;
                    nearestDist = dist;
                }
            }
        }
    }

    if (nearestColor.a < 0.01) {
        discard;
        return;
    }

    float percent = nearestDist / maxDist;
    FragColor = vec4(nearestColor.rgb, 1.0 - percent);
}
