package Robot2;

public class Dot {
	double x;
	double y;
	public Dot(double x, double y){
		 this.x=x;
		 this.y=y;
	}
	public Dot dup(){
		return new Dot(x,y);
	}
	public Dot plus(Dot b){
		return new Dot(x+b.x, y+b.y);
	}
	public Dot minus(Dot b){
		return new Dot(x-b.x, y-b.y);
	}
	public Dot times(double m){
		return new Dot(x*m, y*m);
	}
	public void print(Dot a){
		System.out.println("x= "+x+", y= "+y);
	}
	public boolean crossSign(Dot b){
		if(x*b.y-b.x*y>0)return true;
		return false;
	}
	public double dotProd(Dot b){
		return x*b.x+y*b.y;
	}
	public double mag(){
		return Math.sqrt(x*x+y*y);
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
	
	public void print(){
		System.out.println("x= " +this.x+" y= "+this.y);
	}
	
	
	public static void main(String[] args){
		Dot a= new Dot(1,1);
		Dot b= new Dot(-1,1);
		System.out.println(b.rotationTo(a));
	}

}
