package zChatLib;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

class NetworkUtils {
    static String localIPAddress() {
        try {
            Enumeration en = NetworkInterface.getNetworkInterfaces();

            while(en.hasMoreElements()) {
                NetworkInterface intf = (NetworkInterface)en.nextElement();
                Enumeration enumIpAddr = intf.getInetAddresses();

                while(enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress)enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ipAddress = inetAddress.getHostAddress();
                        int p = ipAddress.indexOf("%");
                        if (p > 0) {
                            ipAddress = ipAddress.substring(0, p);
                        }

                        return ipAddress;
                    }
                }
            }
        } catch (SocketException var6) {
            var6.printStackTrace();
        }

        return "127.0.0.1";
    }

    static String responseContent(String url) throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet();
        request.setURI(new URI(url));
        InputStream is = client.execute(request).getEntity().getContent();
        BufferedReader inb = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String NL = System.getProperty("line.separator");

        String line;
        while((line = inb.readLine()) != null) {
            sb.append(line).append(NL);
        }

        inb.close();
        return sb.toString();
    }

    static String spec(String host, String botid, String custid, String input) {
        String spec;
        if (custid.equals("0"))
            spec = String.format("%s?botid=%s&input=%s", "http://" + host + "/pandora/talk-xml", botid, URLEncoder.encode(input, StandardCharsets.UTF_8));
        else
            spec = String.format("%s?botid=%s&custid=%s&input=%s", "http://" + host + "/pandora/talk-xml", botid, custid, URLEncoder.encode(input, StandardCharsets.UTF_8));

        return spec;
    }
}
