package Robot2;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;


import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math;

public class BezierApproximation4 extends Applet {
	Dot[] controlPoints;
	Dot curveChange;
	int numDiv = Globals.numDiv;//number of points to plot per bezier curve
	ArrayList<Curve> rawCurves;
	ArrayList<Dot> rawPoints; 
	ArrayList<Curve> noCurvature;
	ArrayList<Dot> noCurvaturePoints; 
	ArrayList<Curve> smallAngle;
	ArrayList<Dot> smallAnglePoints; 
	ArrayList<Arc> arcs;
	ArrayList<Arc> arcPoints; 
	
	
	public void init() {
		controlPoints = new Dot[4];
		noCurvature = new ArrayList<Curve>();
		noCurvaturePoints= new ArrayList<Dot>();
		smallAngle = new ArrayList<Curve>();
		smallAnglePoints= new ArrayList<Dot>();
		rawCurves = new ArrayList<Curve>();
		rawPoints= new ArrayList<Dot>();
		arcs = new ArrayList<Arc>();
		
		//rawCurves.add(new Curve(20, 260, 50, 10, 250, 30, 300, 200));
		rawCurves.add(new Curve(20, 260, 50, 10, 250, 300, 300, 200));
		rawCurves.add(new Curve(300, 200, 350, 250, 375, 35, 400, 200));
		//rawCurves.add(new Curve(100,260,10,10,350,35,150,200));
		Compute();
	}

	public void Compute() {
		
		Curve r= new Curve();
		Curve s= new Curve();
		
		for(Curve c : rawCurves){//for each curve
		//for(int i=0; i<2; i++){
			//Curve c= rawCurves.get(i).clone();
			int n=c.getCurvatureChange();//check for a curvature change
			if(n!=-1){//if curvature change found
				c.bezDiv(n, noCurvature);
			}else{
				noCurvature.add(c);
			}
		}
		
		for(Curve c : noCurvature){
			System.out.println("angle:" + c.getAngle());
			
			c.reduceAngle(smallAngle);
		}
		for(Curve c : smallAngle){
			c.approx(arcs);
		}

		for(Curve c : rawCurves) c.pathCopy(rawPoints);
		for(Curve c : noCurvature) c.pathCopy(noCurvaturePoints);
		for(Curve c : smallAngle) c.pathCopy(smallAnglePoints);
		//System.out.println(arcs.size());
	}

	
	public void Draw(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		for (int i = 0; i < rawCurves.size(); i++){
			g2d.drawLine((int) rawCurves.get(i).p1.x, (int) rawCurves.get(i).p1.y, (int) rawCurves.get(i).p2.x, (int) rawCurves.get(i).p2.y);
			g2d.drawLine((int) rawCurves.get(i).p2.x, (int) rawCurves.get(i).p2.y, (int) rawCurves.get(i).p3.x, (int) rawCurves.get(i).p3.y);
			g2d.drawLine((int) rawCurves.get(i).p3.x, (int) rawCurves.get(i).p3.y, (int) rawCurves.get(i).p4.x, (int) rawCurves.get(i).p4.y);
			g2d.fillOval((int) rawCurves.get(i).p1.x, (int)rawCurves.get(i).p1.y, 4, 4);
			g2d.fillOval((int) rawCurves.get(i).p2.x, (int)rawCurves.get(i).p2.y, 4, 4);
			g2d.fillOval((int) rawCurves.get(i).p3.x, (int)rawCurves.get(i).p3.y, 4, 4);
			g2d.fillOval((int) rawCurves.get(i).p4.x, (int)rawCurves.get(i).p4.y, 4, 4);
		}
		
		g2d.setColor(Color.BLUE);
		for (int i = 0; i < noCurvature.size(); i++){
			g2d.drawLine((int) noCurvature.get(i).p1.x, (int) noCurvature.get(i).p1.y, (int) noCurvature.get(i).p2.x, (int) noCurvature.get(i).p2.y);
			g2d.drawLine((int) noCurvature.get(i).p2.x, (int) noCurvature.get(i).p2.y, (int) noCurvature.get(i).p3.x, (int) noCurvature.get(i).p3.y);
			g2d.drawLine((int) noCurvature.get(i).p3.x, (int) noCurvature.get(i).p3.y, (int) noCurvature.get(i).p4.x, (int) noCurvature.get(i).p4.y);
			g2d.fillOval((int) noCurvature.get(i).p1.x, (int)noCurvature.get(i).p1.y, 4, 4);
			g2d.fillOval((int) noCurvature.get(i).p2.x, (int)noCurvature.get(i).p2.y, 4, 4);
			g2d.fillOval((int) noCurvature.get(i).p3.x, (int)noCurvature.get(i).p3.y, 4, 4);
			g2d.fillOval((int) noCurvature.get(i).p4.x, (int)noCurvature.get(i).p4.y, 4, 4);
		}
//		System.out.println(rawCurves.get(0).p1.x +" "+ rawCurves.get(0).p1.y +" "+ rawCurves.get(0).p2.x+" "+ rawCurves.get(0).p2.y);
//		System.out.println(rawCurves.get(0).p3.x +" "+ rawCurves.get(0).p3.y +" "+ rawCurves.get(0).p4.x+" "+ rawCurves.get(0).p4.y);
		//// why is the red line drawn differently????
		// GeneralPath path = new GeneralPath(); // Bezier curve
		// g2d.setColor(Color.RED);
		// path.moveTo(curvePoints[0].x, curvePoints[0].y);
		// for (int i=1; i<curvePoints.length; i++)
		// path.lineTo(curvePoints[i].x, curvePoints[i].y);
		// g2d.draw(path);
		g2d.setColor(Color.RED);
		//drawPoints(g2d, rawPoints);
		g2d.setColor(Color.GREEN);
		drawPoints(g2d, smallAnglePoints);
		g2d.setColor(Color.BLACK);
		for(Arc a: arcs){
			a.draw(g2d);
		}
		
		if(curveChange!=null){
			g2d.setColor(Color.BLUE);
			g2d.fillRect((int)curveChange.x, (int)curveChange.y, 6, 6);
		}
	}
	public void drawPoints(Graphics2D g2d, Dot[] points){
		for (int i=0; i< points.length-1; i++){
			if(i%(2*(numDiv+1))==0){
				g2d.setColor(Color.red);
			}
			if(i%(2*(numDiv+1))==numDiv){
				g2d.setColor(Color.green);
			}
		
			g2d.fillOval((int)points[i].x, (int)points[i].y, 4, 4);
			g2d.drawLine((int) points[i].x, (int) points[i].y, (int) points[i+1].x,
					(int) points[i+1].y);
		}
	}
	public void drawPoints(Graphics2D g2d, ArrayList<Dot> arr){
		Dot[] array=Arrays.copyOf(arr.toArray(), arr.size(), Dot[].class);
		drawPoints(g2d, array);
	}
	public void paint(Graphics g) {
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		Draw(g2d);
		g.drawImage(image, 0, 0, this);
		
	}
}