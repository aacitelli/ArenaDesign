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
import javafx.scene.input.MouseButton;
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
    private Point2D.Double firstClick = new Point2D.Double();
    private double numRows, numColumns;

    private Button generateButton = new Button ("Generate Arena w/ Specified Parameters");
    private Button resetButton = new Button("Reset the Grid w/ The Same Dimensions");

    private Text buttonNotification = new Text(); // This is essentially an error report so this is probably staying global

    private double boxWidth = 0, boxHeight = 0; // These are used a TON throughout the program, they are just variables for clarity

    /**
     *
     * @param primaryStage = The stage that JavaFX displays to the user
     */
    @Override
    public void start(Stage primaryStage)
    {
        // Master-level stuff
        primaryStage.setTitle("Arena Design");
        BorderPane borderPane = new BorderPane();

        // Canvas is always a square
        double canvasWidth = Toolkit.getDefaultToolkit().getScreenSize().height - 250;
        double canvasHeight = Toolkit.getDefaultToolkit().getScreenSize().height - 250;

        // Used for drawing things
        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Making the inputGrid
        GridPane grid = makeGridPane();
        TextField widthField = new TextField(), heightField  = new TextField();
        grid.add(widthField, 1, 0);
        grid.add(heightField, 1, 1);

        // Generate Button Event Handler
        generateButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                // Reading user input
                numRows = parseRows(heightField);
                numColumns = parseColumns(widthField);

                // These variables are used all over the place
                boxWidth = canvasWidth / numColumns;
                boxHeight = canvasHeight / numRows;

                // Clears then draws the grid
                clearGrid(gc, canvasWidth, canvasHeight);
                drawGrid(gc, canvasWidth, canvasHeight);
            }
        });

        resetButton.setOnAction(new EventHandler<ActionEvent>()
        {
            public void handle(ActionEvent e)
            {
                clearGrid(gc, canvasWidth, canvasHeight);
                drawGrid(gc, canvasWidth, canvasHeight); // Todo - Make it so this doesn't re-read the width and the height. Very small inefficiency, but could be slightly optimized
            }
        });

        // Todo - Make the "line creation" element of this work
        canvas.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent e)
            {
                // Checks that it's a left click
                if (e.getButton().equals(MouseButton.PRIMARY))
                {
                    firstClick.setLocation(e.getX(), e.getY());
                }

            }
        });


        // Todo - Implement a real-time preview of the line (this is much harder than it seems cause I have to use GraphicsContext due to this being JavaFX
        canvas.setOnMouseDragged(new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent e)
            {
                // Checks that it's a right click
                if (e.getButton().equals(MouseButton.SECONDARY))
                {

                }
            }
        });

        // When the mouse is released when it is pressed on the canvas
        canvas.setOnMouseReleased(new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent e)
            {
                // Checks that it's a left click
                if (e.getButton().equals(MouseButton.PRIMARY))
                {
                    drawPermanentLine(firstClick.getX(), firstClick.getY(), e.getX(), e.getY(), gc);

                    // Mirrors the line across the line y = x basically
                    // Todo - Fix this line. Took like three seconds to program, but it really should still work tbh
                    drawPermanentLine(canvasWidth - firstClick.getX(), canvasHeight - firstClick.getY(), canvasWidth - e.getX(), canvasHeight - e.getY(), gc);

                }
            }
        });

        // Setting Alignments / Adding them to the BorderPane
        borderPane.setCenter(canvas);
        borderPane.setBottom(grid);

        primaryStage.setScene(new Scene(borderPane)); // Makes a scene out of the borderPane element
        primaryStage.show(); //  Presents everything that's loaded to the user
    }

    /**
     *
     * @return = A GridPane with most input elements needed for the program
     */
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

        HBox hbResetButton = new HBox(10);
        hbResetButton.setAlignment(Pos.BOTTOM_CENTER);
        hbResetButton.getChildren().add(resetButton);
        grid.add(hbResetButton, 1, 3);

        return grid;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Drawing Lines
    //------------------------------------------------------------------------------------------------------------------

    /**
     *
     * @param x1 = X-Coordinate of Line 1
     * @param y1 = Y-Coordinate of Line 1
     * @param x2 = X-Coordinate of Line 2
     * @param y2 = Y-Coordinate of Line 2
     * @param gc = The GraphicsContext linked to the main canvas
     */
    private void drawPermanentLine(double x1, double y1, double x2, double y2, GraphicsContext gc)
    {
        buttonNotification.setText(""); // Resetting this so the error message for the line being too long doesn't stay up for too long

        Point2D.Double snapPoint1 = getClosestPointDouble(x1, y1); // Point 1 of the drawn line
        Point2D.Double snapPoint2 = getClosestPointDouble(x2, y2); // Point 2 of the drawn line

        // Distance formula sqrt(x1-x2)^2 + (y1 - y2)^2)
        double distance = getDistanceBetweenPoints(snapPoint1, snapPoint2);

        if (distance > Math.sqrt(8) * 1.05)
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

    /**
     *
     * @param xPos = The x position of the point (in pixels)
     * @param yPos = The y position of the point (in pixels)
     * @return
     */
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

    /**
     *
     * @param point1 = One endpoint of the line
     * @param point2 = The other endpoint of the line
     * @return = The distance (IN PIXELS) between the two lines
     */
    private double getDistanceBetweenPoints(Point2D.Double point1, Point2D.Double point2)
    {
        // Distance Formula = sqrt((x1 - x2)^2 + (y1 - y2)^2)
        return Math.sqrt(Math.pow(convertDoubleXToGridUnits(point1.getX()) - convertDoubleXToGridUnits(point2.getX()), 2)   // X Component
                       + Math.pow(convertDoubleYToGridUnits(point1.getY()) - convertDoubleYToGridUnits(point2.getY()), 2)); // Y Component
    }

    //------------------------------------------------------------------------------------------------------------------
    // Grid Methods
    //------------------------------------------------------------------------------------------------------------------

    /**
     *
     * @param gc = The GraphicsContext linked to the main canvas
     * @param canvasWidth = The width of the canvas
     * @param canvasHeight = The height of the canvas
     */
    private void clearGrid(GraphicsContext gc, double canvasWidth, double canvasHeight)
    {
        gc.clearRect(0, 0, canvasWidth, canvasHeight);
    }

    /**
     *
     * @param gc = The GraphicsContext linked to the main canvas
     * @param canvasWidth = The width of the canvas
     * @param canvasHeight = The height of the canvas
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

    //------------------------------------------------------------------------------------------------------------------
    // Parsing User Input Methods
    //------------------------------------------------------------------------------------------------------------------

    /**
     *
     * @param widthField = The TextField that the columns are being read from
     * @return = The double-parsed number of columns
     */
    private double parseColumns(TextField widthField)
    {
        // Getting the number of columns
        try
        {
            double numColumns = Integer.parseInt(widthField.getText()); // Here in case java tries to return an error
            return numColumns;
        }

        catch (Exception e)
        {
            System.out.println("There was an error getting the number of columns.");
            return 100;
        }
    }

    /**
     *
     * @param heightField = The TextField that the rows are being read from
     * @return = The double-parsed number of rows
     */
    private double parseRows(TextField heightField)
    {
        // Getting the number of rows
        try
        {
            double numRows = Integer.parseInt(heightField.getText()); // Here in case java tries to return an error
            return numRows;
        }

        catch (Exception e)
        {
            System.out.println("There was an error getting the number of rows.");
            return 100;
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    // Conversion Methods
    //------------------------------------------------------------------------------------------------------------------

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

    //------------------------------------------------------------------------------------------------------------------
    // Main Method
    //------------------------------------------------------------------------------------------------------------------

    public static void main(String[] args)
    {
        launch(args);
    }


}
