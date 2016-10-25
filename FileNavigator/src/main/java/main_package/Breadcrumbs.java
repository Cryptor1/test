package main_package;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;


/**
 * Represents top pane with breadcrumbs.
 * 
 * @author Igor Taranenko
 */
class Breadcrumbs {
	
	//**************************************************************************
	//                                                                         *
	// Fields private static                                                   *
	//                                                                         *
	//**************************************************************************
	/** Contains breadcrumbs. */
	private static final List<String> breadcrumbs;
	
	/** Contains breadcrumbs. */
	private static final HBox breadcrumbsPane;
	private static final ObservableList<Node> topPaneChildren;
	
	/** If {@code true}, breadcrumbs will have <i>/</i> as the first breadcrumb. */
	private static final boolean isLinux;
	
	
	static {
		breadcrumbs = new ArrayList<>();
		breadcrumbsPane = new HBox(10);
		topPaneChildren = breadcrumbsPane.getChildren();
		isLinux = System.getProperty("os.name").toLowerCase().equals("linux");
	}
	
	
	//**************************************************************************
	//                                                                         *
	// Methods package-access static                                           *
	//                                                                         *
	//**************************************************************************
	/**
	 * Provides a&nbsp;set of breadcrubs to specified {@code deepestDirectory}
	 * inclusively.
	 * 
	 * @param deepestDirectory Directory to provide trace to.
	 * 
	 * @exception NullPointerException Passed argument is {@code null}.
	 * 
	 * @exception IllegalArgumentException Passed argument is&nbsp;not
	 * a&nbsp;directory.
	 */
	static void setBreadcrumbs(final File deepestDirectory) {
		if (deepestDirectory == null) {
			throw new NullPointerException("Passed argument is 'null'");
		}
		if (!(deepestDirectory.isDirectory())) {
			throw new IllegalArgumentException(
					"Passed argument is not a directory");
		}
		
		final StringTokenizer tokenizer = new StringTokenizer(
				deepestDirectory.getAbsolutePath(), File.separator);
		
		breadcrumbs.clear();
		
		if (isLinux) {
			breadcrumbs.add(File.separator);
		}
		
		while (tokenizer.hasMoreTokens()) {
			breadcrumbs.add(tokenizer.nextToken());
		}
		
		final int breadcrumbsQuantity = breadcrumbs.size();
		
		topPaneChildren.clear();
		
		for (int i = 0; i < breadcrumbsQuantity; ++i) {
			final Button breadcrumb = new Button(breadcrumbs.get(i));
			StringBuilder fullPath = new StringBuilder();
			
			// Build full path
			for (int j = 0; j <= i; ++j) {
				fullPath.append(File.separatorChar);
				fullPath.append(breadcrumbs.get(j));
			}
			
			final File directory = new File(fullPath.toString());
			
			
			breadcrumb.setOnAction(new EventHandler<ActionEvent>() {
				
				@Override
				public void handle(final ActionEvent event) {
					NavigatorPane.goInto(directory);
				}
			});
			
			
			topPaneChildren.add(breadcrumb);
		}
	}
	
	
	/**
	 * Returns pane representing breadcrumbs.
	 */
	static HBox getBreadcrumbsPane() {
		return breadcrumbsPane;
	}
}
