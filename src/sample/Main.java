package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

/* Ideas:
    - Implement zooming in and out
    - Make it so the user can take "chunks" out of the arena (not every arena is a perfect square, the XLS arena included)
 */
public class Main extends Application
{
    // Todo - Make a lot of these into methods
    private Point2D.Double firstClick;
    private double numRows, numColumns;

    private Button generateButton = new Button ("Generate Arena w/ Specified Parameters"); // Todo - Make this nonglobal
    private Text buttonNotification = new Text(); // This is essentially an error report so this is probably staying global

    private double boxWidth = 0, boxHeight = 0; // These are used a TON throughout the program, they are just variables for clarity

    @Override
    public void start(Stage primaryStage)
    {
        double canvasWidth = Toolkit.getDefaultToolkit().getScreenSize().height - 250;
        double canvasHeight = Toolkit.getDefaultToolkit().getScreenSize().height - 250;

        // Drawing Things
        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Input Grid
        GridPane grid = makeGridPane();
        TextField widthField = new TextField(), heightField  = new TextField();
        grid.add(widthField, 1, 0);
        grid.add(heightField, 1, 1);

        primaryStage.setTitle("Arena Design"); // Title of the window
        BorderPane borderPane = new BorderPane(); // The master container-type-thing

        // When the "generate" button is pressed
        generateButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                numRows = parseRows(heightField);
                numColumns = parseColumns(widthField);

                clearGrid(gc, canvasWidth, canvasHeight);
                drawGrid(gc, canvasWidth, canvasHeight);

                boxWidth = canvasWidth / numColumns;
                boxHeight = canvasHeight / numRows;
            }
        });

        // Todo - Make the "line creation" element of this work
        canvas.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent e)
            {
                firstClick.setLocation(e.getX(), e.getY());
            }
        });

        // Todo - Implement a real-time preview of the line (this is much harder than it seems cause I have to use GraphicsContext due to this being JavaFX
        // This is when the mouse is moved around, pressed down
        canvas.setOnMouseDragged(new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent e)
            {

            }
        });

        // When the mouse is released when it is pressed on the canvas
        canvas.setOnMouseReleased(new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent e)
            {
                drawPermanentLine(firstClick.getX(), firstClick.getY(), e.getX(), e.getY(), gc);
            }
        });

        // Setting Alignments / Adding them to the BorderPane
        borderPane.setCenter(canvas);
        borderPane.setBottom(grid);

        primaryStage.setScene(new Scene(borderPane)); // Makes a scene out of the borderPane element
        primaryStage.show(); //  Presents everything that's loaded to the user
    }

    private GridPane makeGridPane()
    {
        /* Creating the GridPane and all its elements */
        GridPane grid = new GridPane(); // Initializing the GridPane
        grid.setAlignment(Pos.BOTTOM_CENTER);
        grid.setHgap(10); // The horizontal gap between elements
        grid.setVgap(10); // The vertical gap between elements
        grid.setPadding(new Insets(20, 20, 20, 20));

        // The prompt text for the width
        Text widthPrompt = new Text("Arena Width: "); // The text of the label
        grid.add(widthPrompt, 0, 0, 1, 1); // Adding it to the grid

        // The prompt text for the height
        Text heightPrompt = new Text("Arena Height: ");
        grid.add(heightPrompt, 0, 1, 1, 1);

        // This pops up to notify the user that it is generating the grid
        buttonNotification = new Text();
        grid.add(buttonNotification, 1, 4);

        HBox hbGenerateButton = new HBox(10); // Makes the button span both columns in the specified row
        hbGenerateButton.setAlignment(Pos.BOTTOM_CENTER);
        hbGenerateButton.getChildren().add(generateButton);
        grid.add(hbGenerateButton, 1, 2);

        return grid;
    }

    /* Ok, this is fixed!
        My issue was that some of the stuff getting divided was integers.
        This meant decimals were getting dropped occasionally, and after
        doing this several times, it became off by a very large amount.
     */
    private void drawGrid(GraphicsContext gc, double canvasWidth, double canvasHeight)
    {
        gc.setLineWidth(1);

        /* Quick test with using x and y coordinates relative to the canvas itself */
        // Horizontal lines - 20 = Padding
        for (double i = 0; i <= canvasHeight + 1; i += canvasHeight / numRows)
        {
            gc.strokeLine(0, i, canvasWidth, i);
        }

        // Vertical lines - 20 = Padding
        for (double i = 0; i <= canvasWidth + 1; i += canvasWidth / numColumns)
        {
            gc.strokeLine(i, 0, i, canvasHeight);
        }

        gc.setLineWidth(4);
    }

    /* Validates User Input and stores it in global variables */
    // Todo - Figure out if it's possible to make this w/ more local variables
    private double parseColumns(TextField widthField)
    {
        // Getting the number of columns
        try
        {
            return Integer.parseInt(widthField.getText());
        }

        catch (Exception e)
        {
            System.out.println("There was an error getting the number of columns.");
            return 100;
        }
    }

    private double parseRows(TextField heightField)
    {
        // Getting the number of rows
        try
        {
            return Integer.parseInt(heightField.getText());
        }

        catch (Exception e)
        {
            System.out.println("There was an error getting the number of rows.");
            return 100;
        }
    }

    // Draws a line on the canvas with the passed-in coordinates. Works as expected.
    private void drawPermanentLine(double x1, double y1, double x2, double y2, GraphicsContext gc)
    {
        buttonNotification.setText(""); // Resetting this so the error message for the line being too long doesn't stay up for too long

        Point2D.Double snapPoint1 = getClosestPointDouble(x1, y1); // Point 1 of the drawn line
        Point2D.Double snapPoint2 = getClosestPointDouble(x2, y2); // Point 2 of the drawn line

        // Distance formula sqrt(x1-x2)^2 + (y1 - y2)^2)
        double distance = Math.sqrt(Math.pow(convertDoubleXToGridUnits(snapPoint1.getX()) - convertDoubleXToGridUnits(snapPoint2.getX()), 2) // X Component
                                  + Math.pow(convertDoubleYToGridUnits(snapPoint1.getY()) - convertDoubleYToGridUnits(snapPoint2.getY()), 2)); // Y Component

        // Todo - Format this so it's not like ten decimals (use DecimalFormat?)
        if (distance > Math.sqrt(8) * 1.05f)
        {
            DecimalFormat decimalFormat = new DecimalFormat("###.##");
            buttonNotification.setText("A line distance of " + decimalFormat.format(distance) + " is too long! Please try to draw a new line.");
        }

        else
        {
            gc.strokeLine(snapPoint1.getX(), snapPoint1.getY(), snapPoint2.getX(), snapPoint2.getY());
        }

        // Resetting the original position
        firstClick = new Point2D.Double();
    }

    // Todo - Fix this (it's returning the original places and not doing any snapping for whatever reason)
    // This is a REALLY fun method (meaning it's complicated as all hell, but surprisingly coherent
    private Point2D.Double getClosestPointDouble(double xPos, double yPos)
    {
        // Need converted into grid units so it's easier to compare to see what side it's on. Rounds up starting at .5
        double xPosInBoxes = convertDoubleXToGridUnits(xPos);
        double yPosInBoxes = convertDoubleYToGridUnits(yPos);

        // Comparisons are done in grid units, then transformed back to pixels when returning the actual point
        if (Math.abs(xPosInBoxes - Math.floor(xPosInBoxes)) >= .5)
        {
            // Bottom-Right of it's box
            if (Math.abs(yPosInBoxes - Math.floor(yPosInBoxes)) >= .5)
            {
                return new Point2D.Double(convertGridXToDoubleUnits(Math.ceil(xPosInBoxes)), convertGridYToDoubleUnits(Math.ceil(yPosInBoxes)));
            }

            // Top-Right of it's box
            else
            {
                return new Point2D.Double(convertGridXToDoubleUnits(Math.ceil(xPosInBoxes)), convertGridYToDoubleUnits(Math.floor(yPosInBoxes)));
            }
        }

        else
        {
            // Bottom-Left of it's box
            if (Math.abs(yPosInBoxes - Math.floor(yPosInBoxes)) >= .5)
            {
                return new Point2D.Double(convertGridXToDoubleUnits(Math.floor(xPosInBoxes)), convertGridYToDoubleUnits(Math.ceil(yPosInBoxes)));
            }

            // Top-Left of it's box
            else
            {
                return new Point2D.Double(convertGridXToDoubleUnits(Math.floor(xPosInBoxes)) , convertGridYToDoubleUnits(Math.floor(yPosInBoxes)));
            }
        }
    }

    // Kinda hacky, but it works by putting a white rectangle over the entire canvas.
    // Todo - More garbage collection (None of the shapes get deleted AFAIK, just get a white box drawn over them
    // Idea on how to fix - Set the canvas equal to a new grid and redraw the grid with whatever's in the input boxes
    private void clearGrid(GraphicsContext gc, double canvasWidth, double canvasHeight)
    {
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
    }

    // Takes a x in pixels and converts it into grid units - Small utility function
    private double convertDoubleXToGridUnits(double x)
    {
        return x / boxWidth;
    }

    // Takes a y in pixels and coverts it into grid units - Small utility function
    private double convertDoubleYToGridUnits(double y)
    {
        return y / boxHeight;
    }

    // Takes a x in grid units and converts it into pixels - Small utility function
    private double convertGridXToDoubleUnits(double x)
    {
        return x * boxWidth;
    }

    // Takes a y in grid units and converts it into pixels - Small utility function
    private double convertGridYToDoubleUnits(double y)
    {
        return y * boxHeight;
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
