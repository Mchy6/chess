# Multiplayer Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.


- The **Client** directory contains files for the client-side features, like UI and websocket functionality.
- The **Server** directory manages the server and links to the MySQL database.
- The **Shared** directory controls chess game logic and completes user's menu/in-game actions.
<br><br>
### Sequence Diagram ([click to expand view](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDAEooDmSAzmFMARDQVqhFHXyFiwUgBF+wAIIgQKLl0wATeQCNgXFDA3bMmdLwCeXXETTsYARgB09zOygQArthgBiNMCpzVg5uXn4kQV8AdwALJDAxRFRSAFoAPnJKGigALhgAbQAFAHkyABUAXRgAeg99KAAdNABvOsp-AFsUABoYXDUo6A1elA7gJAQAX0xhbJh04M4ePgEhLNE8qBCeSgAKNqhOnr69LkGoYZhR8YQASkw2JbDV2dF5jLkwRWVVLjz2FBgACq9X29SO90+3xUanehm0eTIAFEADJIuBlGAHI4wABm7g6WPqmChShhXHmC0eoRWETWIhUeTQHgQCAe22edNeKjhpJ+ajyIC2-BQIL22OAXV6-TOQ0h8jJvzhRjyAEkAHLIliYiVSk4DIYjMYTGAasrFIm0anLcKCbkGBZ88mC4UJBQeMAxMHtSUoeVfRWwhYq02apHay2HX29YAemJlCAAa3QofNMFjnpJCv5FJSVI5tLt60Z6bjCeTaHZT0L9KobzzGVmlDyGfjSfQMyylEpGSSZjyACYAAxDprNVvl9DTdAaExuTzeHzQaQAmAoiCcNDROIJTB91KNru5AolcpVar6NR0se644y84aaZN2Dpa2cosMlA5BAbpBob1Rnq95ylWNK2rWojpE6vw5ACwKgre-rQr86QqsiaIYpGOL4hAhIHFmAY5j2ixgS8xZfpGoE2mRn7vG+Nb2jk2RILi5iFKcD67Pc9HgfavLZs6MBCigIrup6AEQgRyFBhkIYalqOrgtGpaepOW5mharZSYGub5tWvHkS2ZbtpWPE0XWPINpk1DNipbYVp2Nkvgs+5gIOI5jhOJnTmgs6uO4Xi+G4KApuu7CeMwPixPEiTIGYcLPnk+QyKiSJlEi56Xlw17+MZDnPnRBYGZ+34bhFuxeRW3FFeZkFpNBApbB0EA0GJ9noBVeXoEhOmoQiMgoAggIGJVHYFVZrnuUOpi+SYJjVNUMAAGqUCxqAUp6BjYO4dYaCgGh2Wp6a+TAnA0BSwAwMgPAwBAuLpqyp2+lwjj+QuvhbAdKKhDAADiz3bjFe5xQe1nZElv1pee7C+k0o2Vs+r41VyhnXWA-1dFwnWqSZ1X6bVKhQQJMFoxjqjY+1aA9TmfU5BQQ3gFdP13U9mPaTTSP4yjJUw10qoyL00UJGKgHHAAQggoCJiLRy9LzKDqr6VHvhBhPpIl8v84LO6ikpeoS1LMvKfLitdI5cwTSDbkwMOo4tDAABE8tcA7SXNE7vr867DgDgAzAALL0DtC7rPpdK7DsO0H2iSyA0t6ygEdR47Ju+jkDuTBUPl+fOgU+NgHhQNgQ3wK6Bhk4Du6uQlR5JaelQ1PLcNdWgY6p10T5HpzpHcxZX7CSKZMU2pcu+qbfrKwx5FE4RzoAmglCD57Mi7O3E8NVwDZmb3GyazIk-FX3M-SX8A8JEP8Oj1049Xyg-PU+StN-vUzBrzAf6QH9vpQgfBMOgsiVWZ333uNFyVsppjhTsvb29g-b+xgNnea1QVpQDWqoGAnp+AYJiAYKwKAiC4lQAdeWVwAAeoQuAxl8r0Fi6YhIQB-LAbgMA8EEKITGDQGgNo4KEsAVk3Y9DYIMFsAAjh4VQCQDogAYdADBEAhFAMcKae6aB5HSMYe-CkrC0EHU2pGTRMAABWEA-z7XTBdLcEBtD1GyEosocQKSiPETdZhSA9odGwBABIGBXq50XOYQaP4ogwAAFImK3BXHwMcpbA2SMwABtcTxAgbtUJuuUcYVjHK5OAMioC335p3Jy3dqI70ZMYv8Q9sm5PyTIPGPcPxH3qsTAUcEh57wfihYpKtGLy0wDAB6zAqkaIAIQAF4YDMlZH0kiJSGl1Q3jkYAnDCiSwCVAXYQzoA1PuP0jeT80Avz6Ks7sH95Eix-v0wa+gYDTO3nMwmTTZ4wSWRoYo1ijyr2XjsmAey0gqmfpQZgVibEnIwGc+oFyrizURukSatsZp+QWgoGAXRPQQAOriWRIAhp4D-HYPhCBDDyDxASBRWgvi6H0HYhxBi2gHQ0IXPFGCJF4t8QFRcbhgBBEQCJWAwBsDF0IOBSusU4k1ycklFKaJ0rniMObSgXSp4lWxbyhQArkAgHAlxX+pSUDHx0jkFVeBtW-JVANIaCQHqEvJZdP8RKKV6DEDCtIcKRwmBnJgIAA))
[![Sequence Diagram](https://i.imgur.com/WaHGbew.png)](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDAEooDmSAzmFMARDQVqhFHXyFiwUgBF+wAIIgQKLl0wATeQCNgXFDA3bMmdLwCeXXETTsYARgB09zOygQArthgBiNMCpzVg5uXn4kQV8AdwALJDAxRFRSAFoAPnJKGigALhgAbQAFAHkyABUAXRgAeg99KAAdNABvOsp-AFsUABoYXDUo6A1elA7gJAQAX0xhbJh04M4ePgEhLNE8qBCeSgAKNqhOnr69LkGoYZhR8YQASkw2JbDV2dF5jLkwRWVVLjz2FBgACq9X29SO90+3xUanehm0eTIAFEADJIuBlGAHI4wABm7g6WPqmChShhXHmC0eoRWETWIhUeTQHgQCAe22edNeKjhpJ+ajyIC2-BQIL22OAXV6-TOQ0h8jJvzhRjyAEkAHLIliYiVSk4DIYjMYTGAasrFIm0anLcKCbkGBZ88mC4UJBQeMAxMHtSUoeVfRWwhYq02apHay2HX29YAemJlCAAa3QofNMFjnpJCv5FJSVI5tLt60Z6bjCeTaHZT0L9KobzzGVmlDyGfjSfQMyylEpGSSZjyACYAAxDprNVvl9DTdAaExuTzeHzQaQAmAoiCcNDROIJTB91KNru5AolcpVar6NR0se644y84aaZN2Dpa2cosMlA5BAbpBob1Rnq95ylWNK2rWojpE6vw5ACwKgre-rQr86QqsiaIYpGOL4hAhIHFmAY5j2ixgS8xZfpGoE2mRn7vG+Nb2jk2RILi5iFKcD67Pc9HgfavLZs6MBCigIrup6AEQgRyFBhkIYalqOrgtGpaepOW5mharZSYGub5tWvHkS2ZbtpWPE0XWPINpk1DNipbYVp2Nkvgs+5gIOI5jhOJnTmgs6uO4Xi+G4KApuu7CeMwPixPEiTIGYcLPnk+QyKiSJlEi56Xlw17+MZDnPnRBYGZ+34bhFuxeRW3FFeZkFpNBApbB0EA0GJ9noBVeXoEhOmoQiMgoAggIGJVHYFVZrnuUOpi+SYJjVNUMAAGqUCxqAUp6BjYO4dYaCgGh2Wp6a+TAnA0BSwAwMgPAwBAuLpqyp2+lwjj+QuvhbAdKKhDAADiz3bjFe5xQe1nZElv1pee7C+k0o2Vs+r41VyhnXWA-1dFwnWqSZ1X6bVKhQQJMFoxjqjY+1aA9TmfU5BQQ3gFdP13U9mPaTTSP4yjJUw10qoyL00UJGKgHHAAQggoCJiLRy9LzKDqr6VHvhBhPpIl8v84LO6ikpeoS1LMvKfLitdI5cwTSDbkwMOo4tDAABE8tcA7SXNE7vr867DgDgAzAALL0DtC7rPpdK7DsO0H2iSyA0t6ygEdR47Ju+jkDuTBUPl+fOgU+NgHhQNgQ3wK6Bhk4Du6uQlR5JaelQ1PLcNdWgY6p10T5HpzpHcxZX7CSKZMU2pcu+qbfrKwx5FE4RzoAmglCD57Mi7O3E8NVwDZmb3GyazIk-FX3M-SX8A8JEP8Oj1049Xyg-PU+StN-vUzBrzAf6QH9vpQgfBMOgsiVWZ333uNFyVsppjhTsvb29g-b+xgNnea1QVpQDWqoGAnp+AYJiAYKwKAiC4lQAdeWVwAAeoQuAxl8r0Fi6YhIQB-LAbgMA8EEKITGDQGgNo4KEsAVk3Y9DYIMFsAAjh4VQCQDogAYdADBEAhFAMcKae6aB5HSMYe-CkrC0EHU2pGTRMAABWEA-z7XTBdLcEBtD1GyEosocQKSiPETdZhSA9odGwBABIGBXq50XOYQaP4ogwAAFImK3BXHwMcpbA2SMwABtcTxAgbtUJuuUcYVjHK5OAMioC335p3Jy3dqI70ZMYv8Q9sm5PyTIPGPcPxH3qsTAUcEh57wfihYpKtGLy0wDAB6zAqkaIAIQAF4YDMlZH0kiJSGl1Q3jkYAnDCiSwCVAXYQzoA1PuP0jeT80Avz6Ks7sH95Eix-v0wa+gYDTO3nMwmTTZ4wSWRoYo1ijyr2XjsmAey0gqmfpQZgVibEnIwGc+oFyrizURukSatsZp+QWgoGAXRPQQAOriWRIAhp4D-HYPhCBDDyDxASBRWgvi6H0HYhxBi2gHQ0IXPFGCJF4t8QFRcbhgBBEQCJWAwBsDF0IOBSusU4k1ycklFKaJ0rniMObSgXSp4lWxbyhQArkAgHAlxX+pSUDHx0jkFVeBtW-JVANIaCQHqEvJZdP8RKKV6DEDCtIcKRwmBnJgIAA)


## Maven Support

You can use the following commands to build, test, package, and run the code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`     | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

### Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
