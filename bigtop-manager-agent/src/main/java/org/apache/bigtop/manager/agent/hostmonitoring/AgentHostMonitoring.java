package org.apache.bigtop.manager.agent.hostmonitoring;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AgentHostMonitoring {

    private static boolean sameSubnet(String ipAddress, String subnetMask, String gateway) throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(ipAddress);
        InetAddress subnetAddress = InetAddress.getByName(subnetMask);
        InetAddress gatewayAddress = InetAddress.getByName(gateway);
        byte[] ipBytes = inetAddress.getAddress();
        byte[] subnetBytes = subnetAddress.getAddress();
        byte[] gatewayBytes = gatewayAddress.getAddress();
        for (int i = 0; i < ipBytes.length; i++) {
            if ((ipBytes[i] & subnetBytes[i]) != (gatewayBytes[i] & subnetBytes[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * get Host Info
     * hostname, ip,gateway, os info, cpu arch etc.
     *
     * @return JsonNode
     */
    public static JsonNode getHostInfo() throws UnknownHostException {
        ObjectMapper json = new ObjectMapper();
        ObjectNode objectNode = json.createObjectNode();
        SystemInfo si = new SystemInfo();
        OperatingSystem operatingSystem = si.getOperatingSystem();
        HardwareAbstractionLayer hal = si.getHardware();

        NetworkParams networkParams = operatingSystem.getNetworkParams();
        String hostName = networkParams.getHostName();
        String ipv4DefaultGateway = networkParams.getIpv4DefaultGateway();
        objectNode.put("hostname", hostName)
                .put("os", operatingSystem.toString())
                .put("ipv4Gateway", ipv4DefaultGateway)
                .put("upTimeHours", operatingSystem.getSystemUptime() / 3600)
                .put("bootTime", LocalDateTime.ofEpochSecond(operatingSystem.getSystemBootTime(), 0, ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        for (NetworkIF networkIF : hal.getNetworkIFs()) {
            String[] iPv4addr = networkIF.getIPv4addr();
            if (null == iPv4addr || iPv4addr.length == 0) {
                continue;
            }
            short subnetMaskLen = networkIF.getSubnetMasks()[0];
            String subnetMask = "255.255.255.0";
            if (subnetMaskLen == 8) {
                subnetMask = "255.0.0.0";
            } else if (subnetMaskLen == 16) {
                subnetMask = "255.255.0.0";
            }
            List<InterfaceAddress> interfaceAddresses = networkIF.queryNetworkInterface().getInterfaceAddresses();
            for (InterfaceAddress ifaddr : interfaceAddresses) {
                if (null != ifaddr.getBroadcast() && sameSubnet(iPv4addr[0], subnetMask, ipv4DefaultGateway)) {
                    objectNode.put("iPv4addr", iPv4addr[0]);
                }
            }
        }
        // MEM
        GlobalMemory memory = hal.getMemory();
        objectNode.put("idle_mem", memory.getAvailable())
                .put("total_mem", memory.getTotal());
        // DISK
        List<OSFileStore> fileStores = operatingSystem.getFileSystem().getFileStores(true);
        for (OSFileStore fileStore : fileStores) {
            if (fileStore.getTotalSpace() <= 1024 * 1024 * 1024) {
                continue;
            }
            objectNode.put("total_space", fileStore.getTotalSpace());
            objectNode.put("free_space", fileStore.getFreeSpace());
        }
        // CPU
        CentralProcessor cpu = hal.getProcessor();
        double[] systemLoadAverage = cpu.getSystemLoadAverage(3);
        String loadAvg = Arrays.stream(systemLoadAverage)
                .mapToObj(v -> new DecimalFormat("#.00").format(v))
                .collect(Collectors.joining(","));

        long[] systemCpuLoadTicks1 = cpu.getSystemCpuLoadTicks();
        Util.sleep(3000);
        objectNode.put("cpu_info", cpu.getProcessorIdentifier().getMicroarchitecture())
                .put("logical_cores", cpu.getLogicalProcessorCount())
                .put("physical_cores", cpu.getPhysicalProcessorCount())
                .put("load_avg", loadAvg)
                .put("cpu_usage", cpu.getSystemCpuLoadBetweenTicks(systemCpuLoadTicks1));
        return objectNode;
    }


}
