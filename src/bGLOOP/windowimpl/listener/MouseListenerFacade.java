package bGLOOP.windowimpl.listener;

public interface MouseListenerFacade {
	public void handleMousePressed(boolean button1, boolean button3, int x, int y);
	public void handleMouseDragged(boolean button1, boolean button3, int x, int y);
	public void handleMouseWheel(float wheelRotation);
	public void handleMouseSingleClick(boolean button1, boolean button3);
	public void handleMouseDoubleClick(boolean button1, boolean button3);
	public void handleMouseMoved(int x, int y);
	public void handleMouseReleased(boolean button1, boolean button3);
}