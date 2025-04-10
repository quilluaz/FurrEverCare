package cit.edu.furrevercare.entity;

import java.util.List;

public class MedicalRecord {
    private String recordID;
    private String clinicName;
    private List<String> medications;
    private String type;
    private String vetName;

    public MedicalRecord() {}

    public MedicalRecord(String recordID, String clinicName, List<String> medications, String type, String vetName) {
        this.recordID = recordID;
        this.clinicName = clinicName;
        this.medications = medications;
        this.type = type;
        this.vetName = vetName;
    }

    public String getRecordID() {
        return recordID;
    }

    public void setRecordID(String recordID) {
        this.recordID = recordID;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public List<String> getMedications() {
        return medications;
    }

    public void setMedications(List<String> medications) {
        this.medications = medications;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVetName() {
        return vetName;
    }

    public void setVetName(String vetName) {
        this.vetName = vetName;
    }
}
