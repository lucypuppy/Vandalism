#version 330 core

in vec2 FragCoord;
in vec2 RlTexCoord;
in vec2 AbsTexCoord;
flat in vec2 RlPixelSize;

out vec4 FragColor;

uniform sampler2D tex;
uniform vec2 u_Resolution;
uniform vec2 u_ScaleFactor;
uniform float u_Time;
uniform float u_Opacity;

float rand(vec2 n) {
    return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

float noise(vec2 p){
    vec2 ip = floor(p);
    vec2 u = fract(p);
    u = u*u*(3.0-2.0*u);

    float res = mix(
        mix(rand(ip),rand(ip+vec2(1.0,0.0)),u.x),
        mix(rand(ip+vec2(0.0,1.0)),rand(ip+vec2(1.0,1.0)),u.x),u.y);
    return res*res;
}

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    vec4 textureColor = texture(tex, RlTexCoord);
    if(textureColor.a == 0.0) {
        discard;
        return;
    }

    float chromaScale = 1.5;
    float xx = FragCoord.x / u_Resolution.x * chromaScale;
    float yy = FragCoord.y / u_Resolution.y * chromaScale;
    float timedX = sin(u_Time / 40.0) * 20.0;
    float timedY = cos(u_Time / 40.0) * 20.0;
    vec3 neger = hsv2rgb(vec3(noise(vec2(xx + timedX, yy + timedY)), 1.0, 1.0));
    FragColor = vec4(neger, textureColor.a * u_Opacity);
}