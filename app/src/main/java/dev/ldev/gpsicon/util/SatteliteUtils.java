package dev.ldev.gpsicon.util;


import android.location.GpsSatellite;

public final class SatteliteUtils {

    public static int getFoundCount(Iterable<GpsSatellite> sats) {
        if (sats == null)
            return 0;
        int usedSattelites = 0;
        for (GpsSatellite sat : sats) {
            if (sat.getSnr() > 0)
                usedSattelites++;
        }
        return usedSattelites;
    }

    public static int getFixedCount(Iterable<GpsSatellite> sats) {
        if (sats == null)
            return 0;
        int usedSattelites = 0;
        for (GpsSatellite sat : sats) {
            if (sat.usedInFix())
                usedSattelites++;
        }
        return usedSattelites;
    }

    public static int getTotalCount(Iterable<GpsSatellite> sats) {
        if (sats == null)
            return 0;
        int count = 0;
        for (GpsSatellite sat : sats) {
            count++;
        }
        return count;
    }
}
