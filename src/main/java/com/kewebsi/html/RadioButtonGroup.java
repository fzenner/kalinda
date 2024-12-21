package com.kewebsi.html;

import com.kewebsi.errorhandling.CodingErrorException;

import java.util.ArrayList;
import java.util.Collections;

public class RadioButtonGroup {

    protected ArrayList<HtmlRadioButton2> buttons = new ArrayList<>();

    protected String name;

    public RadioButtonGroup(String name) {
        this.name = name;
    }

    public RadioButtonGroup(String name, HtmlRadioButton2[] buttonsNew) {
        this.name = name;
        Collections.addAll(buttons, buttonsNew);
    }

    public void add(HtmlRadioButton2 button) {
        if (buttons.contains(button)) {
            throw new CodingErrorException("Dupicate addition to button group with button: " + button);
        }
        buttons.add(button);
    }

    public boolean remove(HtmlRadioButton2 button) {
        return buttons.remove(button);
    }

    public ArrayList<HtmlRadioButton2> getButtons() {
        return buttons;
    }

    public void doNotUpdate() {
        for (var buttonRun : buttons) {
            buttonRun.setNotModified();
        }
    }

    public String getName() {
        return name;
    }

    public RadioButtonGroup setName(String name) {
        this.name = name;
        return this;
    }
}
