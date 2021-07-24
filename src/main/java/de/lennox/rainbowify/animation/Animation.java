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
package de.lennox.rainbowify.animation;

public class Animation {

    private final Easing easing;
    private final long duration;
    private boolean lastDirection = false;
    private float lastMax, lastMin;
    private boolean startedAnimating;
    private float animation;
    private long startMillis;

    public Animation(long duration, Easing easing) {
        this.duration = duration;
        this.startMillis = System.currentTimeMillis();
        this.easing = easing;
    }

    public void reset(float animation) {
        this.animation = animation;
        this.startMillis = System.currentTimeMillis();
    }

    public void animate(float min, float max, boolean direction) {
        if (!startedAnimating) {
            startMillis = System.currentTimeMillis();
            startedAnimating = true;
        }
        if (done()) return;
        if (direction) {
            this.animation = min + ((animation + (max - min - animation)) * easeMultiplier());
        } else {
            this.animation = max - ((animation + (max - min - animation)) * easeMultiplier());
        }
        lastDirection = direction;
        lastMax = max;
        lastMin = min;
    }

    public boolean done() {
        return System.currentTimeMillis() >= this.startMillis + duration;
    }

    private float easeMultiplier() {
        float multiplier = baseMultiplication();
        switch (easing) {
            case EASE_OUT_EXPO:
                return multiplier == 1 ? 1 : (float) (1 - Math.pow(2, -10 * multiplier));
            case EASE_OUT_BACK:
                double c1 = 1.70158f;
                double c3 = c1 + 1;
                return (float) (1 + c3 * Math.pow(multiplier - 1, 3) + c1 * Math.pow(multiplier - 1, 2));
            case EASE_OUT_ELASTIC:
                float c4 = (float) ((2 * Math.PI) / 3);
                return multiplier == 0
                    ? 0
                    : (float) (multiplier == 1
                    ? 1
                    : Math.pow(2, -10 * multiplier) * Math.sin((multiplier * 10 - 0.75) * c4) + 1);
            case EASE_IN_SINE_OUT: {
                return (float) (-(Math.cos(Math.PI * multiplier) - 1) / 2);
            }
            case QUINT_SIN: {
                return (float) (1 - Math.pow(1 - multiplier, 5));
            }
            case EASE_OUT_CIRC:
                return (float) 1 - (1 - multiplier) * (1 - multiplier);
            default: {
                return baseMultiplication();
            }
        }
    }

    private float baseMultiplication() {
        return (float) ((duration - (this.startMillis + duration - System.currentTimeMillis())) / (double) duration);
    }

    public float animation() {
        if (done()) {
            return lastDirection ? lastMax : lastMin;
        }
        return animation;
    }

    public void setAnimation(float animation) {
        this.animation = animation;
    }

    public long duration() {
        return duration;
    }

    public Easing easing() {
        return easing;
    }

}
