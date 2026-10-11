import re

with open('pom.xml', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace("<dependency><groupId>com.bucket4j</groupId><artifactId>bucket4j-core</artifactId><version>8.9.0</version></dependency>", "")

with open('pom.xml', 'w', encoding='utf-8') as f:
    f.write(content)
