package org.hspconsortium.client.example.model;

public class Height {

    private final String height;
    private final String date;

    public Height( String height, String date ) {
        this.height = height;
        this.date = date;
    }

    public String getHeight() {
        return height;
    }

    public String getDate() {
        return date;
    }
}
