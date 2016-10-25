package main_package;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.collections.ObservableList;
import javafx.geometry.VerticalDirection;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * Simulates navigation through directories.
 * 
 * @author Igor Taranenko
 */
public class TestNavigationTraversing extends ApplicationTest {

	//**************************************************************************
	//                                                                         *
	// Fields private of-instance                                              *
	//                                                                         *
	//**************************************************************************
	/** Contains <i>navigator&nbsp;pane</i> items. */
	private ObservableList<Node> navigatorPaneChildren;
	/** Is used by {@link #performClicking(int, VerticalDirection)} method
	 * to prevent <i>navigator&nbsp;pane's</i> items overlapping by
	 * <i>breadcrumbs&nbsp;pane</i> when performing clicking. */
	double breadcrumbsHeight;
	
	
	@Override
	public void start(final Stage stage) throws Exception {
		final VBox navigatorPane = NavigatorPane.getNavigatorPane();
		final HBox breadcrumbsPane = Breadcrumbs.getBreadcrumbsPane();
		final BorderPane rootPane = new BorderPane(
				new ScrollPane(navigatorPane),
				breadcrumbsPane,
				null, null, null);
		final Scene scene = new Scene(rootPane);
		
		stage.setScene(scene);
		stage.show();
		
		NavigatorPane.goInto(File.listRoots()[0]);
		navigatorPaneChildren = navigatorPane.getChildren();
		breadcrumbsHeight = breadcrumbsPane
				.localToScene(Breadcrumbs.getBreadcrumbsPane()
						.getBoundsInLocal())
				.getMaxY();
	}
	
	
	/**
	 * Simulates navigating through directories.
	 */
	@Test
	public void navigate() {
		int navigatorPaneChildrenQuantity = navigatorPaneChildren.size();
		final HBox breadcrumbsPane = Breadcrumbs.getBreadcrumbsPane();
		final ObservableList<Node> breadcrumbsPaneChildren =
				breadcrumbsPane.getChildren();
		boolean folderWasEntered = false;
		
		// First enter first possible folder from the very top
		for (int i = 0;
				i < navigatorPaneChildrenQuantity && !folderWasEntered;
				++i) {
			performClicking(i, VerticalDirection.DOWN);
			
			// When successfully entered folder
			if (breadcrumbsPaneChildren.size() == 2) {
				folderWasEntered = true;
			}
		}
		
		assertEquals(2, breadcrumbsPaneChildren.size());
		/* The root directory is expected to have at least one subdirectory
		 * which can be opened */
		assertEquals(true, folderWasEntered);
		
		// If last assertion fails - no need to proceed
		if (!folderWasEntered) {
			return;
		}
		
		
		/* In next step try to open files/directories beginning from the bottom.
		 * Such behavior helps detect files which assigned as foldes (which is
		 * wrong and will end with IllegalArgumentException)
		 * This time there is no strict necessity to open subfolder. */
		folderWasEntered = false;
//		breadcrumbsHeight = breadcrumbsPane
//				.localToScene(breadcrumbsPane.getBoundsInLocal())
//				.getMaxY();
		navigatorPaneChildrenQuantity = navigatorPaneChildren.size();
		scroll(navigatorPaneChildrenQuantity, VerticalDirection.DOWN);
		
		for (int i = navigatorPaneChildrenQuantity - 1;
				i >= 0 && !folderWasEntered;
				--i) {
			performClicking(i, VerticalDirection.UP);
			
			// When successfully entered folder
			if (breadcrumbsPaneChildren.size() == 3) {
				folderWasEntered = true;
			}
		}
		
		clickOn(breadcrumbsPaneChildren.get(0), MouseButton.PRIMARY);
		assertEquals(1, breadcrumbsPaneChildren.size());
	}
	
	
	//**************************************************************************
	//                                                                         *
	// Methods private of-instance                                             *
	//                                                                         *
	//**************************************************************************
	/**
	 * Auxiliary for {@link #navigate()} method. Performs clicking on
	 * the&nbsp;<i>navigator&nbsp;pane</i> item.
	 * 
	 * @param nodeIndex <i>Navigator's&nbsp;pane</i> {@link HBox}
	 * item&nbsp;index to find. Mentioned <i>HBox</i> contains clickable
	 * {@link Label} with file/folder name, which is need to be clicked.
	 * 
	 * @param directionToFindNode Direction to scroll with&nbsp;a&nbsp;view to
	 * find invisible <i>Node</i>.
	 */
	private void performClicking(
			final int nodeIndex, final VerticalDirection directionToFindNode) {
		final Node toClick =
				((HBox)navigatorPaneChildren.get(0)).getChildren().get(0);
		/* 'toClick' node's upper point is compared with 'breadcrumbsHeight'
		 * with a view to ensure that 'toClick' isn't overlapped by
		 * breadcrumbs pane */
		final double toClickUpperPoint =
				toClick.localToScene(toClick.getBoundsInLocal()).getMaxY();
		
		if (!toClick.isVisible() || toClickUpperPoint < breadcrumbsHeight) {
			scroll(directionToFindNode);
		}
		
		// Click on Label object representing folder name
		clickOn(((HBox)navigatorPaneChildren.get(nodeIndex)).getChildren().get(0),
				MouseButton.PRIMARY);
		/* In case of encountering folder which restricts its opening, there is
		 * a dialog window occurrs. Pressing enter makes opportunity to proceed */
		push(KeyCode.ENTER);
	}
}
