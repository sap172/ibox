package edu.csupomona.cs585.ibox;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.api.services.drive.model.File;
import com.google.common.io.Files;

import edu.csupomona.cs585.ibox.sync.GoogleDriveFileSyncManager;
import edu.csupomona.cs585.ibox.sync.GoogleDriveServiceProvider;


public class AppIntegrationTest{

	static java.io.File tempDirectory;
	static TestRunnableClass runnable; 
	static GoogleDriveFileSyncManager fsManager;
	
	@BeforeClass
	public static void init(){

		//create a directory to watch
		tempDirectory = Files.createTempDir();
    		
		System.out.println("Directory: " + tempDirectory.getAbsolutePath());
		
		try{
			//create a separate thread watching the temp dir
			fsManager = new GoogleDriveFileSyncManager(
	        		GoogleDriveServiceProvider.get().getGoogleDriveClient());
			WatchDir dir = new WatchDir(Paths.get(tempDirectory.getAbsolutePath()), fsManager);
			runnable = new TestRunnableClass(dir);  
			
			Thread watchThread = new Thread(runnable);
			watchThread.start();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void finish(){
		
		//kill thread watching directory
		runnable.terminate();
	}
	
	@Test
	public void testAdd(){
		
		try{
			//add random file to directory
			java.io.File tempFile = java.io.File.createTempFile("junk", Long.toString(System.nanoTime()), tempDirectory);
			
			Assert.assertTrue(tempFile.exists());
			
			sleep(5);
			
			//get the list of files in google drive
			Assert.assertTrue(fileExistsInDrive(tempFile.getName()));
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testDelete(){
		
		try{
			
			//add random file to directory
			java.io.File tempFile = java.io.File.createTempFile("junk", Long.toString(System.nanoTime()), tempDirectory);
			
			Assert.assertTrue(tempFile.exists());
			
			sleep(5);

			//save name
			String filename = tempFile.getName();
			
			//check if file exists in drive
			Assert.assertTrue(fileExistsInDrive(filename));
			
			//delete file from dir
			tempFile.delete();
			
			sleep(5);
			
			//check if file no longer exists in drive
			Assert.assertFalse(fileExistsInDrive(filename));
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void testUpdate(){
		
		try{
			
			//add random file to directory
			java.io.File tempFile = java.io.File.createTempFile("junk", Long.toString(System.nanoTime()), tempDirectory);
			
			Assert.assertTrue(tempFile.exists());
			
			sleep(5);

			//save name
			String filename = tempFile.getName();
			
			//check if file exists in drive
			Assert.assertTrue(fileExistsInDrive(filename));
			
			//move file to different name
			java.io.File renamedFile = new java.io.File(tempDirectory.getAbsolutePath() + "/" + "junk2");
			tempFile.renameTo(renamedFile);
			
			sleep(5);
			
			//check if old file doesn't exist in drive
			Assert.assertFalse(fileExistsInDrive(filename));
			
			//check if new file exists in drive
			Assert.assertTrue(fileExistsInDrive(renamedFile.getName()));
			
			//delete renamed file
			renamedFile.delete();
			
			Assert.assertFalse(renamedFile.exists());
			
			sleep(10);
			
			//check if deleted
			Assert.assertFalse(fileExistsInDrive(renamedFile.getName()));

		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static boolean fileExistsInDrive(String filename) throws IOException{
		List<File> fileList = fsManager.getFileList().execute().getItems();
		boolean fileExists = false;
		
		for(int i = 0; i < fileList.size(); i++){
			//the file exists in drive
			if(fileList.get(i).getTitle().equals(filename)){
				fileExists = true;
				break;
			}
		}
		
		return fileExists;
	}
	
	public static void sleep(int seconds){
		try{
			//sleep for seconds
			Thread.sleep(1000 * seconds);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
}

