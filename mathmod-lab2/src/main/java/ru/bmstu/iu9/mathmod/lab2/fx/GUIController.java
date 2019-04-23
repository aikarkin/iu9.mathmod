package ru.bmstu.iu9.mathmod.lab2.fx;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import ru.bmstu.iu9.mathmod.lab2.delaunay.DelaunayUtil;
import ru.bmstu.iu9.mathmod.lab2.delaunay.Triangulation;
import ru.bmstu.iu9.mathmod.lab2.geom.*;

import java.util.ArrayList;
import java.util.List;

public class GUIController {

    @FXML
    private Canvas canvas;

    private List<Point2D> points = new ArrayList<>();

    @FXML
    public void onMouseClicked(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        GraphicsContext ctx = canvas.getGraphicsContext2D();
        ctx.setFill(Color.WHITE);
        ctx.fillRect(x, y, 2.0, 2.0);
        this.points.add(new Point2D(x, y));
    }

    @FXML
    public void onClearClicked() {
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        ctx.clearRect(0, 0, 600, 400);
        this.points.clear();
    }

    @FXML
    public void onBuildTriangulationClicked() {
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        points.forEach(p -> System.out.printf("Point: (%.3f, %.3f)%n", p.x(), p.y()));
        Triangulation triangulation = new Triangulation(points);
        for (Edge e : triangulation.getEdges()) {
            drawEdge(ctx, e, Color.WHITE);
        }

        strokeSuperRectEdges(ctx, triangulation.getSuperRectangle());
        strokeCircumCircles(ctx, triangulation.getTriangles(), Color.RED);
    }

    private void strokeCircumCircles(GraphicsContext ctx, List<Triangle> triangles, Color color) {
        for(Triangle tr : triangles) {
            drawCircle(ctx, DelaunayUtil.getCircumcircleOfTriangle(tr), color);
        }
    }

    private static void drawEdge(GraphicsContext ctx, Edge e, Color c) {
        removePoint(ctx, e.first());
        removePoint(ctx, e.second());
        ctx.setLineWidth(2.0);
        ctx.setStroke(c);
        ctx.strokeLine(e.first().x(), e.first().y(), e.second().x(), e.second().y());
//        ctx.moveTo(e.first().x(), e.first().y());
//        ctx.lineTo(e.second().x(), e.second().y());
    }

    private static void drawCircle(GraphicsContext ctx, Circle circle, Color color) {
        ctx.setLineWidth(2.0);
        ctx.setStroke(color);
        ctx.strokeOval(circle.getCenter().x(), circle.getCenter().y(), 2 * circle.getRadius(), 2 * circle.getRadius());
    }

    private static void strokeSuperRectEdges(GraphicsContext ctx, Rectangle superRectangle) {
        for(Edge e : superRectangle.edges()) {
            drawEdge(ctx, e, Color.BLUE);
        }
    }

    private static void removePoint(GraphicsContext ctx, Point2D pt) {
        ctx.setFill(Color.DARKGRAY);
        ctx.fillRect(pt.x(), pt.y(), 2.0, 2.0);
    }

}
