package com.example.smartsort;

import java.util.ArrayList;

public class Location {

    final private int capacity;
    private int minimum;
    private ArrayList<Individual> members = new ArrayList<>();
    private String name;

    private double[][] maxUnhappiness;
    static double[] maxUnhappiness1;

    private double[][] weights;

    boolean unhappy = false;

    public boolean isUnhappy() {
        return unhappy;
    }

    // Note: maxUnhappiness1 is not copied since it is static and is shared by all classes
    public Location copy() {
        ArrayList<Individual> newMembers = new ArrayList<>();
        for (Individual i : members) {
            newMembers.add(i.copy());
        }

        double[][] newMaxUnhappiness = new double[maxUnhappiness.length][];
        for (int i = 0; i < maxUnhappiness.length; i++) {
            newMaxUnhappiness[i] = new double[maxUnhappiness[i].length];
            for (int j = 0; j < maxUnhappiness[i].length; j++) {
                newMaxUnhappiness[i][j] = maxUnhappiness[i][j];
            }
        }

        double[][] newWeights = new double[weights.length][];
        for (int i = 0; i < weights.length; i++) {
            newWeights[i] = new double[weights[i].length];
            for (int j = 0; j < weights[i].length; j++) {
                newWeights[i][j] = weights[i][j];
            }
        }

        return new Location(capacity, minimum, newMembers, name, newMaxUnhappiness, newWeights);
    }

    public Location(int c, int min, ArrayList<Individual> mem, String n, double[][] mU, double[][] w) {
        capacity = c;
        minimum = min;
        members = mem;
        name = n;
        maxUnhappiness = mU;
        weights = w;
    }

    public Location(int c, String n, int m, double[][] w) {
        weights = w;
        capacity = c;
        name = n;
        minimum = m;

        // Added one more spot
        maxUnhappiness = new double[][] {{Math.pow(minimum * 60, 2)}, {}, {}, {}, {}, {}};
    }

    // TODO: This and other max need to be checked.
    public void calculateMaxUnhappiness(Sort e) {
        maxUnhappiness1 = new double[e.getIndividuals()[0].getAttributes().length];
        
        maxUnhappiness[2] = new double[e.getIndividuals()[0].getAttributes1().length];
        for (int i = 0; i < e.getIndividuals()[0].getAttributes1().length; i ++) {
            ArrayList<String> attributeOptions1 = e.getAttribute1Options(i);
            maxUnhappiness[2][i] = 10 * (1 - (1.0 / attributeOptions1.size()));
        }

        int maxLocationSize = 0;
        for (Location l : e.getLocations())
            if (l.getCapacity() > maxLocationSize)
                maxLocationSize = l.getCapacity();
        maxUnhappiness[3] = new double[e.getIndividuals()[0].getAttributes2().length];
        for (int i = 0; i < e.getIndividuals()[0].getAttributes2().length; i ++) {
            ArrayList<String> attributeOptions2 = e.getAttribute2Options(i);
            maxUnhappiness[3][i] += Math.min(attributeOptions2.size(), maxLocationSize);
        }

        maxUnhappiness[4] = new double[e.getIndividuals()[0].getAttributes3().length];
        for (int i = 0; i < e.getIndividuals()[0].getAttributes3().length; i++) {
            maxUnhappiness[4][i] += maxLocationSize;
        }

        maxUnhappiness[5] = new double[e.getIndividuals()[0].getAttributes4().length];
        for (int i = 0; i < e.getIndividuals()[0].getAttributes4().length; i++) {
            ArrayList<String> attributeOptions4 = e.getAttribute4Options(i);
            int[] attributeCounts = e.getAttribute4Counts(i, attributeOptions4);
            for (int j = 0; j < attributeCounts.length; j++) {
                maxUnhappiness[5][i] += attributeCounts[j] - 1;
            }
        }
    }

    public boolean isSpace() {
        if (capacity > members.size())
            return true;
        return false;
    }

