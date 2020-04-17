# Lauren Hunter
## AddMinutes problem
   first argument: 12-hour time string with the format "[H]H:MM{AM|PM}"
   second argument: (signed) integer. The second argument is the number of
                    minutes to add to the time of day represented by the first arg.

   AddMinutes("9:13 AM", 200) would return "12:33 PM"
   
### Code: src/main/java/AlterTime.java
### Tests : src/test/groovy/AlterTimeSpec.groovy
* wanted to use spock because unroll + access private methods for unit tests,
so used gradle to pull all that in
* to run tests `./gradlew build`
