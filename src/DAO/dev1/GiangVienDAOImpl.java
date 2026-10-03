package dao;

import model.GiangVien;

import java.util.ArrayList;

public class GiangVienDAOImpl implements GiangVienDAO {

    public static GiangVienDAOImpl getInstance(){
        return new GiangVienDAOImpl();
    }

    @Override
    public ArrayList<GiangVien> selectAll() {
        return null;
    }

    @Override
    public GiangVien selectByID(int id) {
        return null;
    }

    @Override
    public void insert(GiangVien giangVien) {

    }

    @Override
    public void delete(GiangVien giangVien) {

    }

    @Override
    public void update(GiangVien giangVien) {

    }
}
