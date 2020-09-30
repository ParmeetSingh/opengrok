package org.opensolaris.opengrok.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

/**
 * 
 * servlet defined to provide generic information in the UI 
 *
 */
		

public class InfoServlet extends HttpServlet{

	
    private static final long serialVersionUID = 1L;
	private static final String ATTRIBUTE_RESULTS      = "file-types";
    private static final String ATTRIBUTE_DURATION     = "duration";
    private static final String ATTRIBUTE_RESULT_COUNT = "resultcount";

    /**
     * function fetches all file-type extensions from SearchHelper Invocation
     */
    @SuppressWarnings({"unchecked"})
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	JSONObject result = new JSONObject();
    	long start = System.currentTimeMillis();
    	Set<Map.Entry<String, String>> fileTypeSet = this.getFileTypes();
    	Iterator<Entry<String, String>> iter = fileTypeSet.iterator();
        List<String> fileTypes = new ArrayList<String>();
        while(iter.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>)iter.next();
            fileTypes.add(entry.getKey());
        }
        long duration = System.currentTimeMillis() - start;
    	result.put(ATTRIBUTE_DURATION, duration);
    	result.put(ATTRIBUTE_RESULT_COUNT, fileTypes.size());
    	result.put(ATTRIBUTE_RESULTS, fileTypes);
    	resp.setContentType("application/json");
    	resp.getWriter().write(result.toString());
    }
    
    
    public Set<Map.Entry<String, String>> getFileTypes() {
    	return SearchHelper.getFileTypeDescriptions();
	}
    

}

