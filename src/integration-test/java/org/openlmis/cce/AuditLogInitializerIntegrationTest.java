/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2017 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details. You should have received a copy of
 * the GNU Affero General Public License along with this program. If not, see
 * http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.cce;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang.StringUtils;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.repository.jql.QueryBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.cce.domain.Alert;
import org.openlmis.cce.domain.BackupGeneratorStatus;
import org.openlmis.cce.domain.CatalogItem;
import org.openlmis.cce.domain.FunctionalStatus;
import org.openlmis.cce.domain.InventoryItem;
import org.openlmis.cce.domain.ManualTemperatureGaugeType;
import org.openlmis.cce.domain.ReasonNotWorkingOrNotInUse;
import org.openlmis.cce.domain.RemoteTemperatureMonitorType;
import org.openlmis.cce.domain.Utilization;
import org.openlmis.cce.domain.VoltageRegulatorStatus;
import org.openlmis.cce.domain.VoltageStabilizerStatus;
import org.openlmis.cce.repository.CatalogItemRepository;
import org.openlmis.cce.repository.InventoryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles({"test", "refresh-db"})
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AuditLogInitializerIntegrationTest {

  private static final String[] ALERT_FIELDS = {
      "id", "externalid", "type", "inventoryitemid", "starttimestamp", "endtimestamp",
      "dismisstimestamp"
  };

  private static final String[] INVENTORY_ITEM_FIELDS = {
      "id", "facilityid", "catalogitemid", "programid", "equipmenttrackingid", "referencename",
      "yearofinstallation", "yearofwarrantyexpiry", "source", "functionalstatus",
      "reasonnotworkingornotinuse", "utilization", "voltagestabilizer", "backupgenerator",
      "voltageregulator", "manualtemperaturegauge", "remotetemperaturemonitor",
      "additionalnotes", "modifieddate", "lastmodifierid"
  };

  private static final String INSERT_ALERT_SQL = String.format(
      "INSERT INTO cce.cce_alerts (%s) VALUES (%s)",
      StringUtils.join(ALERT_FIELDS, ", "),
      StringUtils.repeat("?", ", ", ALERT_FIELDS.length)
  );

  private static final String INSERT_INVENTORY_ITEM_SQL = String.format(
      "INSERT INTO cce.cce_inventory_items (%s) VALUES (%s)",
      StringUtils.join(INVENTORY_ITEM_FIELDS, ", "),
      StringUtils.repeat("?", ", ", INVENTORY_ITEM_FIELDS.length)
  );

  @Autowired
  private InventoryItemRepository inventoryItemRepository;

  @Autowired
  private CatalogItemRepository catalogItemRepository;

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private Javers javers;

  @PersistenceContext
  private EntityManager entityManager;

  @Test
  public void shouldCreateSnapshotsForAlerts() {
    //given
    String alertId = UUID.randomUUID().toString();
    InventoryItem item = addInventoryItem();
    addAlert(alertId, item.getId());

    //when
    QueryBuilder jqlQuery = QueryBuilder.byInstanceId(alertId, Alert.class);
    List<CdoSnapshot> snapshots = javers.findSnapshots(jqlQuery.build());

    assertThat(snapshots, hasSize(0));

    AuditLogInitializer auditLogInitializer = new AuditLogInitializer(applicationContext, javers);
    auditLogInitializer.run();

    snapshots = javers.findSnapshots(jqlQuery.build());

    // then
    assertThat(snapshots, hasSize(1));

    CdoSnapshot snapshot = snapshots.get(0);
    GlobalId globalId = snapshot.getGlobalId();

    assertThat(globalId, is(notNullValue()));
    assertThat(globalId, instanceOf(InstanceId.class));

    InstanceId instanceId = (InstanceId) globalId;
    assertThat(instanceId.getCdoId().toString(), is(alertId));
    assertThat(instanceId.getTypeName(), is("org.openlmis.cce.domain.Alert"));
  }

  @Test
  public void shouldCreateSnapshotsForInventoryItems() {
    //given
    UUID inventoryItemId = UUID.randomUUID();
    CatalogItem catalogItem = addCatalogItem();
    addInventoryItemSql(inventoryItemId, catalogItem.getId());

    //when
    QueryBuilder jqlQuery = QueryBuilder.byInstanceId(inventoryItemId, InventoryItem.class);
    List<CdoSnapshot> snapshots = javers.findSnapshots(jqlQuery.build());

    assertThat(snapshots, hasSize(0));

    AuditLogInitializer auditLogInitializer = new AuditLogInitializer(applicationContext, javers);
    auditLogInitializer.run();

    snapshots = javers.findSnapshots(jqlQuery.build());

    // then
    assertThat(snapshots, hasSize(1));

    CdoSnapshot snapshot = snapshots.get(0);
    GlobalId globalId = snapshot.getGlobalId();

    assertThat(globalId, is(notNullValue()));
    assertThat(globalId, instanceOf(InstanceId.class));

    InstanceId instanceId = (InstanceId) globalId;
    assertThat(instanceId.getCdoId(), is(inventoryItemId));
    assertThat(instanceId.getTypeName(), is("Inventory"));
  }

  private CatalogItem addCatalogItem() {
    CatalogItem catalogItem = new CatalogItemDataBuilder().build();
    return catalogItemRepository.save(catalogItem);
  }

  private InventoryItem addInventoryItem() {
    InventoryItem item = new InventoryItemDataBuilder()
        .withCatalogItem(addCatalogItem())
        .build();
    return inventoryItemRepository.save(item);
  }

  private void addAlert(String alertId, UUID inventoryItemId) {
    entityManager.flush();
    entityManager
        .createNativeQuery(INSERT_ALERT_SQL)
        .setParameter(1, alertId) //alertId
        .setParameter(2, UUID.randomUUID()) //external id
        .setParameter(3, "not_working_hot") //type
        .setParameter(4, inventoryItemId) //inventoryItemId
        .setParameter(5, ZonedDateTime.now()) //start timestamp
        .setParameter(6, ZonedDateTime.now()) //end timestamp
        .setParameter(7, ZonedDateTime.now()) //dismiss timestamp
        .executeUpdate();
  }

  private void addInventoryItemSql(UUID itemId, UUID catalogItemId) {
    entityManager.flush();
    entityManager
        .createNativeQuery(INSERT_INVENTORY_ITEM_SQL)
        .setParameter(1, itemId) //id
        .setParameter(2, UUID.randomUUID()) //facility id
        .setParameter(3, catalogItemId) //catalog item id
        .setParameter(4, UUID.randomUUID()) //program id
        .setParameter(5, UUID.randomUUID())//equipment tracking id
        .setParameter(6, "") //reference name
        .setParameter(7, 2010) //year of installation
        .setParameter(8, 2020) //year of warranty expiry
        .setParameter(9, "") //source
        .setParameter(10, FunctionalStatus.FUNCTIONING.name())
        .setParameter(11, ReasonNotWorkingOrNotInUse.NOT_IN_USE.name())
        .setParameter(12, Utilization.ACTIVE.name())
        .setParameter(13, VoltageStabilizerStatus.YES.name())
        .setParameter(14, BackupGeneratorStatus.YES.name())
        .setParameter(15, VoltageRegulatorStatus.YES.name())
        .setParameter(16, ManualTemperatureGaugeType.BUILD_IN.name())
        .setParameter(17, RemoteTemperatureMonitorType.BUILD_IN.name())
        .setParameter(18, "") //additional notes
        .setParameter(19, ZonedDateTime.now()) //modified date
        .setParameter(20, UUID.randomUUID()) // last modifier id
        .executeUpdate();
  }
}
