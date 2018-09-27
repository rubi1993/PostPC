import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class RemoteDroidServer {
    private static int x,y;
    private static int MAX_X;
    private static int MAX_Y;
    private static int MIN_X;
    private static int MIN_Y;
    private static ServerSocket server = null;
    private static Socket client = null;
    private static BufferedReader in = null;
    private static String line;
    private static boolean isConnected=true;
    private static Robot robot;
    protected static final int SERVER_PORT = 8998;

    private static void processSensorData(float movex,float movez,float movey){
        float magnitude=(float)Math.sqrt(movex*movex+movey*movey+movez*movez);
        if (magnitude<0.10){
            //         System.out.println("MAGNITUDE LESS THAN 0.25");

            return;
        }
        final float sens =35.0f;//15.0f
        int newx=(int)(movex*sens);
        int newy=(int)(movey*sens);
        move(newx,newy);

    }
    public static void move(int movex,int movey){
        x=(x+movex)>MAX_X?MAX_X:((x+movex)<MIN_X?MIN_X:x+movex);
        y=(y+movey)>MAX_Y?MAX_Y:((y+movey)<MIN_Y?MIN_Y:y+movey);
        robot.mouseMove(x,y);
    }
    public static String getLocalIp() {

        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            String ip = socket.getLocalAddress().getHostAddress();
            return ip;
        } catch (SocketException e) {
            System.out.println("Problem with datagram socket...");
            e.printStackTrace();
        } catch (UnknownHostException e) {
            System.out.println("Problem with unknown host...");
            e.printStackTrace();
        }
        return null;
    }
    public static void main() {
        System.out.println("new main thread started...");
        boolean leftpressed=false;
        boolean rightpressed=false;
        Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
        MAX_X=(int)screen.getWidth();
        MAX_Y=(int)screen.getHeight();
        MIN_X=0;
        MIN_Y=0;
        x=MAX_X/2;
        y=MAX_Y/2;
        try{
            robot = new Robot();
            server = new ServerSocket(SERVER_PORT); //Create a server socket on port 8998
            System.out.println(getLocalIp());
            client = server.accept(); //Listens for a connection to be made to this socket and accepts it
            in = new BufferedReader(new InputStreamReader(client.getInputStream())); //the input stream where data will come from client
        }catch (SocketException e) {
            System.out.println("Connection Closed.");
        } catch (IOException e) {
            System.out.println("Error in opening Socket");
            System.exit(-1);
        }catch (AWTException e) {
            System.out.println("Error in creating robot instance");
            System.exit(-1);
        }
        int counter = 0;
        //read input from client while it is connected
        while(isConnected){
            try{

                line = in.readLine(); //read input from client
//                System.out.println(line); //print whatever we get from client

                if (line == null) {
                    client = server.accept(); //Listens for a connection to be made to this socket and accepts it
                    in = new BufferedReader(new InputStreamReader(client.getInputStream())); //the input stream where data will come from client
                    line = in.readLine();
                }

                //if user clicks on next
                if(line.equalsIgnoreCase("next")){
                    //Simulate press and release of key 'n'
                    robot.keyPress(KeyEvent.VK_N);
                    robot.keyRelease(KeyEvent.VK_N);
                }
                //if user clicks on previous
                else if(line.equalsIgnoreCase("previous")){
                    //Simulate press and release of key 'p'
                    robot.keyPress(KeyEvent.VK_P);
                    robot.keyRelease(KeyEvent.VK_P);
                }
                //if user clicks on play/pause
                else if(line.equalsIgnoreCase("play")){
                    //Simulate press and release of spacebar
                    robot.keyPress(KeyEvent.VK_SPACE);
                    robot.keyRelease(KeyEvent.VK_SPACE);
                }
                //input will come in x,y format if user moves mouse on mousepad
                else if(line.contains(",")){
                    float movex=-Float.parseFloat(line.split(",")[2]);//extract movement in x direction
                    float movez=Float.parseFloat(line.split(",")[1]);//extract movement in y direction
                    float movey=-Float.parseFloat(line.split(",")[0]);
                    Point point = MouseInfo.getPointerInfo().getLocation(); //Get current mouse position
                    float nowx=point.x;
                    float nowy=point.y;
                    processSensorData(movex,movez,movey);
//                    robot.mouseMove((int)(nowx+movex),(int)(nowy+movey));//Move mouse pointer to new location
                }
                //if user taps on mousepad to simulate a left click
                else if(line.contains("left_click")){
                    //Simulate press and release of mouse button 1(makes sure correct button is pressed based on user's dexterity)
                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                }
                //Exit if user ends the connection
                else if(line.equalsIgnoreCase("exit")){
                    System.out.println("Exit thru equals, restarting connection");
                    client.close();
                    client = server.accept(); //Listens for a connection to be made to this socket and accepts it
                    in = new BufferedReader(new InputStreamReader(client.getInputStream())); //the input stream where data will come from client
                    line = in.readLine();
                }
            } catch (SocketException e) {
                System.out.println("Connection Closed.");

            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }
    }
}
