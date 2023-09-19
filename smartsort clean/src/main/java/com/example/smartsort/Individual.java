package com.example.smartsort;

import java.util.ArrayList;

public class Individual {

    private String[][] choices;

    private double[] maxUnhappiness;

    private double[][] weights;

    private String name;

    public Individual(ArrayList<String> x, int[] choiceParameters, double[][] w) {
        weights = w;
        name = x.get(0);

        // Order: rankedChoices, rankedNotChoices, choices, notChoices, rankedItems, rankedNotItems, items, notItems, attributes, attributes1, attributes2, attributes3
        choices = new String[12][];
        for (int i = 0; i < choices.length; i++)
            choices[i] = new String[choiceParameters[i]];

        // TODO: Need to check
        int counter = 1;
        for (String[] ch : choices) {
            for (int i = 0; i < ch.length; i++) {
                ch[i] = x.get(i + counter);
            }
            counter += ch.length;
        }

        int nRIM = 0;
        for (int i = choiceParameters[4]; i > 0; i--)
            nRIM += i;
        
        int nRINM = 0;
        for (int i = choiceParameters[5]; i > 0; i--)
            nRINM += i;

        maxUnhappiness = new double[] {
            choiceParameters[0] * 2,
            choiceParameters[1],
            choiceParameters[2] * 2,
            10,
            choiceParameters[4] * 2 + nRIM,
            nRINM,
            2 * choiceParameters[6],
            10 * choiceParameters[7]
        };


    }

    public String[] getAttributes() {
        return choices[8];
    }

    public String[] getAttributes1() {
        return choices[9];
    }
    
    public String[] getAttributes2() {
        return choices[10];
    }  

    public String[] getAttributes3() {
        return choices[11];
    }

    public String getName() {
        return name;
    }

    public double calculateUnhappiness(Location l) {
        double unhappiness = 0;
        ArrayList<Individual> fellowMembers = l.getMembers();
        
        // TODO: All of these should be checked
        double uS = 0;

        boolean chosen = false;
        for (int i = 0; i < choices[0].length; i++) {
            if (l.getName().equals(choices[0][i])) {
                uS += i;
                chosen = true;
            }
        }
        if (! chosen)
            uS += choices[0].length * 2;
        unhappiness += weights[0][0] * MyUtility.interpolate(maxUnhappiness[0], uS);

        uS = 0;
        for (int i = 0; i < choices[1].length; i++) {
            if (l.getName().equals(choices[1][i]))
                uS += choices[1].length - i;
        }
        unhappiness += weights[1][0] * MyUtility.interpolate(maxUnhappiness[1], uS);

        uS = 0;
        chosen = false;
        for (int i = 0; i < choices[2].length; i++) {
            if (l.getName().equals(choices[2][i])) {
                chosen = true;
            }
        }
        if (! chosen)
            uS += choices[2].length * 2;
        unhappiness += weights[2][0] * MyUtility.interpolate(maxUnhappiness[2], uS);

        uS = 0;
        for (int i = 0; i < choices[3].length; i++) {
            if (l.getName().equals(choices[3][i])) {
                uS += 10;
            }
        }
        unhappiness += weights[3][0] * MyUtility.interpolate(maxUnhappiness[3], uS);

        uS = 0;
        chosen = false;
        for (int i = 0; i < choices[4].length; i++) {
            boolean found = false;
            for (Individual k : fellowMembers)
                if (k.name.equals(choices[4][i])) {
                    found = true;
                    chosen = true;
                }
            if (! found)
                uS += choices[4].length - i;
        }
        if (! chosen)
            uS += choices[4].length * 2;
        unhappiness += weights[4][0] * MyUtility.interpolate(maxUnhappiness[4], uS);

        uS = 0;
        for (int i = 0; i < choices[5].length; i++) {
            boolean found = false;
            for (Individual k : fellowMembers)
                if (k.name.equals(choices[5][i])) {
                    found = true;
                    chosen = true;
                }
            if (found)
                uS += choices[5].length - i;
        }
        unhappiness += weights[5][0] * MyUtility.interpolate(maxUnhappiness[5], uS);

        uS = 0;
        for (int i = 0; i < choices[6].length; i++) {
            boolean found = false;
            for (Individual k : fellowMembers)
                if (k.name.equals(choices[6][i])) {
                    found = true;
                }
            if (! found)
                uS += 2;
        }
        unhappiness += weights[6][0] * MyUtility.interpolate(maxUnhappiness[6], uS);

        uS = 0;
        for (int i = 0; i < choices[7].length; i++) {
            boolean found = false;
            for (Individual k : fellowMembers)
                if (k.name.equals(choices[7][i])) {
                    found = true;
                }
            if (found)
                uS += 10;
        }
        unhappiness += weights[7][0] * MyUtility.interpolate(maxUnhappiness[7], uS);
        
        return Math.pow(unhappiness, 2);
    }

}
