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

/* Ideas:
    - Implement zooming in and out
    - Make it so the user can take "chunks" out of the arena (not every arena is a perfect square, the XLS arena included)
 */
public class Main extends Application
{
    // A few global variables - Todo - MAKE MORE OF THESE LOCAL, ESPECIALLY ONES NOT WIDELY USED - This comes after adding all the functionality I want, though

    // Maximizing canvas size (it's a little bit smaller than the screen height, because that is the limiting factor
    private double sideLength = Toolkit.getDefaultToolkit().getScreenSize().height - 250;
    private double canvasWidth = sideLength, canvasHeight = sideLength;

    private int numRows = 0, numColumns = 0;

    private TextField widthField = new TextField(), heightField = new TextField(); // These values are used in several methods
    private Button generateButton = new Button ("Generate Arena w/ Specified Parameters"); // Todo - Make this a local variable (pass it into functions and stuff)

    private double originalXPos = 0, originalYPos = 0;

    // Holds the grid matrix of lines itself
    private Canvas canvas = new Canvas(canvasWidth, canvasHeight);
    private Text buttonNotification = new Text();

    private double boxWidth = 0, boxHeight = 0; // These are used a TON throughout the program, they are just variables for clarity

    @Override
    public void start(Stage primaryStage)
    {
        // Linked to the canvas, used to draw stuff
        GraphicsContext gc = canvas.getGraphicsContext2D();

        primaryStage.setTitle("Arena Design"); // Title of the window
        BorderPane borderPane = new BorderPane(); // The master container-type-thing

        // Making the input system (a GridPane)
        GridPane grid = makeGridPane(); // Makes a GridPane then stores it in the grid object (it's needed to draw all the shapes)
        boxWidth = canvasWidth / numColumns;
        boxHeight = canvasHeight / numRows;

        // When the "generate" button is pressed
        generateButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                clearGrid(gc);
                parseUserInput(widthField, heightField);
                drawGrid(gc);
            }
        });

        // Todo - Make the "line creation" element of this work
        canvas.setOnMousePressed(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent e)
            {
                originalXPos = e.getX();
                originalYPos = e.getY();
            }
        });

        // Todo - Implement a real-time preview of the line
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
                drawPermanentLine(originalXPos, originalYPos, e.getX(), e.getY(), gc);
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

        // The input field for the width
        grid.add(widthField, 1, 0, 1, 1);

        // The input field for the height
        grid.add(heightField, 1, 1, 1, 1);

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
    private void drawGrid(GraphicsContext gc)
    {
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
    }

    /* Validates User Input and stores it in global variables */
    // Todo - Figure out if it's possible to make this w/ more local variables
    private void parseUserInput(TextField widthField, TextField heightField)
    {
        // Getting the number of rows
        try
        {
            numRows = Integer.parseInt(widthField.getText());
        }

        catch (Exception e)
        {
            System.out.println("There was an error getting the number of rows.");
        }

        // Getting the number of columns
        try
        {
            numColumns = Integer.parseInt(heightField.getText());
        }

        catch (Exception e)
        {
            System.out.println("There was an error getting the number of columns.");
        }
    }

    // Draws a line on the canvas with the passed-in coordinates. Works as expected.
    private void drawPermanentLine(double x1, double y1, double x2, double y2, GraphicsContext gc)
    {
        buttonNotification.setText(""); // Resetting this so the error message for the line being too long doesn't stay up for too long

        Point2D.Double snapPoint1 = getClosestPointDouble(x1, y1); // Point 1 of the drawn line
        Point2D.Double snapPoint2 = getClosestPointDouble(x2, y2); // Point 2 of the drawn line

        // Distance formula sqrt(x1-x2)^2 + (y1 - y2)^2)
        double distance = Math.sqrt(Math.pow((snapPoint1.getX() / boxWidth) - (snapPoint2.getX() / boxWidth), 2) // X Component
                                  + Math.pow((snapPoint1.getY() / boxHeight) - (snapPoint2.getY() / boxHeight), 2)); // Y Component

        // Todo - Format this so it's not like ten decimals (use DecimalFormat?)
        if (distance > Math.sqrt(8) * 1.05f)
        {
            buttonNotification.setText("A line distance of " + Double.toString(distance) + " is too long! Please try to draw a new line.");
        }

        else
        {
            gc.strokeLine(snapPoint1.getX(), snapPoint1.getY(), snapPoint2.getX(), snapPoint2.getY());
        }
    }

    // Todo - Fix this (it's returning the original places and not doing any snapping for whatever reason)
    private Point2D.Double getClosestPointDouble(double xPos, double yPos)
    {
        // Converts the positions into grid coordinates essentially
        double xPosInBoxes = xPos / boxWidth;
        double yPosInBoxes = yPos / boxHeight;

        // If it's on the right side of its box
        if (Math.abs(xPosInBoxes - Math.floor(xPosInBoxes)) >= .5)
        {
            // If it's on the bottom
            if (Math.abs(yPosInBoxes - Math.floor(yPosInBoxes)) >= .5)
            {
                return new Point2D.Double((xPosInBoxes + 1) / boxWidth, (yPosInBoxes + 1) / boxHeight);
            }

            // If it's on the top
            else
            {
                return new Point2D.Double((xPosInBoxes + 1) / boxWidth, (yPosInBoxes) / boxHeight);
            }
        }

        // If it's on the left side of nts box
        else
        {
            // If it's on the bottom
            if (Math.abs(yPosInBoxes - Math.floor(yPosInBoxes)) >= .5)
            {
                return new Point2D.Double((xPosInBoxes) / boxWidth, (yPosInBoxes + 1) / boxHeight);
            }

            // If it's on the top
            else
            {
                return new Point2D.Double((xPosInBoxes + 1) / boxWidth, (yPosInBoxes + 1) / boxHeight);
            }
        }
    }

    // Kinda hacky, but it works by putting a white rectangle over the entire canvas.
    // Todo - More garbage collection (None of the shapes get deleted AFAIK, just get a white box drawn over them
    // Idea on how to fix - Set the canvas equal to a new grid and redraw the grid with whatever's in the input boxes
    private void clearGrid(GraphicsContext gc)
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
