package com.king.app.coolg.model.udp;

import com.google.gson.Gson;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.utils.DebugLog;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import io.reactivex.Observable;

/**
 * 描述: UDP监听，接收server ip
 * <p/>作者：景阳
 * <p/>创建时间: 2017/12/5 15:15
 */
public class UdpReceiver {

    public static final String UDP_SERVER_IDENTITY = "JJGALLERY_SERVER";

    private DatagramSocket udpSocket;
    private boolean isRun = true;

    public Observable<ServerBody> observeServer() {
        return Observable.create(obs -> {

            DebugLog.e("[socket]开始接收");
            try {
                udpSocket = new DatagramSocket(null);
                udpSocket.setReuseAddress(true);
                udpSocket.bind(new InetSocketAddress(AppConstants.PORT_RECEIVE));

                byte[] datas = new byte[1024];
                DatagramPacket packet = new DatagramPacket(datas, datas.length);
                while (isRun) {
                    udpSocket.receive(packet);
                    String body = new String(packet.getData(), 0, packet.getLength());
                    DebugLog.e("[socket]body:" + body);
                    ServerBody serverBody = new Gson().fromJson(body, ServerBody.class);
                    if (UDP_SERVER_IDENTITY.equals(serverBody.getIdentity())) {
                        String ip = packet.getAddress().getHostAddress();
                        serverBody.setIp(ip);
                        DebugLog.e("[socket]ip:" + ip + ", port:" + serverBody.getPort());
                        obs.onNext(serverBody);
                        obs.onComplete();
                    }
                    else {
                        obs.onError(new Exception("Unknown server"));
                    }
                }
            } catch (SocketException e) {
                DebugLog.e("[socket]catch SocketException " + e.getMessage());
                e.printStackTrace();
                obs.onError(e);
            } catch (IOException e) {
                DebugLog.e("[socket]catch IOException " + e.getMessage());
                e.printStackTrace();
                obs.onError(e);
            }
        });
    }

    public void destroy() {
        isRun = false;
        if (udpSocket != null) {
            udpSocket.close();
        }
    }
}
