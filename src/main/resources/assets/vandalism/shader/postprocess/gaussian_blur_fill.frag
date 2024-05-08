#version 330 core

// Blur from https://www.shadertoy.com/view/Xltfzj

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
uniform vec3 u_BlurValues; // directions, quality, radius

void main() {
    if (texture(mask, RlTexCoord).a == 0.0) {
        discard;
        return;
    }

    float Pi = 6.28318530718; // Pi*2

    // GAUSSIAN BLUR SETTINGS {{{
    float Directions = u_BlurValues.x; // BLUR DIRECTIONS (Default 16.0 - More is better but slower)
    float Quality = u_BlurValues.y; // BLUR QUALITY (Default 4.0 - More is better but slower)
    float Size = u_BlurValues.z; // BLUR SIZE (Radius)
    // GAUSSIAN BLUR SETTINGS }}}

    //vec2 Radius = Size/iResolution.xy;
    vec2 Radius = Size/u_Resolution;

    // Normalized pixel coordinates (from 0 to 1)
    //vec2 uv = FragCoord / iResolution.xy;
    vec2 uv = RlTexCoord; // we use our own texture coordinates
    // Pixel colour
    vec4 Color = texture(tex, uv);

    // Blur calculations
    for (float d = 0.0; d < Pi; d += Pi / Directions) {
        for (float i = 1.0 / Quality; i <= 1.0; i += 1.0 / Quality) {
            Color += texture(tex, uv + vec2(cos(d), sin(d)) * Radius * i);
        }
    }

    // Output to screen
    Color /= Quality * Directions - 15.0;
    FragColor = Color;
}