#ifdef GL_ES
precision mediump float;
#endif

uniform float u_Time;
uniform vec2 mouse;
uniform vec2 u_WindowSize;

void main( void ) {

	vec4 O = vec4(0.0);
	float maxCoord = max(u_WindowSize.x, u_WindowSize.y);
	float ratio = u_WindowSize.y / u_WindowSize.x;

	vec2 u = gl_FragCoord.xy/maxCoord - vec2(0.5, ratio * 0.5);
	u *= 4.0;
	float T = u_Time;
	for (float i = 0.; i < 100.0; i += .5) {
	O += .001/abs(length(u + vec2(cos(i/4. + T), sin(i*.45 + T)) * sin(T*.5+i*.35)) - sin(i+T*.5) / 60. - .01) * (1. + cos(i*.7 + T + length(u)*6. + vec4(0,1,2,0)));
	}

	gl_FragColor = O;

	return;

	vec2 position = ( gl_FragCoord.xy / u_WindowSize.xy ) + mouse / 74.8;

	float color = 0.0;
	color += sin( position.x * cos(u_Time / 15.0 ) * 80.0 ) + cos(position.y * cos(u_Time / 15.0 ) * 10.0 );
	color += sin( position.y * sin(u_Time / 25.0 ) * 40.0 ) + cos(position.x * sin(u_Time / 25.0 ) * 40.0 );
	color += sin( position.x * sin(u_Time / 5.0 ) * 10.0 ) + sin(position.y * sin(u_Time / 15.0 ) * 80.0 );
	color *= sin(u_Time / 10.0 ) * 0.5;

	gl_FragColor = vec4( vec3( color, color * 0.5, sin( color + u_Time / 0.5 ) * 0.75 ), 1.0 );

}