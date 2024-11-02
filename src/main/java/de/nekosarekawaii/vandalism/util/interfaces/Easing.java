/*
 * This file is part of Vandalism - https://github.com/NekosAreKawaii/Vandalism
 * Copyright (C) 2023-2024 NekosAreKawaii, Verschlxfene, FooFieOwO, Recyz and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nekosarekawaii.vandalism.util.interfaces;

// Easing.java from https://github.com/mattdesl/cisc226game/blob/master/SpaceGame/src/space/engine/easing/Easing.java

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Locale;

public interface Easing {
    
    /**
     * The basic function for easing.
     * @param t the time (either frames or in seconds/milliseconds)
     * @param b the beginning value
     * @param c the value changed
     * @param d the duration time
     * @return the eased value
     */
    float ease(float t, float b, float c, float d);

    default float easePercent(float t, float b, float e, float d) {
        return ease(t, b, e - b, d);
    }
    
    /**
     * Simple linear tweening - no easing.
     */
    Easing LINEAR = (t, b, c, d) -> c*t/d + b;
    
    ///////////// QUADRATIC EASING: t^2 ///////////////////
    
    /** 
     * Quadratic easing in - accelerating from zero velocity.
     */
    Easing QUAD_IN = (t, b, c, d) -> c*(t/=d)*t + b;
    
    /** 
     * Quadratic easing out - decelerating to zero velocity.
     */
    Easing QUAD_OUT = (t, b, c, d) -> -c *(t/=d)*(t-2) + b;
    
    /**
     * Quadratic easing in/out - acceleration until halfway, then deceleration
     */
    Easing QUAD_IN_OUT = (t, b, c, d) -> {
        if ((t/=d/2) < 1) return c/2*t*t + b;
        return -c/2 * ((--t)*(t-2) - 1) + b;
    };
    
    
    ///////////// CUBIC EASING: t^3 ///////////////////////

    /** 
     * Cubic easing in - accelerating from zero velocity.
     */
    Easing CUBIC_IN = (t, b, c, d) -> c*(t/=d)*t*t + b;
    
    /**
     * Cubic easing out - decelerating to zero velocity.
     */
    Easing CUBIC_OUT = (t, b, c, d) -> c*((t=t/d-1)*t*t + 1) + b;
    
    /** 
     * Cubic easing in/out - acceleration until halfway, then deceleration.
     */
    Easing CUBIC_IN_OUT = (t, b, c, d) -> {
        if ((t/=d/2) < 1) return c/2*t*t*t + b;
        return c/2*((t-=2)*t*t + 2) + b;
    };
    
    ///////////// QUARTIC EASING: t^4 /////////////////////

    /**
     * Quartic easing in - accelerating from zero velocity.
     */
    Easing QUARTIC_IN = (t, b, c, d) -> c*(t/=d)*t*t*t + b;
	
    /** 
     * Quartic easing out - decelerating to zero velocity.
     */
    Easing QUARTIC_OUT = (t, b, c, d) -> -c * ((t=t/d-1)*t*t*t - 1) + b;

    /** 
     * Quartic easing in/out - acceleration until halfway, then deceleration.
     */
    Easing QUARTIC_IN_OUT = (t, b, c, d) -> {
        if ((t/=d/2) < 1) return c/2*t*t*t*t + b;
        return -c/2 * ((t-=2)*t*t*t - 2) + b;
    };
    
    ///////////// QUINTIC EASING: t^5  ////////////////////

    /** 
     * Quintic easing in - accelerating from zero velocity.
     */
    Easing QUINTIC_IN = (t, b, c, d) -> c*(t/=d)*t*t*t*t + b;

    /** 
     * Quintic easing out - decelerating to zero velocity.
     */
    Easing QUINTIC_OUT = (t, b, c, d) -> c*((t=t/d-1)*t*t*t*t + 1) + b;
    
    /** 
     * Quintic easing in/out - acceleration until halfway, then deceleration.
     */
    Easing QUINTIC_IN_OUT = (t, b, c, d) -> {
        if ((t/=d/2) < 1) return c/2*t*t*t*t*t + b;
        return c/2*((t-=2)*t*t*t*t + 2) + b;
    };
    
    
    
    ///////////// SINUSOIDAL EASING: sin(t) ///////////////

    /** 
     * Sinusoidal easing in - accelerating from zero velocity.
     */
    Easing SINE_IN = (t, b, c, d) -> -c * (float)Math.cos(t/d * (Math.PI/2)) + c + b;

    /** 
     * Sinusoidal easing out - decelerating to zero velocity.
     */
    Easing SINE_OUT = (t, b, c, d) -> c * (float)Math.sin(t/d * (Math.PI/2)) + b;
    
    /** 
     * Sinusoidal easing in/out - accelerating until halfway, then decelerating.
     */
    Easing SINE_IN_OUT = (t, b, c, d) -> -c/2 * ((float)Math.cos(Math.PI*t/d) - 1) + b;
        
     ///////////// EXPONENTIAL EASING: 2^t /////////////////

    /** 
     * Exponential easing in - accelerating from zero velocity.
     */
    Easing EXPO_IN = (t, b, c, d) -> (t==0) ? b : c * (float)Math.pow(2, 10 * (t/d - 1)) + b;

    /** 
     * Exponential easing out - decelerating to zero velocity.
     */
    Easing EXPO_OUT = (t, b, c, d) -> (t==d) ? b+c : c * (-(float)Math.pow(2, -10 * t/d) + 1) + b;
       
    /** 
     * Exponential easing in/out - accelerating until halfway, then decelerating.
     */
    Easing EXPO_IN_OUT = (t, b, c, d) -> {
        if (t==0) return b;
        if (t==d) return b+c;
        if ((t/=d/2) < 1) return c/2 * (float)Math.pow(2, 10 * (t - 1)) + b;
        return c/2 * (-(float)Math.pow(2, -10 * --t) + 2) + b;
    };
    
    
    /////////// CIRCULAR EASING: sqrt(1-t^2) //////////////

    /** 
     * Circular easing in - accelerating from zero velocity.
     */
    Easing CIRC_IN = (t, b, c, d) -> -c * ((float)Math.sqrt(1 - (t/=d)*t) - 1) + b;
    
    /** 
     * Circular easing out - decelerating to zero velocity.
     */
    Easing CIRC_OUT = (t, b, c, d) -> c * (float)Math.sqrt(1 - (t=t/d-1)*t) + b;
         
    /** 
     * Circular easing in/out - acceleration until halfway, then deceleration.
     */
    Easing CIRC_IN_OUT = (t, b, c, d) -> {
        if ((t/=d/2) < 1) return -c/2 * ((float)Math.sqrt(1 - t*t) - 1) + b;
        return c/2 * ((float)Math.sqrt(1 - (t-=2)*t) + 1) + b;
    };
    
    /////////// ELASTIC EASING: exponentially decaying sine wave  //////////////

    /**
     * A base class for elastic easings.
     */
    @Setter
    @Getter
    abstract class Elastic implements Easing {
        /**
         * -- GETTER --
         *  Returns the amplitude.
         *
         *
         * -- SETTER --
         *  Sets the amplitude to the given value.
         *
         @return the amplitude for this easing
          * @param amplitude the new amplitude
         */
        private float amplitude;
        /**
         * -- GETTER --
         *  Returns the period.
         *
         *
         * -- SETTER --
         *  Sets the period to the given value.
         *
         @return the period for this easing
          * @param period the new period
         */
        private float period;
        
        /**
         * Creates a new Elastic easing with the specified settings.
         * @param amplitude the amplitude for the elastic function
         * @param period the period for the elastic function
         */
        public Elastic(float amplitude, float period) {
            this.amplitude = amplitude;
            this.period = period;
        }
        
        /**
         * Creates a new Elastic easing with default settings (-1f, 0f).
         */
        public Elastic() {
            this(-1f, 0f);
        }

    }
    
    /** An EasingIn instance using the default values. */
    Elastic ELASTIC_IN = new ElasticIn();
    
    /** An Elastic easing used for ElasticIn functions. */
    class ElasticIn extends Elastic {
        public ElasticIn(float amplitude, float period) { 
            super(amplitude, period); 
        }
        public ElasticIn() {
            super();
        }
        
        public float ease(float t, float b, float c, float d) {
            float a = getAmplitude();
            float p = getPeriod();
            if (t==0) return b;  if ((t/=d)==1) return b+c;  if (p==0) p=d*.3f;
            float s = 0;
            if (a < Math.abs(c)) { a=c; s=p/4; } 
            else s = p/(float)(2*Math.PI) * (float)Math.asin(c/a);
            return -(a*(float)Math.pow(2,10*(t-=1)) * (float)Math.sin( (t*d-s)*(2*Math.PI)/p )) + b;
        }
    }
    
    /** An ElasticOut instance using the default values. */
    Elastic ELASTIC_OUT = new ElasticOut();
    
    /** An Elastic easing used for ElasticOut functions. */
    class ElasticOut extends Elastic {
        public ElasticOut(float amplitude, float period) { 
            super(amplitude, period); 
        }
        public ElasticOut() {
            super();
        }
        
        public float ease(float t, float b, float c, float d) {
            float a = getAmplitude();
            float p = getPeriod();
            if (t==0) return b;  if ((t/=d)==1) return b+c;  if (p==0) p=d*.3f;
            float s = 0;
            if (a < Math.abs(c)) { a=c; s=p/4; }
            else s = p/(float)(2*Math.PI) * (float)Math.asin(c/a);
            return a*(float)Math.pow(2,-10*t) * (float)Math.sin( (t*d-s)*(2*Math.PI)/p ) + c + b;
        }
    }
    
    /** An ElasticInOut instance using the default values. */
    Elastic ELASTIC_IN_OUT = new ElasticInOut();
    
    /** An Elastic easing used for ElasticInOut functions. */
    class ElasticInOut extends Elastic {
        public ElasticInOut(float amplitude, float period) { 
            super(amplitude, period); 
        }
        public ElasticInOut() {
            super();
        }
        
        public float ease(float t, float b, float c, float d) {
            float a = getAmplitude();
            float p = getPeriod();
            if (t==0) return b;  if ((t/=d/2)==2) return b+c;  if (p==0) p=d*(.3f*1.5f);
            float s = 0;
            if (a < Math.abs(c)) { a=c; s=p/4f; }
            else s = p/(float)(2*Math.PI) * (float)Math.asin(c/a);
            if (t < 1) return -.5f*(a*(float)Math.pow(2,10*(t-=1)) * (float)Math.sin( (t*d-s)*(2*Math.PI)/p )) + b;
            return a*(float)Math.pow(2,-10*(t-=1)) * (float)Math.sin( (t*d-s)*(2*Math.PI)/p )*.5f + c + b;
        }
    }

    /** A base class for Back easings. */
    @Setter
    @Getter
    abstract class Back implements Easing {
        /** The default overshoot is 10% (1.70158). */
        public static final float DEFAULT_OVERSHOOT = 1.70158f;

        /**
         * -- GETTER --
         *  Returns the overshoot for this easing.
         *
         *
         * -- SETTER --
         *  Sets the overshoot to the given value.
         *
         @return this easing's overshoot
          * @param overshoot the new overshoot
         */
        private float overshoot;

        /** Creates a new Back instance with the default overshoot (1.70158). */
        public Back() { this(DEFAULT_OVERSHOOT); }

        /**
         * Creates a new Back instance with the specified overshoot.
         * @param overshoot the amount to overshoot by -- higher number
         *          means more overshoot and an overshoot of 0 results in
         *          cubic easing with no overshoot
         */
        public Back(float overshoot) { this.overshoot = overshoot; }

    }

    /////////// BACK EASING: overshooting cubic easing: (s+1)*t^3 - s*t^2  //////////////

    /** An instance of BackIn using the default overshoot. */   
    Back BACK_IN = new BackIn();
    
    /** Back easing in - backtracking slightly, then reversing direction and moving to target. */
    class BackIn extends Back {
        public BackIn() { super(); }
        public BackIn(float overshoot) { super(overshoot); }
        
        public float ease(float t, float b, float c, float d) {
            float s = getOvershoot();
            return c*(t/=d)*t*((s+1)*t - s) + b;
        }
    };
    
    /** An instance of BackOut using the default overshoot. */
    Back BACK_OUT = new BackOut();
    
    /** Back easing out - moving towards target, overshooting it slightly, then reversing and coming back to target. */
    class BackOut extends Back {
        public BackOut() { super(); }
        public BackOut(float overshoot) { super(overshoot); }
        
        public float ease(float t, float b, float c, float d) {
            float s = getOvershoot();
            return c*((t=t/d-1)*t*((s+1)*t + s) + 1) + b;
        }
    }
    
    /** An instance of BackInOut using the default overshoot. */
    Back BACK_IN_OUT = new BackInOut();
    
    /**
     * Back easing in/out - backtracking slightly, then reversing direction and moving to target,
     * then overshooting target, reversing, and finally coming back to target.
     */
    class BackInOut extends Back {
        public BackInOut() { super(); }
        public BackInOut(float overshoot) { super(overshoot); }
        
        public float ease(float t, float b, float c, float d) {
            float s = getOvershoot();
            if ((t/=d/2) < 1) return c/2*(t*t*(((s*=(1.525))+1)*t - s)) + b;
            return c/2*((t-=2)*t*(((s*=(1.525))+1)*t + s) + 2) + b;
        }
    }
    
    /////////// BOUNCE EASING: exponentially decaying parabolic bounce  //////////////
    
    /** Bounce easing in. */
    Easing BOUNCE_IN = new Easing() {
        public float ease(float t, float b, float c, float d) {
            return c - Easing.BOUNCE_OUT.ease(d-t, 0, c, d) + b;
        }
    };

    /** Bounce easing out. */
    Easing BOUNCE_OUT = (t, b, c, d) -> {
        if ((t/=d) < (1/2.75f)) {
            return c*(7.5625f*t*t) + b;
        } else if (t < (2/2.75f)) {
            return c*(7.5625f*(t-=(1.5f/2.75f))*t + .75f) + b;
        } else if (t < (2.5f/2.75f)) {
            return c*(7.5625f*(t-=(2.25f/2.75f))*t + .9375f) + b;
        } else {
            return c*(7.5625f*(t-=(2.625f/2.75f))*t + .984375f) + b;
        }
    };
    
    /** Bounce easing in/out. */
    Easing BOUNCE_IN_OUT = (t, b, c, d) -> {
        if (t < d/2) return Easing.BOUNCE_IN.ease(t*2, 0, c, d) * .5f + b;
        return Easing.BOUNCE_OUT.ease(t*2-d, 0, c, d) * .5f + c*.5f + b;
    };

    static String getDefaultEasingName(Easing easing) {
        if (easing == LINEAR) return "Linear";
        if (easing == QUAD_IN) return "QuadIn";
        if (easing == QUAD_OUT) return "QuadOut";
        if (easing == QUAD_IN_OUT) return "QuadInOut";
        if (easing == CUBIC_IN) return "CubicIn";
        if (easing == CUBIC_OUT) return "CubicOut";
        if (easing == CUBIC_IN_OUT) return "CubicInOut";
        if (easing == QUARTIC_IN) return "QuarticIn";
        if (easing == QUARTIC_OUT) return "QuarticOut";
        if (easing == QUARTIC_IN_OUT) return "QuarticInOut";
        if (easing == QUINTIC_IN) return "QuinticIn";
        if (easing == QUINTIC_OUT) return "QuinticOut";
        if (easing == QUINTIC_IN_OUT) return "QuinticInOut";
        if (easing == SINE_IN) return "SineIn";
        if (easing == SINE_OUT) return "SineOut";
        if (easing == SINE_IN_OUT) return "SineInOut";
        if (easing == EXPO_IN) return "ExpoIn";
        if (easing == EXPO_OUT) return "ExpoOut";
        if (easing == EXPO_IN_OUT) return "ExpoInOut";
        if (easing == CIRC_IN) return "CircIn";
        if (easing == CIRC_OUT) return "CircOut";
        if (easing == CIRC_IN_OUT) return "CircInOut";
        if (easing == ELASTIC_IN) return "ElasticIn";
        if (easing == ELASTIC_OUT) return "ElasticOut";
        if (easing == ELASTIC_IN_OUT) return "ElasticInOut";
        if (easing == BACK_IN) return "BackIn";
        if (easing == BACK_OUT) return "BackOut";
        if (easing == BACK_IN_OUT) return "BackInOut";
        if (easing == BOUNCE_IN) return "BounceIn";
        if (easing == BOUNCE_OUT) return "BounceOut";
        if (easing == BOUNCE_IN_OUT) return "BounceInOut";
        return "Unknown";
    }

    static Easing getDefaultEasingByName(String name) {
        return switch (name.toLowerCase(Locale.ROOT)) {
            //case "linear" -> LINEAR; // handled by default
            case "quadin" -> QUAD_IN;
            case "quadout" -> QUAD_OUT;
            case "quadinout" -> QUAD_IN_OUT;
            case "cubicin" -> CUBIC_IN;
            case "cubicout" -> CUBIC_OUT;
            case "cubicinout" -> CUBIC_IN_OUT;
            case "quarticin" -> QUARTIC_IN;
            case "quarticout" -> QUARTIC_OUT;
            case "quarticinout" -> QUARTIC_IN_OUT;
            case "quinticin" -> QUINTIC_IN;
            case "quinticout" -> QUINTIC_OUT;
            case "quinticinout" -> QUINTIC_IN_OUT;
            case "sinein" -> SINE_IN;
            case "sineout" -> SINE_OUT;
            case "sineinout" -> SINE_IN_OUT;
            case "expoin" -> EXPO_IN;
            case "expoout" -> EXPO_OUT;
            case "expoinout" -> EXPO_IN_OUT;
            case "circin" -> CIRC_IN;
            case "circout" -> CIRC_OUT;
            case "circinout" -> CIRC_IN_OUT;
            case "elasticin" -> ELASTIC_IN;
            case "elasticout" -> ELASTIC_OUT;
            case "elasticinout" -> ELASTIC_IN_OUT;
            case "backin" -> BACK_IN;
            case "backout" -> BACK_OUT;
            case "backinout" -> BACK_IN_OUT;
            case "bouncein" -> BOUNCE_IN;
            case "bounceout" -> BOUNCE_OUT;
            case "bounceinout" -> BOUNCE_IN_OUT;
            default -> LINEAR;
        };
    }

    static List<String> getDefaultEasingFunctions() {
        return List.of(
            "Linear",
            "QuadIn", "QuadOut", "QuadInOut",
            "CubicIn", "CubicOut", "CubicInOut",
            "QuarticIn", "QuarticOut", "QuarticInOut",
            "QuinticIn", "QuinticOut", "QuinticInOut",
            "SineIn", "SineOut", "SineInOut",
            "ExpoIn", "ExpoOut", "ExpoInOut",
            "CircIn", "CircOut", "CircInOut",
            "ElasticIn", "ElasticOut", "ElasticInOut",
            "BackIn", "BackOut", "BackInOut",
            "BounceIn", "BounceOut", "BounceInOut"
        );
    }
}