package StockFinalProject;

import java.io.IOException;
import java.io.BufferedReader;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Vector;
import java.io.InputStreamReader;

public class SocketServer implements Runnable
{
	   Socket csocket;
	   String ipString;
	   char threadType;

	   static Vector<String> vec = new Vector<String>(5);
	   
	   static final String newline = "\n";
	   static int first_time = 1;
	   
	   static int port_num = 3333;
	   
	   static int numOfConnections = 0;
	   static int numOfMessages = 0;
	   static int max_connections = 5;
	   static int numOfTransactions = 0; 

	   SocketServer(Socket csocket, String ip)
	   {
	      this.csocket  = csocket;
	      this.ipString = ip;
	   } 

	   public static void runSocketServer()   // throws Exception
	   {
	     boolean sessionDone = false;
	  
	     ServerSocket ssock = null;
	   
	     try
	     {
		   ssock = new ServerSocket(port_num);
	     }
	     catch (BindException e)
	     {
		    e.printStackTrace();
	     }
	     catch (IOException e)
	     {
		    e.printStackTrace();
	     }
	 
	     // update the status text area to show progress of program
	     try 
	     {
		     InetAddress ipAddress = InetAddress.getLocalHost();
		     SocketServerGUI.textArea.append("IP Address: " + ipAddress.getHostAddress() + newline);
	     }
	     catch (UnknownHostException e1)
	     {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
	     }
	 
	     SocketServerGUI.textArea.append("Listening on Port: " + port_num + newline);
	     SocketServerGUI.textArea.setCaretPosition(SocketServerGUI.textArea.getDocument().getLength());
	     SocketServerGUI.textArea.repaint();
	 
	     sessionDone = false;
	     while (sessionDone == false)
	     {
	        Socket sock = null;
		    try
		    {
		    // blocking system call
			   sock = ssock.accept();
		    }
		    catch (IOException e)
		    {
			   e.printStackTrace();
		    }
		 
		    // update the status text area to show progress of program
	        SocketServerGUI.textArea.append("Client Connected : " + sock.getInetAddress() + newline);
	        SocketServerGUI.textArea.setCaretPosition(SocketServerGUI.textArea.getDocument().getLength());
	        SocketServerGUI.textArea.repaint();
	        
	        new Thread(new SocketServer(sock, sock.getInetAddress().toString())).start();
	     }
	 
	     try 
	     {
		    ssock.close();
	     }
	     catch (IOException e) 
	     {
		    e.printStackTrace();
	     }
	}	  

