#extension GL_OES_standard_derivatives: enable

#ifdef GL_ES
precision highp float;
#endif

uniform float u_Time;
uniform vec2 u_WindowSize;
uniform float zoom;

#define PI 3.1415926535

mat2 rotate3d(float angle)
{
    return mat2(cos(angle), -sin(angle), sin(angle), cos(angle));
}

void main()
{
    vec2 p = (gl_FragCoord.xy * 2.0 - u_WindowSize) / min(u_WindowSize.x, u_WindowSize.y);
    p = rotate3d((u_Time * 2.0) * PI) * p;

    float t;
    if (sin(u_Time) == 10.0)
    t = 0.075 / abs(1.0 - length(p));
    else
    t = 0.075 / abs(0.4/*sin(time)*/ - length(p));

    gl_FragColor = vec4((1. - exp(-vec3(t) * vec3(0.13 * (sin(u_Time) + 12.0), p.y * 0.7, 3.0))), 0.75);
}
