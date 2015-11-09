package bGLOOP.windowimpl.listener;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

/** Keyboard listener that handles NEWT mouse events.
 * Its logic is forwarded by implementing KeyboardListenerFacade.
 * A NEWT {@link com.jogamp.newt.event.KeyAdapter} can be adapted to an AWT {@link java.awt.event.KeyListener}
 * via the {@link com.jogamp.newt.event.awt.AWTKeyAdapter} class.
 * @see {@link bGLOOP.windowimpl.AWTWindow#addKeyboardListener(KeyboardListenerFacade)} for details
 * how this is done.
 */
public class KeyboardListener implements KeyListener {

	private KeyboardListenerFacade kpl;

	public KeyboardListener(KeyboardListenerFacade kpl) {
		this.kpl = kpl;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		kpl.handleKeyPressed(e.getKeyChar(), e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		kpl.handleKeyReleased(e.getKeyChar(), e.getKeyCode());
	}
}
