#version 330 core

// Shader from: https://www.shadertoy.com/view/3td3W8

in vec2 FragCoord;
in vec2 RlTexCoord;
in vec2 AbsTexCoord;
flat in vec2 RlPixelSize;

out vec4 FragColor;

uniform sampler2D u_Mask;
uniform sampler2D iChannel0;
uniform vec2 u_Resolution;
uniform vec2 u_ScaleFactor;
uniform int u_Pass;
uniform float u_BlurRadius;

void mainImage0( out vec4 fragColor, in vec2 fragCoord, in vec2 iResolution )
{
    vec2 uv = RlTexCoord;
    vec2 halfpixel = 0.5 / (iResolution.xy / 2.0);
    float offset = u_BlurRadius;

    vec4 sum = texture(iChannel0, uv) * 4.0;
    sum += texture(iChannel0, uv - halfpixel.xy * offset);
    sum += texture(iChannel0, uv + halfpixel.xy * offset);
    sum += texture(iChannel0, uv + vec2(halfpixel.x, -halfpixel.y) * offset);
    sum += texture(iChannel0, uv - vec2(halfpixel.x, -halfpixel.y) * offset);

    fragColor = sum / 8.0;
}

void mainImage1( out vec4 fragColor, in vec2 fragCoord, in vec2 iResolution )
{
    vec2 uv = RlTexCoord;
    vec2 halfpixel = 0.5 / (iResolution.xy / 2.0);
    float offset = u_BlurRadius;

    vec4 sum = texture(iChannel0, uv) * 4.0;
    sum += texture(iChannel0, uv - halfpixel.xy * offset);
    sum += texture(iChannel0, uv + halfpixel.xy * offset);
    sum += texture(iChannel0, uv + vec2(halfpixel.x, -halfpixel.y) * offset);
    sum += texture(iChannel0, uv - vec2(halfpixel.x, -halfpixel.y) * offset);

    fragColor = sum / 8.0;
}

void mainImage2( out vec4 fragColor, in vec2 fragCoord, in vec2 iResolution )
{
    vec2 uv = RlTexCoord;
    vec2 halfpixel = 0.5 / (iResolution.xy * 2.0);
    float offset = u_BlurRadius;

    vec4 sum = texture(iChannel0, uv +vec2(-halfpixel.x * 2.0, 0.0) * offset);

    sum += texture(iChannel0, uv + vec2(-halfpixel.x, halfpixel.y) * offset) * 2.0;
    sum += texture(iChannel0, uv + vec2(0.0, halfpixel.y * 2.0) * offset);
    sum += texture(iChannel0, uv + vec2(halfpixel.x, halfpixel.y) * offset) * 2.0;
    sum += texture(iChannel0, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);
    sum += texture(iChannel0, uv + vec2(halfpixel.x, -halfpixel.y) * offset) * 2.0;
    sum += texture(iChannel0, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);
    sum += texture(iChannel0, uv + vec2(-halfpixel.x, -halfpixel.y) * offset) * 2.0;

    fragColor = sum / 12.0;
}

void mainImage3( out vec4 fragColor, in vec2 fragCoord, in vec2 iResolution )
{
    vec2 uv = RlTexCoord;
    vec2 halfpixel = 0.5 / (iResolution.xy * 2.0);
    float offset = u_BlurRadius;

    vec4 sum = texture(iChannel0, uv +vec2(-halfpixel.x * 2.0, 0.0) * offset);

    sum += texture(iChannel0, uv + vec2(-halfpixel.x, halfpixel.y) * offset) * 2.0;
    sum += texture(iChannel0, uv + vec2(0.0, halfpixel.y * 2.0) * offset);
    sum += texture(iChannel0, uv + vec2(halfpixel.x, halfpixel.y) * offset) * 2.0;
    sum += texture(iChannel0, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);
    sum += texture(iChannel0, uv + vec2(halfpixel.x, -halfpixel.y) * offset) * 2.0;
    sum += texture(iChannel0, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);
    sum += texture(iChannel0, uv + vec2(-halfpixel.x, -halfpixel.y) * offset) * 2.0;

    fragColor = sum / 12.0;
}

void main() {
    vec4 textureColor = texture(u_Mask, RlTexCoord);
    if (textureColor.a == 0.0) {
        discard;
        return;
    }
    switch (u_Pass) {
        case 0:
            mainImage0(FragColor, FragCoord, u_Resolution);
            break;
        case 1:
            mainImage1(FragColor, FragCoord, u_Resolution);
            break;
        case 2:
            mainImage2(FragColor, FragCoord, u_Resolution);
            break;
        case 3:
            mainImage3(FragColor, FragCoord, u_Resolution);
            break;
    }
}
