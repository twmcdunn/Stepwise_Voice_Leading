package org.delightofcomposition.musicxml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import org.audiveris.proxymusic.*;
import org.audiveris.proxymusic.util.Marshalling;
import org.audiveris.proxymusic.ScorePartwise.Part;
import org.audiveris.proxymusic.ScorePartwise.Part.Measure;

public class XMLWriter {

    public static int QNTEMPO = 240;
    public static boolean USEPEDAL = true;
    public static boolean USESLURS = true;
    public static boolean ARPEGGIATE = true;
    public static ObjectFactory factory = new ObjectFactory();
    public static Step[] diatonicSteps = new Step[] {
            Step.C,
            Step.D,
            Step.E,
            Step.F,
            Step.G,
            Step.A,
            Step.B
    };
    public static Step[] sharpSteps = new Step[] { Step.C, Step.C, Step.D, Step.D, Step.E, Step.F, Step.F, Step.G,
            Step.G, Step.A,
            Step.A, Step.B };
    public static Step[] flatSteps = new Step[] { Step.C, Step.D, Step.D, Step.E, Step.E, Step.F, Step.G, Step.G,
            Step.A, Step.A,
            Step.B, Step.B };
    public static boolean[] accidental = new boolean[] { false, true, false, true, false, false, true, false, true,
            false, true,
            false };

