package gr.fragment.projectwalnut.Core;

import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;


public class Network {

    private static final Pattern PATTERN_MAC =
            Pattern.compile("^([0-9A-Fa-f]{2}[\\.:-]){5}([0-9A-Fa-f]{2})$");

    private static final Pattern IPV4_PATTERN =
            Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");

    private static final Pattern IPV6_STD_PATTERN =
            Pattern.compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");

    private static final Pattern IPV6_HEX_COMPRESSED_PATTERN =
            Pattern.compile("^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$");


    public static String clean_ssid(String ssid) throws NullPointerException {

        if(ssid == null || ssid.equals("")) throw new NullPointerException();

        if(ssid.charAt(0) == '"' && ssid.charAt(ssid.length() -1) == '"')
            return ssid.substring(1, ssid.length() - 1);
        return ssid;
    }

    public static String get_device_ssid(){
        return clean_ssid(System.mWifiManager.getConnectionInfo().getSSID());
    }

    public static String get_bssid(){
        return System.mWifiManager.getConnectionInfo().getBSSID();
    }

    public static String int_to_ip(int i){
        return ( i & 0xFF) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ((i >> 24 ) & 0xFF );
    }

    public static boolean is_wifi_enabled(){
        return System.mWifiManager.isWifiEnabled();
    }

    public static boolean is_connected_to_wifi(){
        return Objects.requireNonNull(System.mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).isConnected();
    }

    public static boolean is_mac_address(final String macAddress) {
        return macAddress != null && PATTERN_MAC.matcher(macAddress).matches();
    }

    public static boolean is_ipv4_address(final String address) {
        return address != null && IPV4_PATTERN.matcher(address).matches();
    }

    public static boolean is_ipv6_address(final String address) {
        return address != null && (IPV6_STD_PATTERN.matcher(address).matches() || IPV6_HEX_COMPRESSED_PATTERN.matcher(address).matches());
    }

    private static ArrayList<String> getLinesInARPCache() {
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lines;
    }

    public static String getMACFromIPAddress(String ip) {
        if (ip == null) {
            return null;
        }

        for (String line : getLinesInARPCache()) {
            String[] splitted = line.split(" +");
            if (splitted.length >= 4 && ip.equals(splitted[0])) {
                String mac = splitted[3];
                if (mac.matches("..:..:..:..:..:..") && is_mac_address(mac)) {
                    return mac;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public static String get_device_ip(){
        return Formatter.formatIpAddress(System.mWifiManager.getConnectionInfo().getIpAddress());
    }

    @SuppressLint("HardwareIds")
    public static String get_device_mac(){
        return System.mWifiManager.getConnectionInfo().getMacAddress().toLowerCase();
    }

    public static boolean isPortReachableTCP(InetAddress ia, int portNo, int timeoutMillis){

        Socket s = null;
        try {
            s = new Socket();
            s.connect(new InetSocketAddress(ia, portNo), timeoutMillis);
            return true;
        } catch (IOException e) {
            // Don't log anything as we are expecting a lot of these from closed ports.
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean isPortReachableUDP(InetAddress ia, int portNo, int timeoutMillis) {

        try {
            byte[] bytes = new byte[128];
            DatagramPacket dp = new DatagramPacket(bytes, bytes.length);

            DatagramSocket ds = new DatagramSocket();
            ds.setSoTimeout(timeoutMillis);
            ds.connect(ia, portNo);
            ds.send(dp);
            ds.isConnected();
            ds.receive(dp);
            ds.close();

        } catch (SocketTimeoutException e) {
            return true;
        } catch (Exception ignore) {

        }
        return false;
    }

    public static class IP4Address {

        private byte[] mByteArray = null;
        private String mString = "";
        private final int mInteger;
        private InetAddress mAddress = null;

        public IP4Address(int address) throws UnknownHostException {
            mByteArray = new byte[4];
            mInteger = address;

            if(ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN){
                mByteArray[0] = (byte) (address & 0xFF);
                mByteArray[1] = (byte) (0xFF & address >> 8);
                mByteArray[2] = (byte) (0xFF & address >> 16);
                mByteArray[3] = (byte) (0xFF & address >> 24);
            } else{
                mByteArray[0] = (byte) (0xFF & address >> 24);
                mByteArray[1] = (byte) (0xFF & address >> 16);
                mByteArray[2] = (byte) (0xFF & address >> 8);
                mByteArray[3] = (byte) (address & 0xFF);
            }

            mAddress = InetAddress.getByAddress(mByteArray);
            mString = mAddress.getHostAddress();
        }

        public IP4Address(String address) throws UnknownHostException{
            mString = address;
            mAddress = InetAddress.getByName(address);
            mByteArray = mAddress.getAddress();
            mInteger = ((mByteArray[0] & 0xFF) << 24) +
                    ((mByteArray[1] & 0xFF) << 16) +
                    ((mByteArray[2] & 0xFF) << 8) +
                    (mByteArray[3] & 0xFF);
        }

        public IP4Address(byte[] address) throws UnknownHostException{
            mByteArray = address;
            mAddress = InetAddress.getByAddress(mByteArray);
            mString = mAddress.getHostAddress();
            mInteger = ((mByteArray[0] & 0xFF) << 24) +
                    ((mByteArray[1] & 0xFF) << 16) +
                    ((mByteArray[2] & 0xFF) << 8) +
                    (mByteArray[3] & 0xFF);
        }

        public IP4Address(InetAddress address){
            mAddress = address;
            mByteArray = address.getAddress();
            mString = mAddress.getHostAddress();
            mInteger = ((mByteArray[0] & 0xFF) << 24) +
                    ((mByteArray[1] & 0xFF) << 16) +
                    ((mByteArray[2] & 0xFF) << 8) +
                    (mByteArray[3] & 0xFF);
        }

        public byte[] toByteArray(){
            return mByteArray;
        }

        public String toString(){
            return mString;
        }

        public int toInteger(){
            return mInteger;
        }

        public InetAddress toInetAddress(){
            return mAddress;
        }

    }

}
