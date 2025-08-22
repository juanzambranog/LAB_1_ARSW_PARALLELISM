package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

import java.util.LinkedList;
import java.util.List;

public class HostBlackListThread extends Thread {

    HostBlacklistsDataSourceFacade facade;
    String ip;

    int server_start;

    int server_end;


    List<Integer> cantServersFound = new LinkedList<>();

    int found = 0;


    public HostBlackListThread(String ip, int server_start, int server_end) {

        this.facade = HostBlacklistsDataSourceFacade.getInstance();
        this.ip = ip;
        this.server_start = server_start;
        this.server_end = server_end;

    }

    /*@Override
    public void run() {

        for (int i = server_start; i<server_end -1; i++) {
            if (facade.isInBlackListServer(i, ip)) {
                found++;
                cantServersFound.add(i);
            }
        }


    }*/

    @Override
    public void run() {
        try {
            for (int i = server_start; i < server_end -1; i++) {
                if (facade.isInBlackListServer(i, ip)) {
                    cantServersFound.add(i);
                    found++;
                }
            }

            // Mantener el hilo vivo para poder verlo en VisualVM
            Thread.sleep(2000); // 3 segundos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public int getFoundServers(){
       return found;
    }

    public List<Integer> getServers(){
        return cantServersFound;
    }

}



