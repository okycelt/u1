#version 410

in vec2 gridCoords;

out vec2 texCoords;
out vec3 normal;
out vec3 lightDirection;
out vec3 viewDirection;
out vec3 coneDirection;
out float lightVecDist;
out vec3 tangent;

uniform vec3 lightPosition;
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform int object;

const float M_PI = 3.14159;
const float DELTA = 0.01;

struct VertexAttribs {
    vec3 position;
    vec3 normal;
    vec3 tangent;
};

VertexAttribs doQuadratic(vec2 coords);
vec3 doQuadraticCoords(vec2 coords);

VertexAttribs doMonkeySaddle(vec2 coords);
vec3 doMonkeySaddleCoords(vec2 coords);

VertexAttribs doSphere(vec2 coords);
vec3 doSphereCoords(vec2 coords);

VertexAttribs doTorus(vec2 coords);
vec3 doTorusCoords(vec2 coords);

VertexAttribs doCone(vec2 coords);
vec3 doConeCoords(vec2 coords);

VertexAttribs doVase(vec2 coords);
vec3 doVaseCoords(vec2 coords);

VertexAttribs doFlatSurface(vec2 coords);
vec3 doFlatSurfaceCoords(vec2 coords);

void main() {
    texCoords = gridCoords;

    VertexAttribs va;
    if (object == 1) {
        va = doQuadratic(gridCoords);
    } else if (object == 2) {
        va = doMonkeySaddle(gridCoords);
    } else if (object == 3) {
        va = doSphere(gridCoords);
    } else if (object == 4) {
        va = doTorus(gridCoords);
    } else if (object == 5) {
        va = doCone(gridCoords);
    } else if (object == 6) {
        va = doVase(gridCoords);
    } else {
        va = doFlatSurface(gridCoords);
    }

    vec4 cartesianPosition = vec4(va.position, 1.0);
    normal = va.normal;
    tangent = va.tangent;

    normal = transpose(inverse(mat3(viewMatrix * modelMatrix))) * normal;
    tangent = mat3(viewMatrix * modelMatrix) * tangent;
    vec4 trLightPosition = (viewMatrix * vec4(lightPosition, 1.0));
    vec4 objectPosition = viewMatrix * modelMatrix * cartesianPosition;

    lightDirection = objectPosition.xyz - trLightPosition.xyz;
    viewDirection = -objectPosition.xyz;
    lightVecDist = length(lightDirection);
    coneDirection = ((viewMatrix * vec4(vec3(0.0), 1.0)) - (viewMatrix * vec4(lightPosition, 1.0))).xyz;

    gl_Position = projectionMatrix * objectPosition;
}

VertexAttribs doQuadratic(vec2 coords) {
    vec3 dx = doQuadraticCoords(vec2(coords.x + DELTA, coords.y)) - doQuadraticCoords(vec2(coords.x - DELTA, coords.y));
    vec3 dy = doQuadraticCoords(vec2(coords.x, coords.y + DELTA)) - doQuadraticCoords(vec2(coords.x, coords.y - DELTA));

    VertexAttribs vertexAttribs;
    vertexAttribs.position = doQuadraticCoords(coords);
    vertexAttribs.normal = -cross(dx, dy);
    vertexAttribs.tangent = dx;
    return vertexAttribs;
}

vec3 doQuadraticCoords(vec2 coords) {
    float x = coords.x * 10 - 5;
    float y = coords.y * 10 - 5;
    float z = (- x * x - y * y) * 0.1 + 2.5;
    return vec3(x, y, z) * 0.4;
}

VertexAttribs doMonkeySaddle(vec2 coords) {
    vec3 dx = doMonkeySaddleCoords(vec2(coords.x + DELTA, coords.y)) - doMonkeySaddleCoords(vec2(coords.x - DELTA, coords.y));
    vec3 dy = doMonkeySaddleCoords(vec2(coords.x, coords.y + DELTA)) - doMonkeySaddleCoords(vec2(coords.x, coords.y - DELTA));

    VertexAttribs vertexAttribs;
    vertexAttribs.position = doMonkeySaddleCoords(coords);
    vertexAttribs.normal = -cross(dx, dy);
    vertexAttribs.tangent = dx;
    return vertexAttribs;
}

vec3 doMonkeySaddleCoords(vec2 coords) {
    float x = coords.x * 2 - 1;
    float y = coords.y * 2 - 1;
    float z = (x * x * x - 3 * x * y * y) * 0.5;
    return vec3(x, y, z);
}

