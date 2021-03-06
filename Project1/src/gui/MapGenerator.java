package gui;

import java.io.IOException;

import gui.controller.GridController;
import gui.controller.GridOptionsController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MapGenerator extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	private GridController gridController;
	private GridOptionsController gridOptionsCtrl;
	
	final static double BUTTON_PADDING = 2;
	final static int ROWS = 120; 
	final static int COLS = 160;
	final static int WIDTH = 1400;
	final static int HEIGHT = 780;
	final static int CELL_SIZE = 10;
	final static float FONT_SIZE = 1/72;
	final static Font FONT = new Font(12);

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Map searcher");
		
		initGUI();
	}
	
	public void initGUI() {
		try {
			//Load the layout file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MapGenerator.class.getResource("view/map.fxml"));
			rootLayout = (BorderPane) loader.load();
			gridController = loader.getController();
			
			//Insert the grid options table
			insertGridOptions();
			
			//Insert cell display table
			insertCellDisplayTable();
			
			//show the scene
			Scene scene = new Scene(rootLayout);
			
			primaryStage.setScene(scene);
			primaryStage.setMaximized(true);
			primaryStage.show();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void insertGridOptions() {
		try {
			//Load the layout file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MapGenerator.class.getResource("view/GridButtons.fxml"));
			StackPane stack = (StackPane) loader.load();
			
			//Insert in main file
			AnchorPane anchor = (AnchorPane) rootLayout.getRight();
			SplitPane split = (SplitPane) anchor.getChildren().get(0);
			split.getItems().add(stack);
			
			//Tell GridOptionsController about the GridController
			this.gridOptionsCtrl = loader.getController();
			this.gridOptionsCtrl.setGridController(this.gridController);
			this.gridOptionsCtrl.setStage(this.primaryStage);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void insertCellDisplayTable() {
		
		
		try {
			//Load the layout file
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MapGenerator.class.getResource("view/CellInfoDisplay.fxml"));
			StackPane stack = (StackPane) loader.load();
			
			//Insert in main file
			AnchorPane anchor = (AnchorPane) rootLayout.getRight();
			SplitPane split = (SplitPane) anchor.getChildren().get(0);
			split.getItems().add(stack);
			
			//Tell GridController about the CellDisplayController
			gridController.setCellDisplayController(loader.getController());
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}