package org.delightofcomposition;

public interface Directed{
    /*
     * The class should extend StepwiseVoiceLeading (the non-abstract class you
     * wrote).
     */

    /*
     * A varient of the stepwiseVoiceLeading method. Selects the voicing
     * that is closest to a target note. This allows us to control register.
     * We define the "closest voicing" as the one for which the distance between the
     * the target and the note furthest from the target is minimized.
     * Implement this logic in your heuristic. 
     * 
     * Depends on optimalVoiceLeading
     */
    public int[] directedVoiceLeading(int[] firstChord, int[] secondChord, int target);
}
