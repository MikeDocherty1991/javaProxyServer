import java.net.*;
import java.io.*;

public class myProxy {

	int proxyPort;
	ServerSocket proxySock; // Socked listened to by server
	private int fileno = 0;
	Socket myClient;
	Socket myServer;

	public static void main(String[] args) throws IOException {

		ServerSocket myServerSocket = null;
		boolean on = true;

		int defPort = 50003;
		imageInjSetup();
		try {

			defPort = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.out.println("No system def used port :" + defPort);
		}

		try {

			myServerSocket = new ServerSocket(defPort);
			System.out.println("Server socket running on machine on port :"
					+ defPort);

		} catch (Exception e) {
			System.out.println("Error in myServerSocket.accept() : " + e);

		}
		int conStart = 0;
		while (on) {
			if (conStart == 0)
				System.out.println("Handler created!");
			conStart++;

			new Handler(myServerSocket.accept()).start();
		}

		System.out
				.println("Closing server socket, probably shouldnt happen... ");
		myServerSocket.close();
	}

	private static void imageInjSetup() {

		String filePathString = "C:\\tempInj";

		try {

			File f = new File(filePathString);

			boolean fileCheck = (f.exists());
			if (!fileCheck) {
				boolean success = (new File(filePathString)).mkdirs();
				if (!success) {
					System.out.println("Issue creating folder");
				}
				if (success)
					System.out.println("folder made!");
			}

			f.setWritable(true);
			f.setReadable(true);
			String[] imgString = new String[3];

			// add url to img to use in injection
			imgString[0] = "";
			imgString[1] = "";
			imgString[2] = ""; 
			
			
			for (int i = 0; i < imgString.length; i++) {

				URL url = new URL(imgString[i]);

				InputStream in = new BufferedInputStream(url.openStream());
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buf = new byte[512000];
				int n = 0;
				while ((n = in.read(buf)) != -1) {
					out.write(buf, 0, n);
				}
				out.close();
				in.close();
				byte[] response1 = out.toByteArray();
				// file name generater for stored images
				String imageString = (filePathString + "\\" + i + ".jpg");

				FileOutputStream fos = new FileOutputStream(imageString);
				fos.write(response1);
				fos.close();

			}
		} catch (Exception E) {
			System.out.println("Error E " + E);
		}

	}

}
