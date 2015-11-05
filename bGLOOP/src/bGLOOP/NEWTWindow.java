package bGLOOP;

import com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.util.Animator;

import bGLOOP.windowimpl.KeyboardListener;
import bGLOOP.windowimpl.MouseListener;

final class NEWTWindow extends bGLOOP.Window {
	private GLWindow glWin;

	public static Window createWindowFactory() {
		return new NEWTWindow();
	}

	@Override
	Object createWindow(GLCapabilities caps, int width, int height) {
		final Animator animator;
		setAutoDrawable(glWin = GLWindow.create(caps));
		setAnimator(animator = new Animator(glWin));
		glWin.addWindowListener(new WindowAdapter() {
			@Override
			public void windowDestroyNotify(WindowEvent e) {
				animator.stop();
				glWin.setVisible(false);
				System.exit(0);
			}
		});

		glWin.setDefaultCloseOperation(WindowClosingMode.DISPOSE_ON_CLOSE);
		glWin.setSize(width, height);
		glWin.setTitle("bGLOOP");
		return glWin;
	}

	@Override
	void addCameraMouseListener(MouseListener.MouseHandlerLogic mhl) {
		glWin.addMouseListener(new MouseListener(mhl));
	}

	@Override
	void addKeyboardListener(KeyboardListener.KeyPressedLogic kpl) {
		glWin.addKeyListener(new KeyboardListener(kpl));
	}

	@Override
	void setDecoration(boolean pDecorate) {
		glWin.setUndecorated(pDecorate);
	}

	@Override
	void setFullscreen(boolean pFullscreen) {
		glWin.setFullscreen(pFullscreen);
	}

	@Override
	void startDisplay() {
		glWin.setVisible(true);
	}
}
