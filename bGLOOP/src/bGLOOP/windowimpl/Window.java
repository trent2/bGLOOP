package bGLOOP.windowimpl;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.util.Animator;

import bGLOOP.windowimpl.listener.KeyboardListenerFacade;
import bGLOOP.windowimpl.listener.MouseListenerFacade;

public abstract class Window {
	private GLAutoDrawable adraw;
	private Animator animator;

	public static Window createWindowFactory(boolean asAWT) {
		if(asAWT)
			return new AWTWindow();
		else
			return new NEWTWindow();
	}

	public abstract Object createWindow(GLCapabilities caps, int width, int height);

	public abstract void addMouseListener(MouseListenerFacade mhl);

	public abstract void addKeyboardListener(KeyboardListenerFacade kpl);

	public abstract void setDecoration(boolean pDecorate);

	public abstract void setFullscreen(boolean pFullscreen);

	public abstract void toggleFullscreen();

	public abstract void startDisplay();

	public abstract void updateFPS(float lastFPS);

	public Animator getAnimator() {
		return animator;
	}

	public void setAnimator(Animator animator) {
		this.animator = animator;
	}

	public GLAutoDrawable getAutoDrawable() {
		return adraw;
	}

	public void setAutoDrawable(GLAutoDrawable adraw) {
		this.adraw = adraw;
	}
}
