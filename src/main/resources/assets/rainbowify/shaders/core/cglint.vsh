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

in vec3 Position;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 TextureMat;
uniform mat4 ScreenTextureMat;

out vec2 texCoord0;
out vec2 texCoord1;
out vec3 pos;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    texCoord0 = (TextureMat * vec4(UV0, 0.0, 1.0)).xy;
    texCoord1 = (ScreenTextureMat * vec4(UV0, 0.0, 1.0)).xy;
    pos = Position;
}