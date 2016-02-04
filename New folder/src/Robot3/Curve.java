package Robot3;

import java.util.ArrayList;

public class Curve {
	Dot p1;
	Dot p2;
	Dot p3; 
	Dot p4;
	private Dot[] path;
	private boolean hasCurvatureChange;
	private int curveChange= -2; //-2 means not calculated yet
	private boolean isCounterClockwise;
	private double angle = -1;
	int numDiv= Globals.numDiv;
	
	public Curve(Dot p1, Dot p2, Dot p3, Dot p4){
		this.p1=p1;
		this.p2=p2;
		this.p3=p3;
		this.p4=p4;
		calcPath(numDiv);
		
	}
	
	public Curve (double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4){
		/*p1= new Dot(x1, y1);
		p2= new Dot(x2, y2);
		p3= new Dot(x3, y3);
		p4= new Dot(x4, y4);*/
		this(new Dot(x1, y1, z1), new Dot(x2, y2, z2), new Dot(x3, y3, z3),new Dot(x4, y4, z4));
	}
	
	/*public Curve Curve(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4){
		p1= new Dot(x1, y1);
		p2= new Dot(x2, y2);
		p3= new Dot(x3, y3);
		p4= new Dot(x4, y4);
		return new Curve(new Dot(x1, y1), new Dot(x2, y2), new Dot(x3, y3),new Dot(x4, y4));
	}*/
	
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
	public void print(){
		System.out.println("Curve:");
		p1.print();
		p2.print();
		p3.print();
		p4.print();
	}
	public void calcPath(int numDiv){
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
			result[i]=new Dot(tmp[0].x, tmp[0].y, tmp[0].z);
		}
		path= result;
	}
	public Dot[] getPath(){
		if(path==null){
			calcPath(numDiv);
		}
		return path;
	}
	//takes curve and makes it into actual data points and puts it in dest
	public void pathCopy(ArrayList<Dot> dest){
		if(path==null){
			calcPath(numDiv);
		}
		for(Dot d: path){
			dest.add(d);
		}
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
		if (p1.z > p2.z)
			p1.z -= Math.abs(p1.z - p2.z) * t;
		else
			p1.z += Math.abs(p1.z - p2.z) * t;
	}

	public void bezDiv(int index, ArrayList<Curve> dest){
		double k=(double)index/numDiv;
		Curve r=new Curve();
		Curve s=new Curve();
		r.p1= this.p1.dup();
		r.p2= this.p1.plus(this.p2.minus(this.p1).times(k));
		r.p3= this.p3.minus(this.p2).times(k).plus(this.p2).minus(r.p2).times(k).plus(r.p2);
		r.p4= this.bezExplicit(k);
		s.p1= r.p4.dup();
		Dot p23= this.p3.minus(this.p2).times(k).plus(this.p2);
		s.p3= this.p4.minus(this.p3).times(k).plus(this.p3);
		s.p2= s.p3.minus(p23).times(k).plus(p23);
		s.p4= this.p4.dup();
		dest.add(r);
		dest.add(s);
	}
	
	public void calcCurveChange(){
		if(path==null){
			calcPath(numDiv);
		}
		int size= path.length;
		if(size<4){
			curveChange= -1;
		}
		Dot vector0= path[1].minus(path[0]);
		Dot vector1= path[2].minus(path[1]);
		boolean curvature= vector0.crossSign(vector1);
		for(int i=2; i<size-2; i++){
			Dot vectorA= path[i].minus(path[i-1]);
			Dot vectorB= path[i+1].minus(path[i]);
			boolean currentCurvature= vectorA.crossSign(vectorB);
			if(curvature!=currentCurvature){
				System.out.println("curvature: ("+ path[i].x+" , "+path[i].y+")");
				curveChange=i;
				return;
			}
			vectorA= vectorB.dup();
			vectorB= path[i+2].minus(path[i+1]);
			
		}
		System.out.println("no curvature");
		curveChange= -1;
	}
	public int getCurvatureChange(){
		if(curveChange==-2){
			this.calcCurveChange();
		}
		return curveChange;
	}
	public Dot bezExplicit(double k){
		return new Dot(Math.pow(1-k,3)*this.p1.x+ 3*k*Math.pow(1-k, 2)*this.p2.x+3*Math.pow(k,2)*(1-k)*this.p3.x+Math.pow(k, 3)*this.p4.x, 
					   Math.pow(1-k,3)*this.p1.y+ 3*k*Math.pow(1-k, 2)*this.p2.y+3*Math.pow(k,2)*(1-k)*this.p3.y+Math.pow(k, 3)*this.p4.y);
	}
	public void calcCounterClockwise(){
		if(path==null){
			calcPath(numDiv);
		}
		Dot vector0= path[1].minus(path[0]);
		Dot vector1= path[2].minus(path[1]);
		isCounterClockwise =  vector0.crossSign(vector1);
	}

	public void calcAngle(){
		calcCounterClockwise();
		angle=(p2.minus(p1).rotationTo(p4.minus(p3), isCounterClockwise));
	}
	public double getAngle(){
		if(angle==-1){
			calcAngle();
		}
		return angle;
	}
	
	public void reduceAngle(ArrayList<Curve> arr){
		
		double currentAngle= getAngle();

		if(currentAngle<Math.PI/2){//less than 90 degrees
			arr.add(this.clone());
			
		}else if(currentAngle <Math.PI){//less than 180 degrees
			this.bezDiv(numDiv/2, arr);
			
		}else{
			
			ArrayList<Curve> temp= new ArrayList<Curve>();
			int index = this.firstNinety();
			this.bezDiv(index, temp);
			arr.add(temp.get(0));
			temp.get(1).reduceAngle(arr);
		}		
	}
	public int firstNinety(){
		Dot p12= p2.minus(p1);
		for(int i=0; i<path.length-1; i++){
			Dot tangent= path[i+1].minus(path[i]);
			if(p12.shortAngle(tangent)>Math.PI/2){
				return i; //???
			}
		}
		System.out.println("firstNinety() returned -1"); //error, should never happen
		return -1;
	}
	public void approx(ArrayList<Arc> arr){
		if(path==null){
			calcPath(numDiv);
		}
		Dot middle= bezExplicit(0.5);
		Arc a = new Arc(p1, middle, p4);
		a.print();
		if(withinError(a, Globals.error)){
			System.out.println("within error, adding arc");
			arr.add(a);
		}else{
			ArrayList<Curve> temp= new ArrayList<Curve>();
			bezDiv(numDiv/2, temp);
			temp.get(0).approx(arr);
			temp.get(1).approx(arr);
		}
	}
	public boolean withinError(Arc a, double error){

		
		for(Dot d: path){
			double diff= d.minus(a.center).mag()-a.radius;
			//a.center.print();
			System.out.println("diff "+ diff);
			if(Math.abs(diff)>error){
				return false;
			}
		}
		return true;
	}
	public static void main(String[] args){
		Curve c=new Curve(20, 260, 50, 10, 250, 30, 300, 200);
		
		ArrayList<Arc> arr= new ArrayList<Arc>();
		Dot middle= c.bezExplicit(0.5);
		Arc a = new Arc(c.p1.dup(), middle, c.p4.dup());
		a.print();
/*
		if(c.path==null){
			c.calcPath(10);
		}*/
		
		c.approx(arr);
		
	}

}
