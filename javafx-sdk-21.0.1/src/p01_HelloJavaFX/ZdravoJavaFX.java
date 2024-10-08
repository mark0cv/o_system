package p01_HelloJavaFX;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ZdravoJavaFX extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		VBox root = new VBox(10);
		root.setPadding(new Insets(10, 10, 10, 10));

		Button btKlikniMe = new Button("Klikni me!");
		Button btBrisi = new Button("Brisi!");
		
		Label lblTekst = new Label("");
		lblTekst.setTextFill(Color.RED);
		
		root.getChildren().addAll(lblTekst, btKlikniMe, btBrisi);
	
		btKlikniMe.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				lblTekst.setText("Zdravo svijete!");
			}
		});
		
		btBrisi.setOnAction(e -> {
				lblTekst.setText("");
		});
		
		Scene scene = new Scene(root, 200, 200);
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Hello world");
		primaryStage.show();
	}
}
