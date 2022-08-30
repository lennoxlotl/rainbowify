/*
 * Copyright (c) 2021-2022 Lennox
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

/**
 * Handles one specific animation at a time
 *
 * @author Lennox
 * @since 1.0.0
 */
public class Animation {
  private final long duration;
  private boolean lastDirection, started;
  private float lastMax, lastMin, animation;
  private long startMillis;

  public Animation(long duration) {
    this.duration = duration;
    this.startMillis = System.currentTimeMillis();
  }

  public void reset(float animation) {
    this.animation = animation;
    this.startMillis = System.currentTimeMillis();
  }

  /**
   * Processes the animation with the given parameters
   *
   * @param min       The min value
   * @param max       The max value
   * @param direction The direction the animation should go
   * @since 1.0.0
   */
  public void animate(float min, float max, boolean direction) {
    // If the animation is currently starting, set the start time to now
    if (!started) {
      startMillis = System.currentTimeMillis();
      started = true;
    }
    if (done()) return;
    float goal = direction ? min : max;
    // Execute the actual animation
    this.animation = goal + ((animation + (max - min - animation)) * baseMultiplication());
    lastDirection = direction;
    lastMax = max;
    lastMin = min;
  }

  /**
   * Returns if the calculation is done
   *
   * @return Current completion state
   * @since 1.0.0
   */
  public boolean done() {
    return System.currentTimeMillis() >= this.startMillis + duration;
  }

  /**
   * Calculates and returns the base multiplication factor
   *
   * @return The calculated multiplication factor
   * @since 1.0.0
   */
  private float baseMultiplication() {
    // Return a multiplication factor based on how long the animation should take and how long it's
    // already animating for
    return (float)
        ((duration - (startMillis + duration - System.currentTimeMillis())) / (double) duration);
  }

  /**
   * Returns the current animation progress between 0 and 1
   *
   * @return The progress
   * @since 1.0.0
   */
  public float animation() {
    // If the animation is already finished, give back the last recorded animation factor
    if (done()) {
      return lastDirection ? lastMax : lastMin;
    }
    return animation;
  }
}
