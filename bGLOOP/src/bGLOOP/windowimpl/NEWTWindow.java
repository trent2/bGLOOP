package bGLOOP.windowimpl;

import com.jogamp.common.util.RunnableTask;
import com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.util.Animator;

import bGLOOP.windowimpl.listener.KeyboardListener;
import bGLOOP.windowimpl.listener.KeyboardListenerFacade;
import bGLOOP.windowimpl.listener.MouseListener;
import bGLOOP.windowimpl.listener.MouseListenerFacade;

final public class NEWTWindow extends Window {
	private GLWindow glWin;

	public static Window createWindowFactory() {
		return new NEWTWindow();
	}

	@Override
	public Object createWindow(GLCapabilities caps, int width, int height) {
		final Animator animator;
		setAutoDrawable(glWin = GLWindow.create(caps));
		setAnimator(animator = new Animator(glWin));
		glWin.addWindowListener(new WindowAdapter() {
			@Override
			public void windowDestroyNotify(WindowEvent e) {
				animator.stop();
				glWin.setVisible(false);
				// prevent jogl babbling about unimportant stuff
				System.err.close();
				System.exit(0);
			}
		});

		glWin.setDefaultCloseOperation(WindowClosingMode.DISPOSE_ON_CLOSE);
		glWin.setSize(width, height);
		glWin.setTitle("bGLOOP");
		return glWin;
	}

	@Override
	public void addMouseListener(MouseListenerFacade mhl) {
		glWin.addMouseListener(new MouseListener(mhl));
	}

	@Override
	public void addKeyboardListener(KeyboardListenerFacade kpl) {
		glWin.addKeyListener(new KeyboardListener(kpl));
	}

	@Override
	public void setDecoration(boolean pDecorate) {
		glWin.setUndecorated(pDecorate);
	}

	@Override
	public void setFullscreen(boolean pFullscreen) {
		glWin.setFullscreen(pFullscreen);
	}

	@Override
	public void toggleFullscreen() {
		RunnableTask.invokeOnNewThread(null, "fullscreen_toogle", false, new Runnable() {
			@Override
			public void run() {
				glWin.setFullscreen((glWin.getStateMask() & GLWindow.STATE_MASK_FULLSCREEN) == 0);
			}
		});
	}

	@Override
	public void startDisplay() {
		glWin.setVisible(true);
	}
}
