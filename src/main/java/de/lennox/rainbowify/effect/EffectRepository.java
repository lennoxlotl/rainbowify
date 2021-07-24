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
package de.lennox.rainbowify.effect;

import de.lennox.rainbowify.RainbowifyMod;
import de.lennox.rainbowify.bus.Subscriber;
import de.lennox.rainbowify.bus.events.ScreenDrawEvent;
import de.lennox.rainbowify.effect.effects.BlurEffect;
import de.lennox.rainbowify.effect.effects.RainbowEffect;

import java.util.ArrayList;
import java.util.List;

public class EffectRepository {

    private final List<Effect> effects = new ArrayList<>();
    private final EffectAnimator animator = new EffectAnimator();

    public void init() {
        effects.addAll(List.of(
            new BlurEffect(),
            new RainbowEffect()
        ));
        effects.forEach(Effect::init);
        animator.init(effects);
        RainbowifyMod.instance().eventBus().subscribe(this);
    }

    private final Subscriber<ScreenDrawEvent> screenDrawSubscriber = event -> effects.forEach(effect -> effect.draw(event.matrixStack()));

}
