#version 120

uniform sampler2D inTexture;
uniform vec2 texelSize, direction;
uniform float radius;
uniform float weights[256];
uniform vec3 color;

#define offset texelSize * direction

void main() {
    // if (direction.y > 0 && texture2D(textureToCheck, gl_TexCoord[0].st).a != 0.0) discard;

    vec4 blurredColor = vec4(0.0);

    for (float f = -radius; f <= radius; f++) {
        blurredColor += texture2D(inTexture, gl_TexCoord[0].st + f * offset) * weights[int(abs(f))];
    }

    gl_FragColor = vec4(color.x, color.y, color.z, blurredColor.a);
}
