@author Tomasz Biedrzycki
# Jak testować
Aplikacja wykorzystuje wbudowany serwer kolejki ActiveMQ. Nie ma potrzeby instalowania lokalnie do testów.
Aplikacja napisana z użyciem frameworka spring-boot.
#### Wymagania  

- java version 11
- maven 3.x  

#### Uruchomienie
<code>mvn install && mvn spring-boot:run</code>

#### (1) Wysyłanie informacji do kolejki
odbywa się co 15s, wykorzystano mechanizm @Scheduled klasa [BolidSystemService](src/main/java/com/edu/formula1/bolid/BolidSystemService.java)

#### (2) Zapisywanie danych w logu w czytelnej formie
klasa z własnym formatowaniem [LogService](src/main/java/com/edu/formula1/bolid/LogService.java)
Logi zapisywane są w katalogu logs pod nazwą LogService.

#### (3) Testowanie Routing / Message Router
klasa [MonitorService](src/main/java/com/edu/formula1/bolid/MonitorService.java)

 - wysłanie wiadomości do systemu bolida / przekroczone parametry: 

<code>
POST http://127.0.0.1:8080/bolid/api/v1/sendInfo/bolidState
  
{
	"temperature": "12211.49", 
	"tirePressure": "12311.49",
	"oilPressure": "123213.64",
	"pitStopAllowed": false,
	"pitStopCount": "0",
}
</code>
 - wysłanie wiadomości do systemu bolida i zespołu mechaników / poważna awaria: 

 <code>
POST http://127.0.0.1:8080/bolid/api/v1/sendInfo/bolidState
  
{
	"temperature": "13123.49", 
	"tirePressure": "12313123.49",
	"oilPressure": "123213.64",
	"pitStopAllowed": false,
	"pitStopCount": "0",
	"emergencyLevel": "5"
}
</code>
#### (4) Request-Reply / Temporary Queue with corelation ID: 
W celu uruchomienia komendy: request/pitstop. 

<code>
GET http://127.0.0.1:8080/bolid/api/v1/request/pitstop 
</code>

serwer zwraca odpowiedź w postaci json czy pitstop może zostać wykonany w postaci tekstowej:
<code>Pitstop is rejected.</code>|
<code>Pitstop is accepted.</code>

Za werefyikację żądania odpowiada metoda
[TeamLeaderService.isPitStopAllowed](src/main/java/com/edu/formula1/bolid/TeamLeaderService.java)


### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.6.RELEASE/maven-plugin/)

