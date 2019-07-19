1.0.4 / WIP
=================

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
