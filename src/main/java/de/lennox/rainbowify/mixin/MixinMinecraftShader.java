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
package de.lennox.rainbowify.mixin;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import de.lennox.rainbowify.interfaces.MinecraftShader;
import net.minecraft.client.gl.GlUniform;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;

@Mixin(net.minecraft.client.render.Shader.class)
public class MixinMinecraftShader implements MinecraftShader {

    @Shadow
    @Final
    private String name;
    private final Map<String, GlUniform> customUniforms = Maps.newHashMap();

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "net/minecraft/util/Identifier.<init> (Ljava/lang/String;)V"), index = 0)
    public String renameInit(String toReplace) {
        if (toReplace.contains("rainbowify:")) {
            return "rainbowify:" + toReplace.replace("rainbowify:","");
        }
        return toReplace;
    }

    @ModifyArg(method = "loadProgram", at = @At(value = "INVOKE", target = "net/minecraft/util/Identifier.<init> (Ljava/lang/String;)V"), index = 0)
    private static String renameLoadProgram(String toReplace) {
        if (toReplace.contains("rainbowify:")) {
            return "rainbowify:" + toReplace.replace("rainbowify:","");
        }
        return toReplace;
    }

    @ModifyArg(method = "addUniform", at = @At(value = "INVOKE", target = "java/util/List.add(Ljava/lang/Object;)Z"))
    public Object renameAddUniform(Object orig) {
        if (orig instanceof GlUniform glUniform && this.name.contains("rainbowify:")) {
            customUniforms.put(glUniform.getName(), glUniform);
        }
        return orig;
    }

    @Override
    public GlUniform customUniform(String name) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return this.customUniforms.get(name);
    }

}
