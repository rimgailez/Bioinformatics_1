package com.company;

import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.DNASequence;
import org.biojava.nbio.core.sequence.compound.AmbiguityDNACompoundSet;

import java.util.*;

public class Calculations {

    private static DNASequence dna;
    private static String reverseComplement;

    public static Map<String, List<String>> findStartAndStopPairs (String sequence) {
        Map<String, List<String>> frameSequences = new HashMap<>();
        try {
            if(dna == null)
                dna = new DNASequence(sequence, AmbiguityDNACompoundSet.getDNACompoundSet());
        } catch (CompoundNotFoundException e) {
            e.printStackTrace();
        }
        if(reverseComplement == null)
            reverseComplement = dna.getReverseComplement().getSequenceAsString();

        frameSequences.put("ForwardStrand: 1 reading frame", findStartAndStopPairs2(sequence));
        frameSequences.put("ForwardStrand: 2 reading frame", findStartAndStopPairs2(sequence.substring(1)));
        frameSequences.put("ForwardStrand: 3 reading frame", findStartAndStopPairs2(sequence.substring(2)));

        frameSequences.put("ReverseStrand: 1 reading frame", findStartAndStopPairs2(reverseComplement));
        frameSequences.put("ReverseStrand: 2 reading frame", findStartAndStopPairs2(reverseComplement.substring(1)));
        frameSequences.put("ReverseStrand: 3 reading frame", findStartAndStopPairs2(reverseComplement.substring(2)));

        return frameSequences;
    }

    public static List<String> findStartAndStopPairs2(String sequence){
        int i;
        List<String> triplets;
        List<String> startStopPairs = new ArrayList<>();
        List<String> sequences = DivideSequenceByStopCodons(sequence);

        for(String str : sequences)
        {
            triplets = DivideSequenceIntoTriplets(str);
            if (triplets.indexOf(Constants.START_CODON) == -1) {
                continue;
            }
            i = 0;
            for(String triplet : triplets) {
                if (triplet.equals(Constants.START_CODON)) {
                    startStopPairs.add(String.join("", triplets.subList(i, triplets.size())));
                }
                i++;
            }
        }
        return startStopPairs;
    }

    public static List<String> DivideSequenceByStopCodons(String sequence) {
        int i = 1;
        int nextCutStart = 0;
        List<String> triplets = DivideSequenceIntoTriplets(sequence);
        List<String> sequences = new ArrayList<>();
        for(String triplet : triplets) {
            if(Arrays.asList(Constants.END_CODONS).contains(triplet)) {
                sequences.add(String.join("", triplets.subList(nextCutStart, i)));
                nextCutStart = i;
            }
            i++;
        }
        return sequences;
    }

    public static List<String> DivideSequenceIntoTriplets(String sequence) {
        List<String> triplets = new ArrayList<>();
        for (int i = 0; i < sequence.length(); i += 3)
            triplets.add(sequence.substring(i, Math.min(3 + i, sequence.length())));
        return triplets;
    }

    public static Map<String, List<String>> findCodonPairs(String sequence){
        Map<String, List<String>> frameSequences = new HashMap<>();
        try {
            if(dna == null)
                dna = new DNASequence(sequence, AmbiguityDNACompoundSet.getDNACompoundSet());
        } catch (CompoundNotFoundException e) {
            e.printStackTrace();
        }
        if(reverseComplement == null)
            reverseComplement = dna.getReverseComplement().getSequenceAsString();

        frameSequences.put("ForwardStrand: 1 reading frame", findCodonPairs2(sequence));
        frameSequences.put("ForwardStrand: 2 reading frame", findCodonPairs2(sequence.substring(1)));
        frameSequences.put("ForwardStrand: 3 reading frame", findCodonPairs2(sequence.substring(2)));

        frameSequences.put("ReverseStrand: 1 reading frame", findCodonPairs2(reverseComplement));
        frameSequences.put("ReverseStrand: 2 reading frame", findCodonPairs2(reverseComplement.substring(1)));
        frameSequences.put("ReverseStrand: 3 reading frame", findCodonPairs2(reverseComplement.substring(2)));

        return frameSequences;
    }

    public static List<String> findCodonPairs2(String sequence){
        List<String> sequences = DivideSequenceByStopCodons(sequence);

        List<String> list;
        int indexOfStartCodon;
        List<String> finishedSequences = new ArrayList<>();

        for(String seq : sequences) {
            list = DivideSequenceIntoTriplets(seq);
            indexOfStartCodon = list.indexOf(Constants.START_CODON);
            if (indexOfStartCodon != -1) {
                finishedSequences.add(String.join("", list.subList(indexOfStartCodon, list.size())));
            }
        }
        return finishedSequences;
    }

