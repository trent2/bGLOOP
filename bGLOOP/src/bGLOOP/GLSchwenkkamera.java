package bGLOOP;

import com.jogamp.opengl.math.VectorUtil;

import bGLOOP.windowimpl.MouseListener.MouseHandlerLogic;

public class GLSchwenkkamera extends GLKamera {
	/**
	 * Erstellt eine schwenkbare Kamera mit zusätzlichen
	 * Fenster-Parametern. Das Fenster kann dabei im Vollbildmodus und/oder
	 * mit/ohne Fenstermanager- Dekorationen angezeigt werden.
	 * 
	 * @param pVollbild
	 *            Wenn <code>true</code>, dann wird die Anzeige direkt in den
	 *            Vollbildmodus geschaltet. Wenn <code>false</code>, dann nicht
	 * @param pKeineDekoration
	 *            Wenn <code>true</code>, dann wird das Anzeigefenster ohne
	 *            Dekoration des Fenstermanagers gezeichnet, wenn
	 *            <code>false</code>, dann nicht.
	 */
	public GLSchwenkkamera(boolean pVollbild, boolean pKeineDekoration) {
		super(pVollbild, pKeineDekoration);
		addMouseListener();
	}

	/** Erstellt eine schwenkbare Kamera mit vorgegebener Breite und Höhe.
	 * Das Fenster wird nicht im Vollbild-Modus geöffnet.
	 * @param width Die Breite des Anzeigefensters
	 * @param height Die Höhe des Anzeigefensters
	 */
	public GLSchwenkkamera(int width, int height) {
		super(width, height);
		addMouseListener();
	}

	/**
	 * Erstellt eine schwenkbare Kamera. Die Kamera öffnet ein Fenster mit
	 * den Abmessungen aus der in der bGLOOP-Konfigurationsdatei eingetragenen
	 * Standardgrößen.
	 */
	public GLSchwenkkamera() {
		this(GLWindowConfig.defaultWindowConfig.globalDefaultWidth,
				GLWindowConfig.defaultWindowConfig.golbalDefaultHeight);
	}

	private void addMouseListener() {
		renderer.getWindow().addMouseListener(new MouseHandlerLogic() {
			float[] oldDir = new float[3];
			int xstart, ystart;
			double[] aPrevPos, aPrevUp;
			@Override
			public void handleMouseWheel(float wheelRotation) {
				
				float[] dir = new float[3];
				float[] pos = { (float) aPos[0], (float) aPos[1], (float) aPos[2] };
				float[] lookAt = { (float) aLookAt[0], (float) aLookAt[1], (float) aLookAt[2] };

				VectorUtil.subVec3(dir, pos, lookAt);
				VectorUtil.normalizeVec3(dir);
				if(VectorUtil.normSquareVec3(dir) == 0)
					System.arraycopy(oldDir, 0, dir, 0, 3);
				VectorUtil.scaleVec3(dir, dir, (float) (wheelRotation * getWconf().mouseWheelScale));
				VectorUtil.addVec3(pos, pos, dir);
				aPos[0] = pos[0];
				aPos[1] = pos[1];
				aPos[2] = pos[2];
				System.arraycopy(dir, 0, oldDir, 0, 3);
				getRenderer().scheduleRender();
				
			}
			
			@Override
			public void handleMousePressed(boolean button1, boolean button3, int x, int y) {
				if(button1 | button3) {
					xstart = x;
					ystart = y;
					aPrevPos = aPos.clone();
					aPrevUp = aUp.clone();
				}
			}
			
			@Override
			public void handleMouseDragged(boolean button1, boolean button3, int x, int y) {
				int angleX = ystart - y;
				int angleY = xstart - x;
				double s, c, t;
					s = -Math.sin(Math.toRadians(angleY));
					c = Math.cos(Math.toRadians(angleY));

				if (button1) {
					// rotate around y-axis
					synchronized (GLSchwenkkamera.this) {
						aPos[1] = aPrevPos[1];
						aPos[0] = aPrevPos[0] * c - aPrevPos[2] * s;
						aPos[2] = aPrevPos[2] * c + aPrevPos[0] * s;
						// cam.aUp[0] = aPrevUp[0] * c - aPrevUp[2] * s;
						// cam.aUp[2] = aPrevUp[2] * c + aPrevUp[0] * s;
						s = Math.sin(Math.toRadians(angleX));
						c = Math.cos(Math.toRadians(angleX));

						// rotate around x-axis
						t = aPos[1];
						aUp[0] = aPrevUp[0];
						aPos[1] = t * c - aPos[2] * s;
						aPos[2] = aPos[2] * c + t * s;
						aUp[1] = aPrevUp[1] * c - aPrevUp[2] * s;
						aUp[2] = aPrevUp[2] * c + aPrevUp[1] * s;
						getRenderer().scheduleRender();
					}
				} else if (button3) {
					// rotate around z-axis
					synchronized (GLSchwenkkamera.this) {
						aPos[0] = aPrevPos[0] * c + aPrevPos[1] * s;
						aPos[1] = aPrevPos[1] * c - aPrevPos[0] * s;
						aUp[0] = aPrevUp[0] * c + aPrevUp[1] * s;
						aUp[1] = aPrevUp[1] * c - aPrevUp[0] * s;
						getRenderer().scheduleRender();
					}
				}
			}

			@Override
			public void handleMouseSingleClick(boolean button1, boolean button3) { }

			@Override
			public void handleMouseDoubleClick(boolean button1, boolean button3) { }

			@Override
			public void handleMouseMoved(int x, int y) { }

			@Override
			public void handleMouseReleased(boolean button1, boolean button3) { }

		});
	}
}
