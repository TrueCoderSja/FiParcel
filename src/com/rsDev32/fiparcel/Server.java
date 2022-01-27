package com.rsDev32.fiparcel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.fileupload.FileItem;

import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.sun.net.httpserver.*;
public class Server {
	private static final int PORT=4321;
	private static String sharingContent="";
	private static String notFoundContent;
	
	public static void start(File[] files) throws IOException {
		HttpServer server=HttpServer.create(new InetSocketAddress(PORT), 0);
		Scanner contentReader=new Scanner(Server.class.getResourceAsStream("/404.html"));
		contentReader.useDelimiter("\\A");
		notFoundContent=contentReader.next();
		contentReader.close();
		 
		server.createContext("/", new SendHandler());
		 
		for(int i=0;i<files.length;i++) {
			File file=files[i];
			server.createContext("/"+i+".file", new FileURLHandler(file));
			sharingContent+="<li>\n<ul>\n";
			sharingContent+="<li>"+file.getName()+"</li>\n";
			DecimalFormat df = new DecimalFormat("#.##");
			df.setRoundingMode(RoundingMode.CEILING);
			sharingContent+="<li>"+df.format(((double)file.length())/(1024.0*1024))+"MB</li>\n";
			sharingContent+="<li><a href=\""+i+".file\" download=\""+file.getName()+"\">Download</a></li>";
			sharingContent+="</ul>\n</li>\n";
		}
		server.start();
	}
	
	public static void start() throws IOException {
		HttpServer server=HttpServer.create(new InetSocketAddress(PORT), 0);
		server.createContext("/", new RecieveHandler());
		server.createContext("/recieve",new FileUploadHandler());
		server.start();
	}
	
	static class RecieveHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange request) throws IOException {
			Scanner responseReader=new Scanner(Server.class.getResourceAsStream("/recieve.html"));
			responseReader.useDelimiter("\\A");
			String response=responseReader.next();
			responseReader.close();
			
			response=response.replace("${DEVICE_NAME}", Main.getUserName());
			
			OutputStream os=request.getResponseBody();
			request.sendResponseHeaders(200, response.length());
			os.write(response.getBytes());
			os.close();
		}
	}
	
	static class FileUploadHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange request) {
			try {
				DiskFileItemFactory ds=new DiskFileItemFactory();
				ServletFileUpload upload=new ServletFileUpload(ds);
				List<FileItem> result = upload.parseRequest(new RequestContext() {

                    @Override
                    public String getCharacterEncoding() {
                        return "UTF-8";
                    }

                    @Override
                    public int getContentLength() {
                        return 0; //tested to work with 0 as return
                    }

                    @Override
                    public String getContentType() {
                        return request.getRequestHeaders().getFirst("Content-type");
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return request.getRequestBody();
                    }

                });
				
				for(FileItem fi : result) {
                    InputStream is=fi.getInputStream();
                    OutputStream fos=new FileOutputStream(Main.getShareFolder()+File.separator+fi.getName());
                    		
                    //Copy file
                    byte[] buffer=new byte[4096];
                    int read;
                    while((read=is.read(buffer))>0) {
                    	fos.write(buffer, 0, read);
                    }
                    fos.close();
                    
                }
                request.sendResponseHeaders(200, 0);
				OutputStream response=request.getResponseBody();
                response.write("Success".getBytes());
                response.close();				
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	static class SendHandler implements HttpHandler {
		
		@Override
		public void handle(HttpExchange request) throws IOException {
			Scanner responseReader=new Scanner(Server.class.getResourceAsStream("/index.html"));
			responseReader.useDelimiter("\\A");
			String response=responseReader.next();
			responseReader.close();
			
			response=response.replace("${DEVICE_NAME}", Main.getUserName());
			response=response.replace("~${SHARING_CONTENT}$~", sharingContent);
			
			OutputStream os=request.getResponseBody();
			request.sendResponseHeaders(200, response.length());
			os.write(response.getBytes());
			os.close();
		}
		
	}
	
	static class FileURLHandler implements HttpHandler {
		private File file;
		private FileURLHandler(File file) {
			this.file=file;
		}

		@Override
		public void handle(HttpExchange request) throws IOException {
			OutputStream os=request.getResponseBody();
			try {
				request.sendResponseHeaders(200, file.length());
				Files.copy(file.toPath(), os);
			} catch(IOException e) {
				request.sendResponseHeaders(404, notFoundContent.length());
				notFoundContent.replace("${DEVICE_NAME}", Main.getUserName());
				notFoundContent.replace("${FILE_NAME}", file.getName());
				os.write(notFoundContent.getBytes());
			}
			os.close();
		}
		
	}
	
}
