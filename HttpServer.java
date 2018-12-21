import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Scanner;
import java.io.*;
import java.nio.file.*;



public class HttpServer {

    public static void main(String[] args) throws Throwable {
        ServerSocket ss = new ServerSocket(8080);
        while (true) {
            Socket s = ss.accept();
            System.err.println("Client accepted");
            new Thread(new SocketProcessor(s)).start();
        }
    }

    private static class SocketProcessor implements Runnable {

        private Socket s;
        private InputStream is;
        private OutputStream os;

        private SocketProcessor(Socket s) throws Throwable {
            this.s = s;
            this.is = s.getInputStream();
            this.os = s.getOutputStream();
        }

        public void run() {
            
            
            try {
                
               // FileReader fr = new FileReader("C:\\Users\\alexey\\Desktop\\http server java\\1.html");
           // Scanner scan = new Scanner(fr);
           
               String InputString = readInputHeaders();
              
               String[] requestParam = InputString.split(" ");
                String path = requestParam[1];
                 System.out.println(path);
                 System.out.println(path.equals("/"));
                 if (path.equals("/")) 
                  path = path + "index.html";
                String[] pathSlash = path.split("/");
                String p = pathSlash[1];
                
               
                String PathP="";
                if(!p.contains("."))
                        PathP = p.concat(".html");
                    else PathP = p;

                File f = new File(PathP);
                if (!f.exists()) {
                    writeResponse("<html><head></head><body><h1>Error 404 file not found</h1></body></html>");
               }else{
                            FileReader fr = new FileReader(f);
                            Scanner scan = new Scanner(fr).useDelimiter("\\A");
                                String result = scan.hasNext() ? scan.next() : "";
                            writeResponse(result);
            }
            } catch (Throwable t) {
                /*do nothing*/
            } finally {
                try {
                    s.close();
                } catch (Throwable t) {
                    /*do nothing*/
                }
            }
            System.err.println("Client processing finished");
        }

        private void writeResponse(String s) throws Throwable {
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Server: MyServer\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + s.length() + "\r\n" +
                    "Connection: close\r\n\r\n";
            String result = response + s;
            os.write(result.getBytes());
            os.flush();
        }

        private String readInputHeaders() throws Throwable {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String summ="";
            while(true) {
                String s = br.readLine();
                summ = summ.concat(s);
                if(s == null || s.trim().length() == 0) {
                    break;
                }
            }
            return summ;
        }
    }
}