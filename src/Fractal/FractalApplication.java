package Fractal;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FractalApplication extends Application {

    private double dx;
    private double x0;
    private double y0;
    private int width;
    private int height;
    private ImageView imageView;
    private double tmpX;
    private double tmpY;
    private double distX;
    private double distY;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Circle Fractal");
        Parent root = initInterface();

        initInteraction();

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void initInteraction() {

        imageView.addEventHandler(
                ScrollEvent.SCROLL,
                a -> {
                    double scroll = a.getDeltaY();
                    double oldDx = dx;
                    if (scroll < 0)
                        dx *= 1.5;
                    else
                        dx /= 1.5;

                    x0 += width * (oldDx - dx) / 2;
                    y0 -= height * (oldDx - dx) / 2;

                    imageView.setImage(drawFractal());
                }
        );

        imageView.addEventHandler(
                MouseEvent.MOUSE_DRAGGED,
                a -> {
                    imageView.setX(a.getSceneX() - distX);
                    imageView.setY(a.getSceneY() - distY);
                }
        );

        imageView.addEventHandler(
                MouseEvent.MOUSE_PRESSED,
                a -> {
                    tmpX = a.getSceneX();
                    tmpY = a.getSceneY();
                    distX = tmpX - imageView.getX();
                    distY = tmpY - imageView.getY();
                }
        );

        imageView.addEventHandler(
                MouseEvent.MOUSE_RELEASED,
                a -> {
                    x0 += -(a.getSceneX() - tmpX) * dx;
                    y0 += (a.getSceneY() - tmpY) * dx;
                    imageView.setImage(drawFractal());
                    imageView.setX(0);
                    imageView.setY(0);
                }
        );
    }

    private WritableImage drawFractal() {

        if (height == 0 || width == 0)
            return null;

        Fractal fractal = new Mandelbrot();
        Palette palette = new HSBPalette();
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int i = 0; i < width - 1; i++) {
            for (int j = 0; j < height - 1; j++) {
                double x = x0 + i * dx;
                double y = y0 - j * dx;
                double colorIndex = fractal.getColor(x, y);
                Color color = palette.getColor(colorIndex);
                pixelWriter.setColor(i, j, color);
            }
        }
        return writableImage;
    }

    private Parent initInterface() {
        width = 600;
        height = 600;
        dx = 0.1 / 600;
        x0 = -0.3;
        y0 = 0.8;

        imageView = new ImageView(drawFractal());
        VBox.setVgrow(imageView, Priority.ALWAYS);
        HBox.setHgrow(imageView, Priority.ALWAYS);

        return new Pane(imageView);
    }

    /*
    * WritableImage - это Image (подходит для ImageView), у которого можно менять отедльные пиксели
    * WI wi = new WI (ширина, высота)
    * PixelWriter pw = wi.getPixelWriter();
    * pw.setColor(x, y, color)
    *
    *
    * см. фотографию (x0 = -3, y0 = 3, а не как там написано -200 и 200)
    *
    *
    * как рисовать фрактал
    * перебрать все пиксели
    * for x| от 0 до ширины - 1
    *   for y| от 0 до высоты - 1
    *       x = x0 + x| * dx;
    *       y = y0 - y| * dx;
    *       colorInd = fractal.getColorInd(x, y);
    *       color = palette.getColor(colorInd);
    *       pw.write(x|, y| color)*
    *
    *
    *
    * Дано С - комплексное
    * Процесс:
    * z = 0
    * z -> z^2 + c
    * повторять бесконечное число раз
    * при с = 1 уходит на бесконечность
    * при с = -1 ограничена
    * при с = i ограничена
    * Мандельброт состоит из тех комплексных точек, которые ограничены
    *
    * Алгоритм:
    * Дано с (т.е. точка на плоскости с = x + iy)
    * N = 1000;
    * R = 10^9;
    * z = 0;
    * for i = 0 до N
    *   z -> z^2 + c;       zRe, zIm - две переменные, abs(z) = abs(a + bi) = sqrt(a * a + b * b)
    *   if abs(z) > R then break;
    * цвет = i/N;
    */
}
/*
---------------------------------








---------------------------------
 */