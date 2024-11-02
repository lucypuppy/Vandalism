//From shadertoy.com
//Created by cmzw in 2022-09-16
//Imported to glslsandbox.com by Shad0wolf0


#extension GL_OES_standard_derivatives : enable

precision highp float;

uniform float u_Time;
uniform vec2 mouse;
uniform vec2 u_WindowSize;

float iTime = u_Time;
vec2 iResolution = u_WindowSize;

#define AA 2

float hash12(vec2 p)
{
	vec3 p3  = fract(vec3(p.xyx) * .1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

vec3 aces_tonemap(vec3 color){
	mat3 m1 = mat3(
        0.59719, 0.07600, 0.02840,
        0.35458, 0.90834, 0.13383,
        0.04823, 0.01566, 0.83777
	);
	mat3 m2 = mat3(
        1.60475, -0.10208, -0.00327,
        -0.53108,  1.10813, -0.07276,
        -0.07367, -0.00605,  1.07602
	);
	vec3 v = m1 * color;
	vec3 a = v * (v + 0.0245786) - 0.000090537;
	vec3 b = v * (0.983729 * v + 0.4329510) + 0.238081;
	return pow(clamp(m2 * (a / b), 0.0, 1.0), vec3(1.0 / 2.2));
}

void mainImage( out vec4 O, in vec2 I )
{
    vec3 a = vec3(0); vec3 z = vec3(1); vec3 tot = a;

    for( int m=0; m<AA; m++)
    for( int n=0; n<AA; n++)
    {

        vec2 o = vec2(float(m),float(n)) / float(AA) - .5;
        vec2 p = (2.*I+o - iResolution.xy) / iResolution.y;

        float np = normalize(p).x;
        p *= length(p)*.2+.8;
        vec2 c = abs(p);

        float s = 9.;
        float t = iTime*s*1.;
        float f = max(c.x,c.y);
        float i = s/f;

        vec2 it = vec2(i+t,0);
        vec2 piyx = floor(p*i+it.yx);

        vec3 x = vec3(hash12(floor(p*i + it.xy)));
        vec3 y = vec3(hash12(piyx+.1));

        float g = hash12(piyx+.2);
        float h = hash12(piyx+.3);

        float d = step(abs(np),.68);
        float anp = abs(np);
        float e0 = step(abs(anp-.5),.08);
        float e1 = step(abs(anp-.5),.03);
        float e2 = step(abs(anp-.55),.04);

        if (p.y < 0.)
        {
            y = mix(y,vec3(.7,-.05,-.5)-g*.5, step(sin(it.x+1.5),-.9)*d);
            y = mix(y,vec3(.9,.2,-.5)+h*.5,  step(sin(it.x+.45),-.8)*d);
            y -= e0*.4;
            y = mix(y,vec3(-.2)*h*2.,e1);
            y = mix(y,vec3(3)-h*1.1,e2);
        }

        vec3 col = c.x > c.y ? x : y;

        col = .3+col*.25;

        float ad = abs(-dot(c,vec2(-1,1)));
        col = mix(col, a, smoothstep(.9,0.,ad)*.6);
        col = mix(col, a, smoothstep(.2,0.,ad)*.25);
        col = mix(col, a, smoothstep(.03,0.,ad)*.1);
        col = mix(col, a, smoothstep(.3,1.5,1.-f));

        vec3 yy = vec3(.78,.57,.4)*4.5*smoothstep(5.,-.4,length(p));

        col *= mix(col, yy, smoothstep(.2,2.5,length(p)*1.4));
        col = mix(col, a, smoothstep(.3,3.5,length(p)));

        tot += col;
    }
    tot /= float(AA*AA);

    O = vec4(aces_tonemap(tot),0);
}
void main(void)
{
    mainImage(gl_FragColor, gl_FragCoord.xy);
    gl_FragColor.a = 1.;
}