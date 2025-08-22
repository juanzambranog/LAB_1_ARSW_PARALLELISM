package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT = 5;

    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     *
     * @param ipaddress suspicious host's IP address.
     * @param N number of threads to use.
     * @return Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int N) {

        LinkedList<Integer> blackListOcurrences = new LinkedList<>();
        int ocurrencesCount = 0;

        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();

        int totalServers = skds.getRegisteredServersCount();
        int divi = totalServers / N;
        int remainder = totalServers % N;

        List<HostBlackListThread> threads = new ArrayList<>();

        // 1. Crear y lanzar los hilos
        for (int i = 0; i < N; i++) {
            int start = i * divi;
            int end = (i + 1) * divi;

            if (i == N - 1) {
                end += remainder; // el último se queda con los que sobran
            }

            HostBlackListThread thread = new HostBlackListThread(ipaddress, start, end);
            threads.add(thread);
            thread.start();
        }

        // 2. Esperar a que todos los hilos terminen
        for (HostBlackListThread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Logger.getLogger(HostBlackListsValidator.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        // 3. Recolectar resultados
        for (HostBlackListThread thread: threads) {
            blackListOcurrences.addAll(thread.getServers());
            ocurrencesCount += thread.getFoundServers();
        }

        // 4. Decidir confiabilidad
        if (ocurrencesCount >= BLACK_LIST_ALARM_COUNT) {
            skds.reportAsNotTrustworthy(ipaddress);
        } else {
            skds.reportAsTrustworthy(ipaddress);
        }

        // 5. Log
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}",
                new Object[]{totalServers, skds.getRegisteredServersCount()});

        return blackListOcurrences;
    }

    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());

}
