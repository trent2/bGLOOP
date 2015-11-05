package bGLOOP.windowimpl;

public class MouseListener implements java.awt.event.MouseListener, java.awt.event.MouseWheelListener,
		java.awt.event.MouseMotionListener, com.jogamp.newt.event.MouseListener {

	public interface MouseHandlerLogic {
		public void handleMousePressed(boolean button1Or3, int x, int y);
		public void handleMouseDragged(boolean button1, boolean button3, int x, int y);
		public void handleMouseWheel(float wheelRotation);
	}
	private MouseHandlerLogic mhl;

	public MouseListener(MouseHandlerLogic mhl) {
		this.mhl = mhl;
	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {
		mhl.handleMousePressed((e.getModifiersEx() &
				(java.awt.event.MouseEvent.BUTTON1_DOWN_MASK |
				 java.awt.event.MouseEvent.BUTTON3_DOWN_MASK)) != 0,
				 e.getX(), e.getY());
	}

	// rotation of the camera
	@Override
	public void mouseDragged(java.awt.event.MouseEvent e) {
		int bdm1 = java.awt.event.MouseEvent.BUTTON1_DOWN_MASK,
			bdm3 = java.awt.event.MouseEvent.BUTTON3_DOWN_MASK;
		mhl.handleMouseDragged((e.getModifiersEx() & bdm1) != 0,
				(e.getModifiersEx() & bdm3) != 0,
				e.getX(), e.getY());
	}

	@Override
	public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
		mhl.handleMouseWheel(e.getWheelRotation());
	}

	@Override
	public void mouseMoved(java.awt.event.MouseEvent e) { }

	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) { }

	@Override
	public void mouseReleased(java.awt.event.MouseEvent e) { }

	@Override
	public void mouseEntered(java.awt.event.MouseEvent e) { }

	@Override
	public void mouseExited(java.awt.event.MouseEvent e) { }

	@Override
	public void mouseClicked(com.jogamp.newt.event.MouseEvent e) {
	}

	@Override
	public void mouseEntered(com.jogamp.newt.event.MouseEvent e) {
	}

	@Override
	public void mouseExited(com.jogamp.newt.event.MouseEvent e) {
	}

	@Override
	public void mousePressed(com.jogamp.newt.event.MouseEvent e) {
		mhl.handleMousePressed(
				(com.jogamp.newt.event.MouseEvent.getButtonMask(e.getButton())
				& (com.jogamp.newt.event.MouseEvent.BUTTON1_MASK
						| com.jogamp.newt.event.MouseEvent.BUTTON3_MASK)) != 0,
						e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(com.jogamp.newt.event.MouseEvent e) {
	}

	@Override
	public void mouseMoved(com.jogamp.newt.event.MouseEvent e) {
	}

	@Override
	public void mouseDragged(com.jogamp.newt.event.MouseEvent e) {
		mhl.handleMouseDragged(e.isButtonDown(com.jogamp.newt.event.MouseEvent.BUTTON1),
				e.isButtonDown(com.jogamp.newt.event.MouseEvent.BUTTON3),
				e.getX(), e.getY());
	}

	@Override
	public void mouseWheelMoved(com.jogamp.newt.event.MouseEvent e) {
		mhl.handleMouseWheel(e.getRotation()[1]);
	}
}
