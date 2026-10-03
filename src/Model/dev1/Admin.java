package model;

import java.util.Date;

public class Admin extends Users {
        private String chucVu;

        public Admin() {
                super();
        }

        public Admin(Integer userID, String chucVu) {
                super(userID);
                this.chucVu = chucVu;
        }

        public String getChucVu() {
                return chucVu;
        }

        public void setChucVu(String chucVu) {
                this.chucVu = chucVu;
        }
}
