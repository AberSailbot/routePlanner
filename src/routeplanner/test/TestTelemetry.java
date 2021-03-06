package routeplanner.test;
import javax.swing.JFrame; 
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JDialog;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.IOException;
import java.util.Random;

public class TestTelemetry extends JFrame
{
    long time; //time message was sent
    double lat,lon;
    int wpnum;
    double distance; //distnace to next wp
    int wpHeading;
    double wpLat,wpLon;
    double speed; //GPS speed
    int heading; //compass heading
    short leftSpeed,rightSpeed; //motor speeds
    double currents[] = new double[10]; //current sensor readings
    double xte; //cross track error
    DatagramSocket ds;
    String lastMsg;


    static long last_full_msg=0;
    static long old_time;
    static double old_lat;
    static double old_lon;
    static int old_wpnum;
    static double old_distance;
    static int old_wp_heading;
    static double old_udp_xte;
    static double old_wp_lat;
    static double old_wp_lon;
    static double old_speed;
    static int old_heading;
    static int old_left_speed;
    static int old_right_speed;
    static double old_currents[] = new double[10];

    JLabel timeLabel;
    JLabel hostLabel;
    JLabel ageLabel;
    
    JLabel latLabel;
    JLabel lonLabel;
    JLabel speedLabel;
    JLabel headingLabel;
    JLabel headingErrorLabel;
    JLabel xteLabel;
    
    JLabel wpNumLabel;
    JLabel wpDistLabel;
    JLabel wpHeadingLabel;
    JLabel wpLatLabel;
    JLabel wpLonLabel;
    JLabel leftSpeedLabel;
    JLabel rightSpeedLabel;

    JLabel lastMsgLabel;
    
    JLabel currentLabel[] = new JLabel[10];

    static final int FULL_MSG_INTERVAL=60;

  public TestTelemetry() throws Exception
  {

        ReceiveThread r = new ReceiveThread(this);
        r.start();

        ds = new DatagramSocket();
        JPanel leftPanel = new JPanel();
        JPanel centrePanel = new JPanel();
        JPanel rightPanel = new JPanel();
        Font textFont = new Font("SansSerif",Font.PLAIN,12);

        leftPanel.setLayout(new GridLayout(12,1));
        centrePanel.setLayout(new GridLayout(12,1));
        rightPanel.setLayout(new GridLayout(12,1));

        leftPanel.setFont(textFont);
        centrePanel.setFont(textFont);
        rightPanel.setFont(textFont);

        timeLabel = new JLabel();
        ageLabel = new JLabel();
        latLabel = new JLabel();
        lonLabel = new JLabel();
        speedLabel = new JLabel();
        headingLabel = new JLabel();
        headingErrorLabel = new JLabel();
        xteLabel = new JLabel();
        leftSpeedLabel = new JLabel();
        rightSpeedLabel = new JLabel();
        lastMsgLabel = new JLabel();

        timeLabel.setFont(textFont);
        timeLabel.setForeground(Color.BLACK);
        
        ageLabel.setFont(textFont);
        ageLabel.setForeground(Color.BLACK);
        
        latLabel.setFont(textFont);
        latLabel.setForeground(Color.BLACK);
        
        lonLabel.setFont(textFont);
        lonLabel.setForeground(Color.BLACK);
        
        speedLabel.setFont(textFont);
        speedLabel.setForeground(Color.BLACK);
        
        headingLabel.setFont(textFont);
        headingLabel.setForeground(Color.BLACK);
        
        headingErrorLabel.setFont(textFont);
        headingErrorLabel.setForeground(Color.BLACK);
       
        xteLabel.setFont(textFont);
        xteLabel.setForeground(Color.BLACK);

        leftSpeedLabel.setFont(textFont);
        leftSpeedLabel.setForeground(Color.BLACK);
        
        rightSpeedLabel.setFont(textFont);
        rightSpeedLabel.setForeground(Color.BLACK);

        lastMsgLabel.setFont(textFont);
        lastMsgLabel.setForeground(Color.BLACK);

        leftPanel.add(timeLabel);
        leftPanel.add(ageLabel);
        leftPanel.add(latLabel);
        leftPanel.add(lonLabel);
        leftPanel.add(speedLabel);
        leftPanel.add(headingLabel);
        leftPanel.add(headingErrorLabel);
        leftPanel.add(xteLabel);
        leftPanel.add(leftSpeedLabel);
        leftPanel.add(rightSpeedLabel);
        leftPanel.add(lastMsgLabel);
    
        wpNumLabel = new JLabel();
        wpDistLabel = new JLabel();
        wpHeadingLabel = new JLabel();
        wpLatLabel = new JLabel();
        wpLonLabel = new JLabel();


        for(int i=0;i<currentLabel.length;i++)
        {
          currentLabel[i] = new JLabel();
          currentLabel[i].setFont(textFont);
          currentLabel[i].setForeground(Color.BLACK);
        }
        
        wpNumLabel.setFont(textFont);
        wpNumLabel.setForeground(Color.BLACK);
        
        wpDistLabel.setFont(textFont);
        wpDistLabel.setForeground(Color.BLACK);
        
        wpHeadingLabel.setFont(textFont);
        wpHeadingLabel.setForeground(Color.BLACK);
        
        wpLatLabel.setFont(textFont);
        wpLatLabel.setForeground(Color.BLACK);
        
        wpLonLabel.setFont(textFont);
        wpLonLabel.setForeground(Color.BLACK);
        

        
        rightPanel.add(wpNumLabel);
        rightPanel.add(wpDistLabel);
        rightPanel.add(wpHeadingLabel);
        rightPanel.add(wpLatLabel);
        rightPanel.add(wpLonLabel);

        for(int i=0;i<currentLabel.length;i++)
        {
          centrePanel.add(currentLabel[i]);
        }
        
        this.setBounds(0,0,700,270);
        this.setTitle("Test Data Transmission");
        this.setLayout(new GridLayout(1,3));
        this.ds = ds;

        this.add(leftPanel);
        this.add(centrePanel);
        this.add(rightPanel);



        /*this.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                setVisible(false);
            }
        });*/

        this.setVisible(true);
  }

    
    //calculates difference between two headings taking wrap around into account
    private int getHdgDiff(int heading1,int heading2)
    {
        int result;
    
        result = heading1-heading2;
        
        if(result<-180)
        {
            result = 360 + result;
            return result;
        } 
        if(result>180)
        {
            result = 0 - (360-result);
        }
        return result;
    }

