/*
 * This file is part of onkostar-plugin-dnpm
 *
 * Copyright (C) 2023-2026 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.dnpm.oshelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class VerweisVon {
  private int procedure_id;
  private int data_form_id;
  private String data_catalogue;
  private String data_catalogue_entry;
  private String formname;
  private Date datum;

  public VerweisVon() {
  }

  @SuppressWarnings("unused")
  public int getProcedure_id() { return this.procedure_id; }
  public int getData_form_id() { return this.data_form_id; }
  public String getData_catalogue_name() { return this.data_catalogue; }
  public String getData_catalogue_entry_name() { return this.data_catalogue_entry; }
  public String getFormname() { return this.formname; }
  public Date getDate() { return this.datum; }
  public String getTable() {
    return "dk_" + this.data_catalogue.toLowerCase().replaceAll("[^a-zA-Z0-9]", "_");
  }
  public String getField() {
    return this.data_catalogue_entry.toLowerCase();
  }
  public String getSQL() {
    return "SELECT " + this.getField() + " AS value FROM " + this.getTable() + " WHERE id = " + this.getProcedure_id();
  }
  private String getDatumAsString() {
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    String Datum = null;
    if (this.getDate() != null) {
      Datum = DATE_FORMAT.format(this.getDate());
    }
    return Datum;
  }
  public String getVerbundenesFormular() {
    String FName = "Formular " + this.getFormname();
    if (this.getDatumAsString() != null ) { FName += " vom " + this.getDatumAsString(); }
    return FName;
  }

  @SuppressWarnings("unused")
  public void setProcedure_id(int procedure_id) {this.procedure_id = procedure_id; }
  public void setData_form_id(int data_form_id) {this.data_form_id = data_form_id; }
  public void setData_catalogue_name(String data_catalogue_name) {this.data_catalogue = data_catalogue_name; }
  public void setData_catalogue_entry_name(String data_catalogue_entry) {this.data_catalogue_entry = data_catalogue_entry; }
  public void setDate(Date datum) { this.datum = datum; }
  public void setFormname(String formname) { this.formname = formname; }
}
