package Likelion14th_HufsGlobal_Hackathon_team1.MCM_Archiv.global.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public final class TimeUtils {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private TimeUtils() {
    }

    public static LocalDate toKstDate(Instant instant) {
        return instant.atZone(KST).toLocalDate();
    }

    public static Instant kstStartOfDayToInstant(LocalDate date) {
        return date.atStartOfDay(KST).toInstant();
    }
}
