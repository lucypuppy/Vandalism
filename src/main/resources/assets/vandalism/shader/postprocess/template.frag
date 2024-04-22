#version 330 core

in vec2 FragCoord;          // pixel position (use this instead of gl_FragCoord)
in vec2 RlTexCoord;         // texture coordinate relative to the area that should be post-processed
flat in vec2 RlPixelSize;   // size of a pixel relative to the window size
flat in vec2 RlPixelSize;   // size of a pixel relative to the area that should be post-processed

out vec4 FragColor; // output color

// Default set uniforms
uniform mat4 u_ModelViewMatrix;     // model-view matrix
uniform mat4 u_ProjectionMatrix;    // projection matrix
uniform vec2 u_WindowSize;          // Minecraft's window size
uniform float u_ScaleFactor;        // Minecraft's gui-scale factor
uniform float u_Time;               // Time uniform (seconds since the start of the game)
uniform vec4 u_ShaderBounds;        // area that should be post-processed: vec4(startX, startY, endX, endY)
uniform vec2 u_Resolution;          // resolution of the area that should be post-processed

void main() {
    // ...
    FragColor = vec4(1.0);
}
