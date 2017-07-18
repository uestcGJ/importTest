package weather;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class FileUtil {
	public static String path="D:/weatherData/";
	public static String sortFile="";
	 // 工具类中的方法都是静态方式访问的因此将构造器私有不允许创建对象
    private FileUtil() {
        throw new AssertionError();
    }
	 /**
     * 功能：Java读取txt文件的内容
     * 步骤：1：先获得文件句柄
     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流
     * 4：一行一行的输出。readline()。
     * 备注：需要考虑的是异常情况
     * @param filePath
     */
    public static void readTxtFile(String filePath){
        try {
                String encoding="gbk";
                File file=new File(filePath);
                if(file.isFile() && file.exists()){ //判断文件是否存在
                    InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding);//考虑到编码格式
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    boolean isFirst=true;;
                    int maxSize=0;
                    List<String>stations=new ArrayList<>();
                    Set<String>matStations=new LinkedHashSet<>();
                    while((lineTxt = bufferedReader.readLine()) != null){
                   	 	FileWriter fileWritter;
                   	 	BufferedWriter bufferWritter=null;
                   	 	
                   	 	try {
                   	 		String fileName=path+"check/"+sortFile;
                   	 	    File checkedFile=new File(filePath);
                   	 	    if(!checkedFile.exists()){
                   	 	         checkedFile.createNewFile();
                   	 	    }
                   	 		fileWritter = new FileWriter(fileName,true);
                   	 		bufferWritter = new BufferedWriter(fileWritter);
                   	 		String context=lineTxt.replaceAll("\\s{2,}", " ");
                   	 	    String[] breakLine=context.split(" ");
                   	 		StringBuilder sb=new StringBuilder();
                   	 		if(isFirst){
                   	 			sb.append("时间");
                   	 			sb.append(" ");
                   	 			sb.append(" ");
                           	 	for(int i=2;i<breakLine.length;i++){
                           	 		stations.add(breakLine[i].split("_")[0]);
                           	 		sb.append(breakLine[i].split("_")[0]);
                           	 		sb.append(" ");
                           	 		sb.append(" ");
                           	 	}
                           	 	sb.append("\r\n");
                           	    isFirst=false;
                   	 		}
		                   	sb.append(breakLine[0]);
	               	 		sb.append(" ");
	               	 		sb.append(breakLine[1]);
	               	 		sb.append(" ");
	               	 		sb.append(" ");
	               	 		sb.append(" ");
	               	 		sb.append(" ");
	               	 	    maxSize=maxSize>breakLine.length-2?maxSize:breakLine.length-2;
	                   	 	if(breakLine.length!=(stations.size()+2)){
	                   	 		for(int i=2;i<breakLine.length;i++){
	    	                   	 	matStations.add(breakLine[i].split("_")[0]);
	                   	 		}
	                   	 		for(String str:stations){
	                   	 			boolean contains=false;
	                   	 			for(int i=2;i<breakLine.length;i++){
	                   	 				if(breakLine[i].contains(str)){
	                   	 					String value=breakLine[i].split("_")[1];
	                   	 					value=value.equals("-99")?"#.#":value;
	                   	 					value=value.contains(".")?value:(value+".0");
	                   	 					sb.append(value);
	                   	 					if(value.length()<4){
	                   	 						sb.append(" ");
	                   	 					}
	                   	 					contains=true;
	                   	 					break;
	                   	 				}        
	                   	 			}
	                   	 			if(!contains){
	                   	 				sb.append("#.#");
	                   	 				sb.append(" ");
	                   	 			}
	                   	 			sb.append(" ");
	                   	 			sb.append(" ");
	                   	 			sb.append(" ");
	                   	 		}
	                   	 	}else{
		                   	 	for(int i=2;i<breakLine.length;i++){
		                   	 		String value=breakLine[i].split("_")[1];
               	 					value=value.equals("-99")?"#.#":value;
		                   	 		value=value.contains(".")?value:(value+".0");
		                   	 		sb.append(value);
           	 						if(value.length()<4){
           	 							sb.append(" ");
           	 						}
	               	 				sb.append(" ");
	               	 				sb.append(" ");
	               	 				sb.append(" ");
	               	 			}
	                   	 	}
	                   	 	sb.append("\r\n");
                   	 		context=sb.toString();
	                   	 	bufferWritter.write(context);
                   	 	} catch (IOException e) {
						// TODO Auto-generated catch block
                   	 		e.printStackTrace();
                   	 	}finally{
						 try {
							bufferWritter.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                   	 	}
                    }
                    read.close();
                	System.out.println("首行站点数："+stations.size()+"\n最大站点数："+maxSize);
                	for(String str:matStations){
                		if(!stations.contains(str)){
                    		System.out.println(str);

                		}
                	}
        }else{
            System.out.println("找不到指定的文件");
        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
     
    }
    public static void formatFile(String filePath,String formatedPath){
    	File formatedFile=new File(formatedPath);
    	if(!formatedFile.exists()){
    		try {
				formatedFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	BufferedReader reader=null;
    	BufferedWriter writer=null;
    	try {
    		reader=new BufferedReader(new FileReader(filePath));
    		writer=new BufferedWriter(new FileWriter(formatedFile,true));
    		String line=null; 
    		StringBuilder context=new StringBuilder();
    		int count=0;
    		while((line=reader.readLine())!=null){
    			String[] checks=line.trim().replaceAll("\\s{2,}", ",").split(",");
    			line=checks[0]+","+checks[1];
    			count++;
    			if(!line.contains("##.##")&&!line.contains("#.#")&&count>1){
    				context.append(line.trim().replaceAll("\\s{2,}", ","));
    				context.append("\r\n");
    			}
    		}
    		writer.write(context.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	finally{
    		
    			try {
    				if(reader!=null){
    					reader.close();
    				}
    				if(writer!=null){
    					writer.close();
    				}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}
    }
    /***
     * @description 复制文件内容，采用输入输出流的方式
     * @param source String 源文件完整路径
     * @param target String 目标文件完整路径
     * @throws IOException
     * ***/
    public static void fileCopy(String source, String target) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(source))) {
            try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(target))) {
                byte[] buffer = new byte[4096];
                int bytesToRead;
                while((bytesToRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesToRead);
                }
            }
        }
    }
    /***
     * @description 复制文件内容,采用管道的方式
     * @param source String 源文件完整路径
     * @param target String 目标文件完整路径
     * @throws IOException
     * ***/
    public static void fileCopyNIO(String source, String target) throws IOException {
        try (FileInputStream in = new FileInputStream(source)) {
            try (FileOutputStream out = new FileOutputStream(target)) {
                FileChannel inChannel = in.getChannel();
                FileChannel outChannel = out.getChannel();
                ByteBuffer buffer = ByteBuffer.allocate(4096);
                while(inChannel.read(buffer) != -1) {
                    buffer.flip();
                    outChannel.write(buffer);
                    buffer.clear();
                }
            }
        }
    }
    /**
     * 统计给定文件中给定字符串的出现次数
     * 
     * @param filename  文件名
     * @param word 字符串
     * @return int 字符串在文件中出现的次数
     */
    public static int countWordInFile(String filename,String word) {
        int counter=0;
        try (BufferedReader br=new BufferedReader(new FileReader(filename))) {
             String line=null;
             while ((line=br.readLine())!=null) {
                 int index=-1;
                 //一行里面可能包含多个目标字符串
                 while (line.length()>=word.length()&&(index=line.indexOf(word))>=0){
                    counter++;
                    line=line.substring(index+word.length());
                 }
             }
         } catch (Exception ex) {
            ex.printStackTrace();
        }
        return counter;
    }

    public static void main(String[] args){
    	for(int i=6;i<17;i++){
    		String item=i>9?i+"":"0"+i;
        	formatFile("D:/files/py/data/习水县/习水县—20"+item+".txt","D:/files/py/data/all//rainfall_xs_all.txt");
    	}
    }
}
