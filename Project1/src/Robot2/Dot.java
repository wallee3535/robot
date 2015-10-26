package Robot2;

public class Dot {
	double x;
	double y;
	double z;
	public Dot(double x, double y){
		 this(x, y, 0);
	}
	public Dot(double x, double y, double z){
		 this.x=x;
		 this.y=y;
		 this.z=z;
	}
	public Dot dup(){
		return new Dot(x,y);
	}
	public static Dot circumcenter(Dot a, Dot b, Dot c){
		double d= (a.x*(b.y-c.y) + b.x*(c.y-a.y) + c.x*(a.y-b.y));
		double ux = ((a.x*a.x +a.y*a.y)*(b.y-c.y) + (b.x*b.x+b.y*b.y)*(c.y-a.y) + (c.x*c.x+c.y*c.y)*(a.y-b.y))/d;
		double uy = ((a.x*a.x +a.y*a.y)*(c.x-b.x) + (b.x*b.x+b.y*b.y)*(a.x-c.x) + (c.x*c.x+c.y*c.y)*(b.x-a.x))/d;
		return new Dot(ux,uy);	
	}
	public Dot plus(Dot b){
		return new Dot(x+b.x, y+b.y, z+b.z);
	}
	
	public Dot minus(Dot b){
		return new Dot(x-b.x, y-b.y, z-b.z);
	}
	
	public Dot times(double m){
		return new Dot(x*m, y*m, z*m);
	}
	
	public void print(){
		System.out.println("x= "+x+", y= "+y+",z= "+z);
	}
	
	public boolean crossSign(Dot b){
		return (x*b.y-b.x*y>0);
	}
	
	public double dotProd(Dot b){
		return x*b.x+y*b.y+z*b.z;
	}
	
	public double mag(){
		return Math.sqrt(x*x+y*y+z*z);
	}
	
	public double shortAngle(Dot b){
		return Math.acos(this.dotProd(b)/this.mag()/b.mag());
	}
	/** returns the counterclockwise/clockwise rotation angle from this to dot b
	 */
	public double rotationTo(Dot b, boolean isCounterClockwise){
		boolean crossSign=this.crossSign(b);
		if(crossSign==isCounterClockwise){
			return this.shortAngle(b);
		}else{
			return 2*Math.PI-this.shortAngle(b);
		}
		//counterclockwise:
		/*
		if(this.crossSign(b)==true){
			return this.shortAngle(b);
		}else{
			return 2*Math.PI-this.shortAngle(b);
		}*/
	}
	
	public static void main(String[] args){
		Dot a= new Dot(1,1);
		Dot b= new Dot(-1,1);

	}

}
