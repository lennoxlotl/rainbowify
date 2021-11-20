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
  private final long duration;
  private boolean lastDirection = false;
  private float lastMax, lastMin;
  private boolean startedAnimating;
  private float animation;
  private long startMillis;

  public Animation(long duration) {
    this.duration = duration;
    this.startMillis = System.currentTimeMillis();
  }

  public void reset(float animation) {
    this.animation = animation;
    this.startMillis = System.currentTimeMillis();
  }

  public void animate(float min, float max, boolean direction) {
    // If the animation is currently starting, set the start time to now
    if (!startedAnimating) {
      startMillis = System.currentTimeMillis();
      startedAnimating = true;
    }
    if (done()) return;
    // Animate the object based on the direction it should go
    if (direction) {
      this.animation = min + ((animation + (max - min - animation)) * baseMultiplication());
    } else {
      this.animation = max - ((animation + (max - min - animation)) * baseMultiplication());
    }
    lastDirection = direction;
    lastMax = max;
    lastMin = min;
  }

  public boolean done() {
    return System.currentTimeMillis() >= this.startMillis + duration;
  }

  private float baseMultiplication() {
    // Return a multiplication factor based on how long the animation should take and how long it's
    // already animating for
    return (float)
        ((duration - (this.startMillis + duration - System.currentTimeMillis()))
            / (double) duration);
  }

  public float animation() {
    // If the animation is already finished, give back the last recorded animation factor
    if (done()) {
      return lastDirection ? lastMax : lastMin;
    }
    return animation;
  }
}
