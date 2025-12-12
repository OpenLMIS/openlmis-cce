1.4.1 / 2025-12-12
==================

Changes:
* Added CCE status **NEEDS_ATTENTION**

1.4.0 / 2025-11-27
==================

Changes:
* Upgrade PostgreSQL JDBC driver to 42.6.2 for PostgreSQL 14 compatibility

1.3.5 / 2025-03-31
==================

Minor coverage updates and code analysis improvements.

1.3.4 / 2024-10-31
==================
Improvements:
* [OIS-14](https://openlmis.atlassian.net/browse/OIS-14): Upgrade Transifex API version
* [OIS-48](https://openlmis.atlassian.net/browse/OIS-48): Update service base images to versions without known vulnerabilities

1.3.3 / 2024-04-19
==================
New functionality:
* [OLMIS-7909](https://openlmis.atlassian.net/browse/OLMIS-7909): Add functionality to print inventory equipment for specific facility and program

1.3.2 / 2022-04-21
==================
Breaking changes:
* [OLMIS-7472](https://openlmis.atlassian.net/browse/OLMIS-7472): Upgrade postgres to v12

Improvement:
* [OLMIS-7501](https://openlmis.atlassian.net/browse/OLMIS-7501): Added CCE_INVENTORY_TRANSFER right
* [OLMIS-7568](https://openlmis.atlassian.net/browse/OLMIS-7568): Use openlmis/dev:7 and openlmis/service-base:6.1

New features:
* [OLMIS-7502](https://openlmis.atlassian.net/browse/OLMIS-7502): Add possibility to transfer inventory item to another facility 

1.3.1 / 2021-10-29
==================
Improvement:
* [OLMIS-6983](https://openlmis.atlassian.net/browse/OLMIS-6983): Sonar analysis and contract tests runs only for snapshots

1.3.0 / 2020-04-14
==================

New functionality added in a backwards-compatible manner:
* [OLMIS-6659](https://openlmis.atlassian.net/browse/OLMIS-6659): Updated Spring Boot version to 2.x:
    * Flyway is at 6.0.8, new mechanism for loading Spring Security for OAuth2, new versions for REST Assured, RAML tester, RAML parser, PowerMock, Mockito (so tests will pass) and Java callback mechanism has changed to a general handle() method.
    * Spring application properties for Flyway have changed.
    * Fix repository method signatures (findOne is now findById, etc.); additionally they return Optional.
    * Fix unit tests.
    * Fix integration tests.
    * API definitions require "Keep-Alive" header for web integration tests.
    * CustomSortSerializer added to handle difference of JSON Sort property.

Bug fixes:
* [OLMIS-6776](https://openlmis.atlassian.net/browse/OLMIS-6776): Fixed the issue with invalid token error:
  * A mechanism to retry authentication load after encountering the error was introduced.

1.2.0 / 2020-01-20
==================

New functionality added in a backwards-compatible manner:
* [OLMIS-6734](https://openlmis.atlassian.net/browse/OLMIS-6734): Added GET /api/inventoryItems/volume endpoint.


1.1.0 / 2019-10-17
==================

New functionality:
* [OLMIS-6558](https://openlmis.atlassian.net/browse/OLMIS-6558): Add new environment variable - PUBLIC_URL and use to for email generated links

Improvements:
* [OLMIS-4128](https://openlmis.atlassian.net/browse/OLMIS-4128): Change maximum page size to max integer.
* [OLMIS-6374](https://openlmis.atlassian.net/browse/OLMIS-6374): Add new profile for audit logging.
* [OLMIS-6408](https://openlmis.atlassian.net/browse/OLMIS-6408): Added pageable validator.

1.0.3 / 2019-05-27
==================

Bug fixes:
* [OLMIS-4596](https://openlmis.atlassian.net/browse/OLMIS-4596): Fixed issue with sending a notification about nonfunctional CCE to users with home facility rights 
* [OLMIS-5841](https://openlmis.atlassian.net/browse/OLMIS-5841): Fixed issue with internal server error when updating a catalog item with incorrect ID 

Improvements:
* [OLMIS-4531](https://openlmis.atlassian.net/browse/OLMIS-4531): Added compressing HTTP POST responses.

1.0.2 / 2018-12-12
==================

Improvements:
* [OLMIS-4940](https://openlmis.atlassian.net/browse/OLMIS-4940): Ensured that the microservice gets system time zone from configuration settings on startup.
* [OLMIS-4295](https://openlmis.atlassian.net/browse/OLMIS-4295): Updated checkstyle to use newest google style.
* [OLMIS-3078](https://openlmis.atlassian.net/browse/OLMIS-3078): Made Javers log initializer not iterate over all items, only those without logs.
* [OLMIS-4942](https://openlmis.atlassian.net/browse/OLMIS-4942): Added currency, number and date settings to application properties.
* [OLMIS-5635](https://openlmis.atlassian.net/browse/OLMIS-5635): Adjusted supervisory node structure 

1.0.1 / 2018-08-16
==================

Bug fixes:
* [OLMIS-4588](https://openlmis.atlassian.net/browse/OLMIS-4588): Fixed incorrect link in the CCE notification
* [OLMIS-4057](https://openlmis.atlassian.net/browse/OLMIS-4057): Fixed error code and message for duplicated inventory item.

Improvements:
* [OLMIS-4647](https://openlmis.atlassian.net/browse/OLMIS-4647): Added Jenkinsfile
* [OLMIS-4905](https://openlmis.atlassian.net/browse/OLMIS-4905): Updated notification service to use v2 endpoint.
* [OLMIS-4876](https://openlmis.atlassian.net/browse/OLMIS-4876): Applied new demo data loading approach

1.0.0 / 2018-04-24
==================

Released openlmis-cce 1.0.0 as part of openlmis-ref-distro 3.3. This was the first stable release of openlmis-cce.

Features
* [OLMIS-2604](https://openlmis.atlassian.net/browse/OLMIS-2604): Creates a CCE catalog in the system by uploading a CSV file.
* [OLMIS-2872](https://openlmis.atlassian.net/browse/OLMIS-2872): Download current CCE catalog
* [OLMIS-2619](https://openlmis.atlassian.net/browse/OLMIS-2619): Add new CCE device and info to inventory
* [OLMIS-2608](https://openlmis.atlassian.net/browse/OLMIS-2608): Archive CCE catalog items
* [OLMIS-2897](https://openlmis.atlassian.net/browse/OLMIS-2897): Set functionality status to the CCE inventory item
* [OLMIS-2913](https://openlmis.atlassian.net/browse/OLMIS-2913): Receive notifications for non-functional CCE
* [OLMIS-4106](https://openlmis.atlassian.net/browse/OLMIS-4106): RTM Alert integration
* [OLMIS-3386](https://openlmis.atlassian.net/browse/OLMIS-3386): Provides CCE Inventory as FHIR Device
