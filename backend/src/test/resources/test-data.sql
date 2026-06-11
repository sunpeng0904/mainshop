-- 区域测试数据（省份/城市/区域）
INSERT INTO region_tb (id, cde, name, prnt_cde, lvl) VALUES ('r001', '110000', '北京市', NULL, 1);
INSERT INTO region_tb (id, cde, name, prnt_cde, lvl) VALUES ('r002', '110100', '北京市', '110000', 2);
INSERT INTO region_tb (id, cde, name, prnt_cde, lvl) VALUES ('r003', '110105', '朝阳区', '110100', 3);
INSERT INTO region_tb (id, cde, name, prnt_cde, lvl) VALUES ('r004', '310000', '上海市', NULL, 1);
INSERT INTO region_tb (id, cde, name, prnt_cde, lvl) VALUES ('r005', '310100', '上海市', '310000', 2);
INSERT INTO region_tb (id, cde, name, prnt_cde, lvl) VALUES ('r006', '310101', '黄浦区', '310100', 3);
INSERT INTO region_tb (id, cde, name, prnt_cde, lvl) VALUES ('r007', '440000', '广东省', NULL, 1);
INSERT INTO region_tb (id, cde, name, prnt_cde, lvl) VALUES ('r008', '440100', '广州市', '440000', 2);
INSERT INTO region_tb (id, cde, name, prnt_cde, lvl) VALUES ('r009', '440106', '天河区', '440100', 3);
