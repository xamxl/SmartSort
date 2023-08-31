package com.example.smartsort;

import java.util.ArrayList;

public class Location {

    final private int capacity;
    private int minimum;
    private ArrayList<Individual> members = new ArrayList<>();
    private String name;

    private double[] maxUnhappiness;
    static double maxUnhappiness1 = 0;

    private double[] weights;

    public Location(int c, String n, int m, double[] w) {
        weights = w;
        capacity = c;
        name = n;
        minimum = m;

        maxUnhappiness = new double[] {Math.pow(minimum * 60, 2), 0, 0, 0, 0};
    }

    // TODO: This and other max need to be checked.
    public void calculateMaxUnhappiness(Sort e) {
        for (int i = 0; i < e.getIndividuals()[0].getAttributes1().length; i ++) {
            ArrayList<String> attributeOptions1 = e.getAttribute1Options(i);
            maxUnhappiness[2] += 10 * (1 - (1.0 / attributeOptions1.size()));
        }

        int maxLocationSize = 0;
        for (Location l : e.getLocations())
            if (l.getCapacity() > maxLocationSize)
                maxLocationSize = l.getCapacity();
        for (int i = 0; i < e.getIndividuals()[0].getAttributes2().length; i ++) {
            ArrayList<String> attributeOptions2 = e.getAttribute2Options(i);
            maxUnhappiness[3] += Math.min(attributeOptions2.size(), maxLocationSize);
        }

        maxUnhappiness[4] += maxLocationSize * e.getIndividuals()[0].getAttributes3().length;
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
        if (members.size() == 0)
            unhappiness += weights[10] * MyUtility.interpolate(maxUnhappiness[0], Math.pow(minimum * 60, 2));
        else if (members.size() < minimum)
            unhappiness += weights[10] * MyUtility.interpolate(maxUnhappiness[0], Math.pow((minimum - members.size()) * 60, 2));
        if (members.size() != 0) {
            // TOOD: Why does this balancing not work instantly?
            double uS = 0;
            for (int i = 0; i < members.get(0).getAttributes().length; i++) {
                ArrayList<String> attributeOptions = e.getAttributeOptions(i);
                double[] attributeCounts = new double[attributeOptions.size()];
                for (Individual k : members) {
                    for (int j = 0; j < attributeOptions.size(); j++) {
                        if (attributeOptions.get(j).equals(k.getAttributes()[i]))
                            attributeCounts[j]++;
                    }
                }
                for (int k = 0; k < attributeCounts.length; k++)
                    uS += 10 * Math.abs(attributeCounts[k] / members.size() - (1.0 / attributeOptions.size()));
            }
            if (uS > maxUnhappiness1) {
                maxUnhappiness1 = uS;
            }
            unhappiness += weights[8] * MyUtility.interpolate(maxUnhappiness1, uS);

            uS = 0;
            for (int i = 0; i < members.get(0).getAttributes1().length; i++) {
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
            }
            unhappiness += weights[9] * MyUtility.interpolate(maxUnhappiness[2], uS);

            // TODO: Fix iteration for this attribute
            uS = 0;
            for (int i = 0; i < members.get(0).getAttributes2().length; i++) {
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
            }
            unhappiness += weights[10] * MyUtility.interpolate(maxUnhappiness[3], uS);

            uS = 0;
            for (int i = 0; i < members.get(0).getAttributes3().length; i++) {
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
            }
            unhappiness += weights[11] * MyUtility.interpolate(maxUnhappiness[4], uS);
        }

        return Math.pow(unhappiness, 2);
    }

}
