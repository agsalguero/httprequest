/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package httprequest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Albaerto
 */
public class HTTPRequest {

    public enum Method {GET, POST};
    
    String URL;
    ArrayList<KeyValuePair> parameters;
    ArrayList<String> xPaths;
    ArrayList<KeyValuePair> requestProperties = new ArrayList<KeyValuePair>();
    ArrayList<KeyValuePair> responseProperties = new ArrayList<KeyValuePair>();
    private ILogger logger = null;
    Method method = Method.GET;
    private Document doc = null;
    boolean https = false;
    int connectTimeout = 10000; // 10 seconds by default
    boolean encode = true;
    

    public HTTPRequest(String URL) {
        init(URL, Method.GET, new ArrayList<KeyValuePair>(), new ArrayList<String>(), new DefaultLogger());
    }
    
    public HTTPRequest(String URL, ArrayList<String> xPaths) {
        init(URL, Method.GET, new ArrayList<KeyValuePair>(), xPaths, new DefaultLogger());
    }
    
    public HTTPRequest(String URL, ArrayList<KeyValuePair> parameters, ArrayList<String> xPaths) {
        init(URL, Method.GET, parameters, xPaths, new DefaultLogger());
    }
    
    public HTTPRequest(String URL, Method method, ArrayList<KeyValuePair> parameters, ArrayList<String> xPaths) {
        init(URL, method, parameters, xPaths, new DefaultLogger());
    }
    
    /**
     * 
     * @param URL
     * @param method
     * @param parameters As passed after the question mark in URL (ej: param1=A&param2=hello). 
     */
    public HTTPRequest(String URL, Method method, String parameters) {
        initWithStringParams(URL, method, parameters, new ArrayList<String>(), new DefaultLogger());
    }
    
    /**
     * 
     * @param URL
     * @param method
     * @param parameters As passed after the question mark in URL (ej: param1=A&param2=hello). 
     * @param xPaths 
     */
    public HTTPRequest(String URL, Method method, String parameters, ArrayList<String> xPaths) {
        initWithStringParams(URL, method, parameters, xPaths, new DefaultLogger());
    }
    
    public HTTPRequest(String URL, Method method, ArrayList<KeyValuePair> parameters, ArrayList<String> xPaths, ILogger logger) {
        init(URL, method, parameters, xPaths, logger);
    }
    
    private void initWithStringParams(String URL, Method method, String parameters, ArrayList<String> xPaths, ILogger logger) {
        ArrayList<KeyValuePair> list = new ArrayList<KeyValuePair>();        
        String[] splitted = parameters.split("&");        
        for(String param : splitted) {
            if(!param.isEmpty()) {
                String[] keyvalue = param.split("=");
                list.add(new KeyValuePair(keyvalue[0], keyvalue[1]));
            }
        }
        
        init(URL, method, list, xPaths, logger);
    }
    
    private void init(String URL, Method method, ArrayList<KeyValuePair> parameters, ArrayList<String> xPaths, ILogger logger) {
        this.URL = URL;
        this.parameters = parameters;
        this.xPaths = xPaths;
        this.setLogger(logger);
        this.method = method;
        
        this.https = URL.toLowerCase().startsWith("https");
    }
    
