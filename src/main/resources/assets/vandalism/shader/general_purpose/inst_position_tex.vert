#version 330 core

in vec3 a_Position;
in vec2 a_TexCoord;
in vec3 a_InstPosition; // per-instance

out vec2 v_TexCoord;

uniform mat4 u_ProjectionMatrix;
uniform mat4 u_ModelViewMatrix;
uniform mat4 u_TransformMatrix;

void main() {
    gl_Position = u_ProjectionMatrix * u_ModelViewMatrix * u_TransformMatrix * vec4(a_Position + a_InstPosition, 1.0);
    v_TexCoord = a_TexCoord;
}
