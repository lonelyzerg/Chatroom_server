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
	private ArrayList<HandlingThread> thread_list;
	private HashMap<String, InetAddress> user_list;
	private boolean stop = false;
	private static final int port = 999;
	private static final String register_success = "[REGI]Success\n";
	private static final String register_failure = "[REGI]Failure\n";
	private static final String register_prefix = "[REGI]";
	private static final String exit_code = "[EXIT]\n";
	private static final String chat_prefix = "[CHAT]";
	private static final String suffix = "\n";
	private static final String quit_command = "\\q";
	private static final String charset_utf_8 = "UTF-8";

	public static void main(String[] args) throws IOException {

		ChatroomServer s = new ChatroomServer();
		s.start(port);
	}

	public void start(int port) throws IOException {
		message_list = new ArrayList<Message>();
		user_list = new HashMap<String, InetAddress>();
		serverSocket = new ServerSocket(port);
		thread_list = new ArrayList<HandlingThread>();
		while (!stop) {
			Socket server = serverSocket.accept();

			HandlingThread h = new HandlingThread(server);
			h.start();
			thread_list.add(h);

		}
	}

	public class HandlingThread extends Thread {
		private DataOutputStream out;
		private BufferedReader in;
		private Socket server;
		private boolean stop = false;
		private String username;

		public HandlingThread(Socket s) {
			try {
				server = s;
				out = new DataOutputStream(server.getOutputStream());
				in = new BufferedReader(new InputStreamReader(server.getInputStream(), charset_utf_8));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void send(String message) {
			try {
				out.write(message.getBytes(charset_utf_8));
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				InetAddress addr = server.getInetAddress();
				System.err.println("Connection from " + addr.getHostName());
				// server.setSoTimeout(10000);
				String reg_message = in.readLine();
				if (!reg_message.substring(0, 6).equals(register_prefix)) {
					out.write(register_failure.getBytes(charset_utf_8));
					throw new ServerException("not registering");
				}
				username = reg_message.substring(6);
				if (user_list.containsKey(username)) {
					out.write(register_failure.getBytes(charset_utf_8));
					out.flush();
					throw new ServerException("user exist!");
				}
				user_list.put(username, addr);
				System.err.println("user " + username + " registered.");
				out.write(register_success.getBytes(charset_utf_8));
				out.flush();

				while (!stop) {
					String raw_message = in.readLine();
					System.err.println("message: " + raw_message);
					if (raw_message.equals(quit_command)) {
						stop = true;
						sleep(2000);
						break;
					}
					String message = chat_prefix + "[" + username + "]" + raw_message.substring(6) + suffix;
					for (HandlingThread t : thread_list) {
						if (t.getId() != this.getId()) {
							t.send(message);
						}
					}
				}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ServerException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				try {
					for (int i = 0; i < thread_list.size(); i++) {
						HandlingThread t = thread_list.get(i);
						if (t.getId() == this.getId()) {
							thread_list.remove(i);
						}
					}
					user_list.remove(username);
					in.close();
					out.close();
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}
}
