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

uniform sampler2D DiffuseSampler;
uniform float offset;

in vec2 texCoord;
in vec2 texelSize;

out vec4 fragColor;

void main() {
    vec2 uv = texCoord;
    vec4 sum = texture(DiffuseSampler, uv + vec2(-texelSize.x * 2.0, 0.0) * offset);
    sum += texture(DiffuseSampler, uv + vec2(-texelSize.x, texelSize.y) * offset) * 2.0;
    sum += texture(DiffuseSampler, uv + vec2(0.0, texelSize.y * 2.0) * offset);
    sum += texture(DiffuseSampler, uv + vec2(texelSize.x, texelSize.y) * offset) * 2.0;
    sum += texture(DiffuseSampler, uv + vec2(texelSize.x * 2.0, 0.0) * offset);
    sum += texture(DiffuseSampler, uv + vec2(texelSize.x, -texelSize.y) * offset) * 2.0;
    sum += texture(DiffuseSampler, uv + vec2(0.0, -texelSize.y * 2.0) * offset);
    sum += texture(DiffuseSampler, uv + vec2(-texelSize.x, -texelSize.y) * offset) * 2.0;
    fragColor = vec4(sum.rgb / 12.0, 1.0);
}