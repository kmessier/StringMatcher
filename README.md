# StringMatcher

#### About
This program will search for s set of keywords in a large text

#### Prerequisites
- Java

#### Building
- After cloning the repo, build the executable uber JAR and copy it up to the root dir.

```
$ ./mvnw clean package shade:shade
$ cp target/StringMatcher-1.0-SNAPSHOT.jar .

```

#### Execution
- Executing the following command will read in the keywords from names.csv, and search for matches in big.txt. By default the matches are case-insensitive and match whole words only. All the aggregated matches will be logged to the console. A more detailed execution log file will also be created.

```
java -jar StringMatcher-1.0-SNAPSHOT.jar -k names.csv -i big.txt
```

- All command line options

```
-c (--case-sensitive)    : Optional - Perform case sensitive search (default: false)
-i (--input-file) VAL    : Path to the input file containing the text to be searched
-k (--keywords-file) VAL : Path to a CSV file containing the keywords to be matched
-l (--lines-per-chunk) N : Optional - Number of lines per chunk (default: 1000)
-p (--partial-matches)   : Optional - Match or partial words (default: false - whole words only)
```

- Optional - build_and_execute.sh will run the above build, copy, and exection steps in a single command

#### External libraries
- [log4j](https://logging.apache.org/log4j/2.x)
- [args4j](http://args4j.kohsuke.org)
- [opencsv](http://opencsv.sourceforge.net)
- [Java implementation of the Aho–Corasick algorithm](https://github.com/robert-bor/aho-corasick)
