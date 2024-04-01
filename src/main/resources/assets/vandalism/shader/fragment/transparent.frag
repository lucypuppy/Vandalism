#version 150 core

uniform sampler2D inputTexture;
uniform sampler2D outputTexture;
uniform vec2 resolution;
out vec4 fragColor;

void main() {
    // Honestly this is simple. If the alpha is over 0 it renders it, else it discards it and says fuck you. - Lucy
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    if (texture(inputTexture, uv).a > 0.0) {
        fragColor = texture(outputTexture, uv);
    } else {
        discard;
    }
}