    public static Map<String, List<String>> removeShorterSequences(Map<String, List<String>> allFrameSequences){
        Map<String, List<String>> longSequences = new HashMap<>();
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, List<String>> frame : allFrameSequences.entrySet()) {
            for (String sequence : frame.getValue()) {
                if (sequence.length() >= 100) {
                    list.add(sequence);
                }
            }
            longSequences.put(frame.getKey(), list);
            list = new ArrayList<>();
        }
        return longSequences;
    }

    public static Map<String, Float> findCodonFrequency(Map<String, List<String>> longSequences){
        float numOfTriplets = 0;
        float codonCount = 0;
        Map<String, Float> codonCountList = new HashMap<>();
        Map<String, Float> codonFrequencies = new HashMap<>();

        for(String dnaTriplet : Constants.DNA_TRIPLETS) {
            for (Map.Entry<String, List<String>> frame : longSequences.entrySet()) {
                for(String sequence : frame.getValue()) {
                    List<String> triplets = DivideSequenceIntoTriplets(sequence);
                    for(String codon : triplets) {
                        if (codon.equals(dnaTriplet)) {
                            codonCount++;
                        }
                    }
                }
            }
            codonCountList.put(dnaTriplet, codonCount);
            codonCount = 0;
        }

        for(Map.Entry<String, Float> codon : codonCountList.entrySet()) {
            numOfTriplets = numOfTriplets + codon.getValue();
        }

        for(Map.Entry<String, Float> codon : codonCountList.entrySet()) {
            codonFrequencies.put(codon.getKey(), codon.getValue() / numOfTriplets);
        }

        return codonFrequencies;
    }

    public static Map<String, Float> findDicodonFrequency(Map<String, List<String>> longSequences){
        String dicodon;
        float dicodonCount = 0;
        float countOfAllDicodons = 0;
        List<String> sequenceDicodons;
        Map<String, Float> dicodonCountList = new HashMap<>();
        Map<String, Float> dicodonFrequencies = new HashMap<>();

        for(String dnaTriplet1 : Constants.DNA_TRIPLETS) {
            for(String dnaTriplet2 : Constants.DNA_TRIPLETS) {
                dicodon = dnaTriplet1 + dnaTriplet2;
                for(Map.Entry<String, List<String>> frame : longSequences.entrySet()) {
                    for(String sequence : frame.getValue()) {
                        sequenceDicodons = DivideSequenceIntoDicodons(sequence);
                        for(String dic : sequenceDicodons) {
                            if (dic.equals(dicodon)) {
                                dicodonCount++;
                            }
                        }
                    }
                }
                dicodonCountList.put(dicodon, dicodonCount);
                countOfAllDicodons = countOfAllDicodons + dicodonCount;
                dicodonCount = 0;
            }
        }

        for(Map.Entry<String, Float> count : dicodonCountList.entrySet()){
            dicodonFrequencies.put(count.getKey(), count.getValue() / countOfAllDicodons);
        }
        return dicodonFrequencies;
    }

    public static List<String> DivideSequenceIntoDicodons(String sequence) {
        List<String> dicodons = new ArrayList<>();
        List<String> triplets = DivideSequenceIntoTriplets(sequence);

        for (int i = 1; i < triplets.size(); i++) {
            dicodons.add(triplets.get(i - 1) + triplets.get(i));
        }

        return dicodons;
    }

    public static Map<String, List<Float>> calculateCodonMatrix(Map<String, String> allFiles) {
        float matrixCellValue;
        List<Float> values;
        Map<String, List<String>> longProteinSequences;
        Map<String, Map<String, Float>> codonFrequenciesOfAllFiles = new HashMap<>();
        Map<String, List<Float>> matrix = new HashMap<>();

        for (Map.Entry<String, String> entry : allFiles.entrySet()) {
            longProteinSequences = removeShorterSequences(findCodonPairs(entry.getValue()));
            codonFrequenciesOfAllFiles.put(entry.getKey(), findCodonFrequency(longProteinSequences));
        }

        for (Map.Entry<String, Map<String, Float>> fileCodonFrequencies1 : codonFrequenciesOfAllFiles.entrySet()){
            values = new ArrayList<>();
            for (Map.Entry<String, Map<String, Float>> fileCodonFrequencies2 : codonFrequenciesOfAllFiles.entrySet()) {
                matrixCellValue = 0;
                for (Map.Entry<String, Float> codonFrequency : fileCodonFrequencies1.getValue().entrySet()){
                    matrixCellValue = matrixCellValue + Math.abs(codonFrequency.getValue() - fileCodonFrequencies2.getValue().get(codonFrequency.getKey()));
                }
                    values.add(matrixCellValue);
            }
            matrix.put(fileCodonFrequencies1.getKey(), values);
        }
        return matrix;
    }

    public static Map<String, List<Float>> calculateDicodonMatrix(Map<String, String> allFiles) {
        float matrixCellValue;
        List<Float> values;
        Map<String, List<String>> longProteinSequences;
        Map<String, Map<String, Float>> dicodonFrequenciesOfAllFiles = new HashMap<>();
        Map<String, List<Float>> matrix = new HashMap<>();

        for (Map.Entry<String, String> entry : allFiles.entrySet()) {
            longProteinSequences = removeShorterSequences(findCodonPairs(entry.getValue()));
            dicodonFrequenciesOfAllFiles.put(entry.getKey(), findDicodonFrequency(longProteinSequences));
        }

        for (Map.Entry<String, Map<String, Float>> fileDicodonFrequencies1 : dicodonFrequenciesOfAllFiles.entrySet()){
            values = new ArrayList<>();
            for (Map.Entry<String, Map<String, Float>> fileDicodonFrequencies2 : dicodonFrequenciesOfAllFiles.entrySet()) {
                matrixCellValue = 0;
                for (Map.Entry<String, Float> dicodonFrequency : fileDicodonFrequencies1.getValue().entrySet()){
                    matrixCellValue = matrixCellValue + Math.abs(dicodonFrequency.getValue() - fileDicodonFrequencies2.getValue().get(dicodonFrequency.getKey()));
                }
                values.add(matrixCellValue);
            }
            matrix.put(fileDicodonFrequencies1.getKey(), values);
        }
        return matrix;
    }
}
