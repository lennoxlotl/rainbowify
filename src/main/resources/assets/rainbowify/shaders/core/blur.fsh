/*
 * Copyright (c) 2021 Lennox
 *
 * This file is part of rainbowify.
 *
 * rainbowify is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * rainbowify is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with rainbowify.  If not, see <https://www.gnu.org/licenses/>.
 */
#version 150

uniform sampler2D DiffuseSampler;

uniform vec2 direction;
uniform float radius;

in vec2 texCoord;
in vec2 texelSize;

out vec4 fragColor;

float getKernel(float offset, float sigma) {
    return ((1.0 / sqrt(2.0 * 3.1415926 * sigma * sigma)) * (pow((2.7182818284), -(offset * offset) / (2.0 * sigma * sigma))));
}

void main() {
    vec4 color = vec4(0.0);
    for (float r = -radius; r <= radius; r++) {
        color += texture2D(DiffuseSampler, texCoord + r * texelSize * direction) * getKernel(r, radius / 2);
    }
    fragColor = vec4(color.rgb, 1.0);
}