package vu.pham.todolistvu;

public class ToDoModel {
    private int Id;
    private String NoiDung;
    private boolean TrangThai;
    private int Mau;

    public ToDoModel(int id, String noiDung, boolean trangThai, int mau) {
        Id=id;
        NoiDung = noiDung;
        TrangThai = trangThai;
        Mau=mau;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getMau() {
        return Mau;
    }

    public void setMau(int mau) {
        Mau = mau;
    }

    public String getNoiDung() {
        return NoiDung;
    }

    public void setNoiDung(String noiDung) {
        NoiDung = noiDung;
    }

    public boolean isTrangThai() {
        return TrangThai;
    }

    public void setTrangThai(boolean trangThai) {
        TrangThai = trangThai;
    }
}