  public void updateData()
  {
      //timeLabel.setText("Time: " + new java.util.Date(old_time*1000.toString()));
  //timeLabel.setText("Time: " + ds.getTime());
      ageLabel.setText("Message Age: " + ((System.currentTimeMillis()/1000)-old_time));
      
      latLabel.setText("Lat: " + old_lat);
      lonLabel.setText("Lon: " + old_lon);
      speedLabel.setText("Speed: " + old_speed);
      headingLabel.setText("Heading: "+ old_heading);
      headingErrorLabel.setText("Heading Error: "+ getHdgDiff((int)old_heading,old_wp_heading));
      xteLabel.setText("Cross track err: " + old_udp_xte);
      
      wpNumLabel.setText("Waypoint Number: " + old_wpnum);
      wpDistLabel.setText("Waypoint Distance: " + old_distance);
      wpHeadingLabel.setText("Waypoint Heading: " + old_wp_heading);
      wpLatLabel.setText("Waypoint Lat: " + old_wp_lat);
      wpLonLabel.setText("Waypoint Lon: " + old_wp_lon);

      leftSpeedLabel.setText("Left Motor: " + old_left_speed);
      rightSpeedLabel.setText("Right Motor: " + old_right_speed);

      lastMsgLabel.setText("Last Message: " + lastMsg);

      for(int i=0;i<currentLabel.length;i++)
      {         
        currentLabel[i].setText("Current Sensor " + i + ": " + currents[i]);
      }
      
  }