    public ArrayList<String> download() {
        if(xPaths.size() > 0) {
            ArrayList<String> results = new ArrayList<String>(xPaths.size());
            
            try {
                TagNode tagNode = new HtmlCleaner().clean(doRequest().toString());
                doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);
                
                XPath xpath = XPathFactory.newInstance().newXPath();
                
                for(String path: xPaths) {
                    NodeList nodes = (NodeList) xpath.evaluate(path, getDoc(),
                                    XPathConstants.NODESET);

                    /*String text = "";
                    for (int i = 0; i < nodes.getLength(); i++) {
                        text += nodes.item(i).getTextContent();
                    }
                    results.add(text);*/
                    for (int i = 0; i < nodes.getLength(); i++) {
                        results.add(nodes.item(i).getTextContent());
                    }
                }
                
                return results;
            } catch (XPathExpressionException e) {
                    e.printStackTrace();
                    logger.log(e.getMessage());
            } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                    logger.log(e.getMessage());
            } catch (Exception e) {
                    e.printStackTrace();
                    logger.log(e.getMessage());
            }            
        }
        return null;     
    }
    
    public ArrayList<ArrayList<String>> downloadAsMatrix() {
        if(xPaths.size() > 0) {
            ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>(xPaths.size());
            
            try {
                TagNode tagNode = new HtmlCleaner().clean(doRequest().toString());
                doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);
                
                XPath xpath = XPathFactory.newInstance().newXPath();
                
                for(String path: xPaths) {
                    NodeList nodes = (NodeList) xpath.evaluate(path, getDoc(),
                                    XPathConstants.NODESET);
                    
                    ArrayList<String> list = new ArrayList<String>();
                    results.add(list);

                    /*String text = "";
                    for (int i = 0; i < nodes.getLength(); i++) {
                        text += nodes.item(i).getTextContent();
                    }
                    results.add(text);*/
                    for (int i = 0; i < nodes.getLength(); i++) {
                        list.add(nodes.item(i).getTextContent());
                    }
                }
                
                return results;
            } catch (XPathExpressionException e) {
                    e.printStackTrace();
                    logger.log(e.getMessage());
            } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                    logger.log(e.getMessage());
            } catch (Exception e) {
                    e.printStackTrace();
                    logger.log(e.getMessage());
            }            
        }   
        return null;     
    }
    
    public ArrayList<Node> downloadNodes() {
        if(xPaths.size() > 0) {
            ArrayList<Node> results = new ArrayList<Node>(xPaths.size());
            
            try {
                TagNode tagNode = new HtmlCleaner().clean(doRequest().toString());
                doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);
                
                XPath xpath = XPathFactory.newInstance().newXPath();
                
                for(String xPath: xPaths) {
                    NodeList nodes = (NodeList) xpath.evaluate(xPath, doc,
                                    XPathConstants.NODESET);

                    /*String text = "";
                    for (int i = 0; i < nodes.getLength(); i++) {
                        text += nodes.item(i).getTextContent();
                    }
                    results.add(text);*/
                    for (int i = 0; i < nodes.getLength(); i++) {
                        results.add(nodes.item(i));
                    }
                }
                
                return results;
            } catch (XPathExpressionException e) {
                    e.printStackTrace();
                    logger.log(e.getMessage());
            } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                    logger.log(e.getMessage());
            } catch (Exception e) {
                    e.printStackTrace();
                    logger.log(e.getMessage());
            }            
        }   
        return null;     
    }
    
    // @todo NO LO HE PROBADOOOOOOOOOOOOOOOOOOOOOOO
    public void downloadToFile(String Filename) throws Exception {
        BufferedWriter out = new BufferedWriter(new FileWriter(Filename));
        for( Character c : doRequestBytes() ) {
            out.write(c);
        }
        //out.write((Character[])doRequestBytes().toArray());
        out.flush();
        out.close();
    }
    
    public StringBuffer downloadContent() throws Exception {
        return doRequest();
    }
    
    public StringBuffer downloadContent(int bytes) throws Exception {
        return doRequest(bytes);
    }
    
    private StringBuffer doRequest() throws Exception {
        return doRequest(-1);
    }
    
    private StringBuffer doRequest(int bytes) throws Exception {
        HttpURLConnection con = connect();
        
        int responseCode = con.getResponseCode();
        StringBuffer response = new StringBuffer();
        
        logger.log("Response: " + responseCode, ILogger.Level.INFO);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        
        if(bytes < 0) {        
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine + "\n");
                //logger.log(inputLine);
            }
        } else {
            char[] array = new char[bytes];            
            int read = in.read(array);
            response.append(array);
            
            if(read < bytes) {
                logger.log("Waring: expecting " + bytes + " bytes at least but received " + read, ILogger.Level.INFO);
            }
        }
        
        in.close();
        
        logger.log("\nResponse header:", ILogger.Level.DEBUG);
        int i = 0;
        while(con.getHeaderField(i) != null) {
            responseProperties.add(new KeyValuePair(con.getHeaderFieldKey(i), con.getHeaderField(i)));
            logger.log("\n"+ con.getHeaderFieldKey(i) +": " + con.getHeaderField(i), ILogger.Level.DEBUG);
            ++i;
        }
        
        logger.log("\n" + response, ILogger.Level.DEBUG);
        
        return response;
    }
    
    
    private ArrayList<Character> doRequestBytes() throws Exception {
        HttpURLConnection con = connect();
        
        int responseCode = con.getResponseCode();
        StringBuffer response = new StringBuffer();
        
        logger.log("Response: " + responseCode, ILogger.Level.INFO);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        
        ArrayList<Character> bytes = new ArrayList<>();
        char[] array = new char[1024];
        int read;
        while((read = in.read(array)) > 0){
            for(int i = 0 ; i < read ; ++i) {
                bytes.add(array[i]);
                //response.append(array[i]);
            }
        }
        
        in.close();
        
        logger.log("\nResponse header:", ILogger.Level.DEBUG);
        int i = 0;
        while(con.getHeaderField(i) != null) {
            responseProperties.add(new KeyValuePair(con.getHeaderFieldKey(i), con.getHeaderField(i)));
            logger.log("\n"+ con.getHeaderFieldKey(i) +": " + con.getHeaderField(i), ILogger.Level.DEBUG);
            ++i;
        }
        
        logger.log("\n" + response, ILogger.Level.DEBUG);
        
        return bytes;
    }
    
    private HttpURLConnection connect() throws MalformedURLException, IOException {
        String urlParameters = getParameters(); 
        
        String url = URL;
        if(method == Method.GET && !urlParameters.isEmpty()) {
            if(url.endsWith("&") || url.endsWith("?")) {
                url = url + urlParameters;
            } else {
                url = url + "?" + urlParameters;
            }
        }
        
        URL obj = new URL(url);
        
        HttpURLConnection con;
        if(https) {
            con = (HttpsURLConnection) obj.openConnection();
        } else {
            con = (HttpURLConnection) obj.openConnection();
        }
        
        con.setConnectTimeout(connectTimeout);
        con.setReadTimeout(connectTimeout);

        //add request header
        String methodName = "GET";
        if(method == Method.POST) {
            methodName = "POST";
        }
        con.setRequestMethod(methodName);
        
        for(KeyValuePair par: requestProperties) {
            con.setRequestProperty(par.key, par.value);
        }    

        if(method == Method.GET) {
            // @todo necesita hacer algo diferente a cuando se trata de un POST?
        } else {
            // Send post request
            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.writeBytes(urlParameters);
                wr.flush();
            }
        }

        logger.log("\nSending "+ methodName +" request to URL : " + url, ILogger.Level.INFO);
        if(method == Method.POST) {
            logger.log("POST parameters : " + urlParameters, ILogger.Level.INFO);
        }
        
        return con;
    }
    
    
    public String getParameters() {
        return parametersToString(parameters, encode);
    }
    
    public static String parametersToString(ArrayList<KeyValuePair> parameters) {
        return parametersToString(parameters, true);
    }
    
    public static String parametersToString(ArrayList<KeyValuePair> parameters, boolean encode) {
        String urlParameters = "";
        if(encode) {
            for(KeyValuePair par: parameters) {
                urlParameters += "&" + par.key + "=" + par.value;
            }
        } else {
            for(KeyValuePair par: parameters) {
                urlParameters += "&" + URLEncoder.encode(par.key) + "=" + URLEncoder.encode(par.value);
            }
        }
        
        if(!urlParameters.isEmpty()) {
            urlParameters = urlParameters.substring(1, urlParameters.length());
        }
        
        return urlParameters;
    }
    
    protected String attributesToString(Node node) {
        /*String text = "";
        for(int i = 0 ; i < node.getAttributes().getLength() ; ++i){
            text += node.getAttributes().item(i).toString();
        }
        return text;*/
        if(node.getAttributes() != null) {
            String text = "";
            for(int i = 0 ; i < node.getAttributes().getLength() ; ++i){
                text += ", " + node.getAttributes().item(i).toString();
            }
            
            if(text.length() > 0) {
                text = "[" + text.substring(2) + "]";
            }
            
            return text;
        }
        return "";
    }
    
    protected String indent(int level) {
        String spaces = "";
        
        for(int i = 0 ; i < level ; ++i) {
            spaces += " ";
        }            
        
        return spaces;
    }
    
    protected String traverse(Node node, int level, int maxLevel) {
        //System.out.println(nodo.getNodeName());
        
        //System.out.println(nodo.getChildNodes().getLength());
        
        StringBuffer dom = new StringBuffer();
        
        if(level <= maxLevel) {
            for(int i = 0 ; i < node.getChildNodes().getLength() ; ++i){
                Node n = node.getChildNodes().item(i);
                if(n.getNodeType() == Node.TEXT_NODE)
                {
                    String text = n.getNodeValue();
                    if(text.length() > 20) {
                        text = text.substring(0, 20) + "...";
                    }
                    
                    if(text.trim().length() > 0 ) {
                        dom.append(indent(level) + text + "\n");
                    }
                } else {
                    dom.append(indent(level) + n.getNodeName()
                           + " " + attributesToString(n) + "\n");
                }
                dom.append(traverse(n, level + 1, maxLevel));
            }
        }
        
        return dom.toString();
    }
    
    public String toDOMString() {
        StringBuffer dom = new StringBuffer();
        
        //System.out.println(doc.getChildNodes().getLength());
        
        if(dom != null) {
            for(int i = 0 ; i < doc.getChildNodes().getLength() ; ++i){
                dom.append(traverse(doc.getChildNodes().item(i), 0, 10) + "\n");
            }
        }
        
        return dom.toString();
    }
    
    public void addRequestProperty(String key, String value) {
        requestProperties.add(new KeyValuePair(key, value));
    }
    
    public void setRequestProperties(ArrayList<KeyValuePair> properties) {
        requestProperties = properties;
    }
    
    public ArrayList<KeyValuePair> getResponseProperties() {
        return responseProperties;
    }
    
    public void setConnectTimeout(int timeout)
    {
        connectTimeout = timeout;
    }
    
    public void setEncode(boolean encode) {
        this.encode = encode;
    }
    
    /**
     * @return the logger
     */
    public ILogger getLogger() {
        return logger;
    }

    /**
     * @param logger the logger to set
     */
    public void setLogger(ILogger logger) {
        this.logger = logger;
    }
    
    /**
     * @return the doc
     */
    public Document getDoc() {
        return doc;
    }
    
    
}