	// This is the thread code that ALL clients will run()
	public void run()
	{
	   try
	   {
		  boolean session_done = false; 
	      long threadId;
	      String clientString;
	      String keyString = "";
	    
	      threadId = Thread.currentThread().getId();
	      
	      numOfConnections++;
	      
	      SocketServerGUI.textArea.append("Number of Connections = " + numOfConnections + newline);
	      SocketServerGUI.textArea.setCaretPosition(SocketServerGUI.textArea.getDocument().getLength());
	      SocketServerGUI.textArea.repaint();
	      
	      keyString = ipString + ":" + threadId;
	      
	      if (vec.contains(keyString) == false)
	        {
	    	    int counter = 0;
	        	vec.addElement(keyString);
	        	
	        	SocketServerGUI.textArea_2.setText("");
	        	Enumeration<String> en = vec.elements();
	        	while (en.hasMoreElements())
	        	{
	        		SocketServerGUI.textArea_2.append(en.nextElement() + " || ");
	        		
	        		if (++counter >= 6)
	        		{
	        			SocketServerGUI.textArea_2.append("\r\n");
	        			counter = 0;
	        		}
	        	}

  	            SocketServerGUI.textArea_2.repaint();
	        }
	      
	      BufferedReader rstream = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
	       
	      while (session_done == false)
	      {
	       	if (rstream.ready())   // check for any data messages
	       	{
	              clientString = rstream.readLine();
	              
	              // update the status text area to show progress of program
	   	           SocketServerGUI.textArea.append("RECV : " + clientString + newline);
	     	       SocketServerGUI.textArea.setCaretPosition(SocketServerGUI.textArea.getDocument().getLength());
	     	       SocketServerGUI.textArea.repaint();
	     	       // update the status text area to show progress of program
	     	       SocketServerGUI.textArea.append("RLEN : " + clientString.length() + newline);
	     	       SocketServerGUI.textArea.setCaretPosition(SocketServerGUI.textArea.getDocument().getLength());
	     	       SocketServerGUI.textArea.repaint();
	              
	              if (clientString.length() > 128)
	              {
	           	   session_done = true;
	           	   continue;
	              }

	              if (clientString.contains("quit"))
	              {
	                 session_done = true;
	              }
	              else if (clientString.contains("QUIT"))
	              {
	                 session_done = true;
	              }
	              else if (clientString.contains("Quit"))
	              {
	                 session_done = true;
	              }
	       	   }
	         			    		        	
	           Thread.sleep(500);
	           
	        }    // end while loop
	
            keyString = ipString + ":" + threadId;
	      
	        if (vec.contains(keyString) == true)
	        {
	        	int counter = 0;
	        	vec.removeElement(keyString);
	        	
	        	SocketServerGUI.textArea_2.setText("");
	        	Enumeration<String> en = vec.elements();
	        	while (en.hasMoreElements())
	        	{        		     		
                    SocketServerGUI.textArea_2.append(en.nextElement() + " || ");
	        		
	        		if (++counter >= 6)
	        		{
	        			SocketServerGUI.textArea_2.append("\r\n");
	        			counter = 0;
	        		}
	        	}

  	            SocketServerGUI.textArea_2.repaint();
	        }
	      
	        numOfConnections--;

	        // close client socket
	        csocket.close();
	       
	        // update the status text area to show progress of program
		     SocketServerGUI.textArea.append("Child Thread: " + threadId + " : is Exiting!!!" + newline);
		     SocketServerGUI.textArea.append("Number of Connections = " + numOfConnections);
		     SocketServerGUI.textArea.setCaretPosition(SocketServerGUI.textArea.getDocument().getLength());
		     SocketServerGUI.textArea.repaint();
		     
	     } // end try  
	 
	     catch (SocketException e)
	     {
		  // update the status text area to show progress of program
	      SocketServerGUI.textArea.append("ERROR: Socket Exception!" + newline);
	      SocketServerGUI.textArea.setCaretPosition(SocketServerGUI.textArea.getDocument().getLength());
	      SocketServerGUI.textArea.repaint();
	     }
	     catch (InterruptedException e)
	     {
		  // update the status text area to show progress of program
	      SocketServerGUI.textArea.append("ERROR: Interrupted Exception!" + newline);
	      SocketServerGUI.textArea.setCaretPosition(SocketServerGUI.textArea.getDocument().getLength());
	      SocketServerGUI.textArea.repaint();
	     }
	     catch (UnknownHostException e)
	     {
		  // update the status text area to show progress of program
	      SocketServerGUI.textArea.append("ERROR: Unkonw Host Exception" + newline);
	      SocketServerGUI.textArea.setCaretPosition(SocketServerGUI.textArea.getDocument().getLength());
	      SocketServerGUI.textArea.repaint();
	     }
	     catch (IOException e) 
	     {
	     // update the status text area to show progress of program
	      SocketServerGUI.textArea.append("ERROR: IO Exception!" + newline);
	      SocketServerGUI.textArea.setCaretPosition(SocketServerGUI.textArea.getDocument().getLength());
	      SocketServerGUI.textArea.repaint();       
	     }     
	     catch (Exception e)
	     { 
		  numOfConnections--;
		  
		  // update the status text area to show progress of program
	      SocketServerGUI.textArea.append("ERROR: Generic Exception!" + newline);
	      SocketServerGUI.textArea.setCaretPosition(SocketServerGUI.textArea.getDocument().getLength());
	      SocketServerGUI.textArea.repaint(); 
	     }
	   
	  }  // end run thread method
}