package Model;
import java.util.Date;

public class YeuCauDoiLop {
    private int maYeuCau;
    private int maSV;
    private String lopCu;
    private String lopMoi;
    private TrangThaiDoiLop trangThai;
    private String lyDo;
    private Date ngayTao;
    private Date ngayXuLy;
    private int nguoiXuLy; // Dùng lưu ID của người Chốt đơn

    public YeuCauDoiLop() {}

    public YeuCauDoiLop(int maSV, String lopCu, String lopMoi, String lyDo) {
        this.maSV = maSV;
        this.lopCu = lopCu;
        this.lopMoi = lopMoi;
        this.lyDo = lyDo;
    }

    public int getMaYeuCau() { return maYeuCau; }
    public void setMaYeuCau(int maYeuCau) { this.maYeuCau = maYeuCau; }
    public int getMaSV() { return maSV; }
    public void setMaSV(int maSV) { this.maSV = maSV; }
    public String getLopCu() { return lopCu; }
    public void setLopCu(String lopCu) { this.lopCu = lopCu; }
    public String getLopMoi() { return lopMoi; }
    public void setLopMoi(String lopMoi) { this.lopMoi = lopMoi; }
    public TrangThaiDoiLop getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiDoiLop trangThai) { this.trangThai = trangThai; }
    public String getLyDo() { return lyDo; }
    public void setLyDo(String lyDo) { this.lyDo = lyDo; }
    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }
    public Date getNgayXuLy() { return ngayXuLy; }
    public void setNgayXuLy(Date ngayXuLy) { this.ngayXuLy = ngayXuLy; }
    public int getNguoiXuLy() { return nguoiXuLy; }
    public void setNguoiXuLy(int nguoiXuLy) { this.nguoiXuLy = nguoiXuLy; }
}