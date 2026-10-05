package DAO;

import java.util.ArrayList;
import java.util.List;

public class MonHocDAO {
    // ĐÓNG THẾ CHO DEV 2
    public List<String> layDanhSachMonTienQuyet(String maMon) {
        List<String> listTienQuyet = new ArrayList<>();

        // Giả lập: Để học OOP (IT01) thì phải qua môn Nhập môn C (IT00)
        if (maMon.equals("IT01")) {
            listTienQuyet.add("IT00");
        }
        return listTienQuyet;
    }
}