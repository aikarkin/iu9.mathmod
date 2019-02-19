package ru.bmstu.iu9.mathmod.lab1;

import javafx.scene.control.TextField;

public class InvalidFieldException extends RuntimeException {
    public InvalidFieldException(TextField textField) {
        this(textField, null);
    }

    public InvalidFieldException(TextField textField, String devMessage) {
        super(String.format(
                "Введено недопустимое значение для поля %s: '%s'%s",
                textField.getId(),
                textField.getText(),
                devMessage == null ? "" : " - " + devMessage
            )
        );
    }
}
