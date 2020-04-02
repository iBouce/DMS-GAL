package model;

import java.util.Date;

public class documentModel {

    Integer doc_id;
    String doc_user_id,doc_name,doc_ocr, doc_type,doc_extension,doc_size;
    Date doc_date;
    byte[] doc_data;

    public documentModel(Integer id,String user_id, String name,String ocr,String type, String extension, String size, Date date){
        this.doc_id = id;
        this.doc_user_id = user_id;
        this.doc_name = name;
        this.doc_ocr = ocr;
        this.doc_type = type;
        this.doc_extension = extension;
        this.doc_size = size;
        this.doc_date = date;
        //this.doc_data = data;
    }

    public Integer getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(Integer id) {
        this.doc_id = id;
    }

    public String getDoc_user_id() {
        return doc_user_id;
    }

    public void setDoc_user_id(String user_id) {
        this.doc_user_id = user_id;
    }

    public String getDoc_name() {
        return doc_name;
    }

    public void setDoc_name(String name) {
        this.doc_name = name;
    }

    public String getDoc_ocr() {
        return doc_ocr;
    }

    public void setDoc_ocr(String ocr) {
        this.doc_ocr = ocr;
    }

    public String getDoc_type() {
        return doc_type;
    }

    public void setDoc_type(String type) {
        this.doc_type = type;
    }

    public String getDoc_extension() {
        return doc_extension;
    }

    public void setDoc_extension(String extension) {
        this.doc_extension = extension;
    }

    public String getDoc_size() {
        return doc_size;
    }

    public void setDoc_size(String size) {
        this.doc_size = size;
    }

    public Date getDoc_date() {
        return doc_date;
    }

    public void setDoc_date(Date date) {
        this.doc_date = date;
    }

    public byte[] getDoc_data() { return doc_data; }

    public void setDoc_data(byte[] data) { this.doc_data = data; }

}
