package DAO.dev1;

import Model.dev1.SinhVien;

import java.util.ArrayList;

public interface SinhVienDAO extends BaseDao<SinhVien>{
    SinhVien selectByMaSV(String maSV);

    ArrayList<SinhVien> selectByLop(String lop);

    ArrayList<SinhVien> selectByKhoaHoc(int khoaHoc);


}
