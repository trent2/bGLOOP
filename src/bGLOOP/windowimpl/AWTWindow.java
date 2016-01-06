package bGLOOP.windowimpl;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.jogamp.newt.event.awt.AWTKeyAdapter;
import com.jogamp.newt.event.awt.AWTMouseAdapter;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import bGLOOP.windowimpl.listener.KeyboardListener;
import bGLOOP.windowimpl.listener.KeyboardListenerFacade;
import bGLOOP.windowimpl.listener.MouseListener;
import bGLOOP.windowimpl.listener.MouseListenerFacade;

final public class AWTWindow extends Window {
	private GLCanvas canvas;
	private Frame frame;

	@Override
	public Object createWindow(GLCapabilities caps, int width, int height) {
		final Animator animator;

		frame = new Frame("bGLOOP");
		setAutoDrawable(canvas = new GLCanvas(caps));

		setAnimator(animator = new Animator(canvas));
		frame.setSize(width, height);
		frame.add(canvas);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				shuttingDown();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				shuttingDown();
			}

			private void shuttingDown() {
				animator.stop();

				// prevent jogl babbling about unimportant stuff
				System.err.close();
				System.exit(0);				
			}
		});
		return frame;
	}

	@Override
	public void addMouseListener(MouseListenerFacade mhl) {
		new AWTMouseAdapter(new MouseListener(mhl), canvas).addTo(canvas);
	}

	@Override
	public void addKeyboardListener(KeyboardListenerFacade kpl) {
		new AWTKeyAdapter(new KeyboardListener(kpl), canvas).
			addTo(canvas).addTo(frame);
	}

	@Override
	public void setDecoration(boolean pDecorate) {
		frame.setUndecorated(pDecorate);
	}

	@Override
	public void setFullscreen(boolean pFullscreen) {
		if (pFullscreen)
			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		else
			frame.setExtendedState(frame.getExtendedState() & (~Frame.MAXIMIZED_BOTH));
	}

	@Override
	public void toggleFullscreen() {
		frame.setExtendedState(frame.getExtendedState() ^ Frame.MAXIMIZED_BOTH);
	}

	@Override
	public void startDisplay() {
		frame.setVisible(true);
	}

	@Override
	public void closeDisplay() {
		frame.dispose();
	}

	@Override
	public void updateFPS(float lastFPS) {
		frame.setTitle("bGLOOP.AWT, FPS: " + lastFPS);
	}
}
