package bGLOOP;

import java.awt.Toolkit;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import bGLOOP.windowimpl.listener.MouseListenerFacade;

/** Klasse zur direkten Abfrage der Maus.
 * <p>Mit dieser Klasse kann die Maus in Echtzeit abgefragt werden. Es wird dann
 * zurückgegeben, ob innerhalb der letzten <code>MOUSE_CLICK_TIME_RANGE</code>
 * Millisekunden eine Maustaste geklickt wurde, oder ob innerhalb der letzten
 * <code>MOUSE_MOVE_TIME_RANGE</code> Millesekunden die Maus bewegt wurde.
 * </p>
 * <p>Die genannten Größen können in der Konfigurationsdatei von bGLOOP gesetzt
 * werden; standardmäßig gilt <code>MOUSE_CLICK_TIME_RANGE=1000</code> und
 * <code>MOUSE_MOVE_TIME_RANGE=100</code>. Außerdem kann abgefragt werden, ob
 * eine Maustaste im Augenblick der Anfrage gedrückt ist.
 * </p>
 * <p>Es wird zwischen Klicks und gedrückter Maustaste unterschieden: Eine Maustaste
 * gilt solange als <em>gedrückt</em>, wie sie heruntergedrückt ist. Ein Klick liegt
 * erst dann vor, wenn die Taste wieder losgelassen wird.
 * </p>
 * <p>Daneben wird zwischen einem <em>echten</em> und <em>unechten</em> Linksklick
 * unterschieden: eine <em>echter</em> Linksklick ist nicht Teil eines Doppelklicks.
 * Er kann erst nach Ablauf eines kurzen Augenblicks nach Mausklick positiv geprüft
 * werden, da abgewartet werden muss, ob einer erneuter Linksklick durchgeführt
 * wird. Ein unechter Mausklick hingegen steht einer Abfrage mittels der Methode
 * {@link #linksklick()} direkt zur Verfügung, kann aber auch Teil eines Doppelklicks
 * sein.
 * </p>
 * <p>Unabhängig davon kann jederzeit sofort geprüft werden, ob eine Maustaste momentan
 * heruntergedrückt ist ({@link #gedruecktLinks()}, {@link #gedruecktRechts()}) (z.B. als
 * Teil eines Klicks).
 * </p>
 * @author R. Spillner
 */
public class GLMaus {
	private static final Integer BETWEEN_CLICKS_IN_DOUBLE_CLICK = (Integer) Toolkit.getDefaultToolkit()
			.getDesktopProperty("awt.multiClickInterval");
	private long clickDoubleTime = 0,
			clickLeftTime = 0, clickRightTime = 0,
			moveTime = 0, clickRealSingleLeftTime = 0;
	private boolean clickLeft = false, clickRight = false,
			timerLeftRunning = false, wasDoubleLeftTimer = false;
	private GLKamera associatedCam;
	private int posX, posY;
	private ScheduledThreadPoolExecutor timer;
	private Runnable waitForRealSingleClick;

	/** Erstellt ein GLMaus-Objekt.
	 */
	public GLMaus() {
		timer = new ScheduledThreadPoolExecutor(1);
		waitForRealSingleClick = new Runnable() {
			@Override
			public void run() {
				timerLeftRunning = false;
				// ... if no double click was received in the meantime
				if(!wasDoubleLeftTimer)
					clickRealSingleLeftTime = System.currentTimeMillis();
					// System.err.println("Real single click");
				else
					wasDoubleLeftTimer = false;
			}
		};

		associatedCam = GLKamera.aktiveKamera();
		associatedCam.associatedRenderer.getWindow().addMouseListener(new MouseListenerFacade() {
			@Override
			public void handleMouseWheel(float wheelRotation) {
			}
			
			@Override
			public void handleMousePressed(boolean button1, boolean button3, int x, int y) {
				if(button1)
					clickLeft = true;
				if(button3)
					clickRight = true;
			}

			@Override
			public void handleMouseDragged(boolean button1, boolean button3, int x, int y) {
			}

			@Override
			public void handleMouseSingleClick(boolean button1, boolean button3) {
				if(button1) {
					if(!timerLeftRunning) {
						timer.schedule(waitForRealSingleClick, BETWEEN_CLICKS_IN_DOUBLE_CLICK, TimeUnit.MILLISECONDS);
						timerLeftRunning = true;
					}
					clickLeftTime = System.currentTimeMillis();
					// System.err.println("Single click");
				}
				if(button3)
					clickRightTime = System.currentTimeMillis();
			}

			@Override
			public void handleMouseDoubleClick(boolean button1, boolean button3) {
				if(button1)
					wasDoubleLeftTimer = true;
				clickDoubleTime = System.currentTimeMillis();
				// System.err.println("Double click");
			}

			@Override
			public void handleMouseMoved(int x, int y) {
				posX = x; posY = y;
				moveTime = System.currentTimeMillis();
			}

			@Override
			public void handleMouseReleased(boolean button1, boolean button3) {
				if(button1)
					clickLeft = false;
				if(button3)
					clickRight = false;
			}
		});
	}

