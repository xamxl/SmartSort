package com.example.smartsort;

import java.util.ArrayList;

public class Individual {

    private String[][] choices;
    private String[][] headers;
    private boolean[][] choiceUnhappy;

    private double[] maxUnhappiness;

    private double[][] weights;

    private String name;

    public Individual copy() {
        double[][] copiedWeights = new double[weights.length][];
        for (int i = 0; i < weights.length; i++) {
            copiedWeights[i] = weights[i].clone();
        }

        String[][] copiedChoices = new String[choices.length][];
        for (int i = 0; i < choices.length; i++) {
            copiedChoices[i] = choices[i].clone();
        }

        String[][] copiedHeaders = new String[headers.length][];
        for (int i = 0; i < headers.length; i++) {
            copiedHeaders[i] = headers[i].clone();
        }

        boolean[][] copiedChoiceUnhappy = new boolean[choices.length][];
        for (int i = 0; i < choices.length; i++) {
            copiedChoiceUnhappy[i] = choiceUnhappy[i].clone();
        }

        double[] copiedMaxUnhappiness = maxUnhappiness.clone();

        return new Individual(name, copiedWeights, copiedChoices, copiedMaxUnhappiness, copiedHeaders, copiedChoiceUnhappy);
    }

    public Individual(String n, double[][] w, String[][] c, double[] mU, String[][] h, boolean[][] cU) {
        name = n;
        weights = w;
        choices = c;
        maxUnhappiness = mU;
        headers = h;
        choiceUnhappy = cU;
    }

