/*

// Getting the number of rows and columns from the user input
        generateButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                // Notification text that pops up on button-press
                buttonNotification.setText("Drawing the grid.");

                // Getting number of rows
                try
                {
                    numRows = Integer.parseInt(heightField.getText());
                }

                // Error is thrown (usually because the user didn't put anything in)
                catch(Exception rowError)
                {
                    System.out.println("Had an issue getting the row input.");
                    return; // Breaks out of the method - No point in going further if one is invalid
                }

                // Getting number of columns
                try
                {
                    numColumns = Integer.parseInt(widthField.getText());
                }

                // Error is thrown (usually because the user didn't put anything in
                catch(Exception columnError)
                {
                    System.out.println("Had an issue getting the column input.");
                    return; // Breaks out of the method - No point in going further if one is invalid
                }
            }
        });

 */

package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application
{
    // A few global variables - Todo - Make more of these local and pass more between methods, that is a little more CPU intensive but looks generally better than just having a bunch of global variables
    private GraphicsContext gc;
    private int screenWidth = 1000, screenHeight = 1000, numRows = 0, numColumns = 0;
    private TextField widthField = new TextField(), heightField = new TextField();
    private Button generateButton = new Button ("Generate Arena w/ Specified Parameters");

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        primaryStage.setTitle("Arena Design"); // Title of the window
        BorderPane borderPane = new BorderPane(); // The master container-type-thing

        // Making the input system (a GridPane)
        GridPane grid = makeGridPane(); // Makes a GridPane then stores it in the grid object (it's needed to draw all the shapes)

        // Drawing the grid matrix itself
        Canvas canvas = new Canvas(screenWidth - 40, screenHeight - grid.getHeight() - 200);

        // Button action event
        generateButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent e)
            {
                parseUserInput(widthField, heightField);
                drawShapes(canvas.getGraphicsContext2D(), grid);
            }
        });

        // Setting Alignments
        borderPane.setCenter(canvas);
        borderPane.setBottom(grid);

        primaryStage.setScene(new Scene(borderPane)); // Makes a scene out of the root element
        primaryStage.show(); //  Presents everything that's loaded to the user
    }

    private GridPane makeGridPane()
    {
        /* Creating the GridPane and all its elements */
        GridPane grid = new GridPane(); // Initializing the GridPane
        grid.setAlignment(Pos.BOTTOM_CENTER);
        grid.setHgap(10); // The horizontal gap between elements
        grid.setVgap(10); // The vertical gap between elements
        grid.setPadding(new Insets(25, 25, 25, 25)); // How much space is along the edge of the entire grid

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

        HBox hbGenerateButton = new HBox(10); // Dictates button alignment
        hbGenerateButton.setAlignment(Pos.BOTTOM_CENTER);
        hbGenerateButton.getChildren().add(generateButton);
        grid.add(hbGenerateButton, 1, 2);

        return grid;
    }

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

    private void drawShapes(GraphicsContext gc, GridPane grid)
    {
        // Horizontal lines - The 20 is padding
        for (double i = 20; i < screenHeight - grid.getHeight() - 20; i += (screenHeight - grid.getHeight() - 20) / numColumns - 1)
        {
            gc.strokeLine(20, i, screenWidth - 20, i);
        }

        // Vertical lines - The 20 is padding
        for (double i = 20; i < screenWidth - 20; i += (screenWidth - 20) / numRows - 1)
        {
            gc.strokeLine(i, 20, i, screenHeight - grid.getHeight() - 20);
        }
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