	/** Prüft, ob ein Doppelklick vorliegt.
	 * <p>Die Methode gibt nur dann <code>true</code> zurück, wenn der Doppelklick
	 * innerhalb der vergangenen <code>MOUSE_CLICK_TIME_RANGE</code> Millisekunden
	 * erfolgte. Einem Doppelklick geht immer auch ein Einfachklick voraus, d.h.
	 * auch die Abfrage {@link #linksklick()} gibt im Falle eines Doppelklicks i.d.R.
	 * <code>true</code> zurück.
	 * </p>
	 * @return Gibt <code>true</code> zurück, wenn ein Doppelklick vorliegt, sonst
	 * <code>false</code>. 
	 */
	public boolean doppelklick() {
		if(System.currentTimeMillis() - clickDoubleTime < associatedCam.getWconf().clickTimeRange)
			return (clickDoubleTime = 0) == 0;
		else
			return (clickDoubleTime = 0) != 0;
	}

	/** Prüft, ob ein Linksklick vorliegt.
	 * <p>Die Methode gibt nur dann <code>true</code> zurück, wenn der Linkslick
	 * innerhalb der vergangenen <code>MOUSE_CLICK_TIME_RANGE</code> Millisekunden
	 * erfolgte. Linksklick liefert selbst dann <code>true</code>, wenn es sich um
	 * den ersten Klick eines Doppelklicks handelt. Ein "echter" Singleklick
	 * kann mit der Methode {@link #echterLinksklick()} getestet werden.
	 * </p>
	 * @return Gibt <code>true</code> zurück, wenn ein Linksklick vorliegt, sonst
	 * <code>false</code>. 
	 */
	public boolean linksklick() {
		if (System.currentTimeMillis() - clickLeftTime < associatedCam.getWconf().clickTimeRange)
			return (clickLeftTime = 0) == 0;
		else
			return (clickLeftTime = 0) != 0;
	}

	/** Prüft, ob ein echter Linksklick vorliegt.
	 * <p>Die Methode gibt nur dann <code>true</code> zurück, wenn ein echter Linkslick
	 * innerhalb der vergangenen <code>MOUSE_CLICK_TIME_RANGE</code> Millisekunden
	 * erfolgte. Ein Linksklick ist nur dann <em>echt</em>, wenn er <u>nicht</u> Teil eines
	 * Doppelklicks ist.
	 * </p>
	 * @return Gibt <code>true</code> zurück, wenn ein Linksklick vorliegt, sonst
	 * <code>false</code>. 
	 */
	public boolean echterLinksklick() {
		if (System.currentTimeMillis() - clickRealSingleLeftTime < associatedCam.getWconf().clickTimeRange)
			return (clickRealSingleLeftTime = 0) == 0;
		else
			return (clickRealSingleLeftTime = 0) != 0;
	}
	

	/** Prüft, ob ein Rechtsklick vorliegt.
	 * <p>Die Methode gibt nur dann <code>true</code> zurück, wenn der Rechtsklick
	 * innerhalb der vergangenen <code>MOUSE_CLICK_TIME_RANGE</code> Millisekunden
	 * erfolgte.
	 * </p>
	 * @return Gibt <code>true</code> zurück, wenn ein Rechtslick vorliegt, sonst
	 * <code>false</code>. 
	 */
	public boolean rechtsklick() {
		if(System.currentTimeMillis() - clickRightTime < associatedCam.getWconf().clickTimeRange)
			return (clickRightTime = 0) == 0;
		else
			return (clickRightTime = 0) != 0;
	}

	/** Prüft, ob die linke Maustaste im Augenblick des Aufrufs gedrückt wird.
	 * @return <code>true</code>, wenn die Maustaste gedrückt (gehalten) wird, sonst
	 * <code>false</code>
	 */
	public boolean gedruecktLinks() {
		return clickLeft;

	}

	/** Prüft, ob die rechte Maustaste im Augenblick des Aufrufs gedrückt wird.
	 * @return <code>true</code>, wenn die Maustaste gedrückt (gehalten) wird, sonst
	 * <code>false</code>
	 */
	public boolean gedruecktRechts() {
		return clickRight;
	}

	/** Liefert die x-Koordinate der aktuellen Mauszeigerposition. Die Koordinate
	 * bezieht sich nicht auf das 3D-Koordinatensystem der bGLOOP-Welt, sondern
	 * ist in Pixeln angegeben, relativ zur linken oberen Ecke des Kamerafensters. 
	 * @return x-Koordinate des Mauszeigers im Kamerafenster
	 */
	public int gibX() {
		return posX;
	}

	/** Liefert die y-Koordinate der aktuellen Mauszeigerposition. Die Koordinate
	 * bezieht sich nicht auf das 3D-Koordinatensystem der bGLOOP-Welt, sondern
	 * ist in Pixeln angegeben, relativ zur linken oberen Ecke des Kamerafensters.
	 * @return y-Koordinate des Mauszeigers im Kamerafenster
	 */
	public int gibY() {
		return posY;
	}

	/** Liefert <code>true</code>, wenn die Maus innerhalb der letzten
	 * <code>MOUSE_MOVE_TIME</code> Millisekunden  bewegt wurde. 
	 * @return <code>true</code>, wenn die Maus bewegt wird, sonst
	 * <code>true</code>
	 */
	public boolean wirdBewegt() {
		return System.currentTimeMillis() - moveTime < associatedCam.getWconf().clickTimeRange;
	}

}