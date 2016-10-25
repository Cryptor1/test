package main_package;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


/**
 * Tests {@code NavigatorPane} class.
 * 
 * @author Igor Taranenko
 */
public class TestNavigatorPane extends ApplicationTest {
	
	/** Is used in {@link #testGoIntoNormal()} method to check
	 * {@link NavigatorPane#goInto(File)} method's normal behavior. During
	 * calling to tested method <i>navigator&nbsp;pane</i> passes through having
	 * no&nbsp;items and than having at least one item. {@code true} means
	 * tested method passed <u>first</u> stage. */
	private static boolean navigatorPaneHadNoElements;
	/** Is used in {@link #testGoIntoNormal()} method to check
	 * {@link NavigatorPane#goInto(File)} method's normal behavior. During
	 * calling to tested method <i>navigator&nbsp;pane</i> passes through having
	 * no&nbsp;items and than having at least one item. {@code true} means
	 * method passed </u>second</u> stage. */
	private static boolean navigatorPaneHasElements;
	/** Is used in {@link #testGoIntoNPE()} method to check if
	 * {@link NullPointerException} have&nbsp;been&nbsp;thrown.
	 * {@code true}&nbsp;&#0151; mentioned exception was successfully thrown. */
	private static boolean npeExceptionOccurred;
	/** Is used in {@link #testGoIntoIllegalArgument()} method to check if
	 * {@link IllegalArgumentException} have&nbsp;been&nbsp;thrown.
	 * {@code true}&nbsp;&#0151; mentioned exception was successfully thrown. */
	private static boolean illegalArgumentExceptionOccurred;
	
	
	static {
		navigatorPaneHadNoElements = false;
		navigatorPaneHasElements = false;
		npeExceptionOccurred = false;
		illegalArgumentExceptionOccurred = false;
	}
	
	
	@Override
	public void start(final Stage stage) throws Exception {
		final BorderPane rootPane = new BorderPane(
				new ScrollPane(NavigatorPane.getNavigatorPane()),
				Breadcrumbs.getBreadcrumbsPane(),
				null, null, null);
		final Scene scene = new Scene(rootPane);
		
		stage.setScene(scene);
		stage.show();
	}
	
	
	
	
	/**
	 * Tests normal {@link NavigatorPane#goInto(java.io.File)} method behavior.
	 */
	@Test
	public void testGoIntoNormal() {
		/* Navigator pane's elements quantity is expected to change after
		 * tested method calling */
		
		final ObservableList<Node> navigatorPaneChildren =
				NavigatorPane.getNavigatorPane().getChildren();
		final int navigatorPaneChildrenQuantityBeforeTest =
				navigatorPaneChildren.size();
		
		assertEquals(true, navigatorPaneChildrenQuantityBeforeTest > 0);
		
		
		navigatorPaneChildren.addListener(new ListChangeListener<Node>() {

			@Override
			public void onChanged(
			        javafx.collections.ListChangeListener.Change<? extends Node> c) {
				while (c.next()) {
					// If all elements were removed
					if (c.wasRemoved()
							&& c.getRemovedSize() == navigatorPaneChildrenQuantityBeforeTest) {
						navigatorPaneHadNoElements = true;
					}
					
					if (c.wasAdded()) {
						navigatorPaneHasElements = true;
					}
				}
			}
		});
		
		
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				NavigatorPane.goInto(File.listRoots()[0]);
				assertEquals(true, navigatorPaneHasElements);
				assertEquals(true, navigatorPaneHadNoElements);
			}
		});
	}
	
	
	/**
	 * Tests {@link NavigatorPane#goInto(java.io.File)} for throwing
	 * {@link NullPointerException}.
	 */
	@Test
	public void testGoIntoNPE() {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					NavigatorPane.goInto(null);
				} catch (final NullPointerException e) {
					npeExceptionOccurred = true;
				} finally {
					assertEquals(true, npeExceptionOccurred);
				}
			}
		});
	}
	
	
	/**
	 * Tests {@link NavigatorPane#goInto(File)} for throwing
	 * {@link IllegalArgumentException}.
	 */
	@Test
	public void testGoIntoIllegalArgument() {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					NavigatorPane.goInto(new File(" 343/sf figny@kakaya t0"));
				} catch (final IllegalArgumentException e) {
					illegalArgumentExceptionOccurred = true;
				} finally {
					assertEquals(true, illegalArgumentExceptionOccurred);
				}
			}
		});
	}
}
