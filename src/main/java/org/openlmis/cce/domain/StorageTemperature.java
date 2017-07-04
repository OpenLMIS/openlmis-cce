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

package org.openlmis.cce.domain;

public enum StorageTemperature {
  PLUS4(4),
  PLUS3(3),
  PLUS2(2),
  PLUS1(1),
  ZERO(0),
  MINUS1(-1),
  MINUS2(-2),
  MINUS3(-3),
  MINUS4(-4),
  MINUS5(-5),
  MINUS6(-6),
  MINUS7(-7),
  MINUS8(-8),
  MINUS9(-9),
  MINUS10(-10),
  MINUS11(-11),
  MINUS12(-12),
  MINUS13(-13),
  MINUS14(-14),
  MINUS15(-15),
  MINUS16(-16),
  MINUS17(-17),
  MINUS18(-18),
  MINUS19(-19),
  MINUS20(-20);

  private int value;

  StorageTemperature(int value) {
    this.value = value;
  }
}
