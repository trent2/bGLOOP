package bGLOOP.windowimpl;

// includes methods for AWT and NEWT windows impementation
public class KeyboardListener implements java.awt.event.KeyListener, com.jogamp.newt.event.KeyListener {

	public interface KeyPressedLogic {
		public void handleKeyPressed(char key, int keycode);
		public void handleKeyReleased(char key, int keycode);
	}

	private KeyPressedLogic kpl;

	public KeyboardListener(KeyPressedLogic kpl) {
		this.kpl = kpl;
	}

	@Override
	public void keyPressed(com.jogamp.newt.event.KeyEvent e) {
		kpl.handleKeyPressed(e.getKeyChar(), e.getKeyCode());
	}

	@Override
	public void keyReleased(com.jogamp.newt.event.KeyEvent e) {
	}

	@Override
	public void keyTyped(java.awt.event.KeyEvent e) {
	}

	@Override
	public void keyPressed(java.awt.event.KeyEvent e) {
		kpl.handleKeyPressed(e.getKeyChar(), e.getKeyCode());
	}

	@Override
	public void keyReleased(java.awt.event.KeyEvent e) {
	}
}
