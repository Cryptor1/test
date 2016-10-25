package main_package;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


/**
 * Implements file navigation pane.
 * 
 * @author Igor Taranenko
 */
class NavigatorPane {
	
	//**************************************************************************
	//                                                                         *
	// Fields private static                                                   *
	//                                                                         *
	//**************************************************************************
	private static final Logger LOGGER;
	
	/** Represents files and folders in the specified folder. */
	private static final VBox navigatorPane;
	private static final ObservableList<Node> navigatorPaneChildren;
	
	
	static {
		LOGGER = Logger.getLogger(NavigatorPane.class.getName());
		navigatorPane = new VBox();
		navigatorPaneChildren = navigatorPane.getChildren();
		
		// All root directories on the running machine
		final File[] rootDirectories = File.listRoots();
		
		// Linux has one root directory
		if (rootDirectories.length == 1) {
			final List<File> files = getFileList(rootDirectories[0]);
			
			for (final File i : files)
			{
				addItem(i);
			}
		}
		// TODO: Need to check on Windows
		// Windows may have several root directories
		else
		{
			for (final File i : rootDirectories)
			{
				addItem(i);
			}
		}
	}
	
	
	//**************************************************************************
	//                                                                         *
	// Methods package-access static                                           *
	//                                                                         *
	//**************************************************************************
	/**
	 * Returns object representing file navigation pane.
	 */
	static VBox getNavigatorPane() {
		return navigatorPane;
	}
	
	
	/**
	 * Opens specified {@code directory}.
	 * 
	 * @param directory The directory to open.
	 * 
	 * @exception NullPointerException Passed argument is {@code null}.
	 * 
	 * @exception IllegalArgumentException Passed argument
	 * is&nbsp;not&nbsp;a&nbsp;directory.
	 */
	static void goInto(final File directory) {
		if (directory == null) {
			throw new NullPointerException("Passed argument is 'null'");
		}
		if (!directory.exists()) {
			throw new IllegalArgumentException(
					"Passed directory does\u00A0not\u00A0exist");
		}
		
		final List<File> files = getFileList(directory);
		
		if (files == null) {
			return;
		}
		
		navigatorPaneChildren.clear();
		
		for (final File i : files) {
			addItem(i);
		}
		
		Breadcrumbs.setBreadcrumbs(directory);
	}
	
	
	//**************************************************************************
	//                                                                         *
	// Methods private static                                                  *
	//                                                                         *
	//**************************************************************************
	/**
	 * Performs filling file navigation pane with specified {@code file}.
	 * 
	 * @param file Item to put into file navigation pane.
	 * 
	 * @exception IllegalArgumentException Passed file/folder
	 * does&nbsp;not&nbsp;exist.
	 */
	private static void addItem(final File file) {
		if (!(file.exists())){
			throw new IllegalArgumentException("No such file or directory");
		}
		
		final Label fileName = new Label(file.getName());
		
		if (file.isDirectory()) {
			fileName.setOnMouseClicked(new EventHandler<MouseEvent>() {
				
				@Override
				public void handle(final MouseEvent event) {
					goInto(file);
				}
			});
		}
		
		// TODO: Provide convenient convertation to KB, MB, GB
		final Label fileSize = new Label(String.valueOf(file.length()));
		BasicFileAttributes fileAttributes = null;
		
		try {
			fileAttributes =
					Files.readAttributes(file.toPath(), BasicFileAttributes.class);
		} catch (final UnsupportedOperationException | IOException e) {
			LOGGER.log(
					Level.SEVERE,
					"Cannot read " + file.getAbsolutePath()
					+ " attributes. Exception stack trace:",
					e);
		}
		
		final Label creationDate =
				new Label(fileAttributes.creationTime().toString());
		
		// Contains data relative to file/directory passed
		final HBox itemRepresentative =
				new HBox(10, fileName, fileSize, creationDate);
		itemRepresentative.setUserData(file);
		navigatorPaneChildren.add(itemRepresentative);
	}
	
	
	/**
	 * Returns list of files and directories stored in {@code directory}.
	 * 
	 * @param directory Directory which content needs to be listed.
	 */
	private static List<File> getFileList(final File directory) {
		List<File> files = null;
		
		try {
			files = Arrays.asList(directory.listFiles());
		} catch (final SecurityException | NullPointerException e) {
			final String message = "Not enough permissions to open directory.";
			LOGGER.log(Level.FINE, message, e);
			
			final Alert alert = new Alert(AlertType.ERROR);
			
			alert.setContentText(message);
			alert.showAndWait();
			return null;
		}
		
		files.sort(new Comparator<File>() {

			@Override
			public int compare(final File o1, final File o2) {
				final boolean o1IsDirectory = o1.isDirectory();
				final boolean o2IsDirectory = o2.isDirectory();
				
				if (o1IsDirectory && o2IsDirectory) {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
				if (!o1IsDirectory && !o2IsDirectory) {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}				
				if (o1IsDirectory && !o2IsDirectory) {
					return -1;
				}
				
				return 1;
			}
		});
		
		return files;
	}
}
