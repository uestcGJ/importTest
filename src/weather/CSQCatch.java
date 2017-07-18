package weather;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class CSQCatch implements SerialPortEventListener {
    private CommPortIdentifier portId;
	private InputStream inputStream;
	private OutputStream outputStream;
    private  SerialPort serialPort;
	private  String portName="";
	private  String fileName="";
	private  String pathName;
	private int buadRate;
    private static  long PERIOD_DAY=24*60*60*1000;
 
    public CSQCatch(int buadRate,String pathName){
    	 this.pathName=pathName;
    	 this.buadRate=buadRate;
    	 getGSMMoulde(buadRate);	
    }
    public void periodGetCSQ(){
    	SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
    	fileName =pathName+sf.format(new Date())+".txt";
    	TimerManager();
    	getGSMCSQ(buadRate);
    }
    public String GetGsmPort(){
    	if(!portName.equals(""))
    	return this.portName;
    	else{
    		return null;
    	}
	}
    private Date addDay(Date date, int num) {  
        Calendar startDT = Calendar.getInstance();  
        startDT.setTime(date);  
        startDT.add(Calendar.DAY_OF_MONTH, num);  
        return startDT.getTime();  
    }  
    public void TimerManager() {  
        Calendar calendar = Calendar.getInstance(); 
        calendar.set(Calendar.HOUR_OF_DAY,0); //0 
        calendar.set(Calendar.MINUTE, 0);  
        calendar.set(Calendar.SECOND, 0);   
        Date date=calendar.getTime(); //
        //if current date min than the first execute time,then add a day 
        if (date.before(new Date())) {  
            date = this.addDay(date, 1);  
        }
        Timer timer = new Timer();  
        TimerTask task = new TimerTask(){
        	@Override
			public void run() {
        		System.out.println("check file");
        		// TODO Auto-generated method stub
        		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        		synchronized(fileName){
        			fileName =pathName+sf.format(new Date())+".txt";
    				File file=new File(fileName);
    				if(!file.exists()){
    					try {
    	            		System.out.println("create new file:"+fileName);
    						file.createNewFile();
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
    				}
        		}
			}
        };  
        timer.schedule(task,date,PERIOD_DAY);    
    }  

    private void getGSMCSQ(int buadRate){
    	@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier>portList = CommPortIdentifier.getPortIdentifiers();
    	while (portList.hasMoreElements()) {
    		portId = (CommPortIdentifier) portList.nextElement();
    		if ((portId.getPortType() == CommPortIdentifier.PORT_SERIAL)&&portId.getName().equals(portName)&&(!portId.isCurrentlyOwned())) {
    		try {
					serialPort = (SerialPort) portId.open("serial", 500);
					serialPort.setSerialPortParams(buadRate,
							SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);
					outputStream = serialPort.getOutputStream();
					serialPort.addEventListener(this);  
			        serialPort.notifyOnDataAvailable(true);  
			        Runnable runnable = new Runnable() {  
			            public void run() {  
			                // task to run goes here  
			            	 sendMsg("AT+CSQ\r");//
						     try {
								 inputStream=serialPort.getInputStream();
							     Thread.sleep(500);//

							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			            }  
			        };  
			        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();  
			        service.scheduleAtFixedRate(runnable, 1, 10, TimeUnit.SECONDS);  
			 }
		 	catch ( Exception e )  {  
				e.printStackTrace();
				continue; 
			 }
		   }
      }
		
    }

    private void getGSMMoulde(int buadRate){
    	@SuppressWarnings("unchecked")
		Enumeration<CommPortIdentifier>portList = CommPortIdentifier.getPortIdentifiers();
    	while (portList.hasMoreElements()) {
    		portId = (CommPortIdentifier) portList.nextElement();
    		if ((portId.getPortType() == CommPortIdentifier.PORT_SERIAL)&&(!portId.isCurrentlyOwned())) {
    		try {
					serialPort = (SerialPort) portId.open("serial", 500);
					
					serialPort.setSerialPortParams(buadRate,
							SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);
					outputStream = serialPort.getOutputStream();
					serialPort.addEventListener(this);  
			        serialPort.notifyOnDataAvailable(true);  
			        this.sendMsg("AT\r");
			        inputStream=serialPort.getInputStream();
			        Thread.sleep(500);
			        serialPort.notifyOnDataAvailable(false);
		        	serialPort.removeEventListener();
		        	serialPort.close();
			        if(!portName.equals("")){
			        	System.out.println("get port:"+portName);
			        	break;
			        }
			 }
			catch (Exception e )  {  
				e.printStackTrace();
				continue; 
			 }  
		   }
      }		
    }

	public void serialEvent(SerialPortEvent event) {
	  switch (event.getEventType()) {
		case SerialPortEvent.DATA_AVAILABLE:
			  String name=serialPort.getName();
			  String eventSource=name.substring(name.indexOf("COM"),name.length());
			  if(portName.equals("")||portName.equals(eventSource))
			  readComm(eventSource);          
			  break;
		default:
			break;
		}
	}

	public void readComm(String eventSource) {
		 byte[]readBuffer=new byte[512];
		 int numBytes=0;
		 try {
			 Thread.sleep(200);
			numBytes = inputStream.read(readBuffer);
		  } catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		 } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 byte[] temp = new byte[numBytes];  
         System.arraycopy(readBuffer, 0, temp, 0, numBytes);  
         String reMsg=new String(temp);
         reMsg=reMsg.replaceAll("\r", "").replaceAll("\n", "");
         if(reMsg.equals("OK")||reMsg.equals("ok")){
			   portName=eventSource;
		 }
         else if(reMsg.contains("+CSQ:")){
        	 SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
             String date=sf.format(new Date());
             StringBuilder builder=new StringBuilder();
             builder.append(date);
             builder.append(" ");
             String csq=reMsg.replace("+CSQ:", "").replaceAll("\r", "").replaceFirst("\n", "").replace("OK","").split(",")[0];
             int CSQ=-113+(Integer.parseInt(csq.trim()))*2;
             builder.append(CSQ);
             builder.append("\r\n");
             try {
            	  synchronized(fileName){
            		  FileWriter fileWritter = new FileWriter(fileName,true);
                      BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                      bufferWritter.write(builder.toString());
                      bufferWritter.close();
            	  }
            	 
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             System.out.println(date+" "+CSQ+"dBm");
         } 
	}  


	public  void closeSerialPort() {
		serialPort.notifyOnDataAvailable(false);
		serialPort.removeEventListener();
		serialPort.close();
	}


	public boolean sendMsg(String msg) {
		try {
			outputStream.write(msg.getBytes());
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String args[]) throws Exception{
		String path="D:/CSQ/";
		if(args.length!=0){
			 path=args[0];
			 if(!path.endsWith("/")){
				 path+="/"; 
			 }
		}
		System.out.println("file path:"+path);
		CSQCatch serial=new CSQCatch(9600,path);
		serial.periodGetCSQ();
	}	
}

