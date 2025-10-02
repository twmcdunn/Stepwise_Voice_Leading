package org.delightofcomposition;

public interface NoCommonsDirected {
    /*
     * A varient of the stepwiseVoiceLeading method. Selects the voicing
     * that is closest to a target note, but uses no common tones. Implement the
     * same heuristic as in directedVoiceLeading, but if there is a common-tone
     * return Integer.MAX_VALUE
     * 
     * Depends on optimalVoiceLeading
     */
    public int[] uncommonDirectedVoiceLeading(int[] firstChord, int[] secondChord, int target);

}