    public int numberOfMembers() {
        return members.size();
    }

    public void addMember(Individual ind) {
        members.add(ind);
    }

    public void removeLastMember() {
        members.remove(members.size()-1);
    }

    public Individual removeMember(int x) {
        return members.remove(x);
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        name = n;
    }

    public int getCapacity() {
        return capacity;
    }

    public ArrayList<Individual> getMembers() {
        return members;
    }

    public double calculateUnhappiness(Sort e) {
        double unhappiness = 0;
        // TODO: All weight indexes need to be checked
        unhappy = false;
        if (members.size() == 0) {
            unhappiness += weights[10][0] * MyUtility.interpolate(maxUnhappiness[0][0], Math.pow(minimum * 60, 2));
            unhappy = true;
        } else if (members.size() < minimum) {
            unhappiness += weights[10][0] * MyUtility.interpolate(maxUnhappiness[0][0], Math.pow((minimum - members.size()) * 60, 2));
            unhappy = true;
        }
        if (members.size() != 0) {
            // TOOD: Why does this balancing not work instantly?
            // TODO: Is calling getAttributes every time redundant?
            double uS;
            for (int i = 0; i < members.get(0).getAttributes().length; i++) {
                uS = 0;
                ArrayList<String> attributeOptions = e.getAttributeOptions(i);
                //TODO: what are these lines doing here?
                /*ArrayList<Integer> attributeCountsAll = e.getAttributeCounts(i);
                int totalSize = 0;
                for (Individual k : e.getIndividuals()) {
                    totalSize++;
                }*/
                double[] attributeCounts = new double[attributeOptions.size()];
                for (Individual k : members) {
                    for (int j = 0; j < attributeOptions.size(); j++) {
                        if (attributeOptions.get(j).equals(k.getAttributes()[i]))
                            attributeCounts[j]++;
                    }
                }
                for (int k = 0; k < attributeCounts.length; k++) {
                    uS += 10 * Math.abs(attributeCounts[k] / members.size() - (1.0 / attributeOptions.size()));
                    // TODO: make below work
                    // you should be able to be one off when the proportion does not line up exactly?
                    // test what you have now with differnet size groups (even sized groups)
                    // and think about the logic out loud. to a parent?
                    if ((members.size() * (1.0 / attributeOptions.size()) != (int) (members.size() * (1.0 / attributeOptions.size())))) {
                        // do problem is that it rounds up to 5, then can be one off, so can be 6
                        // so really you should round down and then check if it is that or one bigger?
                        // and then all other fancy stuff to check the 2, 3, 4, 5, and 6 cases?
                        if ((int) (members.size() * (1.0 / attributeOptions.size())) - attributeCounts[k] >= 1 || Math.abs((int) (members.size() * (1.0 / attributeOptions.size())) - attributeCounts[k]) > 1) {
                            for (Individual t : members) {
                                t.setChoiceUnhappyAttributes(i);
                            }
                        }
                    } else {
                        if (Math.abs((members.size() * (1.0 / attributeOptions.size())) - attributeCounts[k]) != 0) {
                            for (Individual t : members) {
                                t.setChoiceUnhappyAttributes(i);
                            }
                        }
                    }
                    /*if (Math.abs((int) (members.size() * (1.0 / attributeOptions.size()) + .5) - attributeCounts[k]) > 1) {
                        for (Individual t : members) {
                            t.setChoiceUnhappyAttributes(i);
                        }
                    }*/
                }
                if (uS > maxUnhappiness1[i]) {
                    maxUnhappiness1[i] = uS;
                }
                unhappiness += weights[8][i] * MyUtility.interpolate(maxUnhappiness1[i], uS);
            }

            for (int i = 0; i < members.get(0).getAttributes1().length; i++) {
                uS = 0;
                ArrayList<String> attributeOptions = e.getAttribute1Options(i);
                double[] attributeCounts = new double[attributeOptions.size()];
                for (Individual k : members) {
                    for (int j = 0; j < attributeOptions.size(); j++) {
                        if (attributeOptions.get(j).equals(k.getAttributes1()[i]))
                            attributeCounts[j]++;
                    }
                }
                int maxIndex = 0;
                for (int k = 0; k < attributeCounts.length; k++)
                    if (attributeCounts[k] > attributeCounts[maxIndex])
                        maxIndex = k;
                for (int k = 0; k < attributeCounts.length; k++) {
                    if (k == maxIndex)
                        uS += 10 * (1 - attributeCounts[k] / members.size());
                }
                if (uS > 0) {
                    for (Individual k : members) {
                        k.setChoiceUnhappyAttributes1(i);
                    }
                }
                unhappiness += weights[9][i] * MyUtility.interpolate(maxUnhappiness[2][i], uS);
            }

            // TODO: Fix iteration for this attribute
            for (int i = 0; i < members.get(0).getAttributes2().length; i++) {
                uS = 0;
                ArrayList<String> attributeOptions = e.getAttribute2Options(i);
                double[] attributeCounts = new double[attributeOptions.size()];
                for (Individual k : members) {
                    for (int j = 0; j < attributeOptions.size(); j++) {
                        if (attributeOptions.get(j).equals(k.getAttributes2()[i]))
                            attributeCounts[j]++;
                    }
                }
                for (int k = 0; k < attributeCounts.length; k++) {
                    if (1 == attributeCounts[k]) {
                        uS += 1;
                    }
                }
                if (uS > 0) {
                    for (Individual k : members) {
                        k.setChoiceUnhappyAttributes2(i);
                    }
                } else {
                    for (Individual k : members) {
                    }
                }
                unhappiness += weights[11][i] * MyUtility.interpolate(maxUnhappiness[3][i], uS);
            }

            for (int i = 0; i < members.get(0).getAttributes3().length; i++) {
                uS = 0;
                ArrayList<String> attributeOptions = e.getAttribute3Options(i);
                double[] attributeCounts = new double[attributeOptions.size()];
                for (Individual k : members) {
                    for (int j = 0; j < attributeOptions.size(); j++) {
                        if (attributeOptions.get(j).equals(k.getAttributes3()[i]))
                            attributeCounts[j]++;
                    }
                }
                for (int k = 0; k < attributeCounts.length; k++) {
                    if (attributeCounts[k] > 1) {
                        uS += attributeCounts[k];
                    }
                }
                if (uS > 0) {
                    for (Individual k : members) {
                        k.setChoiceUnhappyAttributes3(i);
                    }
                }
                unhappiness += weights[12][i] * MyUtility.interpolate(maxUnhappiness[4][i], uS);
            }

            for (int i = 0; i < members.get(0).getAttributes4().length; i++) {
                uS = 0;
                ArrayList<String> attributeOptions = e.getAttribute4Options(i);
                int[] attributeFullCounts = e.getAttribute4Counts(i, attributeOptions);
                int[] attributeCounts = new int[attributeOptions.size()];
                for (Individual k : members) {
                    for (int j = 0; j < attributeOptions.size(); j++) {
                        if (attributeOptions.get(j).equals(k.getAttributes4()[i]))
                            attributeCounts[j]++;
                    }
                }

                for (int k = 0; k < attributeCounts.length; k++) {
                    if (attributeCounts[k] != 0 && attributeCounts[k] != attributeFullCounts[k]) {
                        uS += attributeFullCounts[k] - attributeCounts[k];
                    }
                }
                if (uS > 0) {
                    for (Individual k : members) {
                        k.setChoiceUnhappyAttributes4(i);
                    }
                }
                unhappiness += weights[13][i] * MyUtility.interpolate(maxUnhappiness[5][i], uS);
            }
        }

        return Math.pow(unhappiness, 2);
    }

}
