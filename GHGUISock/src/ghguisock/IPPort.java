/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ghguisock;

/**
 *
 * @author admin
 */
public class IPPort {
    
    private final int[] sections = new int[5];
    
    public IPPort(String ip, String port){
        setIP(ip);
        setPort(port);
        System.out.println("new IPPort: "+this);
    }
    
    public IPPort(){
        this("0.0.0.0", "6969");
    }
    
    static boolean validIP(String ip)
    {
        boolean result = false;
        if (ip.matches("[0-9]{1,3}(\\.[0-9]{1,3}){3}")) {
            String[] sub = ip.split("\\.");
            for (String s : sub) {
                if (Integer.parseInt(s) < 256) {
                    result = true;
                }
            }
        }
        return result;
    }
    static boolean validPort(String port)
    {
        return port.matches("[0-9]{4,}");
    }
    
    public void setIPPort(String ip, String port)
    {
        setIP(ip);
        setPort(port);
    }
    
    public void setIP(String ip)
    {
        if (validIP(ip))
        {
            String[] sub = ip.split("\\.");
            for(int a = 0; a < 4; a++)
            {
                sections[a] = Integer.parseInt(sub[a]);
            }
        }
    }
    
    public void setPort(String port){
        if(validPort(port)){
            sections[4] = Integer.parseInt(port);
        }
    }
    
    public String getIPPort()
    {
        return this.toString();
    }
    
    @Override
    public String toString()
    {
        return getIP()+":"+getPort();
    }
    
    public String getIP()
    {
        return sections[0]+"."+sections[1]+"."+sections[2]+"."+sections[3];
    }
    
    public String getPort()
    {
        return Integer.toString(sections[4]);
    }
    
    public int getPortI()
    {
        return sections[4];
    }
}
