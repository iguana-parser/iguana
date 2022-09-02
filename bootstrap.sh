mvn clean install

java -jar target/iguana-0.1-SNAPSHOT.jar\
  --generate-types\
  --name iggy\
  --grammar src/resources/Iguana.iggy\
  --output src/org/iguana/iggy/gen\
  --package org.iguana.iggy.gen
