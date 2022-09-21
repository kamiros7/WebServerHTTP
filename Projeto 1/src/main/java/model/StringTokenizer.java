package model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class StringTokenizer {

    private String requestLine;
    private List<String> requestLineArray;

    public StringTokenizer() {

    }

    public StringTokenizer(String requestLine) {
        this.requestLine = requestLine;
        this.requestLineArray = new LinkedList<String>(Arrays.asList(requestLine.split("/")));
    }

    public String nextToken() {
        String token;
        if (!requestLine.isEmpty()) {
            token = requestLineArray.remove(0);
            token = token.replace(" HTTP", "");
            return token;
        }
        return null;
    }
}
