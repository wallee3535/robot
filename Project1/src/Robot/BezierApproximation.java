package Robot;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BezierApproximation extends Applet {
	Point[] controlPoints, curvePoints;
	Point curveChange;

	public void init() {
		controlPoints = new Point[4];
		curvePoints = new Point[20];
		
		ArrayList<Point> arcPoints = new ArrayList<Point>();
		controlPoints[0] = new Point(100, 260);
		controlPoints[1] = new Point(10, 10);
		controlPoints[2] = new Point(350, 35);
		controlPoints[3] = new Point(150, 200);
		
		//controlPoints[3]=new Point(450,290);
		for (int i = 0; i < curvePoints.length; i++)
			curvePoints[i] = new Point(0, 0);
	}

	public void SubDivide(Point p1, Point p2, double t) {
		if (p1.x > p2.x)
			p1.x -= Math.abs(p1.x - p2.x) * t;
		else
			p1.x += Math.abs(p1.x - p2.x) * t;
		if (p1.y > p2.y)
			p1.y -= Math.abs(p1.y - p2.y) * t;
		else
			p1.y += Math.abs(p1.y - p2.y) * t;
	}

	public void Compute() { // COMPUTE CURVEPOINTS FOR BEZIER CURVE
		Point[] tmp = new Point[controlPoints.length];
		for (int i = 0; i < tmp.length; i++)
			tmp[i] = new Point(0, 0);
		for (int i = 0; i < curvePoints.length; i++) { // t is percentage of
														// curve done
			double t = ((double) i) / (curvePoints.length - 1);
			// copy points from controlPoints
			for (int j = 0; j < controlPoints.length; j++)
				tmp[j] = new Point(controlPoints[j].x, controlPoints[j].y);
			// dimension of bezier curve
			int Depth = tmp.length;
			// recurse
			while (Depth > 1) {
				for (int j = 0; j < Depth - 1; j++)
					// reduce dimension, to get new lines
					SubDivide(tmp[j], tmp[j + 1], t);
				Depth--;
			}
			// last remaining point is on the path
			curvePoints[i] = new Point(tmp[0].x, tmp[0].y);
		}

		// COMPUTE ARCPOINTS FOR ARCS
		curveChange=curveChange(curvePoints);
		
	}

	public Point curveChange(Point[] curvePoints){
		int length= curvePoints.length;
		
		double slope1= slope(curvePoints[0], curvePoints[1]);
		double slope2= slope(curvePoints[1], curvePoints[2]);
		double slope3= slope(curvePoints[2], curvePoints[3]);
		for(int i=3; i<length-1; i++){
			System.out.println("slope: "+slope1);
			if((slope2-slope1>0&&slope3-slope2<0)||(slope2-slope1<0&&slope3-slope2>0)){
				//curvature changes between i+1 and i+2
				System.out.println("curvature: ("+ curvePoints[i+1].x+" , "+curvePoints[i+1].y+")");
				return curvePoints[i+1];
			}
			slope1=slope2;
			slope2=slope3;
			slope3=slope(curvePoints[i], curvePoints[i+1]);
		}
		System.out.println("no curvature");
		return null;
	}
	public double slope(Point p1, Point p2){
		return (double)(p2.y-p1.y)/(p2.x-p1.x);
	}
	
	public void Draw(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		for (int i = 0; i < controlPoints.length - 1; i++)
			g2d.drawLine((int) controlPoints[i].x, (int) controlPoints[i].y, (int) controlPoints[i + 1].x,
					(int) controlPoints[i + 1].y);
		//// why is the red line drawn differently????
		// GeneralPath path = new GeneralPath(); // Bezier curve
		// g2d.setColor(Color.RED);
		// path.moveTo(curvePoints[0].x, curvePoints[0].y);
		// for (int i=1; i<curvePoints.length; i++)
		// path.lineTo(curvePoints[i].x, curvePoints[i].y);
		// g2d.draw(path);
		g2d.setColor(Color.RED);
		for (int i = 0; i < curvePoints.length - 1; i++)
			g2d.drawLine((int) curvePoints[i].x, (int) curvePoints[i].y, (int) curvePoints[i + 1].x,
					(int) curvePoints[i + 1].y);
		if(curveChange!=null){
			g2d.setColor(Color.BLUE);
			g2d.fillOval(curveChange.x, curveChange.y, 4, 4);
		}
	}

	public void paint(Graphics g) {
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		//g2d.fillOval(curveChange.x, curveChange.y, 4, 4);
		//g2d.setColor(Color.BLUE);
			
		//g2d.fillOval(curveChange.x, curveChange.y, 4, 4);
		Compute();
		Draw(g2d);
		g.drawImage(image, 0, 0, this);
		
	}
}