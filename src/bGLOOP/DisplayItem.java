package bGLOOP;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

abstract class DisplayItem {
	GLRenderer associatedRenderer;

	GLKamera associatedCam;

	boolean aVisible = false;

	abstract void render(GL2 gl, GLU glu);

	abstract boolean isTransparent();
}
