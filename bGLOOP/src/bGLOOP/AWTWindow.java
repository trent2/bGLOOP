package bGLOOP;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import bGLOOP.windowimpl.KeyboardListener;
import bGLOOP.windowimpl.MouseListener;

final class AWTWindow extends Window {
	private GLCanvas canvas;
	private Frame frame;

	public static Window createWindowFactory() {
		return new AWTWindow();
	}

	@Override
	Object createWindow(GLCapabilities caps, int width, int height) {
		final Animator animator;

		frame = new Frame("bGLOOP");
		setAutoDrawable(canvas = new GLCanvas(caps));

		setAnimator(animator = new Animator(canvas));
		frame.setSize(width, height);
		frame.add(canvas);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				animator.stop();

				// prevent jogl babbling about unimportant stuff
				System.err.close();
				System.exit(0);
			}
		});
		return frame;
	}

	@Override
	void addMouseListener(MouseListener.MouseHandlerLogic mhl) {
		MouseListener ml = new MouseListener(mhl);
		canvas.addMouseListener(ml);
		canvas.addMouseMotionListener(ml);
		canvas.addMouseWheelListener(ml);
	}

	@Override
	void addKeyboardListener(KeyboardListener.KeyPressedLogic kpl) {
		KeyboardListener dckl = new KeyboardListener(kpl);
		canvas.addKeyListener(dckl);
		frame.addKeyListener(dckl);
	}

	@Override
	void setDecoration(boolean pDecorate) {
		frame.setUndecorated(pDecorate);
	}

	@Override
	void setFullscreen(boolean pFullscreen) {
		if (pFullscreen)
			frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		else
			frame.setExtendedState(Frame.NORMAL);
	}

	@Override
	void startDisplay() {
		frame.setVisible(true);
	}
}
