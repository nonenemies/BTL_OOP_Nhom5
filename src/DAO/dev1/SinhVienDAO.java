package dao;

import model.SinhVien;

import java.util.ArrayList;

public interface SinhVienDAO extends BaseDao<SinhVien>{
    SinhVien selectByMaSV(String maSV);

    ArrayList<SinhVien> selectByLop(String lop);

    ArrayList<SinhVien> selectByKhoaHoc(int khoaHoc);


}