  public void udp_send(long time,double lat,double lon,int wpnum,double distance,int wp_heading,double udp_xte,double wp_lat,double wp_lon,double speed,int heading,int left_speed,int right_speed,double currents[]) throws Exception
  {
    String databuf="";
    int broadcast=1;
    int param_count=0;
    

         
    if(old_time!=time||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "time="+time + " ";
      this.time=time;
      param_count++;
    }


    if(old_lat!=lat||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "lat="+lat + " ";
      this.lat=lat;
      param_count++;
    }

    
    if(old_lon!=lon||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf=databuf+"lon=" + lon + " ";
      this.lon=lon;
      param_count++;
    }

    
    if(old_wpnum!=wpnum||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "wpnum=" + wpnum  + " ";
      this.wpnum=wpnum;
      param_count++;
    }

    
    if(old_distance!=distance||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "wpdist="+ distance + " ";
      this.distance = distance;
      param_count++;
  
    }            

    
    if(old_wp_heading!=wp_heading||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "wphdg="+wp_heading + " ";
      this.wpHeading = wp_heading;
      param_count++;
    }    

    
    if(old_udp_xte!=udp_xte||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "xte=" + udp_xte + " ";
      this.xte=udp_xte;
      param_count++;
    }    

    
    if(old_wp_lat!=wp_lat||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "wplat=" + wp_lat  + " ";
      this.wpLat=wp_lat;
      param_count++;
    }    


    if(old_wp_lon!=wp_lon||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "wplon="+wp_lon  + " ";
      this.wpLon=wp_lon;
      param_count++;
    }    

    
    if(old_speed!=speed||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "speed=" + speed  + " ";
      this.speed=speed;
      param_count++;
    }        

    
    if(old_heading!=heading||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "hdg=" + heading  + " ";
      this.heading=heading;
      param_count++;
    }        

    
    if(old_left_speed!=left_speed||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "lspd=" + left_speed +  " ";
      this.leftSpeed=(short)left_speed;
      param_count++;
    }        


    if(old_right_speed!=right_speed||(time-last_full_msg)>FULL_MSG_INTERVAL)
    {
      databuf = databuf + "rspd=" +  right_speed  + " ";
      this.rightSpeed=(short)right_speed;
      param_count++;
    }        

    
    for(int i=0;i<currents.length;i++)
    {
      if(old_currents[i]!=currents[i]||(time-last_full_msg)>FULL_MSG_INTERVAL)
      {
        databuf = databuf + "c"+i+"=" + currents[i]  + " ";
        this.currents[i]=currents[i];
        param_count++;
      }            
    }
      

    if(param_count==17)
    {
      last_full_msg=time;
    }
    
    old_time=time;
    old_lat=lat;
    old_lon=lon;
    old_wpnum=wpnum;
    old_distance=distance;
    old_wp_heading=wp_heading;
    old_udp_xte=udp_xte;
    old_wp_lat=wp_lat;
    old_wp_lon=wp_lon;
    old_speed=speed;
    old_heading=heading;
    old_left_speed=left_speed;
    old_right_speed=right_speed;
    
    for(int i=0;i<currents.length;i++)
    {
      old_currents[i] = currents[i];
    }
    
    ds.send(new DatagramPacket(databuf.getBytes(),databuf.length(),InetAddress.getByName("127.255.255.255"),4321));
    updateData();
  }

  static double rad2deg(double x)
  {
    return (180/Math.PI) * x;
  }

  static double deg2rad(double x)
  {
    return x * Math.PI/180;
  }

  public void setWaypoint(int newWaypoint)
  {
    wpnum = newWaypoint;
  }

  public int getWpNum()
  {
    return wpnum;
  }

  public void setLastMsg(String msg)
  {
    lastMsg = msg;
  }

  public void gotoLatLon(float newLat,float newLon)
  {
    wpLat=newLat;
    wpLon=newLon;
  }

  public void loop() throws Exception
  {
    double distance=1.0;
    int heading=0;
    int i=0;
    double speed=0.0;
    long time;
    double transmit_currents[] =  {1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0};

    Random rand = new Random();

    while(true)
    {
      time=System.currentTimeMillis()/1000;
        if(time%10==0)
        {
          //randomize some small changes so we aren't always shown in the same place
          double r = rand.nextDouble();
          r=r/500.0;
          r=r-0.0005;
          
          lat=52.415;
          lat=lat+r;
          
           r = rand.nextDouble();
          r=r/500.0;
          r=r-0.0005;
          
          lon=-4.065;
          lon=lon+r;
        }

      
      //int udp_send(long time,double lat,double lon,int wpnum,double distance,int wp_heading,double udp_xte,double wp_lat,double wp_lon,double speed,int heading,int left_speed,int right_speed,double current1,double current2,double current3,double current4);
        udp_send(time,lat,lon,wpnum,distance,90,1.0,wpLat,wpLon,speed,heading,128,128,transmit_currents);
        
        
        if(time%5==0)
        {
          speed=speed+0.1;
        }
        
        distance=distance+0.1;
        if(distance>2.0)
        {
            distance=1.0;
            heading++;
            wpnum++;
            if(heading>=360)
            {
              heading=0;
            }
        }
        try
        {
          Thread.sleep(1000);
        }
        catch(Exception e){}
    }
  }

