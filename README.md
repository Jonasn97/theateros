# 👋 About

Dieses Projekt ist das Ergebnis der Projektarbeit Entwicklung eines Theaterplaners unter Anwendung des Domain-Driven-Designs mit Quarkus REST-APIs, Webcrawling und Serverseitigem Rendering" im Rahmen des Moduls Software-Architektur – Konzepte und Anwendungen des Studiengangs Informatik - Medieninformatik.

## 📄 Inhalt
Bietet eine Übersicht über die Veranstaltungen des Theaters Osnabrück und ermöglicht es, diese zu filtern, zu suchen und zu favorisieren.\
Weiterhin kann man für die einzelnen Veranstaltungen einen Status setzen.\
Außerdem können die Aufführungen als Kalenderdatei exportiert werden.\

## 🚀 Ausführen der Anwendung
### 🏗 Bauen:
```shell script
./mvnw compile
```
### 🧪 Testen:
```shell script
./mvnw test
```
### 🛠️ Ausführen im DevMode:

```shell script
./mvnw compile quarkus:dev
```
## Angebotene REST-Schnittstellen:

## 🌐 Angebotene Webseiten:
| Pfad                                                                                     | Zweck                                                                                                                                                                                                                                                        |
|------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [mobile/deins](http://localhost:8080/mobile/deins)                                       | persönliche Startseite (noch nicht implementiert)                                                                                                                                                                                                            |
| [mobile/login](http://localhost:8080/mobile/login)                                              | Login-Seite                                                                                                                                                                                                                                                  |
| [mobile/register](http://localhost:8080/mobile/register)                                        | Erstellen eines Accounts                                                                                                                                                                                                                                     |
| [mobile/events](http://localhost:8080/mobile/events)                                            | Veranstaltungen anzeigen, klick auf Veranstaltung öffnet die Detailseite, über die Buttons im header kann man filtern und suchen.                                                                                                                            |
| [mobile/events/{id}](http://localhost:8080/mobile/events/{id})                                  | Veranstaltungsdetails anzeigen, mit Klick auf den Chip kann man einen Status setzen, mit Klick auf das Herz ein Stück favorisieren. Mit Klick auf zum Shop gelangt man zur Website vom Theater. Mit Klick auf zum Kalender wird eine Kalenderdatei erstellt. |
| [mobile/veranstaltungen/{id}/kalender](http://localhost:8080/mobile/performances/{id}/kalender) | Performance als Kalenderdatei exportieren                                                                                                                                                                                                                    |
| [mobile/performances](http://localhost:8080/mobile/performances)                                | Spielzeiten anzeigen und über die Buttons im header filtern und suchen.                                                                                                                                                                                      |

                                                                                                                               |
## ⚙️ verwendete Technologien

### Allgemein

| Technologie                                                  | Zweck                                                                                                                                                   |
|--------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| [Quarkus](https://quarkus.io/)                               | Quarkus, welches in der Version 2.17 verwendet wird, ist ein leichgewichtiges Framework, mit dem Java speziell für Containerplattformen optimiert wird. |
| [JUnit 5](https://quarkus.io/guides/getting-started-testing) | Unit tests creation                                                                                                                                     |
| [Mockito](https://quarkus.io/blog/mocking/)                  | Adds mockito framework for testing purposes                                                                                                             |
| [CDI](https://quarkus.io/guides/cdi-reference)               | Contexts and Dependency Injection                                                                                                                       |
| [Security](https://quarkus.io/guides/security-authentication-mechanisms#form-auth)           | Authentifizierung                                                                                                                                       |

### Datenbank

| Technologie                                                          | Zweck             |
|----------------------------------------------------------------------|-------------------|
| [Hibernate panache](https://quarkus.io/guides/hibernate-orm-panache) | Datenbank Zugriff |
| [Postgre SQL](https://jdbc.postgresql.org/)                          | Postgre SQL       |

### REST adapter

| Technologie | Zweck                                                                                                            |
| ---------- |------------------------------------------------------------------------------------------------------------------|
| [Resteasy](https://quarkus.io/guides/rest-json) | Annotation based Rest controllers with JSON serialization / desearialization                                     |
| [rest-assured](https://rest-assured.io/) | Testen und validieren der REST services                                                                          |
| [smallrye-openapi](https://github.com/smallrye/smallrye-open-api) | Fügt dem Projekt Openapi Spezifikationen hinzu und unterstützt bei der Implementierung des Schema und Swagger UI |
| [OpenAPI](https://github.com/OAI/OpenAPI-Specification) | Die OpenAPI Spezifikation                                                                                        |

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
