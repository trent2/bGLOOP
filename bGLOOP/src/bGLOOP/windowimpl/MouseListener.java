package bGLOOP.windowimpl;

public class MouseListener implements java.awt.event.MouseListener, java.awt.event.MouseWheelListener,
		java.awt.event.MouseMotionListener, com.jogamp.newt.event.MouseListener {
	boolean wasDoubleClick = false;

	public interface MouseHandlerLogic {
		public void handleMousePressed(boolean button1, boolean button3, int x, int y);
		public void handleMouseDragged(boolean button1, boolean button3, int x, int y);
		public void handleMouseWheel(float wheelRotation);
		public void handleMouseSingleClick(boolean button1, boolean button3);
		public void handleMouseDoubleClick(boolean button1, boolean button3);
		public void handleMouseMoved(int x, int y);
		public void handleMouseReleased(boolean button1, boolean button3);
	}
	private MouseHandlerLogic mhl;

	public MouseListener(MouseHandlerLogic mhl) {
		this.mhl = mhl;
	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {
		mhl.handleMousePressed(
				(e.getModifiersEx() & java.awt.event.MouseEvent.BUTTON1_DOWN_MASK) != 0,
				(e.getModifiersEx() & java.awt.event.MouseEvent.BUTTON3_DOWN_MASK) != 0,
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
	public void mouseMoved(java.awt.event.MouseEvent e) {
		mhl.handleMouseMoved(e.getX(), e.getY());
	}

	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) {
		int bdm1 = java.awt.event.MouseEvent.BUTTON1_DOWN_MASK,
			bdm3 = java.awt.event.MouseEvent.BUTTON3_DOWN_MASK;
		
		if(e.getClickCount() == 2)
			mhl.handleMouseDoubleClick((e.getModifiersEx() & bdm1) != 0, (e.getModifiersEx() & bdm3) != 0);
		else if(e.getClickCount() == 1)
			mhl.handleMouseSingleClick((e.getModifiersEx() & bdm1) != 0, (e.getModifiersEx() & bdm3) != 0);
	}

	@Override
	public void mouseReleased(java.awt.event.MouseEvent e) {
		int bdm1 = java.awt.event.MouseEvent.BUTTON1_DOWN_MASK,
			bdm3 = java.awt.event.MouseEvent.BUTTON3_DOWN_MASK;
		mhl.handleMouseReleased((e.getModifiersEx() & bdm1) != 0, (e.getModifiersEx() & bdm3) != 0);
	}

	@Override
	public void mouseEntered(java.awt.event.MouseEvent e) { }

	@Override
	public void mouseExited(java.awt.event.MouseEvent e) { }

	@Override
	public void mouseClicked(com.jogamp.newt.event.MouseEvent e) {
		int bm = com.jogamp.newt.event.MouseEvent.getButtonMask(e.getButton());
		if(e.getClickCount() == 2)
			mhl.handleMouseDoubleClick(
			   (bm & com.jogamp.newt.event.MouseEvent.BUTTON1_MASK) != 0,
			   (bm & com.jogamp.newt.event.MouseEvent.BUTTON3_MASK) != 0);
		else if(e.getClickCount() == 1)
			mhl.handleMouseSingleClick(
					   (bm & com.jogamp.newt.event.MouseEvent.BUTTON1_MASK) != 0,
					   (bm & com.jogamp.newt.event.MouseEvent.BUTTON3_MASK) != 0);

	}

	@Override
	public void mouseEntered(com.jogamp.newt.event.MouseEvent e) {
	}

	@Override
	public void mouseExited(com.jogamp.newt.event.MouseEvent e) {
	}

	@Override
	public void mousePressed(com.jogamp.newt.event.MouseEvent e) {
		int bm = com.jogamp.newt.event.MouseEvent.getButtonMask(e.getButton());
		mhl.handleMousePressed((bm & com.jogamp.newt.event.MouseEvent.BUTTON1_MASK) != 0,
							   (bm & com.jogamp.newt.event.MouseEvent.BUTTON3_MASK) != 0,
						       e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(com.jogamp.newt.event.MouseEvent e) {
		int bm = com.jogamp.newt.event.MouseEvent.getButtonMask(e.getButton());
		mhl.handleMouseReleased((bm & com.jogamp.newt.event.MouseEvent.BUTTON1_MASK) != 0,
							   (bm & com.jogamp.newt.event.MouseEvent.BUTTON3_MASK) != 0);
	}

	@Override
	public void mouseMoved(com.jogamp.newt.event.MouseEvent e) {
		mhl.handleMouseMoved(e.getX(), e.getY());
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
