package cyberLib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import static cyberLib.connection.SyntaxChecker.validIP;

import org.junit.Test;

public class TestClasses {
	// Random:
	private static final int ITERATIONS = 100000;
	private static final float ERROR = 0.05f;
	
	@Test
	public void chechValidIP_valid() {
		assertTrue(validIP("192.168.1.1"));
	}
	
	@Test
	public void chechValidIP_invalidChar() {
		assertFalse(validIP("caco.asido.psaid"));
	}
	
	@Test
	public void chechValidIP_only3numbers() {
		assertFalse(validIP("192.168.1"));
	}
	
	@Test
	public void chechValidIP_moreThan4numbers() {
		assertFalse(validIP("192.168.1.1.90"));
	}
	
	@Test
	public void chechValidIP_invalidNumbers() {
		assertFalse(validIP("896.168.1.1"));
	}
	
	@Test
	public void chechValidIP_oneNumber() {
		assertFalse(validIP("896"));
	}
}
