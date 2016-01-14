package bGLOOP;

import static java.lang.Math.toRadians;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.Quaternion;
import com.jogamp.opengl.math.VectorUtil;

import bGLOOP.windowimpl.listener.KeyboardListenerFacade;
import bGLOOP.windowimpl.listener.MouseListenerFacade;

/** Die Schwenkkamera ist gegenüber der {@link GLKamera} durch Maus-Dragging
 * um ihren Blickpunkt rotierbar. Außerdem ermöglicht sie Zoomen per Mausrad.

/** Neben der Maussteuerung kann die dargestellte Szene durch folgende Tasten
 * verändert werden:
 * <ul>
 * <li><code>d</code>: Setzt die Kamera auf <code>(0,0,500)</code> mit Blickpunkt
 * auf <code>(0,0,0)</code></li>
 * <li><code>&uarr;</code>: Rückt die Kamera ein Stück nach oben</li>
 * <li><code>&darr;</code>: Rückt die Kamera ein Stück nach unten</li>
 * <li><code>&larr;</code>: Rückt die Kamera ein Stück nach links</li>
 * <li><code>&rarr;</code>: Rückt die Kamera ein Stück nach rechts</li>
 * <li><code>w</code>: Rückt die Kamera ein Stück nach vorn in Blickrichtung</li>
 * <li><code>s</code>: Rückt die Kamera ein Stück nach hinten in Blickrichtung</li>
 * </ul>
 * 
 * @author R. Spillner
 */
