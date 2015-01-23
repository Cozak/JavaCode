/**
 * There are some case for illustrating the rule of wideCast
 */
package mp;

import java.util.ArrayList;


class ABall {
	
}

class BBall extends ABall {
	
}

class CBall extends BBall {
	
}

class BallG<T> {
	public T getVal() {
		return null;
	}
	public void setVal(T t) {
		
	}
}

public class GenerTest {
	public static void funa(BallG<? extends BBall> num) {
		ABall a = new ABall();
		BBall b = new BBall();
		CBall c = new CBall();
		Object o = new Object();
		b = num.getVal(); // o,a,b can but c can't
//		num.setVal(b); // no one can
	}
	public static void funb(BallG<? super BBall> num) {
		ABall a = new ABall();
		BBall b = new BBall();
		CBall c = new CBall();
		Object o = new Object();
		o = num.getVal(); // only o can while others can't
		num.setVal(b); // b,c can but a,o can't
	}
	public static void func(BallG<?> num) {
		ABall a = new ABall();
		BBall b = new BBall();
		CBall c = new CBall();
		Object o = new Object();
		o = num.getVal(); // return an Object type 
		c = (CBall) num.getVal(); // can cast to other types
//		num.setVal(o); // no one can
	}
	public static <T> void fund(BallG<?> num) {
		ArrayList<BallG<T>> arr = new ArrayList<BallG<T>>();
		
		//The function add() don't know what the certain type of num is, but all member of the array must be the same
//		arr.add(num); // illegal, cast T can't catch wideCast here
		arr.add((BallG<T>)num); // unless convert its type (all member has the same type)
	}
	public static void main(String...strings) {
		// ? extends BBall
//		funa(new BallG<ABall>()); // error
		funa(new BallG<BBall>());
		funa(new BallG<CBall>());
		// ? super BBall
		funb(new BallG<ABall>());
		funb(new BallG<BBall>());
//		funb(new BallG<CBall>()); // error
		// ?
		func(new BallG<ABall>());
		func(new BallG<BBall>());
		func(new BallG<CBall>());
		
	}
}
