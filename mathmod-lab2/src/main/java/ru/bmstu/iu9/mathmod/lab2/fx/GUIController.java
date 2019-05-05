package ru.bmstu.iu9.mathmod.lab2.fx;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import ru.bmstu.iu9.mathmod.lab2.delaunay.Triangulation;
import ru.bmstu.iu9.mathmod.lab2.geom.Edge;
import ru.bmstu.iu9.mathmod.lab2.geom.Triangle;
import ru.bmstu.iu9.mathmod.lab2.geom.Vector2D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

public class GUIController {

    @FXML
    private Canvas canvas;

    private List<Vector2D> points = new ArrayList<>();

    @FXML
    public void onMouseClicked(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        GraphicsContext ctx = canvas.getGraphicsContext2D();
        ctx.setFill(Color.WHITE);
        ctx.fillRect(x, y, 2.0, 2.0);
        this.points.add(new Vector2D(x, y));
    }

    @FXML
    public void onClearClicked() {
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        ctx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        this.points.clear();
    }

    @FXML
    public void onBuildTriangulationClicked() {
        GraphicsContext ctx = canvas.getGraphicsContext2D();

        if(points.size() < 3) {
            return;
        }

        Triangulation triangulation = new Triangulation(points);
        Set<Vector2D> superStructurePoints = new HashSet<>(asList(triangulation.getSuperRectangle().points()));

        for (Triangle tr : triangulation.getTriangles()) {
            for (Edge e : tr.edges()) {
                if (superStructurePoints.contains(e.first()) || superStructurePoints.contains(e.second())) {
                    drawEdge(ctx, e, Color.BLUE);
                } else {
                    drawEdge(ctx, e, Color.WHITE);
                }
            }
        }
    }

    private static void drawEdge(GraphicsContext ctx, Edge e, Color c) {
        removePoint(ctx, e.first());
        removePoint(ctx, e.second());
        ctx.setLineWidth(2.0);
        ctx.setStroke(c);
        ctx.strokeLine(e.first().x(), e.first().y(), e.second().x(), e.second().y());
    }

    private static void removePoint(GraphicsContext ctx, Vector2D pt) {
        ctx.setFill(Color.DARKGRAY);
        ctx.fillRect(pt.x(), pt.y(), 2.0, 2.0);
    }

}