package com.dell.prototype.apm.instrumentation;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class ChrResponseWrapper extends HttpServletResponseWrapper{
	
	CharArrayWriter output;
	public HttpServletResponse response;
	
	public ChrResponseWrapper(HttpServletResponse response) {
		super(response);
		this.response = response;
		this.output = new CharArrayWriter();
		
	}
	
	public PrintWriter getWriter(){
		return new PrintWriter(this.output);
	}
	
	
	
	public PrintWriter getSuperWriter() {
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return writer;
	} 
	public String toString () {
		return output.toString();
	}	
}
