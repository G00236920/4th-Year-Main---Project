package ie.gmit.sw;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.google.gson.Gson;

public class ServerConnection implements Runnable {

	private Socket csocket;
	private boolean isFound;

	ServerConnection(Socket csocket) {
		this.csocket = csocket;
	}

	public void run() {
		
		try {

				switch(this.csocket.getLocalPort()) {
				case 5000:
					loginAttempt();
					break;
				case 5001:
					createAttempt();
					break;
					default:
				}


		} catch (IOException e) {
			System.out.println(e);
		}
	}

	private void createAttempt() {
		//Code to verify account creation
		
		try {
			DatabaseConnection db = new VerifyLogin();
			
	        InputStream is = csocket.getInputStream();
	        OutputStream os = csocket.getOutputStream();
	        
	        //Receive Login info from unity
	        String received = receiveMessage(is);
	        
	        boolean nameTaken = db.findUserName(received);
	        
	        //Send a Reply to the Unity Client
	        respond(os, !nameTaken);
	        
	        String message = receiveMessage(is);
	        
	        db.add(message);
			
	        //Close Streams and Sockets
			is.close();
			os.close();
			csocket.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	private boolean verifyUser(User user) {

		DatabaseConnection db = new VerifyLogin();
        
		boolean verified = db.verifyPassword(user.getUsername(), user.getPassword());
		
		if(verified) {
			return true;
		}
		else {
			return false;
		}
		
	}

	public User getUserFromJson(String response) {

		Gson gson = new Gson();
		User user = gson.fromJson(response, User.class);

		return user;

	}

	public void respond(OutputStream os, boolean b) {
		
        byte[] toSendBytes = new byte[]{(byte) (b?1:0)};
        int toSendLen = toSendBytes.length;
        byte[] toSendLenBytes = new byte[4];
        
        toSendLenBytes[0] = (byte)(toSendLen & 0xff);
        toSendLenBytes[1] = (byte)((toSendLen >> 8) & 0xff);
        toSendLenBytes[2] = (byte)((toSendLen >> 16) & 0xff);
        toSendLenBytes[3] = (byte)((toSendLen >> 24) & 0xff);
        
        try {
			
        	os.write(toSendLenBytes);
	        os.write(toSendBytes);
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
        
	}
	
	public String receiveMessage(InputStream is) {
		
		byte[] receivedBytes = null;
		int len = 0;
		
        try {
            byte[] lenBytes = new byte[4];
			is.read(lenBytes, 0, 4);
			len = (((lenBytes[3] & 0xff) << 24) 
				| ((lenBytes[2] & 0xff) << 16) 
				| ((lenBytes[1] & 0xff) << 8) 
				| (lenBytes[0] & 0xff));
	        receivedBytes = new byte[len];
	        is.read(receivedBytes, 0, len);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return new String(receivedBytes, 0, len);
        
	}
	
	public void loginAttempt() throws IOException {
		
        InputStream is = csocket.getInputStream();
        OutputStream os = csocket.getOutputStream();
        
        //Receive Login info from unity
        String received = receiveMessage(is);
        //Convert login info to a User object
        User user = getUserFromJson(received);
        
        this.isFound = verifyUser(user);
        
        //Output a line to the console
        System.out.println(user.getUsername()+" Has Logged in");
        
        //Send a Reply to the Unity Client
        respond(os, isFound);
		
        //Close Streams and Sockets
		is.close();
		os.close();
		csocket.close();
	}

}
