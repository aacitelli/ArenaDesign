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

public class Main extends Application
{
    // A few global variables - Todo - Make more of these local and pass more between methods, that is a little more CPU intensive but looks generally better than just having a bunch of global variables
    private int canvasWidth = 800, canvasHeight = 800;

    private int numRows = 0, numColumns = 0;

    private TextField widthField = new TextField(), heightField = new TextField(); // These values are used in several methods
    private Button generateButton = new Button ("Generate Arena w/ Specified Parameters"); // Todo - Make this a local variable (pass it into functions and stuff)

    private Point mouseLocation; // Updated whenever the program needs the cursor position
    private Line currentLine; // Used for the line that the user is currently drawing
    private double originalXPos = 0, originalYPos = 0;

    @Override
    public void start(Stage primaryStage)
    {

        primaryStage.setTitle("Arena Design"); // Title of the window
        BorderPane borderPane = new BorderPane(); // The master container-type-thing

        // Making the input system (a GridPane)
        GridPane grid = makeGridPane(); // Makes a GridPane then stores it in the grid object (it's needed to draw all the shapes)

        // Holds the grid matrix of lines itself
        Canvas canvas = new Canvas(canvasWidth, canvasHeight);

        // Button action event
        generateButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                // Todo - Clear the canvas here
                parseUserInput(widthField, heightField);
                drawGrid(canvas.getGraphicsContext2D());
            }
        });

        canvas.setOnMousePressed(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent e)
            {
                originalXPos = mouseLocation.x;
                originalYPos = mouseLocation.y;

                mouseLocation = java.awt.MouseInfo.getPointerInfo().getLocation();
                currentLine = new Line(mouseLocation.x, mouseLocation.y, mouseLocation.x, mouseLocation.y);
            }
        });

        canvas.setOnMouseDragged(new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent e)
            {
                currentLine = new Line(originalXPos, originalYPos, mouseLocation.x, mouseLocation.y);
            }
        });

        canvas.setOnMouseReleased(new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent e)
            {
                drawPermanentLine(originalXPos, originalYPos, mouseLocation.x, mouseLocation.y, canvas.getGraphicsContext2D());
            }
        });

        // Setting Alignments
        borderPane.setTop(canvas);
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
        final Text buttonNotification = new Text();
        grid.add(buttonNotification, 1, 4);

        HBox hbGenerateButton = new HBox(10); // Makes the button span both columns in the specified row
        hbGenerateButton.setAlignment(Pos.BOTTOM_CENTER);
        hbGenerateButton.getChildren().add(generateButton);
        grid.add(hbGenerateButton, 1, 2);

        return grid;
    }

    /* The only big unexpected behavior I ran into here was that the measurements were screwed up. What I didn't
        understand was that the pixel amounts were relative to the canvas that the GraphicsContext was
        in context to. Not the best explanation but it pretty much fixed my issue.
     */
    private void drawGrid(GraphicsContext gc)
    {
        /* Quick test with using x and y coordinates relative to the canvas itself */
        // Horizontal lines - 20 = Padding
        for (double i = 0; i <= canvasHeight; i += canvasHeight / numRows)
        {
            gc.strokeLine(0, i, canvasWidth, i);
        }

        // Vertical lines - 20 = Padding
        for (double i = 0; i <= canvasWidth; i += canvasWidth / numColumns)
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

    // Draws a line on the canvas with the passed-in coordinates
    private void drawPermanentLine(double x1, double y1, double x2, double y2, GraphicsContext gc)
    {
        gc.strokeLine(x1, y1, x2, y2);
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
