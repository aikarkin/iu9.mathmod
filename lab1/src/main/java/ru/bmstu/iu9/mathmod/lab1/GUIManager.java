package ru.bmstu.iu9.mathmod.lab1;

import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealVector;
import ru.bmstu.iu9.mathmod.balistics.BalisticsUtils;
import ru.bmstu.iu9.mathmod.commons.ParamEq;
import ru.bmstu.iu9.mathmod.commons.RungeKuttaAlgo;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.DoubleStream;

import static java.lang.Math.*;

public class GUIManager {
    private static final double GRAVITY = 9.81;

    @FXML
    private TextField startSpeedField;
    @FXML
    private TextField startAngleField;
    @FXML
    private TextField densityField;
    @FXML
    private TextField radiusField;
    @FXML
    private TextField betaField;
    @FXML
    private TextField deltaTimeField;
    @FXML
    private ScatterChart<Number, Number> chart;

    private static double getDoubleOrThrow(TextField textField) {
        Optional<Double> doubleOpt = tryParseDouble(textField.getText().replaceAll(",", ".").trim());
        if (!doubleOpt.isPresent()) {
            throw new InvalidFieldException(textField);
        }

        return doubleOpt.get();
    }

    private static Optional<Double> tryParseDouble(String value) {
        if (value == null || !value.matches("[0-9]+\\.?[0-9]*")) {
            return Optional.empty();
        }

        try {
            return Optional.of(Double.parseDouble(value));
        } catch (NumberFormatException nfe) {
            return Optional.empty();
        }
    }

    @FXML
    public void onShowClicked() {
        removeCharts();
        addCharts();
    }

    private void removeCharts() {
        chart.getData().clear();
    }

    private void addCharts() {
        try {
            double startSpeed = getDoubleOrThrow(startSpeedField);
            double startAngle = toRadians(getDoubleOrThrow(startAngleField));
            double timeDelta = getDoubleOrThrow(deltaTimeField);
            double density = getDoubleOrThrow(densityField);
            double beta = getDoubleOrThrow(betaField);
            double radius = getDoubleOrThrow(radiusField);

            createNewtonChart(startSpeed, startAngle, timeDelta, beta, radius, density);
            createGalileiChart(startSpeed, startAngle, timeDelta);
        } catch (InvalidFieldException e) {
            showError("Некорректно заполнено поле", e.getMessage());
        }
    }

    private void createNewtonChart(double startSpeed, double startAngle, double timeDelta, double beta, double radius, double density) {
        final double mass = 4.0 / 3.0 * PI * density * pow(radius, 3.0);

        ParamEq func = (t, vec) -> {
            RealVector resVec = new ArrayRealVector(vec.getDimension());

            double u = vec.getEntry(2), w = vec.getEntry(3);
            double v = sqrt(pow(u, 2.0) + pow(w, 2.0));

            resVec.setEntry(0, u);
            resVec.setEntry(1, w);
            resVec.setEntry(2, -(beta * u * v) / mass);
            resVec.setEntry(3, -GRAVITY - (beta * w * v) / mass);

            return resVec;
        };

        RealVector rkStartPoint = MatrixUtils.createRealVector(new double[]{
                0.0,
                0.0,
                startSpeed * cos(startAngle),
                startSpeed * sin(startAngle)
        });

        List<RealVector> rkRes = RungeKuttaAlgo.rungeKutta(func, (t, yPrev, y) -> (y.getEntry(1) <= 0), rkStartPoint, timeDelta);

        int n = rkRes.size();
        double[] xValues = new double[n];
        double[] yValues = new double[n];

        for (int i = 0; i < n; i++) {
            double time = i * timeDelta;
            xValues[i] = time;
            yValues[i] = rkRes.get(i).getEntry(1);
        }

        addDataSeries("Newton method", "t", "y(t)", xValues, yValues);

        // print summary
        System.out.println("Newton method summary:\n");
        printStatistics(startSpeed, startAngle, timeDelta, n);
    }

    private void createGalileiChart(double startSpeed, double startAngle, double timeDelta) {
        double flightTime = BalisticsUtils.Galilei.flightTime(startSpeed, startAngle, GRAVITY);
        int n = (int) (flightTime / timeDelta);
        double[] xValues = new double[n];
        double[] yValues = new double[n];

        for (int i = 0; i < n; i++) {
            double time = i * timeDelta;
            xValues[i] = time;
            yValues[i] = startSpeed * sin(startAngle) * time - GRAVITY * pow(time, 2.0) / 2;
        }

        addDataSeries("Galilei method", "t", "y(t)", xValues, yValues);
        // print summary
        System.out.println("Galilei method summary:\n");
        printStatistics(startSpeed, startAngle, timeDelta, n);
    }

    private void printStatistics(double startSpeed, double startAngle, double timeDelta, int n) {
        System.out.printf("\t* number of iterations: %d%n", n);
        System.out.printf("\t* flight time: %.8f%n", timeDelta * n);
        System.out.printf("\t* flight distance: %.8f%n", BalisticsUtils.flightDistance(startSpeed, startAngle,timeDelta * n));
        System.out.println(new String(new char[20]).replaceAll("\0", "-"));
    }

    private void addDataSeries(String chartLabel, String xAxisLabel, String yAxisLabel, double[] x, double[] y) {
        XYChart.Series<Number, Number> dataSeries = new XYChart.Series<>();

        NumberAxis xAxis = (NumberAxis) chart.getXAxis();
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();

        for (int i = 0; i < min(x.length, y.length); i++) {
            dataSeries.getData().add(new XYChart.Data<>(x[i], y[i]));
        }

        dataSeries.setName(chartLabel);

        setAxisInfo(xAxisLabel, xAxis, x);
        xAxis.setTickLabelRotation(-90.0);
        xAxis.setTickLabelGap(0.01);
        xAxis.setTickUnit(0.025);

        setAxisInfo(yAxisLabel, yAxis, y);
        yAxis.setTickLabelGap(0.05);
        yAxis.setTickUnit(0.05);

        chart.getData().add(dataSeries);
    }

    private void setAxisInfo(String label, NumberAxis axis, double[] axisValues) {
        axis.setLabel(label);
        DoubleStream axisValuesStream = Arrays.stream(axisValues);
        OptionalDouble minOpt = axisValuesStream.min();
        axisValuesStream = Arrays.stream(axisValues);

        OptionalDouble maxOpt = axisValuesStream.max();
        if(minOpt.isPresent()) {
            axis.setLowerBound(minOpt.getAsDouble());
        }
        if(maxOpt.isPresent()) {
            axis.setUpperBound(maxOpt.getAsDouble());
        }
        axis.setAutoRanging(false);
        axis.setMinorTickVisible(false);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
