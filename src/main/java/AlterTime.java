import java.util.Arrays;
import java.util.List;

/*
   first argument: 12-hour time string with the format "[H]H:MM{AM|PM}"
   second argument: (signed) integer. The second argument is the number of
                    minutes to add to the time of day represented by the first arg.

   AddMinutes("9:13 AM", 200) would return "12:33 PM"
*/
public class AlterTime {

    private int getHour(String timeStamp) {
        return Integer.parseInt(timeStamp.split(":")[0]);
    }

    private int getMinutes(String timeStamp) {
        int indexOfSemicolon = timeStamp.indexOf(":");
        return Integer.parseInt(timeStamp.substring(indexOfSemicolon + 1, indexOfSemicolon + 3));
    }

    private Period getPeriod(String timeStamp) {
        int indexOfSemicolon = timeStamp.indexOf(":");
        return Period.valueOf(timeStamp.substring(indexOfSemicolon + 3).trim());
    }

    private List<Integer> minsToHoursAndMins(Integer mins) {
        Integer hour = mins / 60;
        Integer remainingMins = mins - (hour * 60);
        return Arrays.asList(hour, remainingMins);
    }

    private boolean minsIsLessThanOneHourOrMoreThanHour(int mins) {
        return mins < 0 || mins >= 60;
    }

    private boolean hourIsBetweenNegativeTwelveAndPositiveTwelve(int hours) {
        return -12 < hours && hours < 12;
    }

    private int adjustHours(int hours) {
        // if hour less than -12 or greater than 12, adjust 12 / 15 -> 3, or 25/12 -> 1
        return  hourIsBetweenNegativeTwelveAndPositiveTwelve(hours) ? hours :
                hours < 0 ? -(Math.abs(hours) % 12) : hours % 12;
    }

    private String computeNewMins(int mins) {
        int newMins = minsIsLessThanOneHourOrMoreThanHour(mins) ? (mins < 0 ? mins + 60 : mins - 60) : mins;
        String minsToString;
        if (newMins < 10) {
            minsToString = String.format("0%s", String.valueOf(newMins));
        } else minsToString = String.valueOf(newMins);
        return minsToString;
    }

    private String computeNewHour(int hour, int hoursToAdd) {
        int newHour = adjustHours(hoursToAdd) + hour;
        // if new hour is 0 <= || >= 12, (+/-) 12 to compute new hour
        return String.valueOf(
                newHour > 0 && newHour <= 12 ? newHour :
                newHour <= 0 ? 12 + newHour : newHour  - 12
        );
    }

    private Period computeNewPeriod(int hour, int hoursToAdd, Period period) {
        // find 12 hour increments, if even amount (= days) then set flip to false
        boolean flip = !hourIsBetweenNegativeTwelveAndPositiveTwelve(hoursToAdd) && (Math.abs(hoursToAdd) / 12) % 2 != 0;
        int newHour = adjustHours(hoursToAdd) + hour;
        // if hour != 12 -> 12 + (1-11) AND newHour 0 < || >= 12 => flip again
        if (hour != 12 && (newHour < 0 || newHour >= 12)) flip = !flip;
        // if hour = 12 -> 12 - (1-11) => flip again
        if (hour == 12 && hourIsBetweenNegativeTwelveAndPositiveTwelve(hoursToAdd) && newHour < 12) flip = !flip;
        return flip ? Period.opposite(period) : period;
    }

    public String addMinutes(String timeStamp, Integer minuntesToAdd) {
        if (minuntesToAdd == 0) return timeStamp;
        int initialHour = getHour(timeStamp);
        int initialMins = getMinutes(timeStamp);
        Period period = getPeriod(timeStamp);
        List<Integer> hoursAndMins = minsToHoursAndMins(minuntesToAdd);
        int hoursToAdd = hoursAndMins.get(0);
        int remainingMinsToAdd = hoursAndMins.get(1);

        // if mins + remaining mins >= 60 || <= 60 -> update hour by 1
        if (minsIsLessThanOneHourOrMoreThanHour(initialMins + remainingMinsToAdd)) {
            hoursToAdd = minuntesToAdd < 0 ? hoursToAdd - 1 : hoursToAdd + 1;
        }

        String newMins = computeNewMins(initialMins + remainingMinsToAdd);
        String newHour = computeNewHour(initialHour, hoursToAdd);
        Period newPeriod = computeNewPeriod(initialHour, hoursToAdd, period);
        return String.format("%s:%s %s", newHour, newMins, newPeriod.toString());
    }
}

enum Period {
    AM,
    PM;

    public static Period opposite(Period period) {
        return period.equals(AM) ? PM : AM;
    }
}
