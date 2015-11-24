package bGLOOP.windowimpl.listener;

public interface KeyboardListenerFacade {
	public void handleKeyPressed(char key, int keycode, int modifiers);
	public void handleKeyReleased(char key, int keycode);
}
