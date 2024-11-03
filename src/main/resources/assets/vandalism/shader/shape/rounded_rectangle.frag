#version 150

uniform vec2 u_position;        // Position of the rectangle (top-left corner)
uniform vec2 u_WindowSize;      // Size of the window
uniform float u_ScaleFactor;    // Scale factor for the game
uniform vec2 u_size;            // Size of the rectangle (width, height)
uniform float u_radius;         // Radius of the corners
uniform vec4 u_color;           // Rectangle color (RGBA)

out vec4 fragColor;

// SDF for a rounded rectangle
float roundedRectSDF(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + vec2(r);
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

void main() {
    // Convert from OpenGL's bottom-left origin to top-left origin
    vec2 uv = gl_FragCoord.xy;
    uv.y = u_WindowSize.y - uv.y;  // Flip y-axis to start from the top

    // Apply scaling for GUI scale factor
    uv /= u_ScaleFactor;

    // Transform fragment coordinates to center around rectangle position
    uv -= (u_position + u_size * 0.5);

    // Compute the SDF for the rounded rectangle
    float dist = roundedRectSDF(uv, u_size * 0.5, u_radius);

    // Smooth edges by blending based on the distance
    float alpha = smoothstep(0.0, 1.0, -dist);

    // Set the fragment color with the specified color and alpha
    fragColor = vec4(u_color.rgb, u_color.a * alpha);
}