package key;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author 心之&所系
 * 根据服务器IP生成注册码
 * **/
public class KeyMaker extends JFrame{
	/****/
	private static final long serialVersionUID = -1857666462266929644L;
	public KeyMaker(){
		//总的界面
		JPanel totalPanel=new JPanel();
		//服务器MAC label
		JLabel macLabel = new JLabel("服务器MAC:");
		macLabel.setHorizontalAlignment(JTextField.LEFT );
		//注册码label
		JLabel serialLabel = new JLabel("系统注册码:");
		serialLabel.setHorizontalAlignment(JTextField.LEFT);
		//服务器input
		JTextField ipInput=new JTextField(15);
		JPanel macPanpel= new JPanel();
		//注册码input
		JTextField serialInput=new JTextField(15);
		JPanel serialPanel = new JPanel();
		//加入label
		macPanpel.add(macLabel);
		serialPanel.add(serialLabel);
		//加入input
		macPanpel.add(ipInput);
		serialPanel.add(serialInput);
		//提交按键
		JButton submitButton = new JButton("生成注册码");
		JButton closeButton = new JButton("退出");
		totalPanel.add(macPanpel);
		totalPanel.add(serialPanel);
		//菜单栏
		JPanel menuPanel = new JPanel();
		totalPanel.add(menuPanel);
		//加入按键
		menuPanel.add(submitButton);
		menuPanel.add(closeButton);
		//说明部分
		JPanel tipPanel=new JPanel();
		//tip label
		JLabel tipLabel = new JLabel("说明:输入服务器MAC后点击\"生成注册码\"按键即可。");
		tipPanel.add(tipLabel);
		totalPanel.add(tipPanel);
		this.add(totalPanel);
		//提交按键点击事件监听
		submitButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				//读取用户输入的IP
				String serverMac = ipInput.getText();
				System.out.println("ip:"+serverMac);
				//先判断是否为IP
				if(macCheck(serverMac)){
					//生成注册码
					serialInput.setText(createSNCode(serverMac,true));
				}
				else{
					JOptionPane.showMessageDialog(null, "请输入合法的MAC地址。", "提示", JOptionPane.ERROR_MESSAGE); 
				}
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
	 /**
	   * 字节组转为hex形式显示
	   * @param byteArray byte[] 待转换的字节组
	   * @return String Hex形式的字符串
	   * **/
	  public static String byteArrayToHex(byte[] byteArray) { 
	        
	         // 首先初始化一个字符数组，用来存放每个16进制字符 
	         char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'A','B','C','D','E','F' }; 
	         // new一个字符数组，这个就是用来组成结果字符串 
	         char[] resultCharArray =new char[byteArray.length * 2]; 
	         // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去 
	         int index = 0;
	         for (byte b : byteArray) { 
	            resultCharArray[index++]=hexDigits[b>>> 4 & 0xf]; 
	            resultCharArray[index++]=hexDigits[b& 0xf]; 
	         }
	         // 字符数组组合成字符串返回 
	         return new String(resultCharArray); 
	     }
	 /**
	   * 将字符串进行MD5加密
	   * MD5算法是不可逆的
	   * @param String 要加密的字符串
	   * @return String 通过MD5加密后的数据 
	   */
	  public static String passwordMD5(String pw) {
	   try { 
		     // 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”） 
	         MessageDigest messageDigest =MessageDigest.getInstance("MD5"); 
	         // 输入的字符串转换成字节数组 
	         byte[] inputByteArray = pw.getBytes(); 
	         // inputByteArray是输入字符串转换得到的字节数组 
	         messageDigest.update(inputByteArray); 
	         // 转换并返回结果，也是字节数组，包含16个元素 
	         byte[] resultByteArray = messageDigest.digest(); 
	         // 字符数组转换成字符串返回 
	         return byteArrayToHex(resultByteArray); 
	      } catch (NoSuchAlgorithmException e) { 
	         return null; 
	      } 
	  }
	 /**
	   * 基于MAC地址生成20位数的序列号用于部署时的产品验证，
	   * 初始化时必须输入正确的验证码才能使用系统
	   * @param boolean 是否需要格式化  
	   * 		true 生成形如76BA8-E83A7-F2C16-3BA0F的序列号
	   *        false 生成形如76BA8E83A7F2C163BA0F的序列号
	   *             
	   * @return String 基于IP的20位序列号
	   * **/
	  public static String createSNCode(String mac,boolean isFormat){
		  String SN=passwordMD5(mac).substring(0,20);
		  String SNCode="";
		  if(isFormat){//需要格式化
			  for(int i=1;i<=SN.length()/5;i++){
				  SNCode+=SN.substring(5*(i-1), 5*i);
				  if(i!=4){
					  SNCode+="-"; 
				  }
			  }
		  }
		  else{
			  SNCode=SN;
		  }
		 System.out.println(SNCode);
		  return SNCode;
	  }
	/**判断字符串是否为MAC地址
	 * 格式形如A0-36-9F-A1-7A-4C
	 * @param String 需要判断的字符串
	 * @return boolean 
	 * 
	 * **/
	public static boolean macCheck(String text) {
		    boolean isMac=false;
	        if (text != null && !text.isEmpty()) {
	            // 定义正则表达式
	            String regex ="^([0-9|A-F]{2})-"
	            			  +"([0-9|A-F]{2})-"
	            			  +"([0-9|A-F]{2})-"
	            			  +"([0-9|A-F]{2})-"
	            			  +"([0-9|A-F]{2})-"
	            			  +"([0-9|A-F]{2}$)";
	            //String rege1 ="^(([\\d|A-F]){2}-){5}"+"([\\d|A-F]{2}$)";       
	            // 判断ip地址是否与正则表达式匹配
	            if (text.matches(regex)) {
	            	isMac=true;
	            } 
	        }
	       return isMac;
	}
	public static void main(String[] args) throws InterruptedException {
			final KeyMaker keyMaker = new KeyMaker();
			keyMaker.setVisible(true);
			keyMaker.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			keyMaker.setSize(310, 340);
			keyMaker.setLocationRelativeTo(null);
			keyMaker.setResizable(false);
			keyMaker.setTitle("光缆保护系统注册码生成器");
		}
	}

