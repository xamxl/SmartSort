package com.example.smartsort;

import java.util.ArrayList;
import java.util.Random;

public class Sort {

    private Location[] locations;
    private Individual[] individuals;
    private double totalUnhappiness;

    Sort(Location[] ws, Individual[] ss) {
        locations = ws;
        individuals = ss;
    }

    public ArrayList<String> getAttributeOptions(int w) {
        ArrayList<String> options = new ArrayList<>();
        for (Individual i : individuals) {
            boolean there = false;
            for (String k : options) 
                if (i.getAttributes()[w].equals(k))
                    there = true;

            if (! there)
                options.add(i.getAttributes()[w]);
        }
        return options;
    }

    public ArrayList<String> getAttribute1Options(int w) {
        ArrayList<String> options = new ArrayList<>();
        for (Individual i : individuals) {
            boolean there = false;
            for (String k : options)
                if (i.getAttributes1()[w].equals(k))
                    there = true;
            if (! there)
                options.add(i.getAttributes1()[w]);
        }
        return options;
    }

    public ArrayList<String> getAttribute2Options(int w) {
        ArrayList<String> options = new ArrayList<>();
        for (Individual i : individuals) {
            boolean there = false;
            for (String k : options)
                if (i.getAttributes2()[w].equals(k))
                    there = true;
            if (! there)
                options.add(i.getAttributes2()[w]);
        }
        return options;
    }

    public ArrayList<String> getAttribute3Options(int w) {
        ArrayList<String> options = new ArrayList<>();
        for (Individual i : individuals) {
            boolean there = false;
            for (String k : options)
                if (i.getAttributes3()[w].equals(k))
                    there = true;
            if (! there)
                options.add(i.getAttributes3()[w]);
        }
        return options;
    }

    public Individual[] getIndividuals() {
        return individuals;
    }

    public void randomAssignment() {
        boolean stop = true;
        int randomNumber;
        for (Individual i : individuals) {
            stop = false;
            while (! stop) {
                randomNumber = (int) (Math.random() * locations.length);
                if (locations[randomNumber].isSpace()) {
                    locations[randomNumber].addMember(i);
                    stop = true;
                }
            }
        }

    }

    public void allChoiceAssignment() {
        double[] unhappinessSave1 = new double[locations.length];
        boolean stop;
        int minimum = 0;
        for (Individual i : MyUtility.shuffleIndividualArray(individuals)) {
            for (int ii = 0; ii < locations.length; ii++) {
                if (! locations[ii].isSpace())
                    unhappinessSave1[ii] = -1;
                else {
                    locations[ii].addMember(i);
                    sumUnhappiness();
                    unhappinessSave1[ii] = totalUnhappiness;
                    locations[ii].removeLastMember();
                }
            }
            stop = false;
            while (! stop) {
                minimum = (int) (Math.random() * locations.length);
                if (locations[minimum].isSpace()) {
                    stop = true;
                }
            }
            for (int ii = 0; ii < locations.length; ii++)
                if (unhappinessSave1[ii] < unhappinessSave1[minimum] && unhappinessSave1[ii] != -1)
                    minimum = ii;
            locations[minimum].addMember(i);
        }
    }

    public void reassignX(int x) {
        int[] randomNumbers = new int[x];
        Individual[] removed = new Individual[x];
        
        ArrayList<Integer> availableIndexes = new ArrayList<>();
        getOpenLocations(availableIndexes);

        Random random = new Random();
        int randomNumbersCount = 0;
        while (randomNumbersCount < x && !availableIndexes.isEmpty()) {
            int randomIndex = random.nextInt(availableIndexes.size());
            int selectedIndex = availableIndexes.get(randomIndex);

            randomNumbers[randomNumbersCount++] = selectedIndex;
            availableIndexes.remove(randomIndex);
        }
        
        for (int i = 0; i < x; i++) {
            removed[i] = locations[randomNumbers[i]].removeMember((int) (Math.random() * locations[randomNumbers[i]].numberOfMembers()));
        }
        
        reassignXAssign(removed);
    }

    public void reassignXAssign(Individual[] x) {
        double[] unhappinessSave1 = new double[locations.length];
        double minVal = 0;
        int minInd = 0;
        for (Individual i : x) {
            minVal = -1;
            minInd = 0;
            for (int ii = 0; ii < locations.length; ii++) {
                if (! locations[ii].isSpace())
                    unhappinessSave1[ii] = -1;
                else {
                    locations[ii].addMember(i);
                    sumUnhappiness();
                    if (minVal == -1 || totalUnhappiness < minVal) {
                        minVal = totalUnhappiness;
                        minInd = ii;
                    }
                    locations[ii].removeLastMember();
                }
            }
            locations[minInd].addMember(i);
        }
    }

    public void sumUnhappiness() {
        totalUnhappiness = 0;
        for (Location i : locations) {
            totalUnhappiness += i.calculateUnhappiness(this);
            for (Individual ii : i.getMembers()) {
                totalUnhappiness += ii.calculateUnhappiness(i);
            }
        }
    }

    public double getUnhappiness() {
        return totalUnhappiness;
    }

    public Location[] getLocations() {
        return locations;
    }

    public void getOpenLocations(ArrayList<Integer> availableLocations) {
        for (int i = 0; i < locations.length; i++) {
            if (locations[i].numberOfMembers() > 0) {
                availableLocations.add(i);
            }
        }
    }

}
