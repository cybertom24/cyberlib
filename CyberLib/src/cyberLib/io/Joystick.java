package cyberLib.io;

import java.util.ArrayList;
// To simplify the code
import cyberLib.io.Joystick.Arrows.Direction;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Component.Identifier;

/**
 * A class that uses JInput's package to handle a PS3 / XBOX360 controller The
 * PS3 controller must be recognized as a XBOX360 one by the system or it won't
 * work That can be done by external programs like SCP-Toolkit or similar
 * 
 * @author savol
 * 
 */
public class Joystick {

	/**
	 * Contains positions and names of the buttons associated with the controller.
	 * 
	 */
	public static class Buttons {
		public static final int X = 2;
		public static final int Y = 3;
		public static final int A = 0;
		public static final int B = 1;
		public static final int R_THUMB = 9;
		public static final int L_THUMB = 8;
		public static final int START = 7;
		public static final int SELECT = 6;
		public static final int RB = 5;
		public static final int LB = 4;
		public static final int UP = 10;
		public static final int DOWN = 11;
		public static final int RIGHT = 12;
		public static final int LEFT = 13;

		public static final int PRESSED = 1;
		public static final int RELEASED = -1;
		public static final int UNCHANGED = 0;

		public static String getName(int i) {
			String name = "";
			if (i == A)
				name = "A";
			else if (i == B)
				name = "B";
			else if (i == X)
				name = "X";
			else if (i == Y)
				name = "Y";
			else if (i == RB)
				name = "RB";
			else if (i == LB)
				name = "LB";
			else if (i == R_THUMB)
				name = "R_THUMB";
			else if (i == L_THUMB)
				name = "L_THUMB";
			else if (i == START)
				name = "START";
			else if (i == SELECT)
				name = "SELECT";
			else if (i == UP)
				name = "UP";
			else if (i == DOWN)
				name = "DOWN";
			else if (i == RIGHT)
				name = "RIGHT";
			else if (i == LEFT)
				name = "LEFT";
			return name;
		}
	}

	private Controller controller;
	private ArrayList<Component> buttons;
	private ArrayList<Axis> axis;
	private Arrows arrows;

	private boolean[] buttonsExState;
	private int[] buttonsChange;

	private float[] axisExState;
	private boolean[] axisChange;
	
	private ArrayList<ButtonListener> buttonListeners;
	private ArrayList<AxisListener> axisListeners;
	
	// Exceptions messages
	public final static String NOTFOUND_EXCEPTION = "The controller object is null";
	public final static String NOTSUPPORTED_EXCEPTION = "The controller is not supported";

	/**
	 * The main class present in this library. Needs a
	 * net.java.games.input.Controller from whom extract the ...input.Components
	 * which represent the single button or joystick.
	 *  
	 * @throws {@code NotSupportedException} if the controller is not supported
	 * @throws {@code NotFoundException} if the controlled passed is null
	 */
	public Joystick(Controller controller) throws Exception {

		if (controller == null)
			throw new Exception(NOTFOUND_EXCEPTION);

		this.controller = controller;
		buttons = new ArrayList<>();

		for (Component c : controller.getComponents()) {
			Identifier id = c.getIdentifier();

			if (id == Identifier.Button._0 || id == Identifier.Button._1 || id == Identifier.Button._2
					|| id == Identifier.Button._3 || id == Identifier.Button._4 || id == Identifier.Button._5
					|| id == Identifier.Button._6 || id == Identifier.Button._7 || id == Identifier.Button._8
					|| id == Identifier.Button._9) {
				buttons.add(c);
			}
		}
		// Add place for the arrows
		buttonsExState = new boolean[buttons.size() + 4];
		buttonsChange = new int[buttons.size() + 4];

		axis = new ArrayList<>();
		axis.add(new Axis(controller.getComponent(Identifier.Axis.X))); 		// Left x
		axis.add(new Axis(controller.getComponent(Identifier.Axis.Y), true)); 	// Left y
		axis.add(new Axis(controller.getComponent(Identifier.Axis.RX))); 		// Right x
		axis.add(new Axis(controller.getComponent(Identifier.Axis.RY), true)); 	// Right y
		axis.add(new Axis(controller.getComponent(Identifier.Axis.Z), true)); 	// Back = LT and RT

		arrows = new Arrows(controller.getComponent(Identifier.Axis.POV));

		axisExState = new float[axis.size()];
		axisChange = new boolean[axisExState.length];
		
		buttonListeners = new ArrayList<>();
		axisListeners = new ArrayList<>();
	}
	
	
	/**
	 * Updates the status of buttons and levers by polling the controller
	 * 
	 * @return {@code true} if the controller is still connected, {@code false} if not
	 */
	public boolean update() {
		boolean connected = controller.poll();
		if (!connected)
			return false;

		// Check if the buttons have changed state
		for (int i = 0; i < buttons.size(); i++) {
			boolean state = getState(i);
			if (buttonsExState[i] == state)
				buttonsChange[i] = Buttons.UNCHANGED;
			else if (state)
				buttonsChange[i] = Buttons.PRESSED;
			else
				buttonsChange[i] = Buttons.RELEASED;
			buttonsExState[i] = state;
		}

		// Look for any change in the axis
		for (int i = 0; i < axisExState.length; i++) {
			float value = axis.get(i).getValue();
			axisChange[i] = value != axisExState[i];
			axisExState[i] = value;
		}

		// Look for any changes among the arrow keys and convert it to button presses
		Direction dir = arrows.getDirection();
		boolean[] newState = new boolean[4];
		newState[Buttons.UP - Buttons.UP] = dir == Direction.UP || dir == Direction.UP_LEFT
				|| dir == Direction.UP_RIGHT;
		newState[Buttons.DOWN - Buttons.UP] = dir == Direction.DOWN || dir == Direction.DOWN_LEFT
				|| dir == Direction.DOWN_RIGHT;
		newState[Buttons.RIGHT - Buttons.UP] = dir == Direction.RIGHT || dir == Direction.DOWN_RIGHT
				|| dir == Direction.UP_RIGHT;
		newState[Buttons.LEFT - Buttons.UP] = dir == Direction.LEFT || dir == Direction.DOWN_LEFT
				|| dir == Direction.UP_LEFT;
		for (int i = 0; i < 4; i++) {
			int j = Buttons.UP + i;
			if (newState[i] == buttonsExState[j])
				buttonsChange[j] = 0;
			else if (newState[i])
				buttonsChange[j] = 1;
			else
				buttonsChange[j] = -1;
			buttonsExState[j] = newState[i];
		}
		
		
		// Notify all listeners
		for(ButtonListener buttonListener : buttonListeners) {
			for(int i = 0; i < buttonsChange.length; i++) {
				if(buttonsChange[i] == Buttons.PRESSED)
					buttonListener.buttonPressed(i);
				else if(buttonsChange[i] == Buttons.RELEASED)
					buttonListener.buttonReleased(i);
			}
		}
		
		for(AxisListener axisListener : axisListeners) {
			for(int i = 0; i < axisChange.length; i++) {
				if(axisChange[i])
					axisListener.axisChanged(i);
			}
		}

		return true;
	}

