package org.delightofcomposition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class VoiceLeadingFramework {
    /*
     * Implement this with a lambda expression and pass it
     * as a parameter to the voiceleading method below.
     * E.g. if the goal were to find the voiceleading with
     * the lowest first note in the second chord, we would call
     * optimalVoiceLeading(firstChord, secondChord, (f,s) -> s[0])
     * or the highest first note:
     * optimalVoiceLeading(firstChord, secondChord, (f,s) -> -s[0])
     * 
     * 1. In StepwiseVoiceLeading, implement this add up the
     * total half-steps moved horizontally in each voice.
     * 
     * 2. In DirectedStepwiseVoiceLeading, implement this to
     * return the distance in half-steps from the target note.
     */
    @FunctionalInterface
    interface Heuristic {
        int getValue(int[] firstChord, int[] secondChord);
    }

    /*
     * Helper method.
     * Takes pitches (don't need to be mod 12)
     * Returns every permutation of notes.
     * 
     * Use a recursive backtracking algorithm for depth-first search
     * 1. give note at index 0 a turn being first note
     * 2. recursively pass remaining notes to the method itself for ordering
     * 3. give next note, index 1, a turn being first note ("backtracking")
     * 4. etc.
     * 
     * Be sure to handle base case, where notesToOrder.size() == 1
     */
    public abstract ArrayList<ArrayList<Integer>> getAllOrders(ArrayList<Integer> notesToOrder);

    /*
     * Helper method
     * Takes two pitches where the first is ** NOT ** in mod 12
     * returns the second pitch in the octave closest to the first pitch
     */
    public abstract int pitchInClosestOct(int target, int pc);

    /*
     * Helper, implemented for you, for data type conversion
     */
    public ArrayList<Integer> arrToArrList(int[] arr) {
        return new ArrayList<Integer>(Arrays.stream(arr).boxed().collect(Collectors.toList()));
    }

    /*
     * Takes two chords and a heuristic function
     * Returns the second chord voiced in a way that minimizes the
     * value returned by the heuristic function
     * 
     * Depends on getAllOrders, pitchInClosestOct, and arrToArrList
     * 
     * 1. Convert second to array list, using helper
     * 2. Get all possible orders of the notes of second chord (as an array list)
     * 3. For each order, voice each note in the closest octave to the note at the
     * same respective index in the first chord
     * 4. call the function (heuristic.execute(firstChord,secondChord)) to retreive
     * the heuristic value for the given voicing
     * 5. store the octave-adjusted voicing with the smallest heuristic value
     * and store the heuristic value itself to compare with alternative voicings
     * (tournament style)
     * 6. return the optimal voicing as an array of ints in their proper octaves
     * (not mod 12)
     */
    public abstract int[] optimalVoiceLeading(int[] firstChord, int[] secondChord, Heuristic heuristic);

    /*
     * depends on optimalVoiceLeading.
     * 1. call optimalVoiceLeading
     * 2. implement a heuristic function that adds up the total
     * horizontal motion of each voice.  E.g. if all voices have
     * common tones, except the bottom voice, which moves a 6th
     * third, the heuristic should return 8.
     * 3. return the result of calling optimalVoiceLeading with your
     * chords and heuristic
     */
    public abstract int[] stepwiseVoiceLeading(int[] firstChord, int[] secondChord);
}
