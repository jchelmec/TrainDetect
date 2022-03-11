package pl.jchelmec.traindetect;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import java.util.ArrayList;

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
	CzasPrzejazdu czas;
	
	

/**
	 * Klasa rysujaca wunik rozpoznania
	 */
	private static final long serialVersionUID = 1L;


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String file = ("d:/TrainVideo/TrainNight.mp4") ;
	     //Rysowanie
	     
	     EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						new TrainBackGround(file);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
	}
	
	public TrainBackGround(String file) {
		
		 setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 panel = new JPanel();
		 getContentPane().add(panel);
//		 String file = "d:/TrainVideo/Train.mp4";
		 obraz = new Webcam(file);
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
			
			
			while (true) {
				
				double fps= obraz.getVideoFps();
				try {
					nowyobrazBS = obraz.getOneBS();
					nowyobraz = obraz.getOneFrame();
					g.drawImage(nowyobraz, 0, 0, null);
					g.drawImage(nowyobrazBS,nowyobraz.getWidth(), 0, null);
					
					Rect train = obraz.getOneRect();
//					System.out.print((train.x < obraz.getScreenLineRight()) + "   ");
					
					
					if (train.x > obraz.getFrameWidth()/2
							&& train.x < obraz.getScreenLineRight()
								&& train.width > 0
									&& przejazd == false
										&& pociag == false) {
						przejazd = true;
						kierunek = "lewo";
						System.out.println("pocz¹tek przejazdu: " + obraz.getFramePosTime() + "    kierunek: " + kierunek);
						 czas = new CzasPrzejazdu();
						 czas.setStart(obraz.getFramePosTime());
						 
					}
					
					if ((train.x + train.width) < obraz.getFrameWidth()/2
							&& (train.x + train.width) > obraz.getScreenLineLeft()
								&& train.width > 0
									&& przejazd == false
										&& pociag == false) {
						przejazd = true;
						kierunek = "prawo";
						System.out.println("pocz¹tek przejazdu: " + obraz.getFramePosTime() + "    kierunek: " + kierunek);
						 czas = new CzasPrzejazdu();
						 czas.setStart(obraz.getFramePosTime());
					}
					
					
					if (train.x < obraz.getScreenLineLeft() && przejazd && kierunek.equals("lewo")) {
						przejazd = false;
						kierunek = "";
						System.out.println("koniec przejazdu: " + obraz.getFramePosTime());
						czas.setKoniec(obraz.getFramePosTime());
						System.out.println("Czas samego przejazdu: " + czas.getTime());
					}
					
					if ((train.x + train.width) > obraz.getScreenLineRight() && przejazd && kierunek.equals("prawo")) {
						przejazd = false;
						kierunek = "";
						System.out.println("koniec przejazdu: " + obraz.getFramePosTime());
						czas.setKoniec(obraz.getFramePosTime());
						System.out.println("Czas samego przejazdu: " + czas.getTime() + " ms");
					}
					
					// sprawdzenie czy jest to koniec przezdu czy nowy przejazd
					if (train.width >0) {
						pociag = true;
					}else {
						pociag = false;
					}
					
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

		}
	}
}	


//class CzasPrzejazdu{
//
//	public double czasStart;
//	public double czasKoniec;
//	
//	public void setStart(double czas) {
//		this.czasStart = czas;
//	}
//	
//	public void setKoniec(double czas) {
//		this.czasKoniec = czas;
//	}
//	
//	public double getTime() {
//		return czasKoniec - czasStart;
//	}
//}