	public boolean getState(int i) {
		return buttons.get(i).getPollData() >= 0.5;
	}

	public float getValue(int which) {
		return axis.get(which).getValue();
	}

	public float getDelta(int which) {
		return axis.get(which).getValue() - axisExState[which];
	}

	public Direction getArrowDir() {
		return arrows.getDirection();
	}

	public int[] getButtonsChange() {
		return buttonsChange;
	}

	public boolean[] getAxishange() {
		return axisChange;
	}

	public class Axis {
		public static final int LEFT_X = 0;
		public static final int LEFT_Y = 1;
		public static final int RIGHT_X = 2;
		public static final int RIGHT_Y = 3;
		public static final int BACK = 4;

		private Component me;
		private boolean flipped = false;
		private float deadZone = 0.2f;

		public Axis(Component me, boolean flipped) {
			this.me = me;
			this.flipped = flipped;
		}

		public Axis(Component me) {
			this(me, false);
		}

		public float getValue() {
			float value = me.getPollData();
			if (Math.abs(value) < deadZone)
				return 0;
			if (flipped)
				return -value;
			return value;
		}

		public float getDeadZone() {
			return deadZone;
		}

		public void setDeadZone(float deadZone) {
			this.deadZone = deadZone;
		}

		public static String getName(int id) {
			switch (id) {
			case LEFT_X:
				return "LEFT_X";
			case LEFT_Y:
				return "LEFT_Y";
			case RIGHT_X:
				return "RIGHT_X";
			case RIGHT_Y:
				return "RIGHT_Y";
			case BACK:
				return "BACK";
			}
			return "NONE";
		}
	}

	public class Arrows {

		public enum Direction {
			NONE, UP, DOWN, RIGHT, LEFT, UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT;
		}

		private Component me;

		Arrows(Component component) {
			me = component;
		}

		public Direction getDirection() {
			float value = me.getPollData();
			if (Float.compare(value, Component.POV.UP) == 0)
				return Direction.UP;
			else if (Float.compare(value, Component.POV.DOWN) == 0)
				return Direction.DOWN;
			else if (Float.compare(value, Component.POV.RIGHT) == 0)
				return Direction.RIGHT;
			else if (Float.compare(value, Component.POV.LEFT) == 0)
				return Direction.LEFT;
			else if (Float.compare(value, Component.POV.UP_RIGHT) == 0)
				return Direction.UP_RIGHT;
			else if (Float.compare(value, Component.POV.DOWN_RIGHT) == 0)
				return Direction.DOWN_RIGHT;
			else if (Float.compare(value, Component.POV.UP_LEFT) == 0)
				return Direction.UP_LEFT;
			else if (Float.compare(value, Component.POV.DOWN_LEFT) == 0)
				return Direction.DOWN_LEFT;
			return Direction.NONE;
		}

		public float getRawDir() {
			return me.getPollData();
		}
	}
	
	public interface ButtonListener {
		public void buttonPressed(int which);
		public void buttonReleased(int which);
	}
	
	public interface AxisListener {
		public void axisChanged(int which);
	}
	
	public void addListener(ButtonListener bl) {
		buttonListeners.add(bl);
	}
	
	public void addListener(AxisListener al) {
		axisListeners.add(al);
	}
	
	public void removeListener(ButtonListener bl) {
		buttonListeners.remove(bl);
	}
	
	public void removeListener(AxisListener al) {
		axisListeners.remove(al);
	}
}