    public void writeBasicXML(ArrayList<Chord> chords) {
        Work work = factory.createWork();
        work.setWorkTitle("Composition");

        ScorePartwise scorePartwise = factory.createScorePartwise();
        scorePartwise.setWork(work);

        PartList partList = factory.createPartList();
        scorePartwise.setPartList(partList);

        java.lang.String[] partNames = new java.lang.String[] { "Piano" };

        Part[] parts = new Part[partNames.length];

        for (int i = 0; i < partNames.length; i++) {
            ScorePart scorePart = factory.createScorePart();
            scorePart.setId("P" + (i + 2));
            PartName partName = factory.createPartName();
            partName.setValue(partNames[i]);
            scorePart.setPartName(partName);

            partList.getPartGroupOrScorePart().add(scorePart);
            // partwise part is where we write the actual musical content.
            // scorePart is jsut for the partlist
            // they're linked by id
            ScorePartwise.Part part = factory.createScorePartwisePart();
            scorePartwise.getPart().add(part);
            part.setId(scorePart);// links partwisepart to scorePart
            parts[i] = part;
        }

        writeMeasures(parts[0], chords);

        File xmlFile = new File("composition.xml");
        try {
            OutputStream os = new FileOutputStream(xmlFile);
            Marshalling.marshal(scorePartwise, os, true, 2);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public void writeMeasures(ScorePartwise.Part part, ArrayList<Chord> chords) {

        if (chords.size() % 2 == 1)
            chords.add(new Chord());// empty chord to make it even

        int eigthhsPerMeasure = -1;

        int measureNum = 1;
        for (int i = 0; i < chords.size() - 1; i += 2) {

            Measure measure = factory.createScorePartwisePartMeasure();
            part.getMeasure().add(measure);
            measure.setNumber(measureNum + "");
            measureNum++;

            Attributes attributes = factory.createAttributes();
            measure.getNoteOrBackupOrForward().add(attributes);
            attributes.setDivisions(new BigDecimal(1));
            attributes.setStaves(new BigInteger("2"));

            if (i == 0) {
                Key key = factory.createKey();
                attributes.getKey().add(key);
                key.setFifths(new BigInteger("0"));

                Clef clef = factory.createClef();
                attributes.getClef().add(clef);
                clef.setSign(ClefSign.G);
                clef.setLine(new BigInteger("2"));
                clef.setNumber(new BigInteger("1"));

                // Add bass clef for second staff
                Clef clef2 = factory.createClef();
                attributes.getClef().add(clef2);
                clef2.setSign(ClefSign.F);
                clef2.setLine(new BigInteger("4"));
                clef2.setNumber(new BigInteger("2"));

                Direction direction = factory.createDirection();
                DirectionType directionType = factory.createDirectionType();
                direction.getDirectionType().add(directionType);

                Metronome metronome = factory.createMetronome();
                // beatUnit dots perMinut parenthesis tempo
                // DirectionData("quarter", 1, "c. 100-120", YesNo.YES, new BigDecimal(110));
                metronome.setBeatUnit("quarter");
                metronome.getBeatUnitDot().add(factory.createEmpty());// add one dot
                PerMinute perMinute = factory.createPerMinute();
                perMinute.setValue((2 * QNTEMPO / 3) + "");
                metronome.setPerMinute(perMinute);
                metronome.setParentheses(YesNo.NO);

                directionType.setMetronome(metronome);

                Sound sound = factory.createSound();
                sound.setTempo(new BigDecimal(QNTEMPO));
                direction.setSound(sound);
                measure.getNoteOrBackupOrForward().add(direction);
            }

            // time signature
            int eighths = chords.get(i).size() + chords.get(i + 1).size();
            if (!ARPEGGIATE) {
                eighths = 2;
            }
            if (eigthhsPerMeasure != eighths) {
                Time time = factory.createTime();
                attributes.getTime().add(time);
                time.getTimeSignature().add(factory.createTimeBeats(eighths + ""));
                time.getTimeSignature().add(factory.createTimeBeatType("8"));
                eigthhsPerMeasure = eighths;
            }
            for (int j = 0; j < 2; j++) {
                Chord chord = chords.get(i + j);

                for (int n = 0; n < chord.size(); n++) {

                    NoteObj no = chord.get(n);

                    Note note = factory.createNote();

                    if (n == 0) {
                        if (USESLURS && ARPEGGIATE) {
                            Slur slur = factory.createSlur();
                            slur.setType(StartStopContinue.START);
                            // slur.setId(slurNums[v] + "");
                            Notations notations = factory.createNotations();
                            notations.getTiedOrSlurOrTuplet().add(slur);
                            note.getNotations().add(notations);
                        }

                        if (USEPEDAL && ARPEGGIATE) {
                            Direction direction = factory.createDirection();
                            DirectionType directionType = factory.createDirectionType();
                            direction.getDirectionType().add(directionType);
                            direction.setStaff(new BigInteger("2"));

                            Pedal pedal = factory.createPedal();
                            pedal.setType(PedalType.START);
                            directionType.setPedal(pedal);

                            measure.getNoteOrBackupOrForward().add(direction);
                        }
                    } else if (n == chord.size() - 1) {
                        if (USESLURS && ARPEGGIATE) {
                            Slur slur = factory.createSlur();
                            slur.setType(StartStopContinue.STOP);
                            // slur.setId(slurNums[v] + "");
                            Notations notations = factory.createNotations();
                            notations.getTiedOrSlurOrTuplet().add(slur);
                            note.getNotations().add(notations);
                        }

                        if (USEPEDAL && ARPEGGIATE) {
                            Direction direction = factory.createDirection();
                            DirectionType directionType = factory.createDirectionType();
                            direction.getDirectionType().add(directionType);
                            direction.setStaff(new BigInteger("2"));

                            Pedal pedal = factory.createPedal();
                            pedal.setType(PedalType.STOP);
                            directionType.setPedal(pedal);

                            measure.getNoteOrBackupOrForward().add(direction);
                        }
                    }

                    Pitch p = factory.createPitch();
                    p.setStep(sharpSteps[no.midi % 12]);
                    p.setOctave(no.midi / 12 - 1);
                    p.setAlter(new BigDecimal("" + (accidental[no.midi % 12] ? 1 : 0)));

                    note.setPitch(p);

                    NoteType type = factory.createNoteType();
                    note.setType(type);

                    note.setDuration(new BigDecimal("0.5"));
                    type.setValue("eighth");

                    note.setStaff(new BigInteger(no.midi >= 60 ? "1" : "2"));
                    if (!ARPEGGIATE && n > 0)
                        note.setChord(new Empty());
                    measure.getNoteOrBackupOrForward().add(note);
                }

            }
        }
    }
}
