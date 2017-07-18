package weather;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class DataCatcher extends JFrame {

	/****/
	private static final long serialVersionUID = -1857666462266929643L;
	private JTextArea textArea;
	private void ini(){

		//总的界面
		JPanel totalPanel=new JPanel();
		//文件路径
		JLabel pathLabel = new JLabel("存储路径:");
		pathLabel.setHorizontalAlignment(JTextField.LEFT );
		JTextField pathInput=new JTextField(15);
		JPanel filePanpel= new JPanel();
		JPanel areaPanpel= new JPanel();
		JComboBox <String> comboBox=new JComboBox<>();  
		comboBox.setPreferredSize(new Dimension(165,21));//设置下拉菜单的尺寸
		comboBox.addItem("--全部--"); 
        comboBox.addItem("赤水市-520381");  
        comboBox.addItem("仁怀市-520382");  
        comboBox.addItem("习水县-520330");  
        JLabel areaLabel = new JLabel("监测区域:");
        areaLabel.setHorizontalAlignment(JTextField.LEFT );
		areaPanpel.add(areaLabel);
		areaPanpel.add(comboBox);
        filePanpel.add(pathLabel);
		filePanpel.add(pathInput);
		JButton fileButton = new JButton("选择路径");
		JButton closeButton = new JButton("退出");
		JButton startButton = new JButton("开始获取");
		totalPanel.add(areaPanpel);  
		totalPanel.add(filePanpel);
		
		
		
		//菜单栏
		JPanel menuPanel = new JPanel();
		totalPanel.add(menuPanel);
		//加入按键
		menuPanel.add(fileButton);
		menuPanel.add(startButton);
		menuPanel.add(closeButton);
		//说明部分
		JPanel  contentPane=new JPanel();
   	 	contentPane.setBorder(new EmptyBorder(5,5,5,5));
   	 	contentPane.setLayout(new BorderLayout(2,2));
   	 	JScrollPane scrollPane=new JScrollPane();
   	 	contentPane.add(scrollPane,BorderLayout.CENTER);
   	 	textArea=new JTextArea();
   	    scrollPane.setPreferredSize(new Dimension(240,170));//设置下拉菜单的尺寸
   	    textArea.setVisible(true);
   	    scrollPane.add(textArea); 
   	 	scrollPane.setViewportView(textArea);
   	    scrollPane.setVisible(true);
		totalPanel.add(contentPane);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		this.add(totalPanel);
		//文件选择按键点击事件监听
		fileButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				 JFileChooser jfc=new JFileChooser();  
			        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );  
			        jfc.showDialog(new JLabel(), "选择");  
			        File file=jfc.getSelectedFile();  
			        String path="";
			        if(file.isDirectory()){  
			        	path=file.getAbsolutePath();  
			        }else if(file.isFile()){  
			        	path=file.getAbsolutePath();  
			        } 
			        pathInput.setText(path);
			}
		});
		//开始获取按键事件监听
		startButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				 String path=pathInput.getText();
				 fileButton.setEnabled(false);
				 pathInput.setEditable(false);
				
				 class GetRestData extends Thread{
					 private Calendar calendar;
					 private String areaCode;
					 public GetRestData(Calendar calendar,String areaCode){
						 this.calendar=calendar;
						 this.areaCode=areaCode;
					 }
					 public void run(){
						 SimpleDateFormat sf=new SimpleDateFormat("yyyy");
						 Calendar nextYear=Calendar.getInstance();
						 if(calendar.get(Calendar.YEAR)!=2017){
							 nextYear.set(calendar.get(Calendar.YEAR)+1,calendar.get(Calendar.MONTH),
									 calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.HOUR_OF_DAY),
									 calendar.get(Calendar.HOUR_OF_DAY)-1,0);
						 }  
						 String fileName=path+"\\"+areaCode.split("-")[0]+"—"+sf.format(calendar.getTime())+".txt";
						 while(calendar.before(nextYear)){
							 File file=new File(fileName);
							 if(!file.exists()){
								 try {
									file.createNewFile();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							 }
							 StringBuilder context=new StringBuilder();
							 sf=new SimpleDateFormat("yyyy-MM-dd HH");
							 String catchTime=sf.format(calendar.getTime());
							 sf=new SimpleDateFormat("yyyyMMddHHmmss");
							 String time=sf.format(calendar.getTime());
							 int count=0;
							 JSONObject weatherInfo=CallCimissApi.getAreaWeatherInfo(time, areaCode.split("-")[1], "PRE_1h");
							 while(count<3){//如果某条记录未获取到，重复三次
								 if(weatherInfo.getBoolean("statusCode")){
									 break; 
								 }
								 weatherInfo=CallCimissApi.getAreaWeatherInfo(time, areaCode.split("-")[1], "PRE_1h");
								 count++;
							 }
							 textArea.insert(areaCode.split("-")[0]+"—"+catchTime+"\n",0);
							 if(weatherInfo.getBoolean("statusCode")){
								 JSONArray DS=weatherInfo.getJSONArray("DS");
								 context.append(catchTime);
								 context.append(" ");
								 context.append(" ");
								 context.append(" ");
								 context.append(" ");
								 context.append(" ");
								 context.append(" ");
								 context.append(" ");
								 context.append(" ");
								 for(int i=0;i<DS.size();i++){
									 JSONObject info=DS.getJSONObject(i);
									 context.append(info.getString("Station_Name"));
									 context.append("_");
									 context.append(info.getString("PRE_1h"));
									 context.append(" ");
									 context.append(" ");
									 context.append(" ");
									 context.append(" ");
									 context.append(" ");
									 context.append(" ");
								 }
								 context.append("\r\n");
								 FileWriter fileWritter;
								 BufferedWriter bufferWritter=null;
								 try {
									 fileWritter = new FileWriter(fileName,true);
									 bufferWritter = new BufferedWriter(fileWritter);
				                     bufferWritter.write(context.toString());
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
							
							calendar.add(Calendar.HOUR_OF_DAY,1);
						 }
					 }
				 }
				 startButton.setEnabled(false);
				 new Thread(new Runnable(){
					 @Override
					public void run() {
						// TODO Auto-generated method stub
						 String area=(String) comboBox.getSelectedItem();
					     List<String> areaCodes=new ArrayList<>();
						 if(area.contains("全部")){
							 areaCodes.add("赤水市-520381");  
							 areaCodes.add("仁怀市-520382");  
							 areaCodes.add("习水县-520330");   
						 }else{
							 areaCodes.add(area);
						 }
					     ExecutorService exec=Executors.newCachedThreadPool();
					     for(String areaCode:areaCodes){
					    	 for(int year=2016;year<=2017;year++){
								 Calendar calendar = Calendar.getInstance();
								 calendar.set(Calendar.MONTH,0);
								 calendar.set(Calendar.DAY_OF_MONTH,1);
							     calendar.set(Calendar.HOUR_OF_DAY,0); //0 
							     calendar.set(Calendar.MINUTE, 0);  
							     calendar.set(Calendar.SECOND, 0);  
								 calendar.set(Calendar.YEAR,year);
								 exec.execute(new GetRestData(calendar,areaCode));
							 }
					     }
						 exec.shutdown();
						 try {//等待直到所有任务完成  
							 exec.awaitTermination(5, TimeUnit.HOURS);  
					        } catch (InterruptedException e2) {  
					            e2.printStackTrace();  
					     }
						 textArea.insert("======获取完成=====\r\n", 0);
						 fileButton.setEnabled(true);
						 startButton.setEnabled(true);
					}
					 
				 }).start();;  
			}
		});
		// 退出按键点击事件监听
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//关闭当前程序
				System.exit(0);		
			}
		});
	}
	public DataCatcher(){
		ini();
	}
	public static void main(String[] args) throws InterruptedException {
			final DataCatcher keyMaker = new DataCatcher();
			keyMaker.setVisible(true);
			keyMaker.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			keyMaker.setSize(310, 340);
			keyMaker.setLocationRelativeTo(null);
			keyMaker.setResizable(false);
			keyMaker.setTitle("气象数据获取程序");
		}
}
