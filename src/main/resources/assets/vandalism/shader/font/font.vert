#version 330 core

in vec3 a_Position;
in vec2 a_TexCoord;
in vec4 a_Color;

out vec2 v_TexCoord;
out vec4 v_Color;

uniform mat4 u_ModelViewMatrix;
uniform mat4 u_ProjectionMatrix;
uniform mat4 u_TransformMatrix;

void main() {
    gl_Position = u_ProjectionMatrix * u_ModelViewMatrix * u_TransformMatrix * vec4(a_Position, 1.0);
    v_TexCoord = a_TexCoord;
    v_Color = a_Color;
}
