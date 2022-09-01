#extension GL_OES_standard_derivatives : enable

precision highp float;

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

void main( void ) {
	vec2 uv = gl_FragCoord.xy / resolution.xy;
	gl_FragColor = vec4(smoothstep(vec3(0.0, 1.0, 0.0), vec3(1.0, 0.0, 0.0), uv.xxx), 1);
