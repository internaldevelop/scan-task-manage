package com.toolkit.scantaskmng.global.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.hyperic.sigar.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SigarUtils {
    private static Sigar sigar;

    public static Sigar getSigar() {
        if (sigar == null) {
            sigar = new Sigar();
        }
        return sigar;
    }

    public static CpuInfo[] getCpuInfos() {
        CpuInfo[] cpuInfos = new CpuInfo[0];
        try {
            cpuInfos = getSigar().getCpuInfoList();
        } catch (SigarException e) {
            e.printStackTrace();
        }

        return cpuInfos;
    }

    public static CpuPerc[] getCpuPercent() {
        CpuPerc[] cpuPercs = new CpuPerc[0];
        try {
            cpuPercs = getSigar().getCpuPercList();
        } catch (SigarException e) {
            e.printStackTrace();
        }

        return cpuPercs;
    }

    public static Mem getMemInfos() {
        Mem memory = null;
        try {
            memory = getSigar().getMem();
        } catch (SigarException e) {
            e.printStackTrace();
        }

        return memory;
    }

    public static Swap getSwapInfos() {
        Swap swap = null;
        try {
            swap = getSigar().getSwap();
        } catch (SigarException e) {
            e.printStackTrace();
        }

        return swap;
    }

    public static Who[] getWhoInfos() {
        Who[] whos = new Who[0];
        try {
            whos = getSigar().getWhoList();
        } catch (SigarException e) {
            e.printStackTrace();
        }

        return whos;
    }

    public static JSONArray getFSInfos() {
        JSONArray infos = new JSONArray();
        try {
            FileSystem[] fileSystems = getSigar().getFileSystemList();
            infos = (JSONArray)JSONArray.toJSON(fileSystems);
            for (Iterator it = infos.iterator(); it.hasNext(); ) {
                JSONObject fs = (JSONObject) it.next();
                FileSystemUsage usage = getSigar().getFileSystemUsage(fs.getString("dirName"));
                // TYPE_LOCAL_DISK: 2, TYPE_NETWORK: 3, TYPE_RAM_DISK(FlashDisk): 4, TYPE_CDROM: 5
                if (fs.getIntValue("type") == 2) {
                    // Unit: KBytes
                    fs.put("total", usage.getTotal());
                    fs.put("free", usage.getFree());
                    fs.put("avail", usage.getAvail());
                    fs.put("used", usage.getUsed());
                    double freePercent = 100D * usage.getFree() / usage.getTotal();
                    fs.put("usedPercent", 100 - freePercent);
                    fs.put("freePercent", freePercent);
                }
            }
//            for (JSONObject fs: infos)
        } catch (SigarException e) {
            e.printStackTrace();
        }

        return infos;
    }

    public static List<NetInterfaceConfig> getNetIConfig() {
        List<NetInterfaceConfig> configs = new ArrayList<>();
        try {
            String[] iFaces = getSigar().getNetInterfaceList();
            for (String iface: iFaces) {
                NetInterfaceConfig cfg = getSigar().getNetInterfaceConfig(iface);
                if (NetFlags.LOOPBACK_ADDRESS.equals(cfg.getAddress()) ||
                        (cfg.getFlags() & NetFlags.IFF_LOOPBACK) !=0 ||
                        NetFlags.NULL_HWADDR.equals(cfg.getHwaddr())) {
                    continue;
                }
                configs.add(cfg);
            }
        } catch (SigarException e) {
            e.printStackTrace();
        }

        return configs;
    }

    public static JSONObject getDomainInfos() {
        JSONObject infos = new JSONObject();
        try {
            String hostName = InetAddress.getLocalHost().getCanonicalHostName();
            infos.put("hostName", hostName);
            infos.put("FQDN", getSigar().getFQDN());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SigarException e) {
            e.printStackTrace();
        }

        return infos;
    }

    public static JSONArray getIFStatInfos() {
        JSONArray infos = new JSONArray();
        try {
            String[] ifNames = getSigar().getNetInterfaceList();
            for (String name: ifNames) {
                // iface-stat
                NetInterfaceStat stat = getSigar().getNetInterfaceStat(name);
                JSONObject info = (JSONObject) JSONObject.toJSON(stat);

                // iface-config
                NetInterfaceConfig config = getSigar().getNetInterfaceConfig(name);
                info.put("name", name);
                info.put("address", config.getAddress());
                info.put("mask", config.getNetmask());

                infos.add(info);
            }
        } catch (SigarException e) {
            e.printStackTrace();
        }

        return infos;
    }

    public static Object getSystemProps() {
//        getSigar().
        return System.getProperties();
    }
}
