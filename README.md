# ♕ BYU CS 240 Stuart's Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

sequence diagram for phase 2:
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdADZM9qBACu2AMQALADMABwATACcIDD+yPYAFmA6CD6GAEoo9kiqFnJIEGiYiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAEQDlGjAALYo43XjMOMANCu46gDu0ByLy2srKLPASAj7KwC+mMK1MJWs7FyUDRNTUDPzF4fjm6o7UD2SxW63Gx1O52B42ubE43FgD1uogaUCyOTAlAAFJlsrlKJkAI5pXIAShuNVE9yqsnkShU6ga9hQYAAqoNMe9PigyTTFMo1KoqUYdHUAGJITgwNmUXkwHSWGCcuZiHSo4AAaylgxgWyQYASisGXJgwAQao4CpQAA90RpeXSBfdERSVA1pVBeeSRConVVbi8YAozShgBaOhr0ABRK0qbAEQpeu5lB4lcwNQJOYIjCbzdTAJmLFaRqDeeqG6bKk3B0MK+Tq9DQsycTD2-nqX3Vb0oBpoHwIBCJykPVv01R1EBqjHujmDXk87QO9sPYx1BQcDhamXaQc+4cLttjichjEKHz6zHAM8JOct-ejoUrtcb0-6z1I3cPWHPMs49H4tR9lgX7wh2-plm8RrKssDQHKCl76h0ED1mg0ErFciaUB2qYYA04ROE42aTJBXwwDBIIrPBCSIchqEHNc6AcKYXi+AE0DsEysSinAkbSHACgwAAMhA2RFNhzDOtQAYtO03R9AY6gFGg2ZKvM6x-ACHDXGBlTAS8EEVl8UKgupuzfDCTzwpU77dggwkSpiQkiYSxJgGS1mVCOAp1EyrLsip3K3rSB6VCu4qSu6sryuWHzKpgqohpqAByECbrAoq+M21kADzJp5DK9v2GFQDl1l1MWpYNMl0UwAAZhljFiSVNQvBmACMhG5qo+YLDB5XQG62poBAzB1T4nCNgxO4oDlZR5WOEXbu5FSlbUSA1ZYzT6P8uyYiZgLrAt8hkvFGowJt2y7DAACSaAgNAqLgEVM2lX1ZYsjMV7QEgABeKANcg5jPc19RtR1ArdYW4yvW6H36l9v17PRmUuigHl3l5R5yCgL4JJiOM3nNIUik+gZXm+KMzXNq5k9uunFcmpWORKmSqIBT1lHTdT6TFhmwRRV7UQ2ULXI1ZRgXUeEEWMREGT1fPjJRgsocLTaMZ43h+P4XgoOgsTxEk2u645vhYGJQri000iRgJkYdJGPS9PJqiKSMitIegRU6RZLxZW7yEVJgdNWSjdS2fYJsOcJJvOWorlTWjQWjt5zI4xeAvu2g86JwKRNihKz40-IcoKkrcVqpq+PaDAQ0jfVU2U+j+V9gOYFA12ZUlv1Uqwwk8N-cUAMYG3tR1KD0udRDvWd2WY2UX3iOq-XuWN2OlfyPHFRUxwKDcCeV544XwBZ3yo659IO-MoYa-AIH3v02LIfG2eLNs63yac6MIuD2Aw8vJL2ZIzVsxTWqINz+GwBKTUAl0QwAAOLKg0GbCSI8miwNtg7ewypXbp2Qp7ConNfY4PQAHIOy0Q7IFyPA3MmIKFgCoWoGOpIN5Ux8qnP26Bj6LlULnMKBdXxVyiqXE6FdD7V2GrVOu2Vl7ZyboVN+D927Q27nPKAP1+6iwUSPMeOZwYFinhVRUPd54TWRl2BuMjV6H2YSvZOYBoG5FaDVehqhMRklshAbAMBMgWFQDQKsCA4EIMCifHOFQVywOZIE+YnpJpSNmjY+xzAIA1SiSgT0dNh7IkSY45xL8EBYHkR-FYmDcyFkaBMEpKArrSELK1cIwRAigi2AkPUKB3Rcn2D8FIoB1TtKgksH4lTErKgGZcGAXQv6lB-gzYGEt8LZmKQgspFTlTVNqfUxpKxmmtL6bzLpCAem7LloM5Uwz5ijPGYApiGsAgcAAOyRCcCgJwsRIzBDgNxNw8BJyGHoTAAeUzzazKaG0ToGCsE9yVtmIZyotLAy9nCH27C0AVEIjC3m6FSGlUxhiehmIcUoHoYwuO1khRUxgKw-eyLOEHgfCKGAvDSb8KLoIjOZcEpMuvFXGuEixqMTmqBEO1dm5TUFZJMsSj3oqLUYxMCWFv7picO1MYKwJ56KLNPBos9PqqIRssK5cSqbX2sRY8cPy8X0IJivXOJNLWLQptIkJDI7XrwyTM9ucBzXKjyQU4GM0injEqWshodSGkwEmYDd1I9-4qsDasmpIaNnhsXurFi-hLA71slsPWSBEhgAzf2CA2aABSEAJSpNiN0kA6oAXmCBeKhoLQWSyV6JU7BCEM7ZmwAcjNUA4AQFslAdYQbpBwvFQi78dRCEdv9oRbtwBe39sHfsAA6iwK6dtegACEBIKDgAAaROfMYNMBQ2BHMoiqAwd24ACsy1oDxXeiURKUBEljm5FGCcnVjkpeealwSuE8PzpyyKJc2XCJA9y8Ro1TFUFEOY79dQCotz9VG5EkqjG6vUd-X+IMlVgzzOqqGmrDHSr1Qah18TTXGqWiw5keKR00tPmEkUETmAuuAMXCl8b2WnT+TymDsrKNU2Q+zTR6GSP8eg3XDR4ttHjDVT1FYUna58pMUJsxjquF1A4yaxDoYOCITxf5NSPbKBLugEx0Jj51yCn1IYfyMBIBOYSIYedi6B3QBgOW3TbrxOulLc+71AF8liY-hGoeUa-7zLGFc1NmsvALpzXmxLCpEAhlgMAbA3bCD5EKP8pBfpgWNCtjbO2DtjB4PC7fS9164OuhANwPA+KmtQC3RAPAHBiUfq7F+7TjWMu8j0AYVxAHgosbqJkWYEA-GmgCe8VQw6EHrCeCaIhaANB+dKulvAPqwt3y5hF6ZmjotS0-ovIAA

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
