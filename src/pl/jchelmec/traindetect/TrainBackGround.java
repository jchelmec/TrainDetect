package pl.jchelmec.traindetect;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.CvException;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

public class TrainBackGround extends JFrame {

	BufferedImage nowyobraz;
	BufferedImage nowyobrazBS;
	static Webcam obraz;
	JPanel panel;
	double time = 0;
	boolean przejazd = false;
	boolean pociag = false;
	String kierunek = "";
	CzasPrzejazdu czasL;
	CzasPrzejazdu czasP;
	
	

/**
	 * Klasa rysujaca wunik rozpoznania
	 */
	private static final long serialVersionUID = 1L;


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String file = ("d:/TrainVideo/Testy/podmiejski1.mp4") ;
	     //Rysowanie
	     
	     EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						new TrainBackGround(file, args);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
	}
	
	public TrainBackGround(String file, String[] args) {
		
		 setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 panel = new JPanel();
		 getContentPane().add(panel);
//		 String file = "d:/TrainVideo/Train.mp4";
		 
		 obraz = new Webcam(file, Webcam.MOG_METHOD);
		 nowyobrazBS = obraz.getOneBS();
		 nowyobraz = obraz.getOneFrame();
		
		 Size size = obraz.getVideoSize();
		 System.out.println(size);
		 int videoW = (int)obraz.getFrameWidth();
		 int videoH = (int)obraz.getFrameHeight();
		 setBounds(0,0,videoW*2,videoH);
	  	
	  	new DetectThread().start();
	  	setVisible(true);
	     
	}

	public void paint (Graphics g){
		super.paint(g);
		g = panel.getGraphics();
	}

	class DetectThread extends Thread {

		@Override
		public void run() {
			while (panel == null || panel.getGraphics() == null) {
				try {Thread.sleep(1000);}
				catch (InterruptedException ex) {}
			}
			
			Graphics g = panel.getGraphics();
			
			if (nowyobraz!=null & nowyobrazBS != null) {
//				setSize(nowyobraz.getWidth()+nowyobrazBS.getWidth(), nowyobraz.getHeight());
				setSize((int)obraz.getFrameWidth(), (int)obraz.getFrameHeight());
			}
			
			List<CzasPrzejazdu> listaprzejazdow = new ArrayList<CzasPrzejazdu>();
			
			while (true) {
				
				double fps= obraz.getVideoFps();
				try {
					nowyobrazBS = obraz.getOneBS();
					nowyobraz = obraz.getOneFrame();
					g.drawImage(nowyobraz, 0, 0, null);
					g.drawImage(nowyobrazBS,nowyobraz.getWidth(), 0, null);
					
					Rect train = obraz.getOneRect();
//					System.out.println("Train =  " + train);
//					System.out.print((train.x < obraz.getScreenLineRight()) + "   ");
					
					// Wykrycie pocz¹tku przejazdu w lewo
					if (train.x > obraz.getFrameWidth()/2
							&& train.x < obraz.getScreenLineRight()
								&& train.width > 0
									&& przejazd == false
										&& pociag == false) {
						przejazd = true;
						kierunek = "lewo";
						System.out.println("pocz¹tek przejazdu: " + obraz.getFramePosTime() + "    kierunek: " + kierunek);
						 czasL = new CzasPrzejazdu();
						 czasL.setKierunek(kierunek);
						 czasL.setStart(obraz.getFramePosTime());
						 listaprzejazdow.add(czasL);
					}
					
					// Wykrycie pocz¹tku przejazdu w prawo
					if ((train.x + train.width) < obraz.getFrameWidth()/2
							&& (train.x + train.width) > obraz.getScreenLineLeft()
								&& train.width > 0
									&& przejazd == false
										&& pociag == false) {
						przejazd = true;
						kierunek = "prawo";
						System.out.println("pocz¹tek przejazdu: " + obraz.getFramePosTime() + "    kierunek: " + kierunek);
						 czasP = new CzasPrzejazdu();
						 czasP.setKierunek(kierunek);
						 czasP.setStart(obraz.getFramePosTime());
						 listaprzejazdow.add(czasP);
					}
					
					// Wykrycie koñca przejazdu w lewo
					if (train.x < obraz.getScreenLineLeft() && przejazd && kierunek.equals("lewo")) {
						przejazd = false;
						kierunek = "";
						System.out.println("koniec przejazdu: " + obraz.getFramePosTime());
						czasL.setKoniec(obraz.getFramePosTime());
						System.out.println("Czas samego przejazdu: " + czasL.getTime());
					}
					
					// Wykrycie koñca przejazdu w prawo
					if ((train.x + train.width) > obraz.getScreenLineRight() && przejazd && kierunek.equals("prawo")) {
						przejazd = false;
						kierunek = "";
						System.out.println("koniec przejazdu: " + obraz.getFramePosTime());
						czasP.setKoniec(obraz.getFramePosTime());
						System.out.println("Czas samego przejazdu: " + czasP.getTime() + " ms");
					}
					
					// sprawdzenie czy jest to koniec przezdu czy nowy przejazd
					if (train.width >0) {
						pociag = true;
					}else {
						pociag = false;
					}
					
					// Zapis poczatku i konca do pliku
					
					
					
					time = time + 1;
					
					if (obraz.getOneRect().height > 0 && obraz.getOneRect().width > 0) {
						System.out.println(obraz.getOneRect() + "   Time: " + time / fps );
					}
				} catch (CvException e) {
					System.out.println("Ca³kowity czas: " + time / fps);
//					System.out.println("Czas samego przejazdu: ");
					break;
				}
			}
			
			for (int i=0; i < listaprzejazdow.size(); i++){
				System.out.println(listaprzejazdow.get(i));
			}

		}
	}
}	
