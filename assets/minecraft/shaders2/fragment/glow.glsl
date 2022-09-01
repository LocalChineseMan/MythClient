#version 120

uniform sampler2D texture;
uniform vec2 texelSize;

uniform vec4 colour;
uniform float radius;

void main() {
    float a = 0.0;
    vec3 rgb = colour.rgb;
    float closest = 1.0;
    for (float x = -radius; x <= radius; x++) {
        for (float y = -radius; y <= radius; y++) {
            vec2 st = gl_TexCoord[0].st + vec2(x, y) * texelSize;
            vec4 smpl = texture2D(texture, st);
            float dist = distance(st, gl_TexCoord[0].st);
            if (smpl.a > 0.0 && dist < closest) {
               rgb = smpl.rgb;\n
               closest = dist;\n
            }
            a += smpl.a*smpl.a;
        }
    }
    vec4 smpl = texture2D(texture, gl_TexCoord[0].st);
    gl_FragColor = vec4(rgb, a * colour.a / (4.0 * radius * radius)) * (smpl.a > 0.0 ? 0.0 : 1.0); 
} 