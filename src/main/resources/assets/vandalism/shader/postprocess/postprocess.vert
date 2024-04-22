#version 330 core

uniform vec4 u_ShaderBounds;
uniform vec2 u_Resolution;
uniform vec2 u_WindowSize;

in vec3 vi_Position;

out vec2 FragCoord;
out vec2 RlTexCoord;
flat out vec2 RlPixelSize;
flat out vec2 AbsPixelSize;

vec2 to_pixel_pos() {
    vec2 pos = (vi_Position.xy * 0.5 + 0.5) * u_WindowSize;
    pos.y = u_WindowSize.y - pos.y;
    pos -= u_ShaderBounds.xy;
    return pos;
}

void main() {
    FragCoord = to_pixel_pos();
    RlPixelSize = 1.0 / u_Resolution;
    AbsPixelSize = 1.0 / u_WindowSize;
    RlTexCoord = FragCoord * RlPixelSize;
    RlTexCoord.y = 1.0 - RlTexCoord.y;
    gl_Position = vec4(vi_Position, 1.0);
}
