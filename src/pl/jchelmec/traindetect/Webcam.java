package pl.jchelmec.traindetect;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Rect2d;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.video.BackgroundSubtractorKNN;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.KalmanFilter;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;


public class Webcam {
	BufferedImage nowyobraz;
	BufferedImage nowyobrazBS;
	int rows;
	int cols;
	VideoCapture capture;
	Mat matrix;
	BackgroundSubtractorMOG2 mog2 = Video.createBackgroundSubtractorMOG2();
	Mat mask, bg, dmask;
	ConvertImage conv1 = new ConvertImage();
	ConvertImage conv2 = new ConvertImage();
	MatOfRect trackresult;
	MatOfInt numdetect;
	public double fps;
	Vector<Rect> rectArray = new Vector<>();
	Rect oneRect = new Rect();
	double frameWidth;
	double frameHeight;
	ScreenLine scrlinLeft;
	ScreenLine scrlinRight;
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		}
	
	
	Webcam(String file){
	 
	     
//	      Imgcodecs imgcodecs = new Imgcodecs();
		 capture = new VideoCapture(file);
		 fps = capture.get(Videoio.CAP_PROP_FPS);
		 capture.set(Videoio.CAP_PROP_FRAME_WIDTH,1200);
		 capture.set(Videoio.CAP_PROP_FRAME_HEIGHT,800);
		 frameWidth = capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
		 frameHeight = capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
		 System.out.println(frameWidth + " x " + frameHeight);

		 
		 matrix = new Mat();
		 mask = new Mat(); 
		
		 // If camera is opened
	      if(!capture.isOpened()) {
	         System.out.println("camera not detected");
	      } else
	         System.out.println("Camera detected ");
		 
	}
	
	Mat getMask(Mat matrix){
//		Mat dmask = new Mat(matrix.size(), CvType.CV_8UC1);
//		Imgproc.cvtColor(matrix, dmask, Imgproc.COLOR_BGRA2GRAY, 0);
		mog2.setHistory(500);
		mog2.setVarThreshold(10);
		mog2.setBackgroundRatio(0.5);	
		mog2.apply(matrix, mask);
//		Imgproc.cvtColor(dmask, matrix, Imgproc.COLOR_GRAY2BGRA, 0);
//		mog2.setVarMax(100);
		return mask;
	}

	
	public BufferedImage getOneFrame(){
		BufferedImage nowyobraz = null;
		
		nowyobraz = conv1.convert(matrix);
		
		return nowyobraz;
		}
	
	public BufferedImage getOneBS(){
		
		capture.read(matrix);
		mask = getMask(matrix);
		

				Mat erode = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(
						8, 8));
				Mat dilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
						new Size(8, 8));
		
				Mat openElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
						new Size(3, 3), new Point(1, 1));
				Mat closeElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
						new Size(7, 7), new Point(3, 3));
		
				Imgproc.threshold(mask, mask, 127, 255, Imgproc.THRESH_BINARY);
				Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, erode);
				Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, dilate);
				Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, openElem);
				Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, closeElem);
		

		List <MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		Rect rect = null;
		double maxArea=100;
		Scalar colorObictRect = new Scalar(0,0,255);
		scrlinLeft = new ScreenLine(matrix, (int)Math.round(frameWidth*0.1) ,0, (int)Math.round(frameWidth*0.1), (int)frameHeight);
		scrlinRight = new ScreenLine(matrix, (int)Math.round(frameWidth*0.9),0, (int)Math.round(frameWidth*0.9), (int)frameHeight);
		
		Vector<Rect> rectArrayOneFrame = new Vector<>();
		Integer x1 = 0;
		Integer x2 = 0;
		Integer y1 = 0;
		Integer y2 = 0;
		boolean init = true;
		
		for (int i=0; i<contours.size();i++) {
			Mat contour = contours.get(i);
			double contourArea = Imgproc.contourArea(contour);
			if (contourArea > maxArea) {
				rect = Imgproc.boundingRect(contours.get(i));
				if (rect.height > 10 && rect.width >100) {
					if (rect.x < scrlinRight.x1 && (rect.x + rect.width) > scrlinLeft.x1) {
						colorObictRect = new Scalar(0,255,0);
						
						if (rect.x <= x1 || init ) { 
							x1 = rect.x;
						}
						if (rect.y <= y1 || init )  {
							y1 = rect.y;
						}
						if ((rect.x + rect.width) >= x2 || init ) {
							x2 = rect.x + rect.width;
						}
						if ((rect.y + rect.height) >= y2 || init ) {
							y2 = rect.y + rect.height;
						}
						init = false;
						
//						System.out.println(rect);
						
					}
					Imgproc.rectangle(matrix, new Point(rect.x,rect.y), new Point(rect.x + rect.width, rect.y + rect.height), colorObictRect,4);
				}
			}
			
		}
			oneRect = new Rect(x1, y1, x2-x1, y2-y1);
//					System.out.println("rect: " + r);
					
			rectArray.add(oneRect);
		
		
		
		nowyobrazBS = conv2.convert(mask);
		return nowyobrazBS;
	}
	
	public Vector<Rect> getRectBS(){
		
		return rectArray;
	}
	
	public Rect getOneRect(){
		
		return oneRect;
	}
	
	public Size getVideoSize() {
		return matrix.size();
	}
	
	public Size getBSVideoSize() {
		return mask.size();
	}
	
	public double getVideoFps() {
		return fps;
	}
	
	public double getFrameWidth() {
		return frameWidth;
	}
	
	public double getFrameHeight() {
		return frameHeight;
	}
	
	public double getFramePosTime() {
		return capture.get(Videoio.CAP_PROP_POS_MSEC);
	}
	
	public int getScreenLineLeft() {
		return scrlinLeft.getX1();
	}

	public int getScreenLineRight() {
		return scrlinLeft.getX2();
	}
}

class ConvertImage{

	public BufferedImage convert(Mat matrix){
		BufferedImage obraz=null;
			
		MatOfByte imagemat = new MatOfByte();
		Imgcodecs.imencode(".jpg", matrix, imagemat);
		byte[] byteArray = imagemat.toArray();
		
		InputStream in = new ByteArrayInputStream(byteArray);
		BufferedImage image;
		try {
			image = ImageIO.read(in);
			obraz = image;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obraz;
		
	}
}

class ScreenLine{
	
	int x1,y1,x2,y2;

	public ScreenLine(Mat matrix,int x1, int y1, int x2,int y2) {
		// TODO Auto-generated constructor stub
		this.x1=x1;
		this.x2=x2;
		this.y1=y1;
		this.y2=y2;
		Imgproc.line(matrix, new Point(x1,y1), new Point(x2,y2), new Scalar(255,0,0),3);
	}
	
	
	public void setLine(int x1, int y1, int x2,int y2) {
	
			
	}
	
	public int getX1 () {
		return x1;
	}

	public int getX2 () {
		return x2;
	}

	
}