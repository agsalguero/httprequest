package httprequest;


import java.util.ArrayList;

/**
 *
 * @author alberto
 */
public class HTTPRequestTest {

    protected static ArrayList<KeyValuePair> getDefaultRequestProperties() {
        ArrayList<KeyValuePair> requestProperties = new ArrayList<>();

        //requestProperties.add(new KeyValuePair("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0"));
        //requestProperties.add(new KeyValuePair("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.131 Safari/537.36"));
        requestProperties.add(new KeyValuePair("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)"));
        requestProperties.add(new KeyValuePair("Accept-Language", "es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3"));
        requestProperties.add(new KeyValuePair("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
        requestProperties.add(new KeyValuePair("Accept-Encoding", "gzip, deflate"));
        requestProperties.add(new KeyValuePair("Connection", "keep-alive"));
        //requestProperties.add(new KeyValuePair("Host", "ws035.juntadeandalucia.es"));
        //requestProperties.add(new KeyValuePair("Referer", "https://ws035.juntadeandalucia.es/bolsa/http/informe_notacorte_contratacion.php"));
        requestProperties.add(new KeyValuePair("Accept-Encoding", "gzip, deflate"));
        //requestProperties.add(new KeyValuePair("charset", "utf-8"));
        requestProperties.add(new KeyValuePair("Content-Type", "application/x-www-form-urlencoded"));
        requestProperties.add(new KeyValuePair("Cache-Control", "no-cache"));

        return requestProperties;
    }

    public static void main(String[] args) throws Exception {
        
        // Download the entire content as an html document
        HTTPRequest request = new HTTPRequest("http://www.tripadvisor.es/Restaurants-g2355755-Province_of_Cadiz_Andalucia.html");
        StringBuffer response = request.downloadContent();        
        System.out.println(response);
        
        // Download the names of the restaurants
        ArrayList<String> xPaths = new ArrayList<>();
        xPaths.add("//a[@class='property_title']");
        HTTPRequest request2 = new HTTPRequest("http://www.tripadvisor.es/Restaurants-g2355755-Province_of_Cadiz_Andalucia.html", xPaths);
        ArrayList<String> results = request2.download();
        if (results != null) {
            for (String values : results) {
                System.out.println(values);
            }
        }
        
        // Download the names of the restaurants (ii)
        ArrayList<String> xPaths3 = new ArrayList<>();
        xPaths.add("//div[@class='sswi cx']/div/h2/a");
        HTTPRequest request3 = new HTTPRequest("https://11870.com/k/restaurantes/es/es/cadiz?p=1&o=0&v=list", xPaths);
        ArrayList<String> results3 = request3.download();
        if (results3 != null) {
            for (String values : results3) {
                System.out.println(values);
            }
        }
        
        // Download the names of the restaurants using url parameters list
        ArrayList<KeyValuePair> params = new ArrayList<>();
        params.add(new KeyValuePair("p","1"));
        params.add(new KeyValuePair("o","0"));
        params.add(new KeyValuePair("v","list"));
        ArrayList<String> xPaths4 = new ArrayList<>();
        xPaths.add("//div[@class='sswi cx']/div/h2/a");
        HTTPRequest request4 = new HTTPRequest("https://11870.com/k/restaurantes/es/es/cadiz", params, xPaths);
        ArrayList<String> results4 = request4.download();
        if (results4 != null) {
            for (String value : results4) {
                System.out.println(value);
            }
        }
    }

}
