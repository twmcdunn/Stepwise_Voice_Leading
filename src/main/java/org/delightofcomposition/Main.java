package org.delightofcomposition;

import java.util.ArrayList;

import org.delightofcomposition.envelopes.LoadEnvs;
import org.delightofcomposition.musicxml.Chord;
import org.delightofcomposition.musicxml.NoteObj;
import org.delightofcomposition.musicxml.XMLWriter;
import org.delightofcomposition.sequencer.Sequencer;

public class Main {
    public static void main(String[] args) {

        System.out.println("HELLO FROM /src/main/java/org/deligthofcomposition/Main.java");
        LoadEnvs.loadEnvs();//wont hurt to leave this always uncommented 

        //built-in parameters to play with
        XMLWriter.ARPEGGIATE = true;
        XMLWriter.USEPEDAL = true;
        XMLWriter.USESLURS = true;

        // TEXTURE A
        //uncomment this after implementing VoiceLeading.stepwiseVoiceLeading
        // VoiceLeadingFramework vl = new VoiceLeading();
        // generateComposition(vl::stepwiseVoiceLeading, null, 50, 4, false);

        // TEXTURE B
        //uncomment this after implementing VoiceLeading implements Directed
        //comment out TEXTURE A
        // Directed vl = (Directed)new VoiceLeading();
        // generateComposition(null, vl::directedVoiceLeading, 50, 4, true);

        // TEXTURE C
        //uncomment this after implementing VoiceLeading implements Directed
        //comment out TEXTURE A and B
        // NoCommons vl = (NoCommons)new VoiceLeading();
        // generateComposition(vl::uncommonVoiceLeading, null, 50, 4, false);

        // TEXTURE D
        //uncomment this after implementing VoiceLeading implements Directed
        //comment out TEXTURE A, B, and C
        // NoCommonsDirected vl = (NoCommonsDirected)new VoiceLeading();
        // generateComposition(null, vl::uncommonDirectedVoiceLeading, 50, 4, true);
    }

    // call this when your done and open the musicxml it generates in the project
    // root
    public static void generateComposition(VL sw, VLTarget vlTarget, int chordCount, int startingOct,
            boolean useTarget) {
        int eighth = 0;
        double eigthDur = 60 / (double) (XMLWriter.QNTEMPO * 2);
        int target = 0;

        ArrayList<Chord> progression = new ArrayList<Chord>();
        Sequencer seq = new Sequencer();
        int[] lastChordNotes = null;
        for (int i = 0; i < chordCount; i++) {
            int[][] minimalProgression = seq.getChords();
            if (i == 0) {
                for (int n = 0; n < minimalProgression[0].length; n++)
                    minimalProgression[0][n] += 12 * (startingOct + 1);
            }
            for (int[] chordNotes : minimalProgression) {
                System.out.println(chordNotes[0] + " " + chordNotes[1] + " " + chordNotes[2]);
                if (lastChordNotes != null) {
                    if (useTarget) {
                        double time = eigthDur * eighth;
                        target = (int)Math.rint(8 * 12 * LoadEnvs.envs.get(0).getValue(time)) + 12;
                        chordNotes = vlTarget.vl(lastChordNotes, chordNotes, target);
                    } else {
                        chordNotes = sw.vl(lastChordNotes, chordNotes);
                    }
                }
                Chord chord = new Chord();
                for (int n : chordNotes) {
                    chord.add(new NoteObj(n));
                    eighth++;
                }
                progression.add(chord);
                lastChordNotes = chordNotes;
            }
        }

        XMLWriter xml = new XMLWriter();
        xml.writeBasicXML(progression);
    }

    @FunctionalInterface
    interface VL {
        int[] vl(int[] firstChord, int[] secondChord);
    }

    @FunctionalInterface
    interface VLTarget {
        int[] vl(int[] firstChord, int[] secondChord, int target);
    }
}
