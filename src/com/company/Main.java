package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Main extends JFrame implements ActionListener{

    private static JFrame frame;
    private static JFrame secondFrame;

    private final JButton buttonAddFile;
    private final JButton buttonFindStartAndStopCodonPairs;
    private final JButton buttonFindCodonPairs;
    private final JButton buttonFindFragments;
    private final JButton buttonEvaluateCodonFrequency;
    private final JButton buttonEvaluateDicodonFrequency;
    private final JButton buttonCompareFrequencies;

    private JButton buttonAddFiles;
    private JButton buttonFindCodonMatrix;
    private JButton buttonFindDicodonMatrix;

    private final JTextArea textFromFile;

    private JTextArea textMatrix;

    private Map<String, List<String>> startAndStopCodonPairs; //1 - visos start ir stop kodonų poros
    private Map<String, List<String>> codonPairs; //2 - kiekvienam stop kodonui toliausiai esantis start kodonas
    private Map<String, List<String>> longFragments; //3 - kiekvienam stop kodonui toliausiai esantis start kodonas, ilgis nuo 100 simbolių
    private Map<String, Float> codonFrequency; //4 - kodonų dažniai
    private Map<String, Float> dicodonFrequency; //4 - dikodonų dažniai

    private Map<String, String> allFiles = new HashMap<>();
    private Map<String, List<Float>> codonMatrix = new HashMap<>();
    private Map<String, List<Float>> dicodonMatrix = new HashMap<>();

    private Main(){
        frame = new JFrame("Bioinformatikos 1 užduotis");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new FlowLayout());

        JLabel label1 = new JLabel("Pridėkite .fasta failą, kurį norite išanalizuoti: ");
        label1.setBounds(20,20, 440,20);
        frame.add(label1);

        buttonAddFile = new JButton("Pridėti failą");
        buttonAddFile.setBounds(20,60,100, 30);
        frame.add(buttonAddFile);
        buttonAddFile.addActionListener(this);

        buttonFindStartAndStopCodonPairs = new JButton("1. Rasti visas START ir STOP kodonų poras");
        buttonFindStartAndStopCodonPairs.setBounds(20,100,400, 30);
        frame.add(buttonFindStartAndStopCodonPairs);
        buttonFindStartAndStopCodonPairs.addActionListener(this);
        buttonFindStartAndStopCodonPairs.setEnabled(false);

        buttonFindCodonPairs = new JButton("2. Rasti kodonų poras su toliausiai esančiu START kodonu");
        buttonFindCodonPairs.setBounds(20,140,400, 30);
        frame.add(buttonFindCodonPairs);
        buttonFindCodonPairs.addActionListener(this);
        buttonFindCodonPairs.setEnabled(false);

        buttonFindFragments = new JButton("3. Atfiltruoti fragmentus trumpesnius nei 100");
        buttonFindFragments.setBounds(20,180,400, 30);
        frame.add(buttonFindFragments);
        buttonFindFragments.addActionListener(this);
        buttonFindFragments.setEnabled(false);

        buttonEvaluateCodonFrequency = new JButton("4. Įvertinti kodonų dažnius");
        buttonEvaluateCodonFrequency.setBounds(20,220,400, 30);
        frame.add(buttonEvaluateCodonFrequency);
        buttonEvaluateCodonFrequency.addActionListener(this);
        buttonEvaluateCodonFrequency.setEnabled(false);

        buttonEvaluateDicodonFrequency = new JButton("4. Įvertinti dikodonų dažnius");
        buttonEvaluateDicodonFrequency.setBounds(20,260,400, 30);
        frame.add(buttonEvaluateDicodonFrequency);
        buttonEvaluateDicodonFrequency.addActionListener(this);
        buttonEvaluateDicodonFrequency.setEnabled(false);

        buttonCompareFrequencies = new JButton("5. Palyginti kodonų/dikodonų dažnius");
        buttonCompareFrequencies.setBounds(20,300,400, 30);
        frame.add(buttonCompareFrequencies);
        buttonCompareFrequencies.addActionListener(this);

        JLabel label = new JLabel("Seka: ");
        label.setBounds(440,20, 100,20);
        frame.add(label);

        textFromFile = new JTextArea();
        textFromFile.setBounds(440, 60, 700, 600);
        textFromFile.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textFromFile);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        frame.add(textFromFile);
        frame.getContentPane().add(scrollPane);

        frame.setSize(1200,750);
        frame.setTitle("Bioinformatikos 1 užduotis");
        frame.setLayout(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Main();
    }

    public void actionPerformed(ActionEvent e) {
        String sequence = textFromFile.getText().replace("\n", "");
        if (e.getSource() == buttonAddFile) {
            try {
                addFile();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            buttonFindStartAndStopCodonPairs.setEnabled(true);
            buttonFindCodonPairs.setEnabled(true);
        }
        if (e.getSource() == buttonFindStartAndStopCodonPairs) {
            findStartAndStopCodonPairs(sequence);
            buttonFindFragments.setEnabled(true);
        }
        if (e.getSource() == buttonFindCodonPairs) {
            findCodonPairs(sequence);
            buttonFindFragments.setEnabled(true);
        }
        if (e.getSource() == buttonFindFragments) {
            findFragments();
            buttonEvaluateCodonFrequency.setEnabled(true);
            buttonEvaluateDicodonFrequency.setEnabled(true);
        }
        if (e.getSource() == buttonEvaluateCodonFrequency) {
            evaluateCodonFrequency();
        }
        if (e.getSource() == buttonEvaluateDicodonFrequency) {
            evaluateDicodonFrequency();
        }
        if (e.getSource() == buttonCompareFrequencies) {
            frequenciesMatrixPage();
        }
        if (e.getSource() == buttonAddFiles) {
            try {
                addFiles();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            buttonFindCodonMatrix.setEnabled(true);
            buttonFindDicodonMatrix.setEnabled(true);
        }
        if (e.getSource() == buttonFindCodonMatrix) {
            findCodonMatrix();
        }
        if (e.getSource() == buttonFindDicodonMatrix) {
            findDicodonMatrix();
        }
    }

    private void frequenciesMatrixPage(){
        secondFrame = new JFrame("Bioinformatikos 1 užduotis");
        secondFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        secondFrame.getContentPane().setLayout(new FlowLayout());

        JLabel label1 = new JLabel("Pridėkite .fasta failus, kuriuos norite išanalizuoti: ");
        label1.setBounds(20,20, 440,20);
        secondFrame.add(label1);

        buttonAddFiles = new JButton("Pridėti failus");
        buttonAddFiles.setBounds(20,60,140, 30);
        secondFrame.add(buttonAddFiles);
        buttonAddFiles.addActionListener(this);

        textMatrix = new JTextArea();
        textMatrix.setBounds(20, 120, 800, 300);
        textMatrix.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textMatrix);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        secondFrame.add(textMatrix);
        secondFrame.getContentPane().add(scrollPane);

        buttonFindCodonMatrix = new JButton("Rasti kodonų matricą");
        buttonFindCodonMatrix.setBounds(20,440,200, 30);
        secondFrame.add(buttonFindCodonMatrix);
        buttonFindCodonMatrix.addActionListener(this);
        buttonFindCodonMatrix.setEnabled(false);

        buttonFindDicodonMatrix = new JButton("Rasti dikodonų matricą");
        buttonFindDicodonMatrix.setBounds(620,440,200, 30);
        secondFrame.add(buttonFindDicodonMatrix);
        buttonFindDicodonMatrix.addActionListener(this);
        buttonFindDicodonMatrix.setEnabled(false);

        secondFrame.setSize(1000,550);
        secondFrame.setTitle("Bioinformatikos 1 užduotis");
        secondFrame.setLayout(null);
        secondFrame.setVisible(true);
    }

    private void addFile() throws IOException{
        FileDialog fd = new FileDialog(frame, "Choose a file", FileDialog.LOAD);
        fd.setDirectory("C:\\");
        fd.setFile("*.fasta");
        fd.setVisible(true);
        String text = "";
        try (Scanner sc = new Scanner(new File(fd.getDirectory() + fd.getFile()))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.charAt(0) == '>') {
                    System.out.println(line.substring(1));
                } else {
                    text = text + line + "\n";
                }
            }
        }
        textFromFile.setText(text);
    }

    private void addFiles() throws IOException{
        textMatrix.setText("Pridėti failai: \r\n\r\n");
        codonMatrix.clear();
        dicodonMatrix.clear();
        allFiles.clear();
        FileDialog fd = new FileDialog(secondFrame, "Choose files", FileDialog.LOAD);
        fd.setMultipleMode(true);
        fd.setDirectory("C:\\");
        fd.setFile("*.fasta");
        fd.setVisible(true);
        String text = "";
        String name = "";
        File[] files = fd.getFiles();
        for(File file : files){
            try (Scanner sc = new Scanner(new File(file.getAbsolutePath()))) {
                while (sc.hasNextLine()) {
                    String line = sc.nextLine().trim();
                    if (line.charAt(0) == '>') {
                        name = file.getName();
                        textMatrix.append(file.getName() + "\n");
                    } else {
                        text = text + line;
                    }
                }
                allFiles.put(name, text);
                text = "";
            }
        }
    }

    private void findStartAndStopCodonPairs(String text){
        startAndStopCodonPairs = Calculations.findStartAndStopPairs(text);
        showResults(startAndStopCodonPairs);
    }

    private void findCodonPairs(String text){
        codonPairs = Calculations.findCodonPairs(text);
        showResults(codonPairs);
    }

    private void findFragments(){
        longFragments = Calculations.removeShorterSequences(codonPairs);
        showResults(longFragments);
    }

    private void evaluateCodonFrequency(){
        codonFrequency = Calculations.findCodonFrequency(longFragments);
        showFrequencies("Codon frequencies: ", codonFrequency);
    }

    private void evaluateDicodonFrequency(){
        dicodonFrequency = Calculations.findDicodonFrequency(longFragments);
        showFrequencies("Dicodon frequencies: ", dicodonFrequency);
    }

    private void showResults(Map<String, List<String>> pairs){
        String text = "";
        for (Map.Entry<String, List<String>> entry : pairs.entrySet()) {
            text = text + entry.getKey() + "\r\n" + "\r\n" + String.join("; \n", entry.getValue()) + "\r\n" + "\r\n";
        }
        textFromFile.setText(text);
    }

    private void showFrequencies(String type, Map<String, Float> frequencies)
    {
        String text = "\r\n           " + type + " \r\n";
        for (Map.Entry<String, Float> entry : frequencies.entrySet()) {
            text = text + "======================================================================================\r\n";
            text = text + "            " + entry.getKey() + " : " + entry.getValue() + " \r\n";
        }
        textFromFile.setText(text);
    }

    private void findCodonMatrix(){
        codonMatrix = Calculations.calculateCodonMatrix(allFiles);
        showMatrix(codonMatrix);
    }

    private void findDicodonMatrix(){
        dicodonMatrix = Calculations.calculateDicodonMatrix(allFiles);
        showMatrix(dicodonMatrix);
    }

    private void showMatrix(Map<String, List<Float>> matrix){
        String text = "";
        textMatrix.setText(matrix.size() + "\r\n");
        for (Map.Entry<String, List<Float>> entry : matrix.entrySet()) {
            text = text + entry.getKey() + " " + String.join(" ", entry.getValue().toString()) + "\r\n";
        }
        textMatrix.append(text);
    }
}
