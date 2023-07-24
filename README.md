# 👋 About

Dieses Projekt ist das Ergebnis der Projektarbeit Entwicklung eines Theaterplaners unter Anwendung des Domain-Driven-Designs mit Quarkus REST-APIs, Webcrawling und Serverseitigem Rendering" im Rahmen des Moduls Software-Architektur – Konzepte und Anwendungen des Studiengangs Informatik - Medieninformatik.

## 📄 Inhalt
Bietet eine Übersicht über die Veranstaltungen des Theaters Osnabrück und ermöglicht es, diese zu filtern, zu suchen und zu favorisieren.\
Weiterhin kann man für die einzelnen Veranstaltungen einen Status setzen.\
Außerdem können die Aufführungen als Kalenderdatei exportiert werden.\
Auch lassen sich Veranstaltungen als Admin bearbeiten und entfernen, falls sie durch das Crawlen nicht korrekt importiert wurden. 


## Clone
### mit https
```shell script
git clone https://gitlab.hs-osnabrueck.de/swa_mi_sose23/projekt29/theateros.git
```
### mit ssh
```shell script
git clone git@gitlab.hs-osnabrueck.de:swa_mi_sose23/projekt29/theateros.git
```

## 🚀 Ausführen der Anwendung
Vor dem Ausführen der Tests und dem Bearbeiten der Daten sollte zunächst die Datenbank mit einer der folgenden Rest-Anfragen befüllt werden. \
[http://localhost:8080/crawler/web](http://localhost:8080/crawler/web)\
oder\
[http://localhost:8080/crawler/local](http://localhost:8080/crawler/local)\
Beim localen crawlen ist darauf zu achten, dass die Testdaten vorhanden sind.\
Diese sind aus dem Branch testdata herunterzuladen. Und entsprechend der Readme in die Ordner einzufügen.\


### 🏗 Bauen:
```shell script
./mvnw compile
```
### 🧪 Testen:
Testen der EventResourceAPI, ob die Events korrekt zurückgegeben werden.
```shell script
./mvnw test
```
### 🛠️ Ausführen im DevMode:

```shell script
./mvnw compile quarkus:dev
```


## 🌐 Angebotene Websites:
| Pfad                                                                                     | Zweck                                                                                                                                                                                                                                                        |
|------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [mobile/deins](http://localhost:8080/mobile/deins)                                       | persönliche Startseite (noch nicht implementiert)                                                                                                                                                                                                            |
| [mobile/login](http://localhost:8080/mobile/login)                                              | Login-Seite                                                                                                                                                                                                                                                  |
| [mobile/register](http://localhost:8080/mobile/register)                                        | Erstellen eines Accounts                                                                                                                                                                                                                                     |
| [mobile/events](http://localhost:8080/mobile/events)                                            | Veranstaltungen anzeigen, ein Klick auf eine Veranstaltung öffnet die Detailseite, über die Buttons im header kann man filtern und suchen.                                                                                                                   |
| [mobile/events/{id}](http://localhost:8080/mobile/events/{id})                                  | Veranstaltungsdetails anzeigen, mit Klick auf den Chip kann man einen Status setzen, mit Klick auf das Herz ein Stück favorisieren. Mit Klick auf zum Shop gelangt man zur Website vom Theater. Mit Klick auf zum Kalender wird eine Kalenderdatei erstellt. |
| [mobile/veranstaltungen/{id}/kalender](http://localhost:8080/mobile/performances/{id}/kalender) | Performance als Kalenderdatei exportieren                                                                                                                                                                                                                    |
| [mobile/performances](http://localhost:8080/mobile/performances)                                | Spielzeiten anzeigen und über die Buttons im header filtern und suchen. Ein Klick auf eine Spielzeit öffnet die die Detailseite der Veranstaltung.                                                                                                           |

## 📱 Angebotene Dev-Pages:
| Pfad                                                                             | Zweck      |
|----------------------------------------------------------------------------------|------------|
| [Dev UI](http://localhost:8080/theateros/api/dev/)                               | Dev UI     |
| [Swagger UI](http://localhost:8080/theateros/api/swagger-ui/)                    | Swagger UI |
| [OpenAPI](http://localhost:8080/swagger)                                         | OpenAPI    |

## 📞 Angebotene REST-Endpunkte:
| Pfad                                                                                     | Methode | Zweck                                                                         |
|------------------------------------------------------------------------------------------|---------|-------------------------------------------------------------------------------|
| [http://localhost:8080/api/admin/events/{eventId}](http://localhost:8080/api/admin/events/{eventId}) | PUT     | Veranstaltung per ID aktualisieren                                            |
| [http://localhost:8080/api/admin/events/{eventId}](http://localhost:8080/api/admin/events/{eventId}) | DELETE  | Veranstaltung per ID löschen                                                  |
| [http://localhost:8080/api/events](http://localhost:8080/api/events)                      | GET     | Gefilterte Veranstaltungen abrufen                                            |
| [http://localhost:8080/api/events/{eventId}](http://localhost:8080/api/events/{eventId})   | GET     | Veranstaltung per ID abrufen                                                  |
| [http://localhost:8080/api/events/{eventId}/performances](http://localhost:8080/api/events/{eventId}/performances) | GET     | Aufführungen für eine Veranstaltung per Id abrufen                            |
| [http://localhost:8080/api/performances](http://localhost:8080/api/performances)            | GET     | Gefilterte Aufführungen abrufen                                               |
| [http://localhost:8080/api/performances/{performanceId}](http://localhost:8080/api/performances/{performanceId}) | GET     | Aufführung per ID abrufen                                                     |
| [http://localhost:8080/api/performances/{performanceId}/calendar](http://localhost:8080/api/performances/{performanceId}/calendar) | GET     | Kalendereintrag für eine Aufführung abrufen                                   |
| [http://localhost:8080/api/user/userevents](http://localhost:8080/api/user/userevents)    | GET     | Alle UserEvents für einen angemeldeten Benutzer abrufen                       |
| [http://localhost:8080/api/user/userevents](http://localhost:8080/api/user/userevents)    | POST    | Neues UserEvent für den angemeldeten Benutzer erstellen                       |
| [http://localhost:8080/api/user/userevents/{userEventId}](http://localhost:8080/api/user/userevents/{userEventId}) | GET     | UserEvent für den angemeldeten Benutzer per ID abrufen                        |
| [http://localhost:8080/api/user/userevents/{userEventId}](http://localhost:8080/api/user/userevents/{userEventId}) | PUT     | UserEvent für den angemeldeten Benutzer aktualisieren                         |
| [http://localhost:8080/api/user/userevents/{userEventId}](http://localhost:8080/api/user/userevents/{userEventId}) | DELETE  | UserEvent für den angemeldeten Benutzer löschen                               |
| [http://localhost:8080/api/user/userevents/{userEventId}](http://localhost:8080/api/user/userevents/{userEventId}) | PATCH   | isFavorite oder EventState eines UserEvents für den angemeldeten Benutzer ändern |
| [http://localhost:8080/api/user/userperformance](http://localhost:8080/api/user/userperformance) | GET     | Alle UserPerformances für den angemeldeten Benutzer abrufen                   |
| [http://localhost:8080/api/user/userperformance](http://localhost:8080/api/user/userperformance) | POST    | Neue UserPerformance für angemeldeten User erstellen                          |
| [http://localhost:8080/api/user/userperformance/{id}](http://localhost:8080/api/user/userperformance/{id}) | GET     | UserPerformance für angemeldeten User nach ID abrufen                         |
| [http://localhost:8080/api/user/userperformance/{id}](http://localhost:8080/api/user/userperformance/{id}) | PUT     | UserPerformance für angemeldeten User aktualisieren                           |
| [http://localhost:8080/api/user/userperformance/{id}](http://localhost:8080/api/user/userperformance/{id}) | DELETE  | UserPerformance für angemeldeten User löschen                                 |
| [http://localhost:8080/crawler/local](http://localhost:8080/crawler/local)                | GET     | Veranstaltungen der gespeicherten Websites lokal crawlen und Datenbank aktualisieren |
| [http://localhost:8080/crawler/web](http://localhost:8080/crawler/web)                      | GET     | Veranstaltungen von der Website crawlen und Datenbank aktualisieren           |

## ⚙️ verwendete Technologien

### Allgemein

| Technologie                                                  | Zweck                                                                                                                                                   |
|--------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| [Quarkus](https://quarkus.io/)                               | Quarkus, welches in der Version 2.17 verwendet wird, ist ein leichgewichtiges Framework, mit dem Java speziell für Containerplattformen optimiert wird. |
| [JUnit 5](https://quarkus.io/guides/getting-started-testing) | Unit tests creation                                                                                                                                     |
| [Mockito](https://quarkus.io/blog/mocking/)                  | Adds mockito framework for testing purposes                                                                                                             |
| [CDI](https://quarkus.io/guides/cdi-reference)               | Contexts and Dependency Injection                                                                                                                       |
| [Security](https://quarkus.io/guides/security-authentication-mechanisms#form-auth)           | Authentifizierung                                                                                                       |

### Datenbank

| Technologie                                                          | Zweck             |
|----------------------------------------------------------------------|-------------------|
| [Hibernate panache](https://quarkus.io/guides/hibernate-orm-panache) | Datenbank Zugriff |
| [Postgre SQL](https://jdbc.postgresql.org/)                          | Postgre SQL       |

### REST adapter

| Technologie | Zweck                                                                                                            |
| ---------- |------------------------------------------------------------------------------------------------------------------|
| [Resteasy](https://quarkus.io/guides/rest-json) | Annotationsbasierte Rest Controller mit JSON Serialisierung / Deserialisierung                                   |
| [rest-assured](https://rest-assured.io/) | Testen und validieren der REST services                                                                          |
| [smallrye-openapi](https://github.com/smallrye/smallrye-open-api) | Fügt dem Projekt Openapi Spezifikationen hinzu und unterstützt bei der Implementierung des Schema und Swagger UI |
| [OpenAPI](https://github.com/OAI/OpenAPI-Specification) | Die OpenAPI Spezifikation                                                                                        |
| [Swagger UI](https://swagger.io/tools/swagger-ui/) | Swagger UI ist ein Tool, welches die OpenAPI Spezifikation visualisiert.                                         |
| [json:api](https://jsonapi.org/) | JSON:API ist ein Format für die Kommunikation zwischen Client und Server.                                        |

### Web

| Technologie | Zweck                                                                                                            |
| ---------- |------------------------------------------------------------------------------------------------------------------|
| [Qute](https://quarkus.io/guides/qute) | Serverseitiges Rendering von HTML Seiten                                                                          |
| [Unpoly](https://unpoly.com/) | Unpoly ist ein JavaScript Framework, welches das serverseitige Rendering von HTML Seiten unterstützt.            |
### Weiteres

| Technologie | Zweck                                    |
|------------|------------------------------------------|
| [jsoup](https://jsoup.org/)  | Crawling der Daten vom Theater Osnabrück |
| [iCal4j](https://www.ical4j.org/) | Erstellen von Kalenderdateien im MIME-type text/calendar zum export von Veranstaltungen. |

## 📚 Guides