public class GLSchwenkkamera extends GLKamera {
	public GLSchwenkkamera(boolean pVollbild) {
		this(pVollbild, pVollbild);
	}

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
		super(pVollbild, false);
		addMouseListener();
		addKeyboardListener();
	}

	

	/** Erstellt eine schwenkbare Kamera mit vorgegebener Breite und Höhe.
	 * Das Fenster wird nicht im Vollbild-Modus geöffnet.
	 * @param width Die Breite des Anzeigefensters
	 * @param height Die Höhe des Anzeigefensters
	 */
	public GLSchwenkkamera(int width, int height) {
		super(width, height);
		addMouseListener();
		addKeyboardListener();
	}

	/**
	 * Erstellt eine schwenkbare Kamera. Die Kamera öffnet ein Fenster mit
	 * den Abmessungen aus der in der bGLOOP-Konfigurationsdatei eingetragenen
	 * Standardgrößen.
	 */
	public GLSchwenkkamera() {
		this(WindowConfig.defaultWindowConfig.globalDefaultWidth,
				WindowConfig.defaultWindowConfig.globalDefaultHeight);
	}

	private void addMouseListener() {
		associatedRenderer.getWindow().addMouseListener(new MouseListenerFacade() {
			private final Quaternion p = new Quaternion(),  trl = new Quaternion();
			private float[] rotAxisUp = new float[3], tempVec = new float[3], rotAxisRight = new float[3];

			float[] oldDir = new float[3];
			int xstart, ystart;
			float[] aPrevPos, aPrevUp;

			@Override
			public void handleMouseWheel(float wheelRotation) {
				synchronized (GLSchwenkkamera.this) {
					float[] dir = new float[3];
					float[] pos = aPos.clone();
					float[] lookAt = aLookAt.clone();

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
					associatedRenderer.scheduleRender();
				}
			}
			
			@Override
			public void handleMousePressed(boolean button1, boolean button3, int x, int y) {
				if(button1 || button3) {
					xstart = x;
					ystart = y;
					aPrevPos = aPos.clone();
					aPrevUp = aUp.clone();
					/*
					getWconf().aDrawRotAxis = button1;
					associatedRenderer.scheduleRender();
					*/
				}
			}
			
			@Override
			public void handleMouseDragged(boolean button1, boolean button3, int x, int y) {
				if (button1 || button3) {
					trl.set(aLookAt[0], aLookAt[1], aLookAt[2], 0);
					synchronized (GLSchwenkkamera.this) {
						if (button1) {
							// rotate camera position around up vector through aLookAt
							rotAxisUp[0] = aPrevUp[0];
							rotAxisUp[1] = aPrevUp[1];
							rotAxisUp[2] = aPrevUp[2];
							VectorUtil.normalizeVec3(rotAxisUp);

							p.set(aPrevPos[0], aPrevPos[1],aPrevPos[2], 0).subtract(trl)
							.rotateByAngleNormalAxis((float) toRadians(x - xstart),
									rotAxisUp[0], rotAxisUp[1], rotAxisUp[2]).add(trl);

							// rotate camera position around right vector through aLookAt
							tempVec[0] = aLookAt[0] - p.getX();
							tempVec[1] = aLookAt[1] - p.getY();
							tempVec[2] = aLookAt[2] - p.getZ();
							VectorUtil.crossVec3(rotAxisRight, rotAxisUp, tempVec);
							VectorUtil.normalizeVec3(rotAxisRight);

							p.subtract(trl).rotateByAngleNormalAxis((float) toRadians(ystart - y),
									rotAxisRight[0], rotAxisRight[1], rotAxisRight[2]).add(trl);
							aPos[0] = p.getX();
							aPos[1] = p.getY();
							aPos[2] = p.getZ();							

							//--- rotate up vector, rotation is only necessary around the right axis --------------
							p.set(aPrevUp[0], aPrevUp[1], aPrevUp[2], 0)
									.rotateByAngleNormalAxis((float) toRadians(ystart - y),
											rotAxisRight[0], rotAxisRight[1], rotAxisRight[2]);
							aUp[0] = p.getX();
							aUp[1] = p.getY();
							aUp[2] = p.getZ();

							associatedRenderer.scheduleRender();
						} else if (button3) {
							// rotate around lookAt to camera axis
							rotAxisUp[0] = aLookAt[0] - aPrevPos[0];
							rotAxisUp[1] = aLookAt[1] - aPrevPos[1];
							rotAxisUp[2] = aLookAt[2] - aPrevPos[2];
							VectorUtil.normalizeVec3(rotAxisUp);
							p.set(aPrevUp[0], aPrevUp[1],
									aPrevUp[2], 0);
							p.rotateByAngleNormalAxis((float) toRadians(xstart - x), rotAxisUp[0], rotAxisUp[1], rotAxisUp[2]);
							aUp[0] = p.getX();
							aUp[1] = p.getY();
							aUp[2] = p.getZ();

							associatedRenderer.scheduleRender();
						}
					}
					log.fine("(distance^2 LookAt To Camera, angle, aUp): " + String.format("(%g, %d, [%g,%g,%g])",
							(aPos[0] - aLookAt[0]) * (aPos[0] - aLookAt[0])
									+ (aPos[1] - aLookAt[1]) * (aPos[1] - aLookAt[1])
									+ (aPos[2] - aLookAt[2]) * (aPos[2] - aLookAt[2]),
							x - xstart, aUp[0], aUp[1], aUp[2]));

					if(checkCameraVectors() >= 2*FloatUtil.EPSILON)
						log.warning("Camera orthonormality check failed");
				}
			}

			@Override
			public void handleMouseSingleClick(boolean button1, boolean button3) { }

			@Override
			public void handleMouseDoubleClick(boolean button1, boolean button3) { }

			@Override
			public void handleMouseMoved(int x, int y) { }

			@Override
			public void handleMouseReleased(boolean button1, boolean button3) {
				/*
				getWconf().aDrawRotAxis = false;
				associatedRenderer.scheduleRender();
				*/
			}
		});
	}

	private void addKeyboardListener() {
		associatedRenderer.getWindow().addKeyboardListener(new KeyboardListenerFacade() {
			private float[] aLeft = new float[3];

			@Override
			public void handleKeyPressed(char key, int keycode, int modifiers) {
				double moveScale = getWconf().keyMoveScale;
				switch (key) {
				case 'd':
					synchronized (GLSchwenkkamera.this) {
						aPos[0] = 0; aPos[1] = 0; aPos[2] = 500;
						aLookAt[0] = 0; aLookAt[1] = 0; aLookAt[2] = 0;
						aUp[0] = 0; aUp[1] = 1; aUp[2] = 0;
						associatedRenderer.scheduleRender();
					}
					break;
				case 'w':
					vor(moveScale);
					break;
				case 's':
					vor(-moveScale);
					break;
				}
				switch (keycode) {
				case KeyEvent.VK_UP:
					synchronized (GLSchwenkkamera.this) {
						aPos[0] += aUp[0]*moveScale; aPos[1] += aUp[1]*moveScale; aPos[2] += aUp[2]*moveScale;
						aLookAt[0] += aUp[0]*moveScale; aLookAt[1] += aUp[1]*moveScale; aLookAt[2] += aUp[2]*moveScale;
						associatedRenderer.scheduleRender();
					}
					break;
				case KeyEvent.VK_DOWN:
					synchronized (GLSchwenkkamera.this) {
						aPos[0] -= aUp[0]*moveScale; aPos[1] -= aUp[1]*moveScale; aPos[2] -= aUp[2]*moveScale;
						aLookAt[0] -= aUp[0]*moveScale; aLookAt[1] -= aUp[1]*moveScale; aLookAt[2] -= aUp[2]*moveScale;
						associatedRenderer.scheduleRender();
					}
					break;
				case KeyEvent.VK_RIGHT:
					synchronized (GLSchwenkkamera.this) {
						computeVectorLeft();
						aPos[0] -= aLeft[0]*moveScale; aPos[1] -= aLeft[1]*moveScale; aPos[2] -= aLeft[2]*moveScale;
						aLookAt[0] -= aLeft[0]*moveScale; aLookAt[1] -= aLeft[1]*moveScale; aLookAt[2] -= aLeft[2]*moveScale;
						associatedRenderer.scheduleRender();
					}
					break;
				case KeyEvent.VK_LEFT:
					synchronized (GLSchwenkkamera.this) {
						computeVectorLeft();
						aPos[0] += aLeft[0]*moveScale; aPos[1] += aLeft[1]*moveScale; aPos[2] += aLeft[2]*moveScale;
						aLookAt[0] += aLeft[0]*moveScale; aLookAt[1] += aLeft[1]*moveScale; aLookAt[2] += aLeft[2]*moveScale;
						associatedRenderer.scheduleRender();
					}
					break;
				}
			}

			private void computeVectorLeft() {
				float[] dir = new float[3];

				VectorUtil.subVec3(dir, aLookAt, aPos);
				VectorUtil.crossVec3(aLeft, aUp, dir);
				VectorUtil.normalizeVec3(aLeft);
			}

			@Override
			public void handleKeyReleased(char key, int keycode) {
			}
		});
	}
}
