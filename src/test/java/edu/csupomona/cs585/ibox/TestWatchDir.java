package edu.csupomona.cs585.ibox;

import java.io.IOException;
import java.nio.file.Path;

import edu.csupomona.cs585.ibox.sync.FileSyncManager;

public class TestWatchDir extends WatchDir{

	private int processInt;
	
	public TestWatchDir(Path dir, FileSyncManager fileSyncManager)
			throws IOException {
		super(dir, fileSyncManager);
		
		processInt = 0;
	}
	
	public void processEvents() {
		processInt++;
	}

	public int getProcessCounter(){
		return processInt;
	}
}
