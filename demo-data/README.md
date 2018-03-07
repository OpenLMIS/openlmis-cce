# Demo Data for OpenLMIS CCE Service
This folder holds demo data for the CCE service. The demo data is used by developers, QA
staff, and is automatically loaded into some environments for demo and testing purposes. It is not
for use in production environments.

Each .json file contains demo data that corresponds to one database table.

## CCE Catalog

Defined in this file: cce.cce_catalog_items.json

There is a small set of cold chain equipment in the catalog for demoing.

## CCE Inventory

Defined in this file: cce.cce_inventory_items.json

All facilities for the EPI (Vaccine) program have CCE inventory, except the national warehouse.

For the 41 health facilities, they have:
* Approximately five CCE inventory
  * Approximately two of them have functional status **Functioning**
  * Approximately two of them have functional status **Awaiting Repair**
  * One of them has functional status **Unserviceable**

For the two district stores and one regional store, they have:
* Approximately 10 CCE inventory
  * Approximately seven of them have functional status **Functioning**
  * Approximately two of them have functional status **Awaiting Repair**
  * One of them has functional status **Unserviceable**

For all CCE inventory:
* Utilization is set to **Active**
* Year installed is set to **2017**
* They were last updated by the RIVO
* The rest of the values are randomized
