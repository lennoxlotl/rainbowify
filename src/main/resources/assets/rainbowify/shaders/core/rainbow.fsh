/*
 * Copyright (c) 2021-2023 Lennox
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

uniform float time;
uniform vec2 res;
uniform float alpha;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
    vec3 col = 1 - (0.5 + 1 * sin(time + texCoord0.xyx + vec3(0, 2, 4)) * cos(time + texCoord0.xyx + vec3(0, 2, 4)));
    fragColor = vec4(col, alpha);
}