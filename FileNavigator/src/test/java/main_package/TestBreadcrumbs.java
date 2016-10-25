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
 * Tests {@code Breadcrumbs} class.
 * 
 * @author Igor Taranenko
 */
public class TestBreadcrumbs extends ApplicationTest {
	
	/** Is used in {@link #testSetBreadcurmbsNormal()} method to check
	 * {@link Breadcrumbs#setBreadcrumbs(File)} method's normal behavior. During
	 * calling to tested method <i>breadcrumbs&nbsp;pane</i> passes through
	 * having no&nbsp;items and than having at&nbsp;least one item. {@code true}
	 * means tested method passed <u>first</u> stage. */
	private static boolean breadcrumbsPaneHadNoElements;
	/** Is used in {@link #testSetBreadcurmbsNormal()} method to check
	 * {@link Breadcrumbs#setBreadcrumbs(File)} method's normal behavior. During
	 * calling to tested method <i>breadcrumbs&nbsp;pane</i> passes through
	 * having no&nbsp;items and than having at&nbsp;least one item. {@code true}
	 * means tested method passed <u>second</u> stage. */
	private static boolean breadcrumbsPaneHasElements;
	
	
	static {
		breadcrumbsPaneHadNoElements = false;
		breadcrumbsPaneHasElements = false;
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
	 * Tests {@link Breadcrumbs#setBreadcrumbs(java.io.File)} for throwing
	 * {@link NullPointerException}.
	 */
	@Test(expected = NullPointerException.class)
	public void testSetBreadcrumbsNPE() {
		Breadcrumbs.setBreadcrumbs(null);
	}
	
	
	/**
	 * Tests {@link Breadcrumbs#setBreadcrumbs(java.io.File)} for throwing
	 * {@link IllegalArgumentException}.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetBreadcrumbsIllegalArgument() {
		Breadcrumbs.setBreadcrumbs(new File("  8798@3# krakozyabra"));
	}
	
	
	/**
	 * Tests normal {@link Breadcrumbs#setBreadcrumbs(File)} method behavior.
	 */
	@Test
	public void testSetBreadcurmbsNormal() {
		/* Breadcrumb pane's elements quantity is expected to change after
		 * tested method calling */
		
		final ObservableList<Node> breadcrumbsPaneChildren =
				Breadcrumbs.getBreadcrumbsPane().getChildren();
		final int breadcrumbsPaneChildrenQuantityBeforeTest =
				breadcrumbsPaneChildren.size();
		
		assertEquals(true, breadcrumbsPaneChildrenQuantityBeforeTest > 0);
		
		breadcrumbsPaneChildren.addListener(new ListChangeListener<Node>() {

			@Override
			public void onChanged(
					final javafx.collections.ListChangeListener.Change<? extends Node> c) {
				while (c.next()) {
					// If all elements were removed
					if (c.wasRemoved()
							&& c.getRemovedSize() == breadcrumbsPaneChildrenQuantityBeforeTest) {
						breadcrumbsPaneHadNoElements = true;
					}
					
					if (c.wasAdded()) {
						breadcrumbsPaneHasElements = true;
					}
				}
			}
		});
		
		
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				Breadcrumbs.setBreadcrumbs(File.listRoots()[0]);
				assertEquals(true, breadcrumbsPaneHadNoElements);
				assertEquals(true, breadcrumbsPaneHasElements);
			}
		});
	}
}
