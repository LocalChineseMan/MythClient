precision highp float;

uniform float time;
uniform vec2 resolution;

void main(void) {
    vec2 position = (gl_FragCoord.xy / resolution.xy) + vec2(0.12, 0.2);

    float progress = (position.x * (sin(time / 1.0) + 1.0) / 1.0 + position.y * (cos(time / 2.0) + 2.0) / 3.5) * 2.0 + (cos(time) * 4.0 + 1.0) / 4.0;

    progress /= sqrt(position.x * position.y);

    progress = sin(progress) + 1.0;
    progress *= 1.2;

    vec3 col1 = vec3(1.0, 0.733333333333333, 0.733333333333333);
    vec3 col2 = vec3(0.662745098039216, 0.945098039215686, 0.874509803921569);
    vec3 col3 = vec3(0.988235294117647, 0.647058823529412, 0.945098039215686);
    vec3 col4 = vec3(0.709803921568627, 1.0, 1.0);

    vec3 a = col1 + (col2 - col1) * progress;
    vec3 b = a + (col3 - a) * progress / 2.0;
    vec3 c = b + (col4 - b) * progress / 4.0;


    gl_FragColor = vec4(c, 1.0);
