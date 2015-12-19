package bGLOOP.windowimpl.listener;

import com.jogamp.newt.event.MouseAdapter;
import com.jogamp.newt.event.MouseEvent;

/** Mouse listener that handles NEWT mouse events.
 * Its logic is forwarded by implementing MouseListenerFacade.
 * A NEWT {@link com.jogamp.newt.event.MouseAdapter} can be adapted to an AWT {@link java.awt.event.MouseListener}
 * via the {@link com.jogamp.newt.event.awt.AWTMouseAdapter} class.
 * @see {@link bGLOOP.windowimpl.AWTWindow#addMouseListener(MouseListenerFacade)} for details
 * how this is done.
 */
public class MouseListener extends MouseAdapter {
	private MouseListenerFacade mhf;

	public MouseListener(MouseListenerFacade mhf) {
		this.mhf = mhf;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int bm = MouseEvent.getButtonMask(e.getButton());
		if(e.getClickCount() == 2)
			mhf.handleMouseDoubleClick(
			   (bm & MouseEvent.BUTTON1_MASK) != 0,
			   (bm & MouseEvent.BUTTON3_MASK) != 0);
		else if(e.getClickCount() == 1)
			mhf.handleMouseSingleClick(
					   (bm & MouseEvent.BUTTON1_MASK) != 0,
					   (bm & MouseEvent.BUTTON3_MASK) != 0);

	}

	@Override
	public void mousePressed(MouseEvent e) {
		int bm = MouseEvent.getButtonMask(e.getButton());
		mhf.handleMousePressed((bm & MouseEvent.BUTTON1_MASK) != 0,
							   (bm & MouseEvent.BUTTON3_MASK) != 0,
						       e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int bm = MouseEvent.getButtonMask(e.getButton());
		mhf.handleMouseReleased((bm & MouseEvent.BUTTON1_MASK) != 0,
							   (bm & MouseEvent.BUTTON3_MASK) != 0);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mhf.handleMouseMoved(e.getX(), e.getY());
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mhf.handleMouseDragged(e.isButtonDown(MouseEvent.BUTTON1),
				e.isButtonDown(MouseEvent.BUTTON3),
				e.getX(), e.getY());
	}

	@Override
	public void mouseWheelMoved(MouseEvent e) {
		mhf.handleMouseWheel(e.getRotation()[1]);
	}
}
