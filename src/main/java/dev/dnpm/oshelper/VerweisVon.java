/*
 * This file is part of onkostar-plugin-dnpm
 *
 * Copyright (c) 2025 the original author or authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
