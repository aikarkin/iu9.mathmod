package ru.bmstu.iu9.mathmod.lab2;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        final URL fxmlRes = getClass().getClassLoader().getResource("layout.fxml");

        if (fxmlRes == null) {
            System.out.println("[error] Failed to start GUI - layout.xml not found");
            return;
        }

        Parent root = FXMLLoader.load(fxmlRes);
        primaryStage.setTitle("ЛР №2. Триангуляция Делоне");
        primaryStage.setResizable(false);
        Scene scene = new Scene(root, -1, -1);
        scene.getStylesheets().add("styles.css");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
