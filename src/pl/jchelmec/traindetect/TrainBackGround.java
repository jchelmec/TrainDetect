package pl.jchelmec.traindetect;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.security.acl.NotOwnerException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.Rect;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

public class TrainBackGround extends JFrame {

	BufferedImage nowyobraz;
	BufferedImage nowyobrazBS;
	Webcam obraz;
	JPanel panel;
	
	

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
						new TrainBackGround(100,100,850,485);
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
	}
	
	public TrainBackGround(int x, int y, int w, int h){
		
		 setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     panel = new JPanel();
	     getContentPane().add(panel);
	     setBounds(x,y,w,h);
	     
	       //nowyobraz = obraz.getImage();
	     setSize(1800, 500);
	     
	  // TODO Auto-generated method stub
	     obraz = new Webcam();
	     			
	  	new DetectThread().start();
	  	setVisible(true);
	     
	}

	public void paint (Graphics g){
		super.paint(g);
		g = panel.getGraphics();
//		if (nowyobraz!=null){
//            setSize(nowyobraz.getWidth()+5, nowyobraz.getHeight()+5);
//            g.drawImage(nowyobraz, 0, 0, rootPane);
//        }
	}

	public class DetectThread extends Thread {

	
		@Override
		public void run() {
			while (panel == null || panel.getGraphics() == null) {
				try {Thread.sleep(1000);}
				catch (InterruptedException ex) {}
			}
			Graphics g = panel.getGraphics();
			nowyobrazBS = obraz.getOneBS();
			nowyobraz = obraz.getOneFrame();
			System.out.println("wielko�� obrazu: " + obraz.getVideoSize());
			System.out.println("wielko�� obrazu: " + obraz.getBSVideoSize());
			
			
//			ArrayList<Rect> rectArray = obraz.getRectBS();
			
			if (nowyobraz!=null & nowyobrazBS != null) {
				setSize(nowyobraz.getWidth()+nowyobrazBS.getWidth(), nowyobraz.getHeight()+20);
			}
			while (true) {
				nowyobrazBS = obraz.getOneBS();
				nowyobraz = obraz.getOneFrame();
				g.drawImage(nowyobraz, 0, 0, null);
				g.drawImage(nowyobrazBS,nowyobraz.getWidth(), 0, null);
				
				
			}
				
		}
 
	}
}	


