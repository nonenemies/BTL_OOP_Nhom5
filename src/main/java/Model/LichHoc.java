package Model;

import java.time.LocalTime;

public class LichHoc {
    private int maLich;
    private String maLop;
    private int thu;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;

    public LichHoc() {
    }

    public LichHoc(int maLich, String maLop, int thu, LocalTime gioBatDau, LocalTime gioKetThuc) {
        this.maLich = maLich;
        this.maLop = maLop;
        this.thu = thu;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
    }

    public int getMaLich() { return maLich; }
    public void setMaLich(int maLich) { this.maLich = maLich; }
    public String getMaLop() { return maLop; }
    public void setMaLop(String maLop) { this.maLop = maLop; }
    public int getThu() { return thu; }
    public void setThu(int thu) { this.thu = thu; }
    public LocalTime getGioBatDau() { return gioBatDau; }
    public void setGioBatDau(LocalTime gioBatDau) { this.gioBatDau = gioBatDau; }
    public LocalTime getGioKetThuc() { return gioKetThuc; }
    public void setGioKetThuc(LocalTime gioKetThuc) { this.gioKetThuc = gioKetThuc; }
}
