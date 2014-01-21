
import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.StringTokenizer;


class Handler extends Thread{
	//creation of handler specific variables. These are created again for each handler
	//instance
	
	private Socket socket = null; 
	int bufferSize = 512000; 
	private BufferedReader in;
	// USER_AGENT is set as browser, works in chrome as well as firefox
	private final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) ";	
	private boolean debug = true; 
	 
	
	public Handler(Socket socket){    
		super("Handler");
		this.socket = socket;
		if (debug) System.out.println("Instance of handler created");
		// creates a instanc of handler then sets the current socket object
		
	}
	
	public String[] getRequestsInfo(BufferedReader in){
		this.in = in;  

		//this method takes the request, pases it into a small string[] and returns it 
		
		String[] request = new String[3];
		try {
			
		String inward = null,type = null, url = null, userA = null;
		while ((inward = in.readLine()) != null){
			
				//System.out.println(inward); 
				StringTokenizer tok = new StringTokenizer(inward);
				if (debug) System.out.println("tok is " + inward); 
				while (tok.hasMoreTokens()){
					tok.nextToken();				
					 
				}
				
			//System.out.println("Still hits here!"); 
			String[] tokens = inward.split(" "); 
			type = tokens[0]; 
			url = tokens[1];			
			
		if (debug) System.out.println("userA: " + userA); 
			
		if (debug) System.out.println("A " + type + " request for :" + url);
			
			
			request[0] = type; 
			request[1] = url; 
			
			return request;
			
		}	
			 // counter used to make sure only used once
		
			} catch(Exception e )  {
				 e.printStackTrace(); ; 
			
			}
			
			//System.out.println("finished!");
			return request;
		
	}
	
	public void run() {
	if (debug) System.out.println("Thread starting!"); 
	String[] req = new String[3]; 
	
	try {
		
		if (debug) System.out.println("Creation of buffers");  
		
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));	        
        
        req = getRequestsInfo(in); 
        if (debug) System.out.println(" Making a " + req[0] + " to : " + req[1]); 
		
        String tempString = req[1];
        //determins if request is a request to retreive image
        if ((tempString.endsWith(".jpg") || 
        		(tempString.endsWith(".png")) ||
        		(tempString.endsWith(".jpeg")) ||
        		(tempString.endsWith(".bmp")) ||
        		(tempString.endsWith(".ico"))))
        		{
        	//need gif support 
        	imageDownload(req);  
        			
        }
        else {
        	sendReq(req); 
        }
 
	}	
	
	catch (Exception e) 
		{
			System.out.println("error: " +  e);
		}

	}
	
	private void imageDownload(String[] req){
	//images need to be downloaded then loaded back into the html
		// image streams can be used, but performance takes a hit when many images are needed
		
	String imgURLString = req[1]; 	
	String filePathString = "C:\\tempPics";
	

	// random function that means roughly every third image is replaced with another image 
	Random rand = new Random();int  
	rando = rand.nextInt(10) + 1;
	 if (rando < 3)
	 {
	imageInjector(); 
	return; 
	 }
	
    try {
    	
    	if (debug) System.out.println("About to test path maker");
    	File f = new File(filePathString);
    	
    	// checks to see if required folder is present. If first time run, creates folder
    	boolean fileCheck = (f.exists());
    	if (!fileCheck){ 
    	boolean success = (new File(filePathString)).mkdirs();
    	if (!success) {
    	System.out.println("Issue creating folder");
    	}
    	if (success) System.out.println("folder made!");
    	}
    	
    	f.setWritable(true); 
    	f.setReadable(true); 
    	// set windows preferences of folder.
    	
        URL url = new URL(imgURLString);

        InputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[512000];
        int n = 0; 
        while ((n =  in.read()) != -1) {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] response1 = out.toByteArray();
        // file name generater for stored images 
        String imageString = (filePathString + "\\" 
        		+ imgURLString.substring(imgURLString.length()-8)); 
        
        FileOutputStream fos = new FileOutputStream(imageString);
        fos.write(response1);
        fos.close();

        
        BufferedOutputStream outImg = new BufferedOutputStream(socket.getOutputStream() );
        // f is the file to be sent to the client.
        BufferedInputStream reader = new BufferedInputStream( new FileInputStream(imageString) );
        // send OK headers and content length using f.length()
        byte[] buffer = new byte[512000];
        int bytesRead;
        while ( (bytesRead = reader.read(buffer)) != -1 ) {
     	outImg.write( buffer, 0, bytesRead );
        }
        	
        	reader.close();
        	out.flush();
        	out.close();
        
    	} catch (MalformedURLException e) {
    	       e.printStackTrace();
    	    } catch (FileNotFoundException e) {
    	       e.printStackTrace();
    	    } catch (IOException e) {
    	       e.printStackTrace();
    	    }
    		
    		}
 	
	public void sendReq(String[] req){
		// because this server acts as a middle man between browser and url endpoint
		// a request method specifically for GET or POST are not required. 
		// any encryption on parameters are handled by the browser. 
		// and end point		
		 
		try { 
			
		String destination, type, userA; 
		type = req[0];  
	    destination = req[1];
	    
	    
	    URL urlObj = new URL(destination); 
	    HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
	    
			
	    con.setRequestMethod(type);
	 
			//add request header
			con.setRequestProperty("User-Agent", USER_AGENT);
	 
			int responseCode = con.getResponseCode();
			System.out.println("Response code: " + responseCode +  " from" + destination); 
			
	 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
	 
			
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			String StringResponse = response.toString(); 
			out.write(StringResponse);
			out.close();
			
			
		}
			catch (Exception E) {
				
			System.out.println("Error in GET: " + E); 
			}
		}
	
	private void imageInjector(){
		// this swaps out an image that would normally be loaded into the browser 
		// with an image in the C:\tempInj
		//fileno is any given number between 0 and the number of files in the tempinj folder. 
		try {
			
		//int i is equal to the number of files available to be injected
		String path ="C:\\tempInj\\";
		
		Random generator = new Random();
		int i = generator.nextInt(3) + 1;  
		
		String file = Integer.toString(i);
		
		String filename = path + file + ".jpg"; 
				
		BufferedOutputStream outImg = new BufferedOutputStream(socket.getOutputStream() );
        // f is the file to be sent to the client.
        BufferedInputStream reader = new BufferedInputStream(new FileInputStream(filename));
        // send OK headers and content length using f.length()
        byte[] buffer = new byte[512000];
        int bytesRead;
        while ( (bytesRead = reader.read(buffer)) != -1 ) {
     	
        	outImg.write( buffer, 0, bytesRead );
        }
        	
        	reader.close();
        	outImg.close();
		
		
	}
	 catch (Exception E) {
		
	System.out.println("Error : " + E);	
	}
	}
	
}