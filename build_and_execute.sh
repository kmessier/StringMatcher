./mvnw clean package shade:shade
cp target/StringMatcher-1.0-SNAPSHOT.jar .
java -jar StringMatcher-1.0-SNAPSHOT.jar -k names.csv -i big.txt