VertexAttribs doTorus(vec2 coords) {
    vec3 dx = doTorusCoords(vec2(coords.x + DELTA, coords.y)) - doTorusCoords(vec2(coords.x - DELTA, coords.y));
    vec3 dy = doTorusCoords(vec2(coords.x, coords.y + DELTA)) - doTorusCoords(vec2(coords.x, coords.y - DELTA));

    VertexAttribs vertexAttribs;
    vertexAttribs.position = doTorusCoords(coords);
    vertexAttribs.normal = -cross(dx, dy);
    vertexAttribs.tangent = dx;
    return vertexAttribs;
}

vec3 doTorusCoords(vec2 coords) {
    float s = coords.x * 2 * M_PI;
    float t = coords.y * 2 * M_PI;
    float x = 3 * cos(s) + cos(t) * cos(s);
    float y = 3 * sin(s) + cos(t) * sin(s);
    float z = sin(t);
    return vec3(x, y, z) * 0.5;
}

VertexAttribs doSphere(vec2 coords) {
    vec3 dx = doSphereCoords(vec2(coords.x + DELTA, coords.y)) - doSphereCoords(vec2(coords.x - DELTA, coords.y));
    vec3 dy = doSphereCoords(vec2(coords.x, coords.y + DELTA)) - doSphereCoords(vec2(coords.x, coords.y - DELTA));

    VertexAttribs vertexAttribs;
    vertexAttribs.position = doSphereCoords(coords);
    vertexAttribs.normal = -cross(dx, dy);
    vertexAttribs.tangent = dx;
    return vertexAttribs;
}

vec3 doSphereCoords(vec2 coords) {
    float s = coords.x * 2.0 * M_PI;
    float t = coords.y * M_PI - M_PI;
    float r = 1.5;
    float x = r * sin(t) * cos(s);
    float y = r * sin(t) * sin(s);
    float z = r * cos(t);
    return vec3(x, y, z);
}

VertexAttribs doCone(vec2 coords) {
    vec3 dx = doConeCoords(vec2(coords.x + DELTA, coords.y)) - doConeCoords(vec2(coords.x - DELTA, coords.y));
    vec3 dy = doConeCoords(vec2(coords.x, coords.y + DELTA)) - doConeCoords(vec2(coords.x, coords.y - DELTA));

    VertexAttribs vertexAttribs;
    vertexAttribs.position = doConeCoords(coords);
    vertexAttribs.normal = -cross(dx, dy);
    vertexAttribs.tangent = dx;
    return vertexAttribs;
}

vec3 doConeCoords(vec2 coords) {
    float s = coords.x * 2 * M_PI;
    float t = coords.y * 4 - 2;
    float r = 2 - abs(t);
    float x = r * cos(s);
    float y = r * sin(s);
    float z = t;
    return vec3(x, y, z);
}

VertexAttribs doVase(vec2 coords) {
    vec3 dx = doVaseCoords(vec2(coords.x + DELTA, coords.y)) - doVaseCoords(vec2(coords.x - DELTA, coords.y));
    vec3 dy = doVaseCoords(vec2(coords.x, coords.y + DELTA)) - doVaseCoords(vec2(coords.x, coords.y - DELTA));

    VertexAttribs vertexAttribs;
    vertexAttribs.position = doVaseCoords(coords);
    vertexAttribs.normal = -cross(dx, dy);
    vertexAttribs.tangent = dx;
    return vertexAttribs;
}

vec3 doVaseCoords(vec2 coords) {
    float s = coords.x * 2 * M_PI;
    float t = coords.y * 2 * M_PI - M_PI;
    float r = sin(2 * t - M_PI * 0.5) + 2;
    float x = r * cos(s);
    float y = r * sin(s);
    float z = t;
    return vec3(x, y, z) * 0.5;
}

VertexAttribs doFlatSurface(vec2 coords) {
    vec3 dx = doFlatSurfaceCoords(vec2(coords.x + DELTA, coords.y)) - doFlatSurfaceCoords(vec2(coords.x - DELTA, coords.y));
    vec3 dy = doFlatSurfaceCoords(vec2(coords.x, coords.y + DELTA)) - doFlatSurfaceCoords(vec2(coords.x, coords.y - DELTA));

    VertexAttribs vertexAttribs;
    vertexAttribs.position = doFlatSurfaceCoords(coords);
    vertexAttribs.normal = -cross(dx, dy);
    vertexAttribs.tangent = dx;
    return vertexAttribs;
}

vec3 doFlatSurfaceCoords(vec2 coords) {
    coords.x = coords.x * 6 - 3;
    coords.y = coords.y * 6 - 3;
    return vec3(coords, 1.0);
}