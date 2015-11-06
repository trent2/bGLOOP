package bGLOOP;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

abstract class GLDisplayItem {
	GLRenderer associatedRenderer;

	GLKamera associatedCam;

	boolean aVisible = true;
	boolean hasChanged = true;

	abstract void render(GL2 gl, GLU glu);
}
