package gui.controller;

import java.io.File;

import files.GridFile;
import gui.model.Grid;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class GridOptionsController {

	@FXML
	private Button importMap, exportMap, findPath, findPath2, findPath3, changeStart, changeEnd;
	@FXML
	private TextField row, column;
	private GridController gridCtrl;
	private FileChooser fileChoose = new FileChooser();
	private Stage stage;
	
	@FXML
	public void initialize() {
		
		importMap.setOnMouseClicked(e -> {
			fileChoose.setInitialDirectory(new File("./"));
			File file = fileChoose.showOpenDialog(stage);
			if(file != null) {
				Grid grid = GridFile.importFile(file.getAbsolutePath(), gridCtrl.ROWS, gridCtrl.COLS);
				gridCtrl.changeGrid(grid);
			}
		});
		
		exportMap.setOnMouseClicked(e -> {
			Grid grid = gridCtrl.getGrid();
			fileChoose.setInitialDirectory(new File("./"));
			File file = fileChoose.showSaveDialog(stage);
			if(file != null) {
				GridFile.exportFile(grid, file);
			}
		});
		
		findPath.setOnMouseClicked(e -> {
			System.out.println("This should generate the path and changeGrid with its new version or something");
		});
		
		changeStart.setOnMouseClicked(e -> {
			try {
				int[] newStart = {0, 0};
				newStart[0] = Integer.parseInt(row.getText());
				newStart[1] = Integer.parseInt(column.getText());
				Grid grid = gridCtrl.getGrid();
				boolean changed = grid.changeStart(newStart);
				if (changed) {
					gridCtrl.colorGrid();
				} else {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Error");
					alert.setHeaderText("Invalid numbers");
					alert.setContentText("You gave invalid coordinates for the new start. It is either out of bounds or blocked");
					alert.showAndWait();
				}
			} catch(Exception ex) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error");
				alert.setHeaderText("Not numbers");
				alert.setContentText("You did not give numbers for the new start");
				alert.showAndWait();
			}
			
		});
		
		changeEnd.setOnMouseClicked(e -> {
			try {
				int[] endStart = {0, 0};
				endStart[0] = Integer.parseInt(row.getText());
				endStart[1] = Integer.parseInt(column.getText());
				Grid grid = gridCtrl.getGrid();
				boolean changed = grid.changeEnd(endStart);
				if (changed) {
					gridCtrl.colorGrid();
				} else {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Error");
					alert.setHeaderText("Invalid numbers");
					alert.setContentText("You gave invalid coordinates for the new end. It is either out of bounds or blocked");
					alert.showAndWait();
				}
			} catch(Exception ex) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error");
				alert.setHeaderText("Not numbers");
				alert.setContentText("You did not give numbers for the new end");
				alert.showAndWait();
			}
			
		});
	}
	
	public void setGridController(GridController gc) {
		this.gridCtrl = gc;
	}
	
	public void setStage(Stage s) {
		this.stage = s;
	}
}
