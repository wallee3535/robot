package Robot3;

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

public class Main3d extends Applet {

	Dot curveChange;
	int numDiv = Globals.numDiv;// number of points to plot per bezier curve
	ArrayList<Curve> rawCurves;
	ArrayList<Dot> rawPoints;
	ArrayList<Dot> controlPoints;
	ArrayList<Dot> arcPoints;
	ArrayList<Arc> arcs;
	ArrayList<Dot> testPoints;
	
	public void init() {

		rawCurves = new ArrayList<Curve>();
		rawPoints = new ArrayList<Dot>();
		testPoints = new ArrayList<Dot>();
		arcPoints = new ArrayList<Dot>();
		controlPoints = new ArrayList<Dot>();
		arcs = new ArrayList<Arc>();

		Curve c1 = new Curve(new Dot(20, 260, 0), new Dot(50, 10, 40), new Dot(250, 300, 80), new Dot(300, 200, 120));
		rawCurves.add(c1);
		// rawCurves.add(new Curve(20, 260, 50, 10, 250, 300, 300, 200));
		// rawCurves.add(new Curve(300, 200, 350, 250, 375, 35, 400, 200));
		// rawCurves.add(new Curve(100,260,10,10,350,35,150,200));
		// make actual data points and put them in rawPoints
		for (Curve c : rawCurves)
			c.pathCopy(rawPoints);
		controlPoints.add(c1.p1);
		controlPoints.add(c1.p2);
		controlPoints.add(c1.p3);
		controlPoints.add(c1.p4);
		Compute();
	}

	public void Compute() {
		/*
		int start = 0;
		int end = 2;
		arcPoints.add(rawPoints.get(0));
		while (end < rawPoints.size()) {

			int middle = (start + end) / 2;
			Dot center = Dot.circumcenter(rawPoints.get(start), rawPoints.get(middle), rawPoints.get(end));
			double radius = rawPoints.get(start).distTo(center);
			for (int i = start; i <= end; i++) {
				if (Math.abs(rawPoints.get(i).distTo(center) - radius) > Globals.error) {
					end++;
					break;
				}
			}
			arcPoints.add(rawPoints.get(middle));
			arcPoints.add(rawPoints.get(end));
			start = end;
			end += 2;

		}*/
		
		//start and end of chunk of points left to approximate
		int start = 0;
		int end = 2;
		arcPoints.add(rawPoints.get(0));
		while (end < rawPoints.size()) {

			int middle = (start + end) / 2;
			
			//calculate center and radius of arc approximating the chunk
			Dot center = Dot.circumcenter2(rawPoints.get(start), rawPoints.get(middle), rawPoints.get(end));
			double radius = rawPoints.get(start).distTo(center);
			
			boolean good = true;
			for (int i = start; i <= end; i++) {
				
				//if the approximation is not close enough for any of the points...
				if (Math.abs(rawPoints.get(i).distTo(center) - radius) > Globals.error) {
					//arcs.add(new Arc(rawPoints.get(start), rawPoints.get((start + end-1) / 2), rawPoints.get(end-1)));
					//end++;
					good= false;
					break;
				}
				
				
			}
			
			if (!good){
				arcs.add(new Arc(rawPoints.get(start), rawPoints.get((start + end-1) / 2), rawPoints.get(end-1)));
				arcPoints.add(rawPoints.get(end-1));
				start=end-1;
				end=start+2;
			}else{
				end++;
			}
			//arcPoints.add(rawPoints.get(middle));
			//arcPoints.add(rawPoints.get(end));
			
			
		}
		
		arcs.add(new Arc(rawPoints.get(start), rawPoints.get((start + end) / 2), rawPoints.get(end-1)));
		System.out.println(arcs.size());
		
	}

	public void Draw(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		// draws lines conecting control points
		drawPoints(g2d, controlPoints);

		g2d.setColor(Color.RED);
		// draw raw points, calculated from control points
		drawPoints(g2d, rawPoints);
		g2d.setColor(Color.GREEN);
		drawPoints(g2d, arcPoints);
		g2d.setColor(Color.BLACK);
		for(Arc a : arcs){
			a.draw(g2d);
			a.print();
			//g2d.drawOval((int)(center.x-radius), (int)(center.y-radius), (int)(2*radius), (int)(2*radius));
		}

	}

	public void drawPoints(Graphics2D g2d, Dot[] points) {
		for (int i = 0; i < points.length - 1; i++) {//loops through each point, processess that point and th enext one
			// if (i % (2 * (numDiv + 1)) == 0) {
			// g2d.setColor(Color.red);
			// }
			// if (i % (2 * (numDiv + 1)) == numDiv) {
			// g2d.setColor(Color.green);
			// }
			switch (Globals.view) {
			case 'x':
				g2d.fillOval((int) points[i].y, (int) points[i].z, 4, 4);
				g2d.drawLine((int) points[i].y, (int) points[i].z, (int) points[i + 1].y, (int) points[i + 1].z);
				break;
			case 'y':
				g2d.fillOval((int) points[i].z, (int) points[i].x, 4, 4);
				g2d.drawLine((int) points[i].z, (int) points[i].x, (int) points[i + 1].z, (int) points[i + 1].x);
				break;
			case 'z':
				g2d.fillOval((int) points[i].x, (int) points[i].y, 4, 4);
				g2d.drawLine((int) points[i].x, (int) points[i].y, (int) points[i + 1].x, (int) points[i + 1].y);
				break;
			}
		}
		
		
	}

	public void drawPoints(Graphics2D g2d, ArrayList<Dot> arr) {
		Dot[] array = Arrays.copyOf(arr.toArray(), arr.size(), Dot[].class);
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