  public static void main(String args[]) throws Exception
  {
    
    TestTelemetry test = new TestTelemetry();
    test.loop();
  }

  public class ReceiveThread extends Thread
  {
  public static final byte NUDGE_LEFT=1;
  public static final byte NUDGE_RIGHT=2;
  public static final byte STOP=3;
  public static final byte SKIP_WP=4;
  public static final byte GO_HOME=5;
  public static final byte GOTO_WP=6;
  public static final byte GOTO_LATLON=7;
  public static final byte LEFT=8;
  public static final byte RIGHT=9;
  public static final byte BACK=10;
  public static final byte FORWARD=11;
  public static final byte START=12;
  public static final byte START_GENERATOR=13;
  public static final byte STOP_GENERATOR=14;


    DatagramSocket serverSocket;
    byte[] buf = new byte[1024];
    TestTelemetry gui;

    public ReceiveThread(TestTelemetry gui) throws SocketException
    {
      serverSocket = new DatagramSocket(1235);
      this.gui=gui;
    }

    public void run()
    {
        try {
         recieve();
        }
        catch(IOException e)
        {
          e.printStackTrace();
        }
    }

    public void recieve() throws IOException
    {
      while(true)
      {
        DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
        serverSocket.receive(receivePacket);
        
                
        if(NUDGE_LEFT==buf[0])
          {

            gui.setLastMsg("Nudge Left");
          }
          else if(NUDGE_RIGHT==buf[0])
          {
          gui.setLastMsg("Nudge Right");
            
          }
          else if(STOP==buf[0])
          {
          gui.setLastMsg("STOP");
            

          }
          else if(SKIP_WP==buf[0])
          {
                      gui.setLastMsg("Skip Wp");
            gui.setWaypoint(gui.getWpNum()+1);
            
          }
          else if(GO_HOME==buf[0])
          {
                      gui.setLastMsg("Go Home");

          }
          else if(GOTO_WP==buf[0])
          {   
            int new_wp = (int) buf[1];
            gui.setWaypoint(new_wp);
                      gui.setLastMsg("Go To WP " + new_wp);

          }
          else if(FORWARD==buf[0])
          {
          gui.setLastMsg("Foward");
            
            
          }
          else if(BACK==buf[0])
          {
                      gui.setLastMsg("Back");

            
          }
          else if(LEFT==buf[0])
          {
                      gui.setLastMsg("Left");

          }
          else if(RIGHT==buf[0])
          {
                      gui.setLastMsg("Right");

          } 
          else if(GOTO_LATLON==buf[0])
          {
            //decode lat lon
            int tmp_lat=0,tmp_lon=0;
            
            tmp_lat = (((int)buf[4]) << 24)&0xFF000000;
            tmp_lat = tmp_lat + ((((int)buf[3]) << 16)&0x00FF0000);
            tmp_lat = tmp_lat + ((((int)buf[2]) << 8)&0x0000FF00);
            tmp_lat = tmp_lat + (((int)buf[1])&0x000000FF);

            
            tmp_lon = (((int)buf[8]) << 24)&0xFF000000;
            tmp_lon = tmp_lon + ((((int)buf[7]) << 16)&0x00FF0000);
            tmp_lon = tmp_lon + ((((int)buf[6]) << 8)&0x0000FF00);
            tmp_lon = tmp_lon + ((int)buf[5]&0x000000FF);
          
            float new_lat = Float.intBitsToFloat(tmp_lat);
            float new_lon = Float.intBitsToFloat(tmp_lon);

          gui.setLastMsg("Go to lat lon " + new_lat + ","+new_lon);

            
            gui.gotoLatLon(new_lat,new_lon);
          }
          else if(START==buf[0])
          {        
            gui.setLastMsg("START\n");
          }
          else if(START_GENERATOR==buf[0])
          {        
            gui.setLastMsg("Start Geneartor\n");
          }
          else if(STOP_GENERATOR==buf[0])
          {        
            gui.setLastMsg("Stop Generator\n");
          }

      }
    }
  }
}

