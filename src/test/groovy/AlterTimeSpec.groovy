import spock.lang.Specification
import spock.lang.Unroll

class AlterTimeSpec extends Specification {

    AlterTime alterTime = new AlterTime()

    @Unroll
    def "should get hour from timestamp"() {
        expect:
        assert alterTime.getHour(timeStamp) == hour

        where:
        timeStamp  | hour
        "9:13 AM"  | 9
        "10:10 PM" | 10
        "1:05 AM"  | 1
    }

    @Unroll
    def "should get minutes from timestamp"() {
        expect:
        assert alterTime.getMinutes(timeStamp) == mins

        where:
        timeStamp  | mins
        "9:13 AM"  | 13
        "10:00 PM" | 0
        "1:05AM"   | 5
    }

    @Unroll
    def "should get period from timeStamp"() {
        expect:
        assert alterTime.getPeriod(timeStamp) == period

        where:
        timeStamp  | period
        "9:13 AM"  | Period.AM
        "10:00 PM" | Period.PM
        "1:05AM"   | Period.AM
    }

    @Unroll
    def "should convert mins to hours and remaining mins"() {
        expect:
        assert alterTime.minsToHoursAndMins(mins) == hourAndMin

        where:
        mins  | hourAndMin
        200   | [3, 20]
        -200  | [-3, -20]
        232   | [3, 52]
        60    | [1, 0]
        1440  | [24, 0]
        -1440 | [-24, 0]
        5     | [0, 5]
        -5    | [0, -5]
        -72   | [-1, -12]
        -500  | [-8, -20]

    }

    @Unroll
    def "should compute new mins"() {
        expect:
        assert alterTime.computeNewMins(mins) == newMins

        where:
        mins | newMins
        0    | "00"
        10   | "10"
        70   | "10"
        60   | "00"
        -60  | "00"
        -10  | "50"
        -5   | "55"
        -55  | "05"
    }

    @Unroll
    def "should compute new hour"() {
        expect:
        assert alterTime.computeNewHour(initialHour, hoursToAdd) == newHour

        where:
        initialHour | hoursToAdd | newHour
        5           | 5          | "10"
        5           | -5         | "12"
        12          | 1          | "1"
        12          | - 1        | "11"
        9           | 3          | "12"
        9           | -3         | "6"
        9           | 4          | "1"
        9           | 15         | "12"
        9           | -15        | "6"
        5           | 12         | "5"
        3           | 24         | "3"
        3           | -24        | "3"
        5           | 26         | "7"
        1           | 32         | "9"
        5           | -4         | "1"
        5           | -12        | "5"
        2           | -18        | "8"
        5           | -26        | "3"
        1           | -32        | "5"
        1           | -1         | "12"

    }

    @Unroll
    def "should compute new period"() {
        expect:
        assert alterTime.computeNewPeriod(initialHour, hoursToAdd, period).equals(newPeriod)

        where:
        initialHour | hoursToAdd | period    | newPeriod
        1           | 2          | Period.AM | Period.AM
        1           | -2         | Period.AM | Period.PM
        12          | 1          | Period.AM | Period.AM
        12          | 1          | Period.PM | Period.PM
        12          | -1         | Period.PM | Period.AM
        12          | -1         | Period.AM | Period.PM
        12          | 12         | Period.AM | Period.PM
        12          | -12        | Period.AM | Period.PM
        12          | 18         | Period.AM | Period.PM
        12          | -18        | Period.AM | Period.PM
        1           | 11         | Period.AM | Period.PM
        1           | -11        | Period.AM | Period.PM
        1           | 12         | Period.AM | Period.PM
        1           | -12        | Period.AM | Period.PM
        1           | 15         | Period.AM | Period.PM
        1           | -15        | Period.AM | Period.AM
        1           | 24         | Period.AM | Period.AM
        1           | -24        | Period.AM | Period.AM
        1           | 32         | Period.AM | Period.AM
        1           | -32        | Period.AM | Period.PM
        1           | 36         | Period.AM | Period.PM
        1           | -36        | Period.AM | Period.PM
    }

    @Unroll
    def "should add mins"() {
        expect:
        assert alterTime.addMinutes(timestamp, minsToAdd) == expect

        where:
        timestamp | minsToAdd | expect
        "9:13 AM" | 200       | "12:33 PM"
        "9:13 AM" | 1440      | "9:13 AM"
        "9:13 AM" | 0         | "9:13 AM"
        "12:13 PM"| 500       | "8:33 PM"
        "11:55 AM"| 5         | "12:00 PM"
        "12:55 AM"| 5         | "1:00 AM"
        "12:05 PM"| -5        | "12:00 PM"
        "12:05 PM"| -6        | "11:59 AM"
        "9:13 AM" | -200      | "5:53 AM"
        "1:20PM"  | -24       | "12:56 PM"
        "1:20AM"  | -2        | "1:18 AM"
        "12:00AM" | -72       | "10:48 PM"
        "12:00AM" | -500      | "3:40 PM"
    }
}
