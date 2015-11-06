package bGLOOP;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.util.Animator;

import bGLOOP.windowimpl.KeyboardListener;
import bGLOOP.windowimpl.MouseListener;

abstract class Window {
	private GLAutoDrawable adraw;
	private Animator animator;

	abstract Object createWindow(GLCapabilities caps, int width, int height);

	abstract void addMouseListener(MouseListener.MouseHandlerLogic mhl);

	abstract void addKeyboardListener(KeyboardListener.KeyPressedLogic kpl);

	abstract void setDecoration(boolean pDecorate);

	abstract void setFullscreen(boolean pFullscreen);

	abstract void startDisplay();

	Animator getAnimator() {
		return animator;
	}

	void setAnimator(Animator animator) {
		this.animator = animator;
	}

	GLAutoDrawable getAutoDrawable() {
		return adraw;
	}

	void setAutoDrawable(GLAutoDrawable adraw) {
		this.adraw = adraw;
	}
}
