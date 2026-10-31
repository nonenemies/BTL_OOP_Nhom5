CREATE DATABASE QuanLyTinChi;
USE QuanLyTinChi;

CREATE TABLE Users (
    UserID INT AUTO_INCREMENT PRIMARY KEY,
    HoTen VARCHAR(100) NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    PasswordHash VARCHAR(255) NOT NULL,
    Role VARCHAR(20) NOT NULL,
    MaSo VARCHAR(20) UNIQUE,
    Khoa VARCHAR(100) NULL,
    NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
    TrangThai ENUM('HOAT_DONG', 'NGUNG_HOAT_DONG') NOT NULL DEFAULT 'HOAT_DONG'
);

CREATE TABLE SinhVien (
    UserID INT PRIMARY KEY,
    MaSV VARCHAR(20) UNIQUE,
    NgaySinh DATE NULL,
    GioiTinh TINYINT(1) NULL,
    Lop VARCHAR(50) NULL,
    KhoaHoc INT NULL,
    HeDaoTao VARCHAR(50) NULL,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

CREATE TABLE GiaoVien (
    UserID INT PRIMARY KEY,
    MaGV VARCHAR(20) UNIQUE,
    ChuyenNganh VARCHAR(100) NULL,
    HocVi VARCHAR(50) NULL,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

CREATE TABLE Admin (
    UserID INT PRIMARY KEY,
    ChucVu VARCHAR(100) NULL,
    FOREIGN KEY (UserID) REFERENCES Users(UserID)
);

CREATE TABLE MonHoc (
    MaMon VARCHAR(20) PRIMARY KEY,
    TenMon VARCHAR(200) NOT NULL,
    SoTinChi INT NOT NULL,
    LoaiMon VARCHAR(20) NOT NULL,
    MoTa VARCHAR(500) NULL,
    TrangThai ENUM('HOAT_DONG', 'NGUNG_HOAT_DONG') NOT NULL DEFAULT 'HOAT_DONG'
);

CREATE TABLE MonTienQuyet (
    MaMon VARCHAR(20),
    MaMonTienQuyet VARCHAR(20),
    PRIMARY KEY (MaMon, MaMonTienQuyet),
    FOREIGN KEY (MaMon) REFERENCES MonHoc(MaMon),
    FOREIGN KEY (MaMonTienQuyet) REFERENCES MonHoc(MaMon)
);

CREATE TABLE LopHoc (
    MaLop VARCHAR(20) PRIMARY KEY,
    MaMon VARCHAR(20),
    MaGV VARCHAR(20),
    HocKy VARCHAR(20) NOT NULL,
    SiSoToiDa INT NOT NULL,
    SiSoHienTai INT NOT NULL DEFAULT 0,
    TrangThai ENUM('HOAT_DONG', 'NGUNG_HOAT_DONG') NOT NULL DEFAULT 'HOAT_DONG',
    FOREIGN KEY (MaMon) REFERENCES MonHoc(MaMon),
    FOREIGN KEY (MaGV) REFERENCES GiaoVien(MaGV)
);

CREATE TABLE LichHoc (
    MaLich INT AUTO_INCREMENT PRIMARY KEY,
    MaLop VARCHAR(20) NOT NULL,
    Thu INT NOT NULL,
    GioBatDau TIME NOT NULL,
    GioKetThuc TIME NOT NULL,
    PhongHoc VARCHAR(50) NULL,
    FOREIGN KEY (MaLop) REFERENCES LopHoc(MaLop)
);

CREATE TABLE ThanhPhanDanhGia (
    MaTP INT AUTO_INCREMENT PRIMARY KEY,
    MaMon VARCHAR(20),
    TenThanhPhan VARCHAR(200) NOT NULL,
    TrongSo DECIMAL(5,2) NOT NULL,
    ThuTu INT NULL,
    TrangThai TINYINT(1) DEFAULT 1,
    FOREIGN KEY (MaMon) REFERENCES MonHoc(MaMon)
);

CREATE TABLE DangKyHoc (
    MaDangKy INT AUTO_INCREMENT PRIMARY KEY,
    MaLop VARCHAR(20) NOT NULL,
    MaSV VARCHAR(20) NOT NULL,
    CONSTRAINT uq_dangky_sv_lop UNIQUE (MaSV, MaLop),
    NgayDangKy DATETIME DEFAULT CURRENT_TIMESTAMP,
    DiemTongKet DECIMAL(5,2) NULL,
    TrangThai VARCHAR(20) DEFAULT 'DANG_HOC',
    FOREIGN KEY (MaLop) REFERENCES LopHoc(MaLop),
    FOREIGN KEY (MaSV) REFERENCES SinhVien(MaSV)
);

CREATE TABLE DiemThanhPhan (
    MaDiemTP INT AUTO_INCREMENT PRIMARY KEY,
    MaDangKy INT,
    MaTP INT,
    DiemDatDuoc DECIMAL(5,2) NOT NULL,
    NgayNhap DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaDangKy) REFERENCES DangKyHoc(MaDangKy),
    FOREIGN KEY (MaTP) REFERENCES ThanhPhanDanhGia(MaTP)
);

CREATE TABLE YeuCauDoiLop (
    MaYeuCau INT AUTO_INCREMENT PRIMARY KEY,
    MaSV VARCHAR(20) NOT NULL,
    LopCu VARCHAR(20),
    LopMoi VARCHAR(20),
    TrangThai VARCHAR(20) DEFAULT 'CHO_XAC_NHAN',
    LyDo VARCHAR(200) NULL,
    NgayTao DATETIME DEFAULT CURRENT_TIMESTAMP,
    NgayXuLy DATETIME NULL,
    NguoiXuLy INT NULL,
    FOREIGN KEY (MaSV) REFERENCES SinhVien(MaSV),
    FOREIGN KEY (LopCu) REFERENCES LopHoc(MaLop),
    FOREIGN KEY (LopMoi) REFERENCES LopHoc(MaLop),
    FOREIGN KEY (NguoiXuLy) REFERENCES Users(UserID)
);

-- Thêm dữ liệu mẫu (Mock Data)
INSERT INTO Users (HoTen, Email, PasswordHash, Role, MaSo) VALUES
('Quản Trị Viên', 'admin@truong.edu.vn', '123', 'ADMIN', 'AD01'),
('Thầy Trần Code', 'code.tran@truong.edu.vn', '123', 'GIAOVIEN', 'GV01'),
('Nguyễn Văn Sinh', 'sinh.nguyen@sv.edu.vn', '123', 'SINHVIEN', 'SV001'),
('Lê Thị Viên', 'vien.le@sv.edu.vn', '123', 'SINHVIEN', 'SV002'),
('Trần Văn Ba', 'ba.tran@sv.edu.vn', '123', 'SINHVIEN', 'SV003');

INSERT INTO Admin (UserID, ChucVu) VALUES (1, 'Trưởng phòng Đào tạo');
INSERT INTO GiaoVien (UserID, MaGV, ChuyenNganh, HocVi) VALUES (2, 'GV01', 'Công nghệ phần mềm', 'Thạc sĩ');
INSERT INTO SinhVien (UserID, MaSV, Lop, KhoaHoc) VALUES (3, 'SV001', 'CN01', 2021);
INSERT INTO SinhVien (UserID, MaSV, Lop, KhoaHoc) VALUES (4, 'SV002', 'CN02', 2021);
INSERT INTO SinhVien (UserID, MaSV, Lop, KhoaHoc) VALUES (5, 'SV003', 'CN03', 2021);

INSERT INTO MonHoc (MaMon, TenMon, SoTinChi, LoaiMon) VALUES
('IT01', 'Lập trình OOP', 3, 'BATBUOC'),
('IT02', 'Cơ sở dữ liệu', 3, 'BATBUOC');

INSERT INTO ThanhPhanDanhGia (MaMon, TenThanhPhan, TrongSo, ThuTu) VALUES
('IT01', 'Giữa kỳ', 0.3, 1),
('IT01', 'Cuối kỳ', 0.7, 2);

INSERT INTO LopHoc (MaLop, MaMon, MaGV, HocKy, SiSoToiDa, SiSoHienTai) VALUES
('OOP_L01', 'IT01', 'GV01', '2024-1', 40, 0),
('DB_L01', 'IT02', 'GV01', '2024-1', 40, 0);

INSERT INTO LichHoc (MaLop, Thu, GioBatDau, GioKetThuc, PhongHoc) VALUES
('OOP_L01', 2, '08:00:00', '10:00:00', 'Phòng 101 - Nhà A'),
('DB_L01', 3, '08:00:00', '10:00:00', 'Phòng 202 - Nhà A');
