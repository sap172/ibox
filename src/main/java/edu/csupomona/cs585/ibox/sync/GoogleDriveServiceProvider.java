package edu.csupomona.cs585.ibox.sync;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

public class GoogleDriveServiceProvider {

	private static GoogleDriveServiceProvider INSTANCE;
	private Drive googleDriveClient;

	private GoogleDriveServiceProvider() {
		try {
			initGoogleDriveServices();
		} catch (IOException e) {
			System.out.println("Failed to create the Google drive client. Please check your Internet connection and your Google credentials.");
			e.printStackTrace();
		}
	}

	public static GoogleDriveServiceProvider get() {
		if (INSTANCE == null) {
			INSTANCE = new GoogleDriveServiceProvider();
		}
		return INSTANCE;
	}

	public void initGoogleDriveServices() throws IOException {
		HttpTransport httpTransport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();

		try{
			GoogleCredential credential = new  GoogleCredential.Builder()
		      .setTransport(httpTransport)
		      .setJsonFactory(jsonFactory)
		      .setServiceAccountId("983277890948-7rgrfp7egku9poqk3n51v9n2taq98n82@developer.gserviceaccount.com")
		      .setServiceAccountScopes(Collections.singleton(DriveScopes.DRIVE))
		      .setServiceAccountPrivateKeyFromP12File(new File("ibox-fcb2ce8c8540.p12"))
		      .build();

	        googleDriveClient = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("ibox").build();  
		}catch(GeneralSecurityException e){
			e.printStackTrace();
		}
		       
	}

	public Drive getGoogleDriveClient() {
		return googleDriveClient;
	}
}
