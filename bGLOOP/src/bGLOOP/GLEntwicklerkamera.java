package bGLOOP;

import com.jogamp.opengl.math.VectorUtil;

import bGLOOP.windowimpl.KeyboardListener.KeyPressedLogic;
import bGLOOP.windowimpl.MouseListener.MouseHandlerLogic;

/**
 * Die Entwicklerkamera hat zusätzliche Fähigkeiten gegenüber der
 * {@link GLKamera}. Sie ist durch Maus-Dragging um ihren Blickpunkt rotierbar.
 * Außerdem kann die dargestellte Szene durch folgende Tasten verändert werden:
 * <ul>
 * <li><code>g</code>: Wechselt zwischen individueller und
 * Drahtgitterdarstellung.</li>
 * <li><code>a</code>: Blendet die Koordinatenachsen ein und aus</li>
 * </ul>
 * 
 * @author R. Spillner
 */
public class GLEntwicklerkamera extends GLKamera {
	/**
	 * Erstellt eine bGLOOP-Entwicklerkamera mit zusätzlichen
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
	public GLEntwicklerkamera(boolean pVollbild, boolean pKeineDekoration) {
		super(pVollbild, pKeineDekoration);
		addMouseListener();
		addKeyboardListener();
	}

	/**
	 * Erstellt eine bGLOOP-Entwicklerkamera. Die Kamera öffnet ein Fenster mit
	 * den Abmessungen aus der in der bGLOOP-Konfigurationsdatei eingetragenen
	 * Standardgrößen.
	 */
	public GLEntwicklerkamera() {
		this(GLWindowConfig.defaultWindowConfig.globalDefaultWidth,
				GLWindowConfig.defaultWindowConfig.golbalDefaultHeight);
	}

	/** Erstellt eine bGLOOP-Entwicklerkamera mit vorgegebener Breite und Höhe.
	 * Das Fenster wird nicht im Vollbild-Modus geöffnet.
	 * @param width Die Breite des Anzeigefensters
	 * @param height Die Höhe des Anzeigefensters
	 */
	public GLEntwicklerkamera(int width, int height) {
		super(width, height);
		addMouseListener();
		addKeyboardListener();
	}

	private void addMouseListener() {
		renderer.getWindow().addMouseListener(new MouseHandlerLogic() {
			int xstart, ystart;
			double[] aPrevPos, aPrevUp;
			@Override
			public void handleMouseWheel(float wheelRotation) {
				
				float[] dir = new float[3];
				float[] pos = { (float) GLEntwicklerkamera.this.aPos[0], (float) GLEntwicklerkamera.this.aPos[1], (float) GLEntwicklerkamera.this.aPos[2] };
				float[] lookAt = { (float) GLEntwicklerkamera.this.aLookAt[0], (float) GLEntwicklerkamera.this.aLookAt[1], (float) GLEntwicklerkamera.this.aLookAt[2] };

				VectorUtil.subVec3(dir, pos, lookAt);
				VectorUtil.normalizeVec3(dir);
				VectorUtil.scaleVec3(dir, dir, (float) (wheelRotation * getWconf().mouseWheelScale));
				VectorUtil.addVec3(pos, pos, dir);
				VectorUtil.addVec3(lookAt, lookAt, dir);
				GLEntwicklerkamera.this.aPos[0] = pos[0];
				GLEntwicklerkamera.this.aPos[1] = pos[1];
				GLEntwicklerkamera.this.aPos[2] = pos[2];
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
					synchronized (GLEntwicklerkamera.this) {
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
					synchronized (GLEntwicklerkamera.this) {
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

	private void addKeyboardListener() {
		renderer.getWindow().addKeyboardListener(new KeyPressedLogic() {
			@Override
			public void handleKeyPressed(char key, int keycode) {
				switch (key) {
				case 'a':
					zeigeAchsen(!getWconf().aDisplayAxes);
					break; // "zeigeAchsen" does a scheduleRender()
				case 'g':
					getWconf().aWireframe = !getWconf().aWireframe;
					getRenderer().scheduleRender();
					break;
				case 's':
					aPos[0] = 0; aPos[1] = 0; aPos[2] = 500;
					aLookAt[0] = 0; aLookAt[1] = 0; aLookAt[2] = 0;
					aUp[0] = 0; aUp[1] = 1; aUp[2] = 0;
					getRenderer().scheduleRender();
					break;
				}
			}

			@Override
			public void handleKeyReleased(char key, int keycode) {
			}
		});

	}
}
