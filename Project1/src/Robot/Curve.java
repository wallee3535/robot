package Robot;

import java.util.ArrayList;

public class Curve {
	Dot p1;
	Dot p2;
	Dot p3; 
	Dot p4;
	
	public Curve(Dot p1, Dot p2, Dot p3, Dot p4){
		this.p1=p1;
		this.p2=p2;
		this.p3=p3;
		this.p4=p4;
	}
	
	public Curve(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4){
		p1= new Dot(x1, y1);
		p2= new Dot(x2, y2);
		p3= new Dot(x3, y3);
		p4= new Dot(x4, y4);
	}
	
	public Curve(){
		p1= new Dot(0, 0);
		p2= new Dot(0, 0);
		p3= new Dot(0, 0);
		p4= new Dot(0, 0);
	}
	public Curve clone(){
		Dot p1 = new Dot(this.p1.x, this.p1.y);
		Dot p2 = new Dot(this.p2.x, this.p2.y);
		Dot p3 = new Dot(this.p3.x, this.p3.y);
		Dot p4 = new Dot(this.p4.x, this.p4.y);
		
		return new Curve(p1, p2, p3, p4);
	}
	public Dot[] dotData(int numDiv){
		Dot[] result= new Dot[numDiv+1];
		Dot[] tmp= new Dot[4];
		for (int i = 0; i <= numDiv; i++) { 
			// t is percentage of curve done
			double t = ((double) i) / (numDiv);
			// copy points from Curve
			tmp = new Dot[]{p1.dup(), p2.dup(), p3.dup(), p4.dup()};
			// dimension of bezier curve???
			int Depth = 4;
			// recurse
			while (Depth > 1) {
				for (int j = 0; j < Depth - 1; j++)
					// reduce dimension, to get new lines
					SubDivide(tmp[j], tmp[j + 1], t);
				Depth--;
			}
			// last remaining point is on the path
			result[i]=new Dot(tmp[0].x, tmp[0].y);
		}
		return result;
	}
	public ArrayList<Dot> dotData2(int numDiv){
		ArrayList<Dot> result= new ArrayList<Dot>();
		Dot[] tmp= new Dot[4];
		for (int i = 0; i <= numDiv; i++) { 
			// t is percentage of curve done
			double t = ((double) i) / (numDiv);
			// copy points from Curve
			tmp = new Dot[]{p1.dup(), p2.dup(), p3.dup(), p4.dup()};
			// dimension of bezier curve???
			int Depth = 4;
			// recurse
			while (Depth > 1) {
				for (int j = 0; j < Depth - 1; j++)
					// reduce dimension, to get new lines
					SubDivide(tmp[j], tmp[j + 1], t);
				Depth--;
			}
			// last remaining point is on the path
			result.add(new Dot(tmp[0].x, tmp[0].y));
		}
		return result;
	}
	public void SubDivide(Dot p1, Dot p2, double t) {
		if (p1.x > p2.x)
			p1.x -= Math.abs(p1.x - p2.x) * t;
		else
			p1.x += Math.abs(p1.x - p2.x) * t;
		if (p1.y > p2.y)
			p1.y -= Math.abs(p1.y - p2.y) * t;
		else
			p1.y += Math.abs(p1.y - p2.y) * t;
	}




	public static void main(String[] args){
		Curve c = new Curve(20, 260, 50, 10, 250, 300, 300, 200);
		System.out.println(c.getAngle()*180/Math.PI);
	}

}
