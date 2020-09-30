package org.opensolaris.opengrok.web;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import org.mockito.Mockito;

@SuppressWarnings("unchecked")
public class InfoServletTest{
		
	    @Test
	    public void doGetTest() throws Exception {
	        //preparing the mock input
	    	HttpServletRequest request   = mock(HttpServletRequest.class);       
	        HttpServletResponse response = mock(HttpServletResponse.class);    
	        Map<String,String> fileTypes = new HashMap<>();
	        fileTypes.put("java", "java");
	        fileTypes.put("cxx","cxx"); 
	        
	        InfoServlet servletClass = Mockito.spy(new InfoServlet());
	        Mockito.when(servletClass.getFileTypes()).thenReturn(fileTypes.entrySet());
	        StringWriter sw = new StringWriter();
	        PrintWriter writer = new PrintWriter(sw);
	        when(response.getWriter()).thenReturn(writer);
	        
	        //method call
	        servletClass.doGet(request, response);

	        JSONParser parser = new JSONParser();
	        JSONObject json = (JSONObject) parser.parse(sw.toString());
	        List<String> arr = (List<String>)json.get("file-types");
	        
	        //testing the file types added before
	        assertTrue(arr.contains("java"));
	        assertTrue(arr.contains("cxx"));
	        
	    }
	    
	    @Test
	    public void getFileTypesTest(){
	    	//function contains static method call
	    	InfoServlet servletClass = new InfoServlet();
	    	Set<Map.Entry<String, String>> fileTypeSet = servletClass.getFileTypes();
	    	assertTrue(fileTypeSet.containsAll(SearchHelper.getFileTypeDescriptions()));
	    	
	    }
	    
	   
    
}   
    