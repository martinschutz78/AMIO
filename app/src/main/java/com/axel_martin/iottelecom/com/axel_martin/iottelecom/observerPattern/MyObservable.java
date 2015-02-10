package com.axel_martin.iottelecom.com.axel_martin.iottelecom.observerPattern;

public interface MyObservable {
	public void addObserver(MyObserver obs);
	public void updateObserver();
}
