#version 410

in vec2 texCoords;
in vec3 normal;
in vec3 tangent;
in vec3 lightDirection;
in vec3 viewDirection;
in vec3 coneDirection;
in float lightVecDist;

uniform vec3 baseColor;
uniform sampler2D diffuseTexture;
uniform sampler2D normalTexture;
uniform sampler2D heightTexture;
uniform sampler2D roughnessTexture;
uniform sampler2D ambOccTexture;

const vec3 ambient = vec3(0.05, 0.05, 0.05);
const vec3 diffuse = vec3(1.0, 1.0, 1.0);
const vec3 lightColor = vec3(0.8, 0.8, 0.7);
const float specularPower = 3;
const float coneAngle = 0.02;

const float constantAttenuation = 1.0;
const float linearAttenuation = 0.1;
const float quadraticAttenuation = 0.01;

out vec4 outColor;

void main() {
    vec3 ld = normalize(lightDirection);
    vec3 nd = normalize(normal);
    vec3 cd = normalize(coneDirection);

    vec3 vBinormal = cross(nd, normalize(tangent));
    vec3 vTangent = cross(vBinormal, nd);

    mat3 TBN = mat3(vTangent, vBinormal, nd);

    vec3 tbnConeDirection = normalize(cd * TBN);
    vec3 tbnLightDirection = normalize(ld * TBN);

    float ambOccCoef = texture(ambOccTexture, texCoords).r;
    vec3 texDiffuseColor = texture(diffuseTexture, texCoords).rgb;

//    vec3 totalAmbient = ambient * baseColor;
    vec3 totalAmbient = ambient * texDiffuseColor * ambOccCoef;
    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

//    float ndotl = max(dot(ld, nd), 0.0);

    float height = 1.0 - texture(heightTexture, texCoords.xy).r;
    vec2 offset = tbnConeDirection.xy * (height * 0.005 - 0.005);
    vec2 offsetTexCoord = texCoords.xy + offset;

    vec3 bump = texture(normalTexture, offsetTexCoord).rgb * 2.0 - 1.0;
    bump.x = -bump.x;
    float ndotl = max(dot(bump, tbnLightDirection), 0.0);

    float spotEffect = max(dot(tbnConeDirection, tbnLightDirection), 0.0);
    if (spotEffect > (1 - coneAngle)) {
        if (ndotl > 0) {
//            totalDiffuse = diffuse * ndotl * baseColor;
            totalDiffuse = diffuse * ndotl * texDiffuseColor * ambOccCoef;

            vec3 specular = lightColor * max(texture(roughnessTexture, offsetTexCoord).r, 0.5);

            vec3 halfVector = normalize(tbnLightDirection + tbnConeDirection);
            float ndoth = max(0.0, dot(bump, halfVector));
            totalSpecular = specular * (pow(ndoth, specularPower * 4));
        }

        float blend = clamp(((spotEffect - 1 + coneAngle)/(coneAngle)), 0.0, 1.0);
        float attenuation = 1.0 / (constantAttenuation + linearAttenuation * lightVecDist + quadraticAttenuation * lightVecDist * lightVecDist);

        outColor = vec4(mix(totalAmbient, totalAmbient + attenuation * (totalDiffuse + totalSpecular), blend), 1.0);
    } else {
        outColor = vec4(totalAmbient, 1.0);
    }

}
