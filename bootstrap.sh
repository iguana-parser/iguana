mvn clean install -Dtest=\!org.iguana.iggy.IggyParserBootstrapTest

java -jar target/iguana-0.1-SNAPSHOT.jar\
  --generate-types\
  --name iggy\
  --grammar src/resources/Iguana.iggy\
  --grammar-output src/resources\
  --output src\
  --package org.iguana.iggy.gen
