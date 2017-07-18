package weather;

import java.io.IOException;
import java.util.Iterator;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;



public class CallCimissApi {
	//API账号密码信息
	private final static String userInfo="userId=BEGY_ZY_CS_JCYJ_API" //用户名
	        + "&pwd=111"//用户密码
	        +"&dataFormat=json"//返回数据格式为JSON
	        + "&orderby=Station_ID_C:ASC";  //排序：按照站号从小到大
	/***
	 * 根据传入的参数调用CIMISS接口查询数据并返回JSON格式的查询结果
	 * @param params String以&key=value方式连接的字符串
	 * @return JSONObject JSON格式的查询结果 
	 * **/
	 public static JSONObject getRstJson(String params){
		  params=userInfo+params;
		  RestUtil restUtil = new RestUtil();
		  return  restUtil.getRestData(params);
	 } 

	 /**
	  * 通过监测区域代号、监测时间点和气象要素，返回该监测区域所有站点的气象信息
	  * @param dateTime 监测时间点
	  * @param areaCode 监测区域代码，多个站用逗号隔开
	  * @param element 气象要素,如果有多个中间用逗号隔开即可
	  * @return 返回的是一个JSONObject；
	  * sstatusCode true表示成功  false表示失败；
	  * DS是一个JSONArray，字段有站点ID、站点名称、经度、纬度、气象要素。
	 * 
	  * **/
	 public static JSONObject getAreaWeatherInfo(String dateTime,String areaCode, String element){
		 String params="&interfaceId=getSurfEleInRegionByTime" /** 按时间、地区统计地面数据要素 */        		         
         + "&elements=Station_ID_C,Station_Name,"+element/**站点ID、站点名称、经度、纬度、气象要素*/
         + "&times="+dateTime //检索时间
         + "&adminCodes="+areaCode//检索地区
		 + "&dataCode=SURF_CHN_MUL_HOR";/**表示地面逐小时数据*/
		JSONObject jsonData = getRstJson(params);
	   //for test
		//jsonData=RestUtil.testJson("d:/chiShuiWeather/return json.txt");
		//对returnCode进行处理
		try {
			if(jsonData.getString("returnCode").equals("0")){
				jsonData.put("statusCode", true);
				//去掉返回值中多余的信息
				try{
					jsonData.remove("returnMessage");
					jsonData.remove("rowCount");
					jsonData.remove("colCount");
					jsonData.remove("requestParams");
					jsonData.remove("requestTime");
					jsonData.remove("responseTime");
					jsonData.remove("takeTime");
					jsonData.remove("fieldNames");
					jsonData.remove("fieldUnits");
				}catch(Exception e){
					e.printStackTrace();
				}
			}else{
				jsonData.put("statusCode", false);
			}
			jsonData.remove("returnCode");
		} catch (JSONException e) {
			jsonData.put("statusCode", false);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//对气象数据中的空值进行处理
		JSONArray jsonDataArray=null;
		try{
			jsonDataArray=jsonData.getJSONArray("DS");
		}catch(Exception e){
			jsonDataArray=new JSONArray();
			jsonData.put("DS", jsonDataArray);
		}
		formatWeatherData(jsonDataArray);
		return jsonData; 
	}
	 /**
	  * 根据开始时间、截止时间获取赤水市各监测站的温度或雨量信息
	  * @param startTime 开始时间 形如20170310020000
	  * @param endTime 截止时间  形如20170310020000
	  * @param element 气象要素,如果有多个中间用逗号隔开即可
	  * @return 返回的是一个JSONObject；
	  * statusCode true表示成功  false表示失败；
	  * infos 是一个JSONArray，字段有站点ID、站点名称、经度、纬度、气象要素。
	 * 
	  * **/
	 public static JSONObject getHistoryStationWeatherInfo(String startTime,String endTime, String element){
		 String interfaceId="statSurfPreInRegion";
		 if(element.contains("temp")){
			 interfaceId="statSurfTemInRegion";
		 }
		 String params="&interfaceId="+interfaceId /**按时间、站点号统计地面数据要素 */        		         
		         + "&elements=Station_ID_C,Station_Name"/**站点ID、站点名称、气象要素*/
		         + "&timeRange=["+startTime+","+endTime+"]" //检索时间
				 + "&adminCodes=520381,";
		JSONObject jsonData =getRstJson(params);//查询数据
		JSONObject result=new JSONObject();//返回结果
		//for test
		jsonData=RestUtil.testJson("d:/chiShuiWeather/rainfall.txt");
		 if(element.contains("temp")){
			 jsonData=RestUtil.testJson("d:/chiShuiWeather/temp.txt");
		 }
		try {
			if(jsonData.getString("returnCode").equals("0")){
				  //对气象数据中的空值进行处理
				JSONArray jsonDataArray = jsonData.getJSONArray("DS");
				String key="";
				switch(element){
					case"rainfall":
						key="COUNT_PRE_1H";
					break;
					case"temp_ave":
						key="AVG_TEM";
					break;
					case"temp_high":
						key="MAX_TEM_MAX";
					break;
					case"temp_low":
						key="MIN_TEM_MIN";
					break;
				default:
					break;
				}
				JSONArray items=new JSONArray();
			    String[] checkKeys=new String[]{"Lon","Lat",key};
			    String[] keys=new String[]{"lng","lat","value"};
				for(int i =0;i<jsonDataArray.size();i++){
					JSONObject jsonobject= jsonDataArray.getJSONObject(i);	
					JSONObject item= new JSONObject();	
					for(int j=0;j<checkKeys.length;j++){
						String value=jsonobject.getString(checkKeys[j]);
					    if(value.equals("999999")||value.equals("999998")||value.equals("999990")){
					    	value="0";
					    }
					    item.put(keys[j],value);
					}
					items.add(item);
				}
				if(!items.isEmpty()){
					result.put("statusCode", true);
					result.put("infos", items);//封装数据结果
				}
			}else{
				result.put("statusCode", false);
			}
		 } catch (JSONException e) {
			 result.put("statusCode", false);
					// TODO Auto-generated catch block
					e.printStackTrace();
		}
		return result; 
	} 
	 /***
	  *对查询的结果数据进行处理，将无效数据设置为-99 
	  * @param jsonDataArray  JSONArray 将要处理的JSONArray
	  * @return JSONArray 处理后的JSONArray
	  * ***/
	 public static void formatWeatherData(JSONArray jsonDataArray){
		for(int i =0;i<jsonDataArray.size();i++){
			JSONObject jsonobject= jsonDataArray.getJSONObject(i);		
			@SuppressWarnings("unchecked")
			Iterator<String> it=jsonobject.keys();
			boolean isMissing=false;
			while(it.hasNext()){//检查缺失
				String key=it.next();
				String value=jsonobject.getString(key);
			    if(value.equals("999999")||value.equals("999998")){
			        jsonobject.put(key,"-99");
			        isMissing|=true;
			    }else if(value.equals("999990")){
			       jsonobject.put(key, "0");
			    }
			   }
			  jsonobject.put("isMissing", isMissing);
		}
	 }
	 /**
	   * main方法
	   * 如：按时间检索地面数据要素 getSurfEleByTime
	 * @throws JSONException 
	 * @throws IOException 
	   */
	 public static void main(String[] args) throws JSONException, IOException{
		 JSONObject testData=getAreaWeatherInfo("20000101000000","520381","PRE_1h");
		 System.out.println(testData);
	 }
}
