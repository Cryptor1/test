package main_package;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


/**
 * Program's initial class.
 * 
 * @author Igor Taranenko
 */
public class MainClass extends Application {
	
	// TODO: Set logger
	
	public static void main(final String[] args) {
		launch((String[])null);
	}
	
	
	@Override
	public void start(final Stage primaryStage) throws Exception {
		final BorderPane rootPane = new BorderPane(
				new ScrollPane(NavigatorPane.getNavigatorPane()),
				Breadcrumbs.getBreadcrumbsPane(),
				null, null, null);
		final Scene scene = new Scene(rootPane);
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
