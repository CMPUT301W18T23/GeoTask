package com.geotask.myapplication.QueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * SuperBooleanBuilder - because every other builder breaks our dependencies
 *
 */
public class SuperBooleanBuilder {
    private final String startString = "{\n" +
                                        "\t\"query\": {\n" +
                                        "\t\t\"bool\": {\n" +
                                        "\t\t\t\"must\": [";
    private final String endString = "\n" +
                                        "\t\t\t]\n" +
                                        "\t\t}\n" +
                                        "\t}\n" +
                                        "}";
    private String terms = "";

    /**
     * put (single pair) - adds a single term to the list of terms
     *
     * @param field - key of the pair
     * @param term - value of the pair
     */
    public void put(String field, String term) {
        if (!terms.equals("")) {
            terms += ",";
        }
        terms +="\n{" +
                "\t\t\t\t\"term\": {\n" +
                "\t\t\t\t\t\"" + field + "\": \"" + term + "\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}";
    }

    /**
     * put (multiple pairs) - adds multiple pairs to the list of terms
     *
     * @param parameters - list of strings that are formatted as [field1, term1, field2, term2 ...]
     */
    public void put(ArrayList<String> parameters) {
        for(int i = 0; i < parameters.size(); i+=2) {
            if (!terms.equals("")) {
                terms += ",";
            }
            this.put(parameters.get(i), parameters.get(i+1));
        }
    }

    /**
     * toString - returns the formatted query
     *
     * @return - the formatted query
     */
    public String toString() {
        return startString + terms + endString;
    }
}
