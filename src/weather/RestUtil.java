package weather;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import net.sf.json.JSONObject;
/**
 * 采用rest方式访问数据库接口
 * ***/
public class RestUtil {
  //贵州省MUSIC系统服务器地址
  private final String host="10.203.89.55";
  //连接超时时间
  private final int timeoutInMilliSeconds=10000;//1000*60*2 ;//2 MINUTE
  /**
   *采用rest方式连接数据库获取数据
   *@param params String 包含请求参数的url
   *@return String 数据库服务器返回的数据，字符串形式
   */
  public JSONObject getRestData(String params) {
	JSONObject response=new JSONObject();
    try {
      URI uri = new URI("http",this.host,"/cimiss-web/api",params,"");
      URL url=uri.toURL();
      URLConnection con = url.openConnection();
      con.setConnectTimeout( this.timeoutInMilliSeconds ); 
      StringBuilder retStr=new StringBuilder();
      BufferedReader reader=new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));//new BufferedReader(new InputStreamReader(con.getInputStream()));
      String line = reader.readLine();
      while(line!=null) {
        retStr.append(line).append("\r\n");
        line = reader.readLine();
      }
      reader.close();
      response=JSONObject.fromObject(retStr.toString());
     } catch (Exception e) {
       // e.printStackTrace();
        response.clear();
        response.put("returnCode", "1");
    }
 
    return response;
  }

  //测试用
  public static JSONObject testJson(String file){
	  JSONObject json=new JSONObject();
		try {
			FileInputStream fs=new FileInputStream(file);
			String response="";
			byte[] bbf=new byte[1024]; 
			int has=0;
			while((has=fs.read(bbf))>0){
				response+=new String(bbf,0,has);
			}
			fs.close();
			String err=response.replaceAll("\"", "'");
			json=JSONObject.fromObject(err);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
  }
  /**
   存储数据 
   */
  public String setRestData(String params,String inString) {
    StringBuilder retStr = new StringBuilder();
    URI uri = null;
    URL url = null;
    java.io.BufferedReader reader = null;
    URLConnection con;
    params=params+"&instring="+inString;
    try {
      uri = new URI("http", this.host, "/cimiss-web/write", params, "");
      url = uri.toURL();
      con = url.openConnection();
      con.setConnectTimeout( this.timeoutInMilliSeconds ); 
      reader = new BufferedReader(  new InputStreamReader(con.getInputStream()));
      String line = reader.readLine();
      while (line != null) {
        retStr.append(line).append("\r\n");
        line = reader.readLine();
      }
      reader.close();
    } catch (Exception ex1) {
      ex1.printStackTrace();
    }
    return retStr.toString();
  }
}
