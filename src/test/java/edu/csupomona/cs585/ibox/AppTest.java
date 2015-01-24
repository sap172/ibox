package edu.csupomona.cs585.ibox;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import edu.csupomona.cs585.ibox.sync.FileSyncManager;
import edu.csupomona.cs585.ibox.sync.GoogleDriveFileSyncManager;

/**
 * Placeholder for unit test
 */
public class AppTest extends TestCase {
	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
    	try{
    		File filename = File.createTempFile("junk", null);
    		String dirPath = filename.getParent() + "/";
    		String newFile = "junk2";
    		FileSyncManager fsManager = mock(GoogleDriveFileSyncManager.class);
    		WatchDir dir = new WatchDir(Paths.get(dirPath), fsManager);
    		
    		//test WatchDir processEvents is working
    		TestRunnableClass runnable = new TestRunnableClass(dir);  
    		Thread t1 = new Thread(runnable);
    	   		
    		t1.start();
    		
    		//delete file if it exists
    		try{
        		File file = new File(dirPath + newFile);
        		file.delete();

        		if(file.exists()){
	        		//test googledrivefilesyncmanager deleteFile/updateFile is called
        			verify(fsManager, atLeastOnce()).updateFile(new File(dirPath + newFile));
	    			verify(fsManager, atLeastOnce()).deleteFile(new File(dirPath + newFile));
        		}
        	}catch(Exception e){
        		e.printStackTrace();
        	}
    		
    		//add file to directory
    		PrintWriter writer = new PrintWriter(dirPath + newFile, "UTF-8");
    		writer.println("test" + System.currentTimeMillis());
    		writer.close();
        
    		try{
    			//wait 1 seconds
    			Thread.sleep(1000);
    		}catch(InterruptedException e){
    			e.printStackTrace();
    		}
    		    		
			//test googledrivefilesyncmanager addFile is called
			verify(fsManager, atLeastOnce()).addFile(new File(dirPath + newFile));
			
			runnable.terminate();
			
    	}
    	catch(IOException e){
    		e.printStackTrace();
    	}
    }
}
