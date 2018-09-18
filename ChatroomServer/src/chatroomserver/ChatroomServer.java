package chatroomserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

import data.Message;
import exceptions.ServerException;

public class ChatroomServer {
	private ServerSocket serverSocket;
	private ArrayList<Message> message_list;
	private HashMap<String, InetAddress> user_list;
	private DataOutputStream out;
	private BufferedReader in;
	private boolean stop = false;
	private static final int port = 999;
	private static final String register_success = "[REGI]Success\n";
	private static final String register_failure = "[REGI]Failure\n";
	private static final String register_prefix = "[REGI]";
	private static final String exit_code= "[EXIT]\n";
	private static final String chat_prefix = "[CHAT]";
	private static final String suffix = "\n";
	private static final String quit_command = "\\q";

	public static void main(String[] args) throws IOException {

		ChatroomServer s = new ChatroomServer();
		s.start(port);
	}

	public void start(int port) throws IOException {
		message_list = new ArrayList<Message>();
		user_list = new HashMap<String, InetAddress>();
		serverSocket = new ServerSocket(port);
		Socket server = serverSocket.accept();
		
		
		HandlingThread h = new HandlingThread(server);
		h.start();
	}

	public class HandlingThread extends Thread {
		Socket server;

		public HandlingThread(Socket s) {
			server = s;
		}

		public void run() {
			try {
				InetAddress addr = server.getInetAddress();
				System.err.println("Connection from " + addr.getHostName());
				server.setSoTimeout(10000);
				out = new DataOutputStream(server.getOutputStream());
				in = new BufferedReader(new InputStreamReader(server.getInputStream(), "UTF-8"));
				String message = in.readLine();
				if (!message.substring(1, 5).equals(register_prefix)) {
					throw new ServerException("not registering");
				}
				String name = message.substring(7, message.indexOf("]", 6));
				if (user_list.containsKey(name)) {
					out.write(register_failure.getBytes("UTF-8"));
					out.flush();
					throw new ServerException("user exist!");
				}
				user_list.put(name, addr);
				System.err.println("user " + name + "registered.");
				
				while(!stop) {
					Thread.sleep(1000);
				}
				server.close();
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ServerException e) {
				e.printStackTrace();
			}
			finally {
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
