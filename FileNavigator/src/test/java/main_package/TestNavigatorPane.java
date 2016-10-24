package main_package;

import static org.junit.jupiter.api.Assertions.expectThrows;

import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.athaydes.automaton.FXApp;


@RunWith(JUnitPlatform.class)
public class TestNavigatorPane {
	@Before
	public void preparation() {
		FXApp.startApp(new MainClass());
	}
	
	@Test
	@DisplayName("throws NPE")
	public void testAddItem() {
		expectThrows(NullPointerException.class, () -> NavigatorPane.addItem(null));
	}

}
