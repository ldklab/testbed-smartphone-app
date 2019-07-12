package edu.wpi.messagebrokersmartphoneapp;

public class InteractionResponse {

    String name;
    String value;

    public InteractionResponse() {
    }

    public InteractionResponse(String name) {
        this.name = name;
    }

    public InteractionResponse(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "InteractionResponse{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
