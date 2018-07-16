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
    // A few global variables - Todo - MAKE MORE OF THESE LOCAL, ESPECIALLY ONES NOT WIDELY USED

    // Maximizing canvas size (it's a little bit smaller than the screen height, because that is the limiting factor
    private double sideLength = Toolkit.getDefaultToolkit().getScreenSize().height - 250;
    private double canvasWidth = sideLength, canvasHeight = sideLength;

    private int numRows = 0, numColumns = 0;

    private TextField widthField = new TextField(), heightField = new TextField(); // These values are used in several methods
    private Button generateButton = new Button ("Generate Arena w/ Specified Parameters"); // Todo - Make this a local variable (pass it into functions and stuff)

    private double originalXPos = 0, originalYPos = 0, finalXPos = 0, finalYPos = 0;

    // Holds the grid matrix of lines itself
    private Canvas canvas = new Canvas(canvasWidth, canvasHeight);
    private Text buttonNotification = new Text();

    @Override
    public void start(Stage primaryStage)
    {
        // Linked to the canvas, used to draw stuff
        GraphicsContext gc = canvas.getGraphicsContext2D();

        primaryStage.setTitle("Arena Design"); // Title of the window
        BorderPane borderPane = new BorderPane(); // The master container-type-thing

        // Making the input system (a GridPane)
        GridPane grid = makeGridPane(); // Makes a GridPane then stores it in the grid object (it's needed to draw all the shapes)

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

        // Todo - Make this work
        // Todo - Make it lock onto the nearest intersection on the grid
        // When the mouse is pressed down on a point on the canvas
        canvas.setOnMousePressed(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent e)
            {
                originalXPos = e.getX();
                originalYPos = e.getY();
            }
        });

        // Todo - Implement This (It still works, but this would make this look a lot better
        // Todo - Make it so line can't get longer than a certain length
        // When the mouse is moved around when it is pressed on the canvas
        canvas.setOnMouseDragged(new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent e)
            {
                /* What I want to do here:
                    Remove the last line drawn. (Once I figure out how to do this, of course)
                    Using GraphicsContext to draw an up-to-date line.
                 */

                // Drawing the new line
                // gc.strokeLine(originalXPos, originalYPos, e.getX(), e.getY());
            }
        });

        // Todo - Reject line if it's longer than a certain length (roughly sqrt(5) - Pythagorean theorem) units plus a very small amount
        // When the mouse is released when it is pressed on the canvas
        canvas.setOnMouseReleased(new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent e)
            {
                finalXPos = e.getX();
                finalYPos = e.getY();
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
        // Distance formula
        double distance = Math.sqrt(Math.abs(Math.pow(Math.abs(x1 - x2), 2) + Math.pow(Math.abs(y1 - y2), 2)));
        buttonNotification.setText(""); // Resetting this so the error message for the line being too long doesn't stay up for too long

        // Converts them into the boxes they are in - Note that the upper-leftmost box is (1, 1), and not (0, 0).
        // These need cast to integers because apparently Math.floor doesn't convert to integer, and I really don't care about the decimals
        int boxX = (int)Math.floor((originalXPos / (canvasWidth / numColumns)));
        int boxY = (int)Math.floor((originalYPos / (canvasHeight / numRows)));

        int boxX2 = (int)Math.floor((finalXPos / (canvasWidth / numColumns)));
        int boxY2 = (int)Math.floor((finalYPos / (canvasHeight / numRows)));



        Point2D.Double closestPoint1, closestPoint2; // 1 is the starting point, 2 is the ending point

        // Todo - Make this a method that returns a Point object of the closest point. I'm doing the same thing twice, which is not good and takes up a lot of space.
        // Figuring out which intercept is the closest to the original point
        // Left Half of the box
        if (Math.abs((double)boxX - originalXPos) < .5)
        {
            if (Math.abs((double)boxY - originalYPos) < .5) // Top-left half of the box
            {
                closestPoint1 = new Point2D.Double(boxX * (canvasWidth / numColumns), boxY * (canvasHeight / numRows));
            }

            else // Bottom-left half of the box
            {
                closestPoint1 = new Point2D.Double(boxX * (canvasWidth / numColumns), (boxY * (canvasHeight / numRows)) + (canvasHeight / numRows));
            }
        }

        // Right side of the box
        else
        {
            if (Math.abs((double)boxY - originalYPos) < .5) // Top-right
            {
                closestPoint1 = new Point2D.Double((boxX * (canvasWidth / numColumns)) + (canvasWidth / numColumns), boxY * (canvasHeight / numRows));
            }

            else // Bottom-right half of the box
            {
                closestPoint1 = new Point2D.Double((boxX * (canvasWidth / numColumns)) + (canvasWidth / numColumns), (boxY * (canvasHeight / numRows)) + (canvasHeight / numRows));
            }
        }

        // Figuring out which intercept is closest to the endpoint (done indentically to the method above)
        if (Math.abs((double)boxX - finalXPos) < .5)
        {
            if (Math.abs((double)boxY - finalYPos) < .5) // Top-left half of the box
            {
                closestPoint2 = new Point2D.Double(boxX2 * (canvasWidth / numColumns), boxY2 * (canvasHeight / numRows));
            }

            else // Bottom-left half of the box
            {
                closestPoint2 = new Point2D.Double(boxX2 * (canvasWidth / numColumns), (boxY2 * (canvasHeight / numRows)) + (canvasHeight / numRows));
            }
        }

        // Right side of the box
        else
        {
            if (Math.abs((double)boxY - finalYPos) < .5) // Top-right
            {
                closestPoint2 = new Point2D.Double((boxX2 * (canvasWidth / numColumns)) + (canvasWidth / numColumns), boxY2 * (canvasHeight / numRows));
            }

            else // Bottom-right half of the box
            {
                closestPoint2 = new Point2D.Double((boxX2 * (canvasWidth / numColumns)) + (canvasWidth / numColumns), (boxY2 * (canvasHeight / numRows)) + (canvasHeight / numRows));
            }
        }

        /* These two if statements check that it isn't too long to be a legitimate barrier (the longest of which
            can be the square root of 8. The 25 is just a little bit of leeway.     */
        // Wider than it is tall
        if (Math.abs(x1 - x2) > Math.abs(y1 - y2))
        {
            // The 1.1f is multiplicative instead of a hardcoded pixel value so that it works fine even with more rows/columns
            if (distance < Math.sqrt(8) * canvasWidth / numColumns * 1.1f)
            {
                gc.strokeLine(closestPoint1.getX(), closestPoint1.getY(), closestPoint2.getX(), closestPoint2.getY());
            }

            // If the line is too long
            else
            {
                buttonNotification.setText("Segment is too long! Please try again.");
            }
        }

        // Taller than it is wide
        else
        {
            if (distance < Math.sqrt(8) * canvasHeight / numColumns * 1.1f)
            {
                gc.strokeLine(closestPoint1.getX(), closestPoint1.getY(), closestPoint2.getX(), closestPoint2.getY());
            }

            // If the line is too long
            else
            {
                buttonNotification.setText("Segment is too long! Please try again.");
            }
        }
    }

    // Kinda hacky, but it works by putting a white rectangle over the entire canvas.
    // Todo - More garbage collection (None of the shapes get deleted AFAIK, just get a white box drawn over them
    private void clearGrid(GraphicsContext gc)
    {
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
