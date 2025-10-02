package org.delightofcomposition;

public interface NoCommons {
    /*
     * A varient of the stepwiseVoiceLeading method. Selects the voicing
     * that is most stepwise, but uses no common tones. Implement the
     * same heuristic as in stepwiseVoiceLeading, but if there is a common-tone
     * return Integer.MAX_VALUE
     * 
     * Depends on optimalVoiceLeading
     */
    public int[] uncommonVoiceLeading(int[] firstChord, int[] secondChord);
}
