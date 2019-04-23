package ru.bmstu.iu9.mathmod.lab2;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class App extends Application {

    private static final String WORKING_DIR = "/home/alex/dev/src/java/iu9/mathmod/mathmod-lab2/src/main/resources";
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        App.primaryStage = primaryStage;
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

    public static File showFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chooser elevation mapping");
        fileChooser.setInitialDirectory(new File(WORKING_DIR));
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Json elevation map", "*.json"));
        return fileChooser.showOpenDialog(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
