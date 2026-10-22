package DAO.dev1;

import Model.dev1.GiangVien;

import java.util.ArrayList;

public interface GiangVienDAO extends BaseDao<GiangVien> {

    ArrayList<GiangVien> selectByName(String name);

    ArrayList<GiangVien> selectByKhoa(String khoa);

    int countGiangVien();

    boolean existsByID(int id);
}