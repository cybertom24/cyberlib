package cyberLib.io;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Console Line Interface
 *
 * Provides methods to manage a CLI
 */
public class CLI {
    private String title;
    private ArrayList<String> history;
    private int historyIndex = 0;
    private boolean historyChanged = false;
    private boolean enterPressed = false;

    public CLI(String title) throws IOException {
        this.title = title;
        this.history = new ArrayList<>();

        // Disable JNativeHook's logger
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        // Disable the parent handlers.
        logger.setUseParentHandlers(false);

        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            throw new IOException("CLI failed to initialize cause: " + ex.getMessage());
        }

        NativeKeyListener listener = new NativeKeyListener() {
            @Override
            public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

            }

            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {

            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
                int keyCode = nativeKeyEvent.getKeyCode();

                switch(keyCode) {
                    case NativeKeyEvent.VC_UP -> {
                        historyIndex++;
                    }

                    case NativeKeyEvent.VC_DOWN -> {
                        historyIndex--;
                        if(historyIndex < 0)
                            historyIndex = 0;
                    }

                    default -> {
                        return;
                    }
                }

                historyChanged = true;
                System.out.println("History index: " + historyIndex);
            }
        };

        //GlobalScreen.addNativeKeyListener(listener);
    }

    public String input() {
        System.out.println(title + ": ");
        String command = null;

        enterPressed = false;
        while(!enterPressed) {
            command = Input.askString();
            enterPressed = true;
        }

        return command;
    }

    public void clearHistory() {
        history.clear();
    }
}
