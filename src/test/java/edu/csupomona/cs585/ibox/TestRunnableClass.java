package edu.csupomona.cs585.ibox;


class TestRunnableClass implements Runnable{

	boolean running = true;
	WatchDir globalDir;
	
	public TestRunnableClass(WatchDir dir){
		globalDir = dir;
	}
	
	public void terminate(){
		running = false;
	}
    public void run() {
    	while(running){
    		globalDir.processEvents();
    	}
    }
}