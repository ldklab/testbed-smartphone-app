package edu.wpi.messagebrokersmartphoneapp;

import java.util.List;

public class InteractionInput {

    public class Element {
        String text;
        String value;

        public Element(String text, String value) {
            this.text = text;
            this.value = value;
        }

        public String getText() {
            return text;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Element{" +
                    "text='" + text + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    String title;
    String name;
    String type;
    List<Element> elements;
    Boolean required;

    public InteractionInput(String title, String name, String type, List<Element> elements, Boolean required) {
        this.title = title;
        this.name = name;
        this.type = type;
        this.elements = elements;
        this.required = required;
    }

    public String getTitle() {
        return title;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<Element> getElements() {
        return elements;
    }

    public Boolean getRequired() {
        return required;
    }

    @Override
    public String toString() {
        return "InteractionInput{" +
                "title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", elements=" + elements +
                ", required=" + required +
                '}';
    }
}
