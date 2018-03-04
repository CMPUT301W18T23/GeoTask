package com.geotask.myapplication.QueryBuilder;

import java.util.HashMap;

public class SuperBooleanBuilder {

    private final String startString = "{\n" +
                                        "\t\"query\": {\n" +
                                        "\t\t\"bool\": {\n" +
                                        "\t\t\t\"must\": {";
    private final String endString = "\n" +
                                        "\t\t\t}\n" +
                                        "\t\t}\n" +
                                        "\t}\n" +
                                        "}";
    private String terms;

    public void put(String field, String term) {
        terms = "\n" +
                "\t\t\t\t\"term\": {\n" +
                "\t\t\t\t\t\"" + field + "\": \"" + term + "\"\n" +
                "\t\t\t\t}";
    }

    public String toString() {
        return startString + terms + endString;
    }
}