    public Individual(ArrayList<String> x, ArrayList<String> h, int[] choiceParameters, double[][] w) {
        weights = w;
        name = x.get(0);

        // Order: rankedChoices, rankedNotChoices, choices, notChoices, rankedItems, rankedNotItems, items, notItems, attributes, attributes1, attributes2, attributes3, attribute4
        choices = new String[13][];
        headers = new String[13][];
        choiceUnhappy = new boolean[13][];
        for (int i = 0; i < choices.length; i++) {
            choices[i] = new String[choiceParameters[i]];
            headers[i] = new String[choiceParameters[i]];
            choiceUnhappy[i] = new boolean[choiceParameters[i]];
        }

        // TODO: Need to check
        int counter = 1;
        for (String[] ch : choices) {
            for (int i = 0; i < ch.length; i++) {
                ch[i] = x.get(i + counter);
            }
            counter += ch.length;
        }
        counter = 1;
        for (String[] he : headers) {
            for (int i = 0; i < he.length; i++) {
                he[i] = h.get(i + counter);
            }
            counter += he.length;
        }

        int nRIM = 0;
        for (int i = choiceParameters[4]; i > 0; i--)
            nRIM += i;
        
        int nRINM = 0;
        for (int i = choiceParameters[5]; i > 0; i--)
            nRINM += i;

        // value below used to be 2 (now is 10)
        maxUnhappiness = new double[] {
            choiceParameters[0] * 10,
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

    public String[] getAttributes4() {
        return choices[12];
    }

    public void setChoiceUnhappyAttributes(int i) {
        choiceUnhappy[8][i] = true;
    }

    public void setChoiceUnhappyAttributes1(int i) {
        choiceUnhappy[9][i] = true;
    }

    public void setChoiceUnhappyAttributes2(int i) {
        choiceUnhappy[10][i] = true;
    }

    public void setChoiceUnhappyAttributes3(int i) {
        choiceUnhappy[11][i] = true;
    }

    public void setChoiceUnhappyAttributes4(int i) {
        choiceUnhappy[12][i] = true;
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
        boolean unhappy = false;
        for (int i = 0; i < choices[0].length; i++) {
            if (l.getName().equals(choices[0][i])) {
                uS += i;
                chosen = true;
                if (i == 0)
                    unhappy = true;
            }
        }
        // value below used to be 2
        if (! chosen)
            uS += choices[0].length * 10;
        if (!unhappy)
            for (int i = 0; i < choiceUnhappy[0].length; i++)
                choiceUnhappy[0][i] = true;
        unhappiness += weights[0][0] * MyUtility.interpolate(maxUnhappiness[0], uS);

        uS = 0;
        unhappy = false;
        for (int i = 0; i < choices[1].length; i++) {
            if (l.getName().equals(choices[1][i])) {
                uS += choices[1].length - i;
                unhappy = true;
            }
        }
        unhappiness += weights[1][0] * MyUtility.interpolate(maxUnhappiness[1], uS);
        if (unhappy)
            for (int i = 0; i < choiceUnhappy[1].length; i++)
                choiceUnhappy[1][i] = true;

        uS = 0;
        chosen = false;
        for (int i = 0; i < choices[2].length; i++) {
            if (l.getName().equals(choices[2][i])) {
                chosen = true;
            }
        }
        if (! chosen) {
            uS += choices[2].length * 2;
            for (int i = 0; i < choiceUnhappy[2].length; i++)
                choiceUnhappy[2][i] = true;
        }
        unhappiness += weights[2][0] * MyUtility.interpolate(maxUnhappiness[2], uS);

        uS = 0;
        for (int i = 0; i < choices[3].length; i++) {
            if (l.getName().equals(choices[3][i])) {
                uS += 10;
            }
        }
        if (uS != 0)
            for (int i = 0; i < choiceUnhappy[3].length; i++)
                choiceUnhappy[3][i] = true;
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
            if (! found) {
                uS += choices[4].length - i;
                choiceUnhappy[4][i] = true;
            }
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
            if (found) {
                uS += choices[5].length - i;
                choiceUnhappy[5][i] = true;
            }
        }
        unhappiness += weights[5][0] * MyUtility.interpolate(maxUnhappiness[5], uS);

        uS = 0;
        for (int i = 0; i < choices[6].length; i++) {
            boolean found = false;
            for (Individual k : fellowMembers)
                if (k.name.equals(choices[6][i])) {
                    found = true;
                }
            if (! found) {
                uS += 2;
                choiceUnhappy[6][i] = true;
            }
        }
        unhappiness += weights[6][0] * MyUtility.interpolate(maxUnhappiness[6], uS);

        uS = 0;
        for (int i = 0; i < choices[7].length; i++) {
            boolean found = false;
            for (Individual k : fellowMembers)
                if (k.name.equals(choices[7][i])) {
                    found = true;
                }
            if (found) {
                uS += 10;
                choiceUnhappy[7][i] = true;
            }
        }
        unhappiness += weights[7][0] * MyUtility.interpolate(maxUnhappiness[7], uS);
        
        return Math.pow(unhappiness, 2);
    }

    public String[] getChoices() {
        // Convert 2D choices array to 1D string array
        int rows = choices.length;
        int totalElements = 0;
        for (int i = 0; i < rows; i++) {
            totalElements += choices[i].length;
        }
        String[] flattenedChoices = new String[totalElements];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < choices[i].length; j++) {
                flattenedChoices[index++] = choices[i][j];
            }
        }
        return flattenedChoices;
    }

    public String[] getHeaders() {
        // Convert 2D headers array to 1D string array
        int rows = headers.length;
        int totalElements = 0;
        for (int i = 0; i < rows; i++) {
            totalElements += headers[i].length;
        }
        String[] flattenedHeaders = new String[totalElements];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < headers[i].length; j++) {
                flattenedHeaders[index++] = headers[i][j];
            }
        }
        return flattenedHeaders;
    }

    public boolean[] getChoiceUnhappy() {
        // Convert 2D choiceUnhappy array to 1D boolean array
        int rows = choiceUnhappy.length;
        int totalElements = 0;
        for (int i = 0; i < rows; i++) {
            totalElements += choiceUnhappy[i].length;
        }
        boolean[] flattenedChoiceUnhappy = new boolean[totalElements];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < choiceUnhappy[i].length; j++) {
                flattenedChoiceUnhappy[index++] = choiceUnhappy[i][j];
            }
        }
        return flattenedChoiceUnhappy;
    }

    public void setFalseChoiceUnhappy() {
        for (int i = 0; i < choiceUnhappy.length; i++) {
            for (int j = 0; j < choiceUnhappy[i].length; j++) {
                choiceUnhappy[i][j] = false;
            }
        }
    }

}
