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
	
	

/**
	 * Klasa rysujaca wunik rozpoznania
	 */
	private static final long serialVersionUID = 1L;


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	     //Rysowanie
	     
	     EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						new TrainBackGround();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
	}
	
	public TrainBackGround() {
		
		 setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 panel = new JPanel();
		 getContentPane().add(panel);
		 String file = "d:/TrainVideo/TrainLewo.mp4";
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
					
					if (obraz.getOneRect().width > 0 && przejazd == false) {
						przejazd = true;
						System.out.println("pocz�tek przejazdu: " + obraz.getFramePosTime());
					}
					
					if (obraz.getOneRect().width <0 && przejazd) {
						przejazd = false;
						System.out.println("koniec przeajzdu: " + obraz.getFramePosTime());
					}
					time = time + 1;
					System.out.println(obraz.getOneRect() + "   Time: " + time / fps );
					
				} catch (CvException e) {
					System.out.println("Ca�kowity czas: " + time / fps);
					break;
				}
			}

		}
	}
}	


