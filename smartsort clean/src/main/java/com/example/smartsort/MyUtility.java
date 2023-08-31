package com.example.smartsort;

import java.util.ArrayList;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import java.lang.reflect.Type;

public class MyUtility {

    public static Individual[] shuffleIndividualArray(Individual[] x) {
        Individual[] y = new Individual[x.length];
        boolean stop = false;
        int randomNumber = 0;
        for (Individual i : x) {
            stop = false;
            while (! stop) {
                randomNumber = (int) (Math.random() * y.length);
                if (y[randomNumber] == null) {
                    y[randomNumber] = i;
                    stop = true;
                }
            }

        }
        return y;
    }

    public static ArrayList<ArrayList<String>> readJsonStringInput(String jsonString) {
        Type listType = new TypeToken<ArrayList<ArrayList<String>>>() {}.getType();
        return new Gson().fromJson(jsonString, listType);
    }

    public static double interpolate(double max, double x) {
        if (max != 0)
            return (x / max);
        return 0;
    }

    public static ArrayList<Integer> copyArrayListInteger(ArrayList<Integer> toCopy) {
        ArrayList<Integer> copy = new ArrayList<>();
        for (int i : toCopy)
            copy.add(i);
        return copy;
    }

    public static ArrayList<Individual> convertToArrayListIndividual(Individual[] toConv) {
        ArrayList<Individual> ret = new ArrayList<>();
        for (Individual i : toConv)
            ret.add(i);
        return ret;
    }

}