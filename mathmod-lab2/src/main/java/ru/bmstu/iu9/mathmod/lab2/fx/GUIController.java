package ru.bmstu.iu9.mathmod.lab2.fx;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ru.bmstu.iu9.mathmod.lab2.App;
import ru.bmstu.iu9.mathmod.lab2.delaunay.Triangulation;
import ru.bmstu.iu9.mathmod.lab2.geom.*;
import ru.bmstu.iu9.mathmod.lab2.elevation.ElevationPoint;
import ru.bmstu.iu9.mathmod.lab2.elevation.ElevationPointsDeserializer;
import ru.bmstu.iu9.mathmod.lab2.elevation.ElevationPointsList;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GUIController {

    @FXML
    private Canvas canvas;

    @FXML
    private Label fileNameLabel;

    @FXML
    private Button buildTriangulationButton;

    @FXML
    private Label elevationCoordinates;

    private ElevationPointsList points;

    private Vector2D selectedPoint;

    private PlaneParamsCache planeParamsCache = new PlaneParamsCache();

    private Map<Vector2D, Double> heightOfPoint;

    private Triangulation triangulation;

    @FXML
    public void onChooseFile() {
        File chosenFile = App.showFileChooser();
        if (chosenFile != null && chosenFile.exists()) {
            try {
                loadPoints(chosenFile);
                fileNameLabel.setDisable(false);
                fileNameLabel.setText(chosenFile.getName());
                buildTriangulationButton.setDisable(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showErrorMessage("Failed to load file");
            buildTriangulationButton.setDisable(true);
        }
    }

    @FXML
    public void onBuildTriangulationClicked() {
        if (points == null) {
            showErrorMessage("Unable to build elevation map: no elevation pointsSorted loaded");
            return;
        }

        buildTriangulationButton.setDisable(false);
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        ctx.clearRect(0, 0, 600, 400);
        points.forEach(p -> System.out.printf("Point: (%.3f, %.3f)%n", p.x(), p.y()));
        heightOfPoint = points
                .stream()
                .collect(Collectors.toMap(ElevationPoint::xyProjection, ElevationPoint::h));

        triangulation = new Triangulation(points.getXYProjections());

        for (Triangle tr : triangulation.getTriangles()) {
            for (Edge e : tr.edges()) {
                drawEdge(ctx, e, Color.BLUE);

            }
            Circle circumscribedCircle = GeometryUtils.getCircumscribedCircle(tr);
            drawCircle(ctx, circumscribedCircle, Color.RED);
            drawPoint(ctx, circumscribedCircle.getCenter(), 4.0, Color.YELLOWGREEN);
        }

        Set<Vector2D> superRectPoints = new HashSet<>(Arrays.asList(triangulation.getSuperRectangle().points()));

        triangulation.getTriangles()
                .stream()
                .filter(tr -> !superRectPoints.contains(tr.p1()) && !superRectPoints.contains(tr.p2()) && !superRectPoints.contains(tr.p3()))
                .flatMap(tr -> Arrays.stream(tr.edges()))
                .forEach(e -> drawEdge(ctx, e, Color.WHITE));

        for (Vector2D pt : points.getXYProjections()) {
            drawPoint(ctx, pt, 5.0, Color.RED);
        }


        for (ElevationPoint pt : points) {
            addLabelForPoint(ctx, pt.xyProjection(), String.format("%d", (int) pt.h()));
        }
    }

    @FXML
    public void onMouseClicked(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        pickPoint(x, y);
    }

    private void loadPoints(File chosenFile) throws IOException {
        this.points = parseElevationFile(chosenFile);
        planeParamsCache.clear();
    }

    private void pickPoint(double x, double y) {
        if (triangulation == null || heightOfPoint == null) {
            return;
        }
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        Vector2D pt = GeometryUtils.vec2d(x, y);

        if (selectedPoint != null) {
            drawPoint(ctx, selectedPoint, Color.BLACK);
        }

        selectedPoint = pt;
        drawPoint(ctx, selectedPoint, Color.YELLOW);

        Optional<Triangle> boundingTriangle = triangulation.findBoundingTriangle(pt);

        if (!boundingTriangle.isPresent()) {
            return;
        }

        Triangle tr = boundingTriangle.get();
        System.out.println("Bounding triangle: " + tr.toString());
        double h = calcPointHeight(tr, pt);
        elevationCoordinates.setText(String.format("x: %.2f, y: %.2f, h: %.2f", x, y, h));
    }

    private double calcPointHeight(Triangle tr, Vector2D pt) {
        Plane plane = new Plane(
                new ElevationPoint(tr.p1(), heightOfPoint.get(tr.p1())),
                new ElevationPoint(tr.p2(), heightOfPoint.get(tr.p2())),
                new ElevationPoint(tr.p3(), heightOfPoint.get(tr.p3()))
        );
        return planeParamsCache.get(plane).distanceToPoint(pt);
    }

    private void showErrorMessage(String msg) {
        System.err.println(msg);
    }

    private static void drawEdge(GraphicsContext ctx, Edge e, Color c) {
        drawPoint(ctx, e.first(), Color.BLACK);
        drawPoint(ctx, e.second(), Color.BLACK);
        ctx.setLineWidth(2.0);
        ctx.setStroke(c);
        ctx.strokeLine(e.first().x(), e.first().y(), e.second().x(), e.second().y());
    }

    private static void addLabelForPoint(GraphicsContext ctx, Vector2D pt, String label) {
        ctx.setStroke(Color.DARKORANGE);
        ctx.setLineWidth(1.5);
        ctx.setFont(Font.font(null, FontWeight.EXTRA_LIGHT, 14.0));
        ctx.strokeText(label, pt.x() - 5.0, pt.y() - 5);
    }

    private static void drawCircle(GraphicsContext ctx, Circle circle, Color color) {
        System.out.println("Drawing circle: " + circle);
        Vector2D center = circle.getCenter();
        double r = circle.getRadius();

        ctx.setLineWidth(1.0);
        ctx.setStroke(color);
        ctx.strokeOval(center.x() - r, center.y() - r, 2.0 * r, 2.0 * r);
    }

    private static void strokeEdges(GraphicsContext ctx, List<Edge> edges, Color c) {
        for (Edge e : edges) {
            drawEdge(ctx, e, c);
        }
    }

    private static void drawPoint(GraphicsContext ctx, Vector2D pt, Color c) {
        ctx.setFill(c);
        ctx.fillRect(pt.x() - 1.0, pt.y() - 1.0, 2.0, 2.0);
    }

    private static void drawPoint(GraphicsContext ctx, Vector2D pt, double size, Color c) {
        ctx.setFill(c);
        ctx.fillRect(pt.x() - size / 2.0, pt.y() - size / 2.0, size, size);
    }

    private static ElevationPointsList parseElevationFile(File file) throws IOException {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ElevationPointsList.class, new ElevationPointsDeserializer());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);

        return mapper.readValue(file, ElevationPointsList.class);
    }

}
