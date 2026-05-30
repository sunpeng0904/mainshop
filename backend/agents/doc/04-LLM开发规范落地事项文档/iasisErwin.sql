
CREATE TABLE iasis.certf_send_news_rcod
(
	id                   VARCHAR(32) NOT NULL,
	name                 VARCHAR(100) NULL,
	news                 TEXT NULL,
	entr_time            TIMESTAMP NULL,
	news_sts             VARCHAR(1) NULL
);

ALTER TABLE iasis.certf_send_news_rcod
ADD PRIMARY KEY (id);

CREATE TABLE iasis.certf_send_prsn_cfg_tb
(
	id                   VARCHAR(32) NOT NULL,
	on_line_port_set     TEXT NULL,
	lower_line_port_set  TEXT NULL,
	dept_name            VARCHAR(100) NULL,
	name                 VARCHAR(100) NULL,
	mob_nbr              VARCHAR(50) NULL,
	vld_id               VARCHAR(1) NULL,
	entr_time            TIMESTAMP NULL,
	alter_time           TIMESTAMP NULL
);

ALTER TABLE iasis.certf_send_prsn_cfg_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.cras_random_select_spvs_prjc_rcod_tb
(
	random_select_spvs_prjc_rcod_id VARCHAR(32) NOT NULL,
	select_time          TIMESTAMP NULL,
	exch_name            VARCHAR(10) NULL,
	pub_board            VARCHAR(2) NULL,
	exch_sbmtd_data_time TIMESTAMP NULL,
	select_rate          INTEGER NULL,
	sbmtd_prjc_vol       INTEGER NULL,
	select_prjc_vol      INTEGER NULL,
	select_result        VARCHAR(1) NULL,
	spvs_psn_name        VARCHAR(100) NULL,
	work_flow_exmp_id    VARCHAR(64) NULL,
	insd_aprv_indc       CHAR(1) NULL,
	flow_sts             VARCHAR(1) NULL,
	entr_psn_id          VARCHAR(32) NULL,
	entr_psn_name        VARCHAR(100) NULL,
	entr_time            TIMESTAMP NULL,
	alter_time           TIMESTAMP NULL
);

ALTER TABLE iasis.cras_random_select_spvs_prjc_rcod_tb
ADD PRIMARY KEY (random_select_spvs_prjc_rcod_id);

CREATE TABLE iasis.dl_port_sum_tb
(
	id                   VARCHAR(32) NOT NULL,
	data_src             VARCHAR(10) NULL,
	port_nbr             VARCHAR(100) NULL,
	data_date            TIMESTAMP NULL,
	port_dirc            VARCHAR(50) NULL,
	btch_nbr             VARCHAR(10) NULL,
	port_file_name       VARCHAR(100) NULL,
	stkt_tn_data_qtt     VARCHAR(32) NULL,
	un_stkt_tn_file_data_qtt VARCHAR(32) NULL,
	s_time               TIMESTAMP NULL,
	e_time               TIMESTAMP NULL
);

ALTER TABLE iasis.dl_port_sum_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.exch_sbmtd_data_lst_tb
(
	id                   VARCHAR(32) NOT NULL,
	send_id              VARCHAR() NULL,
	data_src             VARCHAR(100) NULL,
	port_nbr             VARCHAR(100) NULL,
	port_dirc            VARCHAR(50) NULL,
	data_date            TIMESTAMP NULL,
	btch_nbr             VARCHAR(100) NULL,
	file_name            VARCHAR(100) NULL,
	stkt_tn_data_qtt     VARCHAR(32) NULL,
	un_stkt_tn_file_data_qtt VARCHAR(32) NULL,
	port_sts             VARCHAR(2) NULL,
	sys_time             TIMESTAMP NULL,
	import_time          TIMESTAMP NULL,
	biz_renew_time       TIMESTAMP NULL,
	if_new_data          VARCHAR(2) NULL,
	echg_file_name       VARCHAR(100) NULL,
	data_src__2887       VARCHAR(10) NULL
);

ALTER TABLE iasis.exch_sbmtd_data_lst_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.exch_sbmtd_debt_spvs_prjc_info_tb
(
	reg_rcod_id          VARCHAR(32) NULL,
	sbmtd_debt_spvs_prjc_id VARCHAR(32) NOT NULL,
	prjc_name            VARCHAR(1024) NULL,
	pub_board            VARCHAR(2) NULL,
	fin_type             VARCHAR(2) NULL,
	acpt_time            TIMESTAMP NULL,
	prjc_sts             VARCHAR(16) NULL,
	sml_amt_fast_indc    VARCHAR(2) NULL,
	lower_week_cnvk_ctr_audit_meet_indc VARCHAR(2) NULL,
	clct_fund_excd100_billion_yuan_indc VARCHAR(2) NULL,
	prprd_rasm_amt_billion_yuan DECIMAL(22,10) NULL,
	ipo_est_mkt_val_excd500_billion_yuan_indc VARCHAR(2) NULL,
	ipo_est_mkt_val_billion_yuan DECIMAL(22,10) NULL,
	for_impot_meaning_prjc_indc VARCHAR(2) NULL,
	version_nbr          MEDIUMINT NULL,
	renew_time           TIMESTAMP NULL,
	corlt_id             VARCHAR(100) NULL,
	import_time          TIMESTAMP NULL,
	biz_renew_time       TIMESTAMP NULL,
	new_data_indc        CHAR(1) NULL,
	echg_file_name       VARCHAR(500) NULL,
	data_src             VARCHAR(10) NULL,
	ext_field_one        VARCHAR(2) NULL
);

ALTER TABLE iasis.exch_sbmtd_debt_spvs_prjc_info_tb
ADD PRIMARY KEY (sbmtd_debt_spvs_prjc_id);

CREATE TABLE iasis.field_chk_already_go_ntrl_d_tb
(
	id                   VARCHAR(32) NOT NULL,
	already_go_ntrl_d    INTEGER NULL,
	two_idv_m_aft_date   DATE NULL,
	vrtm_date            DATE NULL,
	if_vrtm_indc         CHAR(1) NULL,
	prjc_info_tb_id      VARCHAR(32) NULL
);

ALTER TABLE iasis.field_chk_already_go_ntrl_d_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.field_chk_apro_condt_tb
(
	id                   VARCHAR(32) NOT NULL,
	fact_apro_date       DATE NULL,
	rmak                 TEXT NULL,
	entr_psn_name        VARCHAR(100) NULL,
	entr_psn_id          VARCHAR(32) NULL,
	entr_time            TIMESTAMP NULL,
	alter_time           TIMESTAMP NULL,
	prjc_info_tb_id      VARCHAR(32) NULL,
	alter_psn_name       VARCHAR(100) NULL,
	alter_psn_id         VARCHAR(32) NULL,
	data_oprt_cde        VARCHAR(1) NULL
);

ALTER TABLE iasis.field_chk_apro_condt_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.field_chk_aprv_flow_info_tb
(
	id                   VARCHAR(32) NOT NULL,
	chk_prgm_tb_id       VARCHAR(32) NULL,
	cthr_tb_id           VARCHAR(32) NULL,
	app_postp_tb_id      VARCHAR(32) NULL,
	flow_sts_cde         VARCHAR(1) NULL,
	entr_time            TIMESTAMP NULL,
	aprv_opni            VARCHAR(3) NULL,
	work_flow_exmp_id    VARCHAR(20) NULL,
	work_flow_flow_id    VARCHAR(64) NULL,
	alter_time           TIMESTAMP NULL
);

ALTER TABLE iasis.field_chk_aprv_flow_info_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.field_chk_chk_prgm_tb
(
	id                   VARCHAR(32) NOT NULL,
	prjc_info_tb_id      VARCHAR(32) NULL,
	plan_apro_date       DATE NULL,
	plan_fnsh_date       DATE NULL,
	data_oprt_cde        VARCHAR(2) NULL,
	entr_time            TIMESTAMP NULL,
	alter_time           TIMESTAMP NULL,
	entr_psn_name        VARCHAR(100) NULL,
	entr_psn_id          VARCHAR(32) NULL,
	alter_psn_name       VARCHAR(100) NULL,
	alter_psn_id         VARCHAR(32) NULL,
	chk_prgm_rcve_sts_cde VARCHAR(1) NULL,
	first_tm_submit_time TIMESTAMP NULL
);

ALTER TABLE iasis.field_chk_chk_prgm_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.field_chk_chk_team_memb_info_tb
(
	id                   VARCHAR(32) NOT NULL,
	chk_prgm_tb_id       VARCHAR(32) NULL,
	name                 VARCHAR(100) NULL,
	at_unit_name         VARCHAR(200) NULL,
	at_dept_name         VARCHAR(50) NULL,
	title_name           VARCHAR(50) NULL,
	team_inr_title_cde   VARCHAR(20) NULL,
	cntct_mode           VARCHAR(50) NULL,
	prsn_type_cde        VARCHAR(10) NULL,
	data_vld_indc        VARCHAR(20) NULL,
	entr_time            TIMESTAMP NULL,
	alter_time           TIMESTAMP NULL,
	time_stmp            MEDIUMINT NULL
);

ALTER TABLE iasis.field_chk_chk_team_memb_info_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.field_chk_cnvk_csta_meet_condt_tb
(
	id                   VARCHAR(32) NOT NULL,
	cnvk_csta_meet_date  DATE NULL,
	cnvk_year            VARCHAR(4) NULL,
	cnt                  INTEGER NULL,
	prjc_info_tb_id      VARCHAR(32) NULL,
	entr_psn_name        VARCHAR(100) NULL,
	entr_psn_id          VARCHAR(32) NULL,
	alter_psn_name       VARCHAR(100) NULL,
	alter_psn_id         VARCHAR(20) NULL,
	entr_time            TIMESTAMP NULL,
	alter_time           TIMESTAMP NULL,
	data_oprt_cde        VARCHAR(1) NULL
);

ALTER TABLE iasis.field_chk_cnvk_csta_meet_condt_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.field_chk_corlt_atch_tb
(
	id                   VARCHAR(32) NOT NULL,
	atch_name            VARCHAR(300) NULL,
	atch_id              VARCHAR(64) NULL,
	atch_clsf_cde        VARCHAR(10) NULL,
	chk_prgm_tb_id       VARCHAR(32) NULL,
	atch_sts_cde         VARCHAR(2) NULL,
	cthr_tb_id           VARCHAR(32) NULL,
	atch_tb_id           VARCHAR(32) NULL,
	upload_psn_name      VARCHAR(100) NULL,
	upload_psn_id        VARCHAR(32) NULL,
	upload_time          TIMESTAMP NULL,
	rmak                 VARCHAR(1000) NULL
);

ALTER TABLE iasis.field_chk_corlt_atch_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.field_chk_cthr_tb
(
	id                   VARCHAR(32) NOT NULL,
	prjc_info_tb_id      VARCHAR(32) NULL,
	fact_fnsh_date       DATE NULL,
	cthr_dscr            TEXT NULL,
	prly_proc_advi       TEXT NULL,
	entr_time            TIMESTAMP NULL,
	alter_time           TIMESTAMP NULL,
	entr_psn_name        VARCHAR(100) NULL,
	entr_psn_id          VARCHAR(32) NULL,
	alter_psn_id         VARCHAR(32) NULL,
	alter_psn_name       VARCHAR(100) NULL,
	data_oprt_cde        VARCHAR(1) NULL,
	cthr_rcve_sts_cde    VARCHAR(1) NULL,
	first_tm_submit_time TIMESTAMP NULL
);

ALTER TABLE iasis.field_chk_cthr_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.field_chk_entrp_info_tb
(
	id                   VARCHAR(32) NOT NULL,
	entrp_fname          VARCHAR(512) NULL,
	prjc_name            VARCHAR(1024) NULL,
	acpt_vbl_ptb_id      VARCHAR(32) NULL,
	reg_audit_ptb_id     VARCHAR(32) NULL,
	pub_board_cde        VARCHAR(2) NULL,
	ppdm_zjj_cde         VARCHAR(32) NULL,
	prjc_insd_sts_cde    VARCHAR(10) NULL,
	af_name              VARCHAR(200) NULL,
	lf_name              VARCHAR(200) NULL,
	renew_time           TIMESTAMP NULL,
	data_vld_indc        CHAR(1) NULL,
	entr_time            TIMESTAMP NULL,
	alter_time           TIMESTAMP NULL,
	reg_rcod_id          VARCHAR(32) NULL,
	spnsos_name          VARCHAR(1000) NULL
);

ALTER TABLE iasis.field_chk_entrp_info_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.field_chk_postp_app_tb
(
	id                   VARCHAR(32) NOT NULL,
	postp_days           INTEGER NULL,
	postp_app_dscr       TEXT NULL,
	entr_time            TIMESTAMP NULL,
	alter_time           TIMESTAMP NULL,
	prjc_info_tb_id      VARCHAR(32) NULL,
	entr_psn_name        VARCHAR(100) NULL,
	entr_psn_id          VARCHAR(32) NULL
);

ALTER TABLE iasis.field_chk_postp_app_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.field_chk_prjc_info_tb
(
	id                   VARCHAR(32) NOT NULL,
	chk_infm_leno        VARCHAR(100) NULL,
	entrp_name           VARCHAR(512) NULL,
	prjc_phase_cde       VARCHAR(1) NULL,
	pub_board_cde        VARCHAR(2) NULL,
	ppdm_zjj_cde         VARCHAR(20) NULL,
	prjc_insd_sts_cde    VARCHAR(10) NULL,
	af_name              VARCHAR(200) NULL,
	lf_name              VARCHAR(200) NULL,
	chk_infm_emit_time   TIMESTAMP NULL,
	chk_chrc_cde         VARCHAR(1) NULL,
	chk_impl_unit_cde    VARCHAR(10) NULL,
	prjc_dscr            TEXT NULL,
	rmak                 TEXT NULL,
	entr_psn_id          VARCHAR(32) NULL,
	entr_psn_name        VARCHAR(100) NULL,
	entr_time            TIMESTAMP NULL,
	alter_time           TIMESTAMP NULL,
	data_oprt_cde        VARCHAR(1) NULL,
	reg_rcod_id          VARCHAR(32) NULL,
	nbr                  VARCHAR(11) NULL,
	entr_psn_dept_id     VARCHAR(32) NULL,
	entr_psn_dept_name   VARCHAR(100) NULL,
	chk_schd_cde         VARCHAR(2) NULL,
	data_vld_indc        CHAR(1) NULL,
	alter_psn_name       VARCHAR(100) NULL,
	alter_psn_id         VARCHAR(20) NULL,
	postp_cnt            INTEGER NULL,
	acpt_vbl_ptb_id      VARCHAR(32) NULL,
	alter_psn_dept_id    VARCHAR(32) NULL,
	alter_psn_dept_name  VARCHAR(100) NULL,
	first_tm_submit_time TIMESTAMP NULL,
	prjc_name            VARCHAR(1024) NULL
);

ALTER TABLE iasis.field_chk_prjc_info_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.field_chk_schd_time_axis_tb
(
	id                   VARCHAR(32) NOT NULL,
	prjc_info_tb_id      VARCHAR(32) NULL,
	schd_whn_pt_cde      VARCHAR(2) NULL,
	schd_whn_pt_name     VARCHAR(20) NULL,
	schd_whn_pt_time     TIMESTAMP NULL,
	entr_time            TIMESTAMP NULL,
	exh_ico_cde          VARCHAR(2) NULL,
	data_vld_id          CHAR(1) NULL
);

ALTER TABLE iasis.field_chk_schd_time_axis_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.field_chk_spnsos_info_tb
(
	id                   VARCHAR(32) NOT NULL,
	spnsos_name          VARCHAR(200) NULL,
	prjc_chk_info_tb_id  VARCHAR(32) NULL,
	data_vld_indc        CHAR(1) NULL,
	entr_time            TIMESTAMP NULL,
	alter_time           TIMESTAMP NULL,
	time_stmp            MEDIUMINT NULL
);

ALTER TABLE iasis.field_chk_spnsos_info_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.inspct_port_cfg_tb
(
	id                   VARCHAR(32) NOT NULL,
	data_src             VARCHAR(10) NULL,
	port_nbr             VARCHAR(100) NULL,
	port_dirc            VARCHAR(50) NULL,
	vld_id               VARCHAR(1) NULL,
	entr_time            TIMESTAMP NULL,
	alter_time           TIMESTAMP NULL
);

ALTER TABLE iasis.inspct_port_cfg_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.iss_dept_send_debt_spvs_prjc_info_tb
(
	id                   VARCHAR(32) NOT NULL,
	reg_rcod             VARCHAR(32) NULL,
	select_spvs_id       VARCHAR(32) NULL,
	prjc_name            VARCHAR(1024) NULL,
	pub_board            VARCHAR(2) NULL,
	prjc_vol             INTEGER NULL,
	sendlt_time          TIMESTAMP NULL,
	version_nbr          MEDIUMINT NULL,
	renew_time           TIMESTAMP NULL,
	corlt_id             CHAR(18) NULL,
	import_time          TIMESTAMP NULL,
	biz_renew_time       TIMESTAMP NULL,
	if_new_data          CHAR(1) NULL,
	echg_file_name       VARCHAR(500) NULL,
	data_src             VARCHAR(10) NULL,
	board                VARCHAR(2) NULL
);

ALTER TABLE iasis.iss_dept_send_debt_spvs_prjc_info_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.iss_sys_send_field_chk_corlt_prjc_info_tb
(
	id                   VARCHAR(32) NOT NULL,
	prjc_info_tb_id      VARCHAR(32) NULL,
	reg_rcod_id          VARCHAR(32) NULL,
	pub_board_cde        VARCHAR(20) NULL,
	entrp_name           VARCHAR(512) NULL,
	version_nbr          MEDIUMINT NULL,
	renew_time           TIMESTAMP NULL,
	import_time          TIMESTAMP NULL,
	biz_renew_time       TIMESTAMP NULL,
	if_new_data_indc     CHAR(1) NULL,
	data_at_file_name    VARCHAR(500) NULL,
	data_src             VARCHAR(10) NULL,
	board                VARCHAR(10) NULL,
	prjc_name            VARCHAR(1024) NULL,
	if_exch_sbmtd_prjc_indc CHAR(1) NULL
);

ALTER TABLE iasis.iss_sys_send_field_chk_corlt_prjc_info_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.iss_sys_send_field_chk_corlt_prjc_info_tb
(
	id                   VARCHAR(32) NOT NULL,
	prjc_info_tb_id      VARCHAR(32) NULL,
	reg_rcod_id          VARCHAR(32) NULL,
	pub_board_cde        VARCHAR(20) NULL,
	entrp_name           VARCHAR(512) NULL,
	version_nbr          MEDIUMINT NULL,
	renew_time           TIMESTAMP NULL,
	import_time          TIMESTAMP NULL,
	biz_renew_time       TIMESTAMP NULL,
	if_new_data_indc     CHAR(1) NULL,
	data_at_file_name    VARCHAR(500) NULL,
	data_src             VARCHAR(10) NULL,
	board                VARCHAR(10) NULL,
	prjc_name            VARCHAR(1024) NULL,
	if_exch_sbmtd_prjc_indc CHAR(1) NULL
);

ALTER TABLE iasis.iss_sys_send_field_chk_corlt_prjc_info_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.iss_sys_send_field_cthr_atch_tb
(
	id                   VARCHAR(32) NOT NULL,
	prjc_chk_info_tb_id  VARCHAR(32) NULL,
	reg_rcod_id          VARCHAR(32) NULL,
	atch_cde             VARCHAR(100) NULL,
	atch_name            VARCHAR(512) NULL,
	file_suf_name        VARCHAR(10) NULL,
	file_clsf_cde        VARCHAR(16) NULL,
	atch_tot_vol         INTEGER NULL,
	version_nbr          MEDIUMINT NULL,
	renew_time           TIMESTAMP NULL,
	import_time          TIMESTAMP NULL,
	biz_renew_time       TIMESTAMP NULL,
	if_new_data_indc     CHAR(1) NULL,
	data_at_file_name    VARCHAR(512) NULL,
	data_src             VARCHAR(10) NULL,
	board                VARCHAR(10) NULL
);

ALTER TABLE iasis.iss_sys_send_field_cthr_atch_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.iss_sys_send_field_cthr_atch_tb
(
	id                   VARCHAR(32) NOT NULL,
	prjc_chk_info_tb_id  VARCHAR(32) NULL,
	reg_rcod_id          VARCHAR(32) NULL,
	atch_cde             VARCHAR(100) NULL,
	atch_name            VARCHAR(512) NULL,
	file_suf_name        VARCHAR(10) NULL,
	file_clsf_cde        VARCHAR(16) NULL,
	atch_tot_vol         INTEGER NULL,
	version_nbr          MEDIUMINT NULL,
	renew_time           TIMESTAMP NULL,
	import_time          TIMESTAMP NULL,
	biz_renew_time       TIMESTAMP NULL,
	if_new_data_indc     CHAR(1) NULL,
	data_at_file_name    VARCHAR(512) NULL,
	data_src             VARCHAR(10) NULL,
	board                VARCHAR(10) NULL
);

ALTER TABLE iasis.iss_sys_send_field_cthr_atch_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.my_prgm_dtld_fl_tb
(
	id                   VARCHAR(32) NOT NULL,
	my_prgm_tb_id        VARCHAR(32) NULL,
	wait_select_index_tb_eng_name VARCHAR(100) NULL,
	wait_select_index_tb_chn_name VARCHAR(100) NULL,
	wait_select_index_eng_name VARCHAR(100) NULL,
	wait_select_index_chn_name VARCHAR(100) NULL,
	wait_select_index_order INTEGER NULL,
	wait_select_range_dict_class_name VARCHAR(100) NULL,
	wait_select_range_dict_code_value VARCHAR(100) NULL,
	wait_select_range_dict_name VARCHAR(100) NULL,
	wait_select_range_dict_order INTEGER NULL
);

ALTER TABLE iasis.my_prgm_dtld_fl_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.my_prgm_tb
(
	id                   VARCHAR(32) NOT NULL,
	prgm_name            VARCHAR(50) NULL,
	entr_psn_id          VARCHAR(32) NULL,
	entr_psn_name        VARCHAR(100) NULL,
	entr_time            DATE NULL,
	alter_time           DATE NULL,
	if_vld_indc          CHAR(1) NULL
);

ALTER TABLE iasis.my_prgm_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.port_sum_cntrst_info
(
	id                   VARCHAR(32) NOT NULL,
	data_src             VARCHAR(10) NULL,
	port_nbr             VARCHAR(100) NULL,
	data_date            TIMESTAMP NULL,
	port_dirc            VARCHAR(50) NULL,
	port_btch_nbr        VARCHAR(10) NULL,
	file_name            VARCHAR(100) NULL,
	check_cde            TEXT NULL,
	check_info           TEXT NULL,
	entr_time            TIMESTAMP NULL,
	alter_time           TIMESTAMP NULL
);

ALTER TABLE iasis.port_sum_cntrst_info
ADD PRIMARY KEY (id);

CREATE TABLE iasis.pub_stmt_corlt_prjc_info_tb
(
	id                   VARCHAR(32) NOT NULL,
	pub_stmt_info_id     VARCHAR(32) NULL,
	reg_rcodUUID         VARCHAR(32) NULL,
	entrp_abbr           VARCHAR(512) NULL,
	stock_cde            VARCHAR(100) NULL,
	entrp_name           VARCHAR(512) NULL,
	board                VARCHAR(2) NULL,
	version_nbr          INTEGER NULL,
	renew_time           TIMESTAMP NULL,
	corlt_id             VARCHAR(100) NULL,
	import_time          TIMESTAMP NULL,
	biz_renew_time       TIMESTAMP NULL,
	if_new_data          VARCHAR(2) NULL,
	echg_file_name       VARCHAR(500) NULL,
	data_src             VARCHAR(10) NULL,
	ext_field_one        VARCHAR(10) NULL
);

ALTER TABLE iasis.pub_stmt_corlt_prjc_info_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.pub_stmt_data_chg_qtt_stat_tb
(
	id                   VARCHAR(32) NOT NULL,
	vol                  INTEGER NULL,
	chg_type             VARCHAR(1) NULL,
	data_src             VARCHAR(10) NULL,
	stat_date            DATE NULL
);

ALTER TABLE iasis.pub_stmt_data_chg_qtt_stat_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.pub_stmt_info_sum_tb
(
	id                   VARCHAR(32) NOT NULL,
	pub_stmt_title       VARCHAR(1024) NULL,
	pub_stmt_dlr         VARCHAR(1000) NULL,
	media_src_name       VARCHAR(1024) NULL,
	tsm_qtt              VARCHAR(32) NULL,
	pub_stmt_info_id     VARCHAR(32) NULL,
	emo_col              VARCHAR(1) NULL,
	orig_doc_link        VARCHAR(1024) NULL,
	issue_date           TIMESTAMP NULL,
	pub_stmt_ftxt        TEXT NULL,
	reg_rcod_cde         VARCHAR(32) NULL,
	entrp_abbr           VARCHAR(512) NULL,
	stock_cde            VARCHAR(100) NULL,
	entrp_name           VARCHAR(512) NULL,
	board                VARCHAR(2) NULL,
	entr_time            TIMESTAMP NULL,
	alter_time           TIMESTAMP NULL
);

ALTER TABLE iasis.pub_stmt_info_sum_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.pub_stmt_info_tb
(
	id                   VARCHAR(32) NOT NULL,
	pub_stmt_title       VARCHAR(1024) NULL,
	pub_stmt_dlr         VARCHAR(1000) NULL,
	media_src_name       VARCHAR(1024) NULL,
	tsm_qtt              VARCHAR(32) NULL,
	pub_stmt_info_id     VARCHAR(32) NULL,
	emo_col              VARCHAR(1) NULL,
	orig_doc_link        VARCHAR(1024) NULL,
	issue_date           TIMESTAMP NULL,
	pub_stmt_ftxt        TEXT NULL,
	version_nbr          INTEGER NULL,
	renew_time           TIMESTAMP NULL,
	corlt_id             VARCHAR(100) NULL,
	import_time          TIMESTAMP NULL,
	biz_renew_time       TIMESTAMP NULL,
	if_new_data          VARCHAR(2) NULL,
	echg_file_name       VARCHAR(500) NULL,
	data_src             VARCHAR(10) NULL,
	ext_field_one        VARCHAR(10) NULL
);

ALTER TABLE iasis.pub_stmt_info_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.sbmtd_csrc_file_lst
(
	id                   VARCHAR(32) NOT NULL,
	reg_rcod             VARCHAR(32) NULL,
	atch_type            VARCHAR(500) NULL,
	lst_type             VARCHAR(10) NULL,
	file_name            VARCHAR(1024) NULL,
	submit_csrc_reg_turn INTEGER NULL,
	data_cde             VARCHAR(256) NULL,
	file_format          VARCHAR(10) NULL,
	submit_csrc_reg_time TIMESTAMP NULL,
	atch_sort            INTEGER NULL,
	atch_idv_vol         INTEGER NULL,
	exch_file_listcode   VARCHAR(32) NULL,
	aprv_opni            VARCHAR(2028) NULL,
	atch_if_for_impot_file VARCHAR(10) NULL,
	version_nbr          MEDIUMINT NULL,
	renew_time           TIMESTAMP NULL,
	corlt_id             VARCHAR(100) NULL,
	import_time          TIMESTAMP NULL,
	biz_renew_time       TIMESTAMP NULL,
	if_new_data          VARCHAR(2) NULL,
	echg_file_name       VARCHAR(500) NULL,
	data_src             VARCHAR(10) NULL,
	board                VARCHAR(10) NULL,
	data_oprt_indc       CHAR(1) NULL
);

ALTER TABLE iasis.sbmtd_csrc_file_lst
ADD PRIMARY KEY (id);

CREATE TABLE iasis.select_spvs_prjc_tb
(
	id                   VARCHAR(32) NOT NULL,
	prin_board_acpt_vbl_ptb_id VARCHAR(32) NULL,
	reg_rcod_id          VARCHAR(64) NULL,
	pub_board            VARCHAR(2) NULL,
	prjc_name            VARCHAR(1024) NULL,
	work_flow_exmp_id    VARCHAR(64) NULL,
	if_insd_aprv         CHAR(1) NULL,
	sts                  VARCHAR(8) NULL,
	entr_time            TIMESTAMP NULL,
	alter_time           TIMESTAMP NULL,
	if_exh               CHAR(1) NULL,
	rmak                 TEXT NULL,
	random_select_spvs_prjc_rcod_id VARCHAR(32) NULL
);

ALTER TABLE iasis.select_spvs_prjc_tb
ADD PRIMARY KEY (id);

CREATE TABLE iasis.tmplt_cfg
(
	tmplt_cfg_tb_id      CHAR(32) NOT NULL,
	cfg_blngs_class      VARCHAR(32) NULL,
	cfg_blngs_class_describ VARCHAR(100) NULL,
	cfg_cde              VARCHAR(100) NULL,
	cfg_cde_value        VARCHAR(500) NULL,
	cfg_cde_describ      VARCHAR(200) NULL,
	cfg_rule             TEXT NULL,
	vld_sts_cde          VARCHAR(2) NULL,
	cfg_cde_value_clsf   VARCHAR(2) NULL,
	entr_time            TIMESTAMP NULL,
	renew_time           TIMESTAMP NULL
);

ALTER TABLE iasis.tmplt_cfg
ADD PRIMARY KEY (tmplt_cfg_tb_id);

CREATE TABLE iasis.wait_select_index_cfg_tb
(
	id                   VARCHAR(32) NOT NULL,
	wait_select_index_tb_eng_name VARCHAR(100) NULL,
	wait_select_index_tb_chn_name VARCHAR(100) NULL,
	wait_select_index_eng_name VARCHAR(100) NULL,
	wait_select_index_chn_name VARCHAR(100) NULL,
	clm_wide             INTEGER NULL,
	many_line_exh_indc   CHAR(1) NULL,
	tb_order             INTEGER NULL,
	field_order          INTEGER NULL,
	if_excd_clm_wide_indc CHAR(1) NULL,
	rmak                 VARCHAR(500) NULL
);

ALTER TABLE iasis.wait_select_index_cfg_tb
ADD PRIMARY KEY (id);

comment on TABLE iasis.certf_send_news_rcod is '证联讯推送消息记录';
comment on column iasis.certf_send_news_rcod.id  is '标识';
comment on column iasis.certf_send_news_rcod.name  is '姓名';
comment on column iasis.certf_send_news_rcod.news  is '消息';
comment on column iasis.certf_send_news_rcod.entr_time  is '创建时间';
comment on column iasis.certf_send_news_rcod.news_sts  is '消息状态';

comment on TABLE iasis.certf_send_prsn_cfg_tb is '证联讯推送人员配置表';
comment on column iasis.certf_send_prsn_cfg_tb.id  is '标识';
comment on column iasis.certf_send_prsn_cfg_tb.on_line_port_set  is '上行接口集合';
comment on column iasis.certf_send_prsn_cfg_tb.lower_line_port_set  is '下行接口集合';
comment on column iasis.certf_send_prsn_cfg_tb.dept_name  is '部门名称';
comment on column iasis.certf_send_prsn_cfg_tb.name  is '姓名';
comment on column iasis.certf_send_prsn_cfg_tb.mob_nbr  is '手机号码';
comment on column iasis.certf_send_prsn_cfg_tb.vld_id  is '有效标识';
comment on column iasis.certf_send_prsn_cfg_tb.entr_time  is '创建时间';
comment on column iasis.certf_send_prsn_cfg_tb.alter_time  is '修改时间';

comment on TABLE iasis.cras_random_select_spvs_prjc_rcod_tb is '随机抽选监督项目记录表';
comment on column iasis.cras_random_select_spvs_prjc_rcod_tb.random_select_spvs_prjc_rcod_id  is '随机抽选监督项目记录标识';
comment on column iasis.cras_random_select_spvs_prjc_rcod_tb.select_time  is '抽选时间';
comment on column iasis.cras_random_select_spvs_prjc_rcod_tb.exch_name  is '交易所名称';
comment on column iasis.cras_random_select_spvs_prjc_rcod_tb.pub_board  is '上市板块';
comment on column iasis.cras_random_select_spvs_prjc_rcod_tb.exch_sbmtd_data_time  is '交易所报送数据时间';
comment on column iasis.cras_random_select_spvs_prjc_rcod_tb.select_rate  is '抽选比例';
comment on column iasis.cras_random_select_spvs_prjc_rcod_tb.sbmtd_prjc_vol  is '报送项目数';
comment on column iasis.cras_random_select_spvs_prjc_rcod_tb.select_prjc_vol  is '抽选项目数';
comment on column iasis.cras_random_select_spvs_prjc_rcod_tb.select_result  is '抽选结果';
comment on column iasis.cras_random_select_spvs_prjc_rcod_tb.spvs_psn_name  is '监督人姓名';
comment on column iasis.cras_random_select_spvs_prjc_rcod_tb.work_flow_exmp_id  is '工作流实例标识';
comment on column iasis.cras_random_select_spvs_prjc_rcod_tb.insd_aprv_indc  is '内部审批标志';
comment on column iasis.cras_random_select_spvs_prjc_rcod_tb.flow_sts  is '流程状态';
comment on column iasis.cras_random_select_spvs_prjc_rcod_tb.entr_psn_id  is '创建人标识';
comment on column iasis.cras_random_select_spvs_prjc_rcod_tb.entr_psn_name  is '创建人姓名';
comment on column iasis.cras_random_select_spvs_prjc_rcod_tb.entr_time  is '创建时间';
comment on column iasis.cras_random_select_spvs_prjc_rcod_tb.alter_time  is '修改时间';

comment on TABLE iasis.dl_port_sum_tb is '每日接口汇总表';
comment on column iasis.dl_port_sum_tb.id  is '标识';
comment on column iasis.dl_port_sum_tb.data_src  is '数据源';
comment on column iasis.dl_port_sum_tb.port_nbr  is '接口编号';
comment on column iasis.dl_port_sum_tb.data_date  is '数据日期';
comment on column iasis.dl_port_sum_tb.port_dirc  is '接口方向';
comment on column iasis.dl_port_sum_tb.btch_nbr  is '批次号';
comment on column iasis.dl_port_sum_tb.port_file_name  is '接口文件名称';
comment on column iasis.dl_port_sum_tb.stkt_tn_data_qtt  is '结构化数据量';
comment on column iasis.dl_port_sum_tb.un_stkt_tn_file_data_qtt  is '非结构化文件数据量';
comment on column iasis.dl_port_sum_tb.s_time  is '开始时间';
comment on column iasis.dl_port_sum_tb.e_time  is '结束时间';

comment on TABLE iasis.exch_sbmtd_data_lst_tb is '交易所报送数据清单表';
comment on column iasis.exch_sbmtd_data_lst_tb.id  is '标识';
comment on column iasis.exch_sbmtd_data_lst_tb.send_id  is '推送标识';
comment on column iasis.exch_sbmtd_data_lst_tb.data_src  is '数据源';
comment on column iasis.exch_sbmtd_data_lst_tb.port_nbr  is '接口编号';
comment on column iasis.exch_sbmtd_data_lst_tb.port_dirc  is '接口方向';
comment on column iasis.exch_sbmtd_data_lst_tb.data_date  is '数据日期';
comment on column iasis.exch_sbmtd_data_lst_tb.btch_nbr  is '批次号';
comment on column iasis.exch_sbmtd_data_lst_tb.file_name  is '文件名称';
comment on column iasis.exch_sbmtd_data_lst_tb.stkt_tn_data_qtt  is '结构化数据量';
comment on column iasis.exch_sbmtd_data_lst_tb.un_stkt_tn_file_data_qtt  is '非结构化文件数据量';
comment on column iasis.exch_sbmtd_data_lst_tb.port_sts  is '接口状态';
comment on column iasis.exch_sbmtd_data_lst_tb.sys_time  is '系统时间';
comment on column iasis.exch_sbmtd_data_lst_tb.import_time  is '导入时间';
comment on column iasis.exch_sbmtd_data_lst_tb.biz_renew_time  is '业务更新时间';
comment on column iasis.exch_sbmtd_data_lst_tb.if_new_data  is '是否新数据';
comment on column iasis.exch_sbmtd_data_lst_tb.echg_file_name  is '交换文件名';
comment on column iasis.exch_sbmtd_data_lst_tb.data_src__2887  is '数据来源';

comment on TABLE iasis.exch_sbmtd_debt_spvs_prjc_info_tb is '交易所报送同步监督项目信息表';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.sbmtd_debt_spvs_prjc_id  is '报送同步监督项目标识';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.reg_rcod_id  is '注册记录标识';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.prjc_name  is '项目名称';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.pub_board  is '上市板块';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.fin_type  is '融资类型';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.acpt_time  is '受理时间';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.prjc_sts  is '项目状态';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.sml_amt_fast_indc  is '小额快速标志';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.lower_week_cnvk_ctr_audit_meet_indc  is '下周召开中心审核会标志';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.clct_fund_excd100_billion_yuan_indc  is '募集资金超过100亿元标志';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.prprd_rasm_amt_billion_yuan  is '拟筹资额（亿元）';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.ipo_est_mkt_val_excd500_billion_yuan_indc  is '首发预计市值超过500亿元标志';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.ipo_est_mkt_val_billion_yuan  is '首发预计市值（亿元）';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.for_impot_meaning_prjc_indc  is '为重要意义项目标志';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.version_nbr  is '版本号';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.renew_time  is '更新时间';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.corlt_id  is '关联标识';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.import_time  is '导入时间';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.biz_renew_time  is '业务更新时间';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.new_data_indc  is '新数据标志';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.echg_file_name  is '交换文件名';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.data_src  is '数据来源';
comment on column iasis.exch_sbmtd_debt_spvs_prjc_info_tb.ext_field_one  is '扩展字段一';

comment on TABLE iasis.field_chk_already_go_ntrl_d_tb is '现场检查已过自然日表';
comment on column iasis.field_chk_already_go_ntrl_d_tb.id  is '标识';
comment on column iasis.field_chk_already_go_ntrl_d_tb.already_go_ntrl_d  is '已过自然日';
comment on column iasis.field_chk_already_go_ntrl_d_tb.two_idv_m_aft_date  is '两个月后日期';
comment on column iasis.field_chk_already_go_ntrl_d_tb.vrtm_date  is '超时日期';
comment on column iasis.field_chk_already_go_ntrl_d_tb.if_vrtm_indc  is '是否超时标志';
comment on column iasis.field_chk_already_go_ntrl_d_tb.prjc_info_tb_id  is '立项信息表标识';

comment on TABLE iasis.field_chk_apro_condt_tb is '现场检查进场情况表';
comment on column iasis.field_chk_apro_condt_tb.id  is '标识';
comment on column iasis.field_chk_apro_condt_tb.fact_apro_date  is '实际进场日期';
comment on column iasis.field_chk_apro_condt_tb.rmak  is '备注';
comment on column iasis.field_chk_apro_condt_tb.entr_psn_name  is '创建人姓名';
comment on column iasis.field_chk_apro_condt_tb.entr_psn_id  is '创建人标识';
comment on column iasis.field_chk_apro_condt_tb.entr_time  is '创建时间';
comment on column iasis.field_chk_apro_condt_tb.alter_time  is '修改时间';
comment on column iasis.field_chk_apro_condt_tb.prjc_info_tb_id  is '立项信息表标识';
comment on column iasis.field_chk_apro_condt_tb.alter_psn_name  is '修改人姓名';
comment on column iasis.field_chk_apro_condt_tb.alter_psn_id  is '修改人标识';
comment on column iasis.field_chk_apro_condt_tb.data_oprt_cde  is '数据操作代码';

comment on TABLE iasis.field_chk_aprv_flow_info_tb is '现场检查审批流程信息表';
comment on column iasis.field_chk_aprv_flow_info_tb.id  is '标识';
comment on column iasis.field_chk_aprv_flow_info_tb.chk_prgm_tb_id  is '检查方案表标识';
comment on column iasis.field_chk_aprv_flow_info_tb.cthr_tb_id  is '检查结果表标识';
comment on column iasis.field_chk_aprv_flow_info_tb.app_postp_tb_id  is '申请延期表标识';
comment on column iasis.field_chk_aprv_flow_info_tb.flow_sts_cde  is '流程状态代码';
comment on column iasis.field_chk_aprv_flow_info_tb.aprv_opni  is '审批意见';
comment on column iasis.field_chk_aprv_flow_info_tb.work_flow_exmp_id  is '工作流实例标识';
comment on column iasis.field_chk_aprv_flow_info_tb.work_flow_flow_id  is '工作流流程主键';
comment on column iasis.field_chk_aprv_flow_info_tb.entr_time  is '创建时间';
comment on column iasis.field_chk_aprv_flow_info_tb.alter_time  is '修改时间';

comment on TABLE iasis.field_chk_chk_prgm_tb is '现场检查检查方案表';
comment on column iasis.field_chk_chk_prgm_tb.id  is '标识';
comment on column iasis.field_chk_chk_prgm_tb.prjc_info_tb_id  is '立项信息表标识';
comment on column iasis.field_chk_chk_prgm_tb.plan_apro_date  is '计划进场日期';
comment on column iasis.field_chk_chk_prgm_tb.plan_fnsh_date  is '计划完成日期';
comment on column iasis.field_chk_chk_prgm_tb.data_oprt_cde  is '数据操作代码';
comment on column iasis.field_chk_chk_prgm_tb.chk_prgm_rcve_sts_cde  is '检查方案接收状态代码';
comment on column iasis.field_chk_chk_prgm_tb.entr_time  is '创建时间';
comment on column iasis.field_chk_chk_prgm_tb.alter_time  is '修改时间';
comment on column iasis.field_chk_chk_prgm_tb.entr_psn_name  is '创建人姓名';
comment on column iasis.field_chk_chk_prgm_tb.entr_psn_id  is '创建人标识';
comment on column iasis.field_chk_chk_prgm_tb.alter_psn_name  is '修改人姓名';
comment on column iasis.field_chk_chk_prgm_tb.alter_psn_id  is '修改人标识';
comment on column iasis.field_chk_chk_prgm_tb.first_tm_submit_time  is '第一次提交时间';

comment on TABLE iasis.field_chk_chk_team_memb_info_tb is '现场检查检查组成员信息表';
comment on column iasis.field_chk_chk_team_memb_info_tb.id  is '标识';
comment on column iasis.field_chk_chk_team_memb_info_tb.chk_prgm_tb_id  is '检查方案表标识';
comment on column iasis.field_chk_chk_team_memb_info_tb.name  is '姓名';
comment on column iasis.field_chk_chk_team_memb_info_tb.at_unit_name  is '所在单位名称';
comment on column iasis.field_chk_chk_team_memb_info_tb.at_dept_name  is '所在部门名称';
comment on column iasis.field_chk_chk_team_memb_info_tb.title_name  is '职务名称';
comment on column iasis.field_chk_chk_team_memb_info_tb.team_inr_title_cde  is '组内职务代码';
comment on column iasis.field_chk_chk_team_memb_info_tb.cntct_mode  is '联系方式';
comment on column iasis.field_chk_chk_team_memb_info_tb.prsn_type_cde  is '人员类别代码';
comment on column iasis.field_chk_chk_team_memb_info_tb.data_vld_indc  is '数据有效标志';
comment on column iasis.field_chk_chk_team_memb_info_tb.entr_time  is '创建时间';
comment on column iasis.field_chk_chk_team_memb_info_tb.alter_time  is '修改时间';
comment on column iasis.field_chk_chk_team_memb_info_tb.time_stmp  is '时间戳';

comment on TABLE iasis.field_chk_cnvk_csta_meet_condt_tb is '现场检查召开会商会情况表';
comment on column iasis.field_chk_cnvk_csta_meet_condt_tb.id  is '标识';
comment on column iasis.field_chk_cnvk_csta_meet_condt_tb.cnvk_csta_meet_date  is '召开会商会日期';
comment on column iasis.field_chk_cnvk_csta_meet_condt_tb.cnvk_year  is '召开年度';
comment on column iasis.field_chk_cnvk_csta_meet_condt_tb.cnt  is '次数';
comment on column iasis.field_chk_cnvk_csta_meet_condt_tb.prjc_info_tb_id  is '立项信息表标识';
comment on column iasis.field_chk_cnvk_csta_meet_condt_tb.entr_psn_name  is '创建人姓名';
comment on column iasis.field_chk_cnvk_csta_meet_condt_tb.entr_psn_id  is '创建人标识';
comment on column iasis.field_chk_cnvk_csta_meet_condt_tb.alter_psn_name  is '修改人姓名';
comment on column iasis.field_chk_cnvk_csta_meet_condt_tb.alter_psn_id  is '修改人标识';
comment on column iasis.field_chk_cnvk_csta_meet_condt_tb.entr_time  is '创建时间';
comment on column iasis.field_chk_cnvk_csta_meet_condt_tb.alter_time  is '修改时间';
comment on column iasis.field_chk_cnvk_csta_meet_condt_tb.data_oprt_cde  is '数据操作代码';

comment on TABLE iasis.field_chk_corlt_atch_tb is '现场检查关联附件表';
comment on column iasis.field_chk_corlt_atch_tb.id  is '标识';
comment on column iasis.field_chk_corlt_atch_tb.chk_prgm_tb_id  is '检查方案表标识';
comment on column iasis.field_chk_corlt_atch_tb.cthr_tb_id  is '检查结果表标识';
comment on column iasis.field_chk_corlt_atch_tb.atch_name  is '附件名称';
comment on column iasis.field_chk_corlt_atch_tb.atch_id  is '附件标识';
comment on column iasis.field_chk_corlt_atch_tb.atch_clsf_cde  is '附件分类代码';
comment on column iasis.field_chk_corlt_atch_tb.atch_sts_cde  is '附件状态代码';
comment on column iasis.field_chk_corlt_atch_tb.atch_tb_id  is '附件表标识';
comment on column iasis.field_chk_corlt_atch_tb.upload_psn_name  is '上传人姓名';
comment on column iasis.field_chk_corlt_atch_tb.upload_psn_id  is '上传人标识';
comment on column iasis.field_chk_corlt_atch_tb.upload_time  is '上传时间';
comment on column iasis.field_chk_corlt_atch_tb.rmak  is '备注';

comment on TABLE iasis.field_chk_cthr_tb is '现场检查检查结果表';
comment on column iasis.field_chk_cthr_tb.id  is '标识';
comment on column iasis.field_chk_cthr_tb.prjc_info_tb_id  is '立项信息表标识';
comment on column iasis.field_chk_cthr_tb.fact_fnsh_date  is '实际完成日期';
comment on column iasis.field_chk_cthr_tb.cthr_dscr  is '检查结果说明';
comment on column iasis.field_chk_cthr_tb.prly_proc_advi  is '初步处理建议';
comment on column iasis.field_chk_cthr_tb.cthr_rcve_sts_cde  is '检查结果接收状态代码';
comment on column iasis.field_chk_cthr_tb.entr_time  is '创建时间';
comment on column iasis.field_chk_cthr_tb.alter_time  is '修改时间';
comment on column iasis.field_chk_cthr_tb.entr_psn_name  is '创建人姓名';
comment on column iasis.field_chk_cthr_tb.entr_psn_id  is '创建人标识';
comment on column iasis.field_chk_cthr_tb.alter_psn_id  is '修改人标识';
comment on column iasis.field_chk_cthr_tb.alter_psn_name  is '修改人姓名';
comment on column iasis.field_chk_cthr_tb.data_oprt_cde  is '数据操作代码';
comment on column iasis.field_chk_cthr_tb.first_tm_submit_time  is '第一次提交时间';

comment on TABLE iasis.field_chk_entrp_info_tb is '现场检查企业信息表';
comment on column iasis.field_chk_entrp_info_tb.id  is '标识';
comment on column iasis.field_chk_entrp_info_tb.entrp_fname  is '企业全称';
comment on column iasis.field_chk_entrp_info_tb.prjc_name  is '项目名称';
comment on column iasis.field_chk_entrp_info_tb.acpt_vbl_ptb_id  is '受理可见主表标识';
comment on column iasis.field_chk_entrp_info_tb.reg_audit_ptb_id  is '注册审核主表标识';
comment on column iasis.field_chk_entrp_info_tb.reg_rcod_id  is '注册记录标识';
comment on column iasis.field_chk_entrp_info_tb.pub_board_cde  is '上市板块代码';
comment on column iasis.field_chk_entrp_info_tb.ppdm_zjj_cde  is '辖区证监局代码';
comment on column iasis.field_chk_entrp_info_tb.prjc_insd_sts_cde  is '项目内部状态代码';
comment on column iasis.field_chk_entrp_info_tb.spnsos_name  is '保荐机构名称';
comment on column iasis.field_chk_entrp_info_tb.af_name  is '会计师事务所名称';
comment on column iasis.field_chk_entrp_info_tb.lf_name  is '律师事务所名称';
comment on column iasis.field_chk_entrp_info_tb.renew_time  is '更新时间';
comment on column iasis.field_chk_entrp_info_tb.data_vld_indc  is '数据有效标志';
comment on column iasis.field_chk_entrp_info_tb.entr_time  is '创建时间';
comment on column iasis.field_chk_entrp_info_tb.alter_time  is '修改时间';

comment on TABLE iasis.field_chk_postp_app_tb is '现场检查延期申请表';
comment on column iasis.field_chk_postp_app_tb.id  is '标识';
comment on column iasis.field_chk_postp_app_tb.postp_days  is '延期天数';
comment on column iasis.field_chk_postp_app_tb.postp_app_dscr  is '延期申请说明';
comment on column iasis.field_chk_postp_app_tb.entr_time  is '创建时间';
comment on column iasis.field_chk_postp_app_tb.alter_time  is '修改时间';
comment on column iasis.field_chk_postp_app_tb.prjc_info_tb_id  is '立项信息表标识';
comment on column iasis.field_chk_postp_app_tb.entr_psn_name  is '创建人姓名';
comment on column iasis.field_chk_postp_app_tb.entr_psn_id  is '创建人标识';

comment on TABLE iasis.field_chk_prjc_info_tb is '现场检查立项信息表';
comment on column iasis.field_chk_prjc_info_tb.id  is '标识';
comment on column iasis.field_chk_prjc_info_tb.chk_infm_leno  is '检查通知函号';
comment on column iasis.field_chk_prjc_info_tb.entrp_name  is '企业名称';
comment on column iasis.field_chk_prjc_info_tb.prjc_name  is '项目名称';
comment on column iasis.field_chk_prjc_info_tb.reg_rcod_id  is '注册记录标识';
comment on column iasis.field_chk_prjc_info_tb.acpt_vbl_ptb_id  is '受理可见主表标识';
comment on column iasis.field_chk_prjc_info_tb.prjc_phase_cde  is '项目阶段代码';
comment on column iasis.field_chk_prjc_info_tb.pub_board_cde  is '上市板块代码';
comment on column iasis.field_chk_prjc_info_tb.ppdm_zjj_cde  is '辖区证监局代码';
comment on column iasis.field_chk_prjc_info_tb.prjc_insd_sts_cde  is '项目内部状态代码';
comment on column iasis.field_chk_prjc_info_tb.af_name  is '会计师事务所名称';
comment on column iasis.field_chk_prjc_info_tb.lf_name  is '律师事务所名称';
comment on column iasis.field_chk_prjc_info_tb.chk_infm_emit_time  is '检查通知发出时间';
comment on column iasis.field_chk_prjc_info_tb.chk_chrc_cde  is '检查性质代码';
comment on column iasis.field_chk_prjc_info_tb.chk_impl_unit_cde  is '检查实施单位代码';
comment on column iasis.field_chk_prjc_info_tb.prjc_dscr  is '立项说明';
comment on column iasis.field_chk_prjc_info_tb.rmak  is '备注';
comment on column iasis.field_chk_prjc_info_tb.chk_schd_cde  is '检查进度代码';
comment on column iasis.field_chk_prjc_info_tb.nbr  is '编号';
comment on column iasis.field_chk_prjc_info_tb.postp_cnt  is '延期次数';
comment on column iasis.field_chk_prjc_info_tb.data_oprt_cde  is '数据操作代码';
comment on column iasis.field_chk_prjc_info_tb.data_vld_indc  is '数据有效标志';
comment on column iasis.field_chk_prjc_info_tb.entr_psn_id  is '创建人标识';
comment on column iasis.field_chk_prjc_info_tb.entr_psn_name  is '创建人姓名';
comment on column iasis.field_chk_prjc_info_tb.entr_psn_dept_id  is '创建人部门标识';
comment on column iasis.field_chk_prjc_info_tb.entr_psn_dept_name  is '创建人部门名称';
comment on column iasis.field_chk_prjc_info_tb.entr_time  is '创建时间';
comment on column iasis.field_chk_prjc_info_tb.alter_time  is '修改时间';
comment on column iasis.field_chk_prjc_info_tb.alter_psn_name  is '修改人姓名';
comment on column iasis.field_chk_prjc_info_tb.alter_psn_id  is '修改人标识';
comment on column iasis.field_chk_prjc_info_tb.alter_psn_dept_id  is '修改人部门标识';
comment on column iasis.field_chk_prjc_info_tb.alter_psn_dept_name  is '修改人部门名称';
comment on column iasis.field_chk_prjc_info_tb.first_tm_submit_time  is '第一次提交时间';

comment on TABLE iasis.field_chk_schd_time_axis_tb is '现场检查进度时间轴表';
comment on column iasis.field_chk_schd_time_axis_tb.id  is '标识';
comment on column iasis.field_chk_schd_time_axis_tb.prjc_info_tb_id  is '立项信息表标识';
comment on column iasis.field_chk_schd_time_axis_tb.schd_whn_pt_cde  is '进度时点代码';
comment on column iasis.field_chk_schd_time_axis_tb.schd_whn_pt_name  is '进度时点名称';
comment on column iasis.field_chk_schd_time_axis_tb.schd_whn_pt_time  is '进度时点时间';
comment on column iasis.field_chk_schd_time_axis_tb.entr_time  is '创建时间';
comment on column iasis.field_chk_schd_time_axis_tb.exh_ico_cde  is '展示图标代码';
comment on column iasis.field_chk_schd_time_axis_tb.data_vld_id  is '数据有效标识';

comment on TABLE iasis.field_chk_spnsos_info_tb is '现场检查保荐机构信息表';
comment on column iasis.field_chk_spnsos_info_tb.id  is '标识';
comment on column iasis.field_chk_spnsos_info_tb.spnsos_name  is '保荐机构名称';
comment on column iasis.field_chk_spnsos_info_tb.prjc_chk_info_tb_id  is '立项检查信息表标识';
comment on column iasis.field_chk_spnsos_info_tb.data_vld_indc  is '数据有效标志';
comment on column iasis.field_chk_spnsos_info_tb.entr_time  is '创建时间';
comment on column iasis.field_chk_spnsos_info_tb.alter_time  is '修改时间';
comment on column iasis.field_chk_spnsos_info_tb.time_stmp  is '时间戳';

comment on TABLE iasis.inspct_port_cfg_tb is '核查接口配置表';
comment on column iasis.inspct_port_cfg_tb.id  is '标识';
comment on column iasis.inspct_port_cfg_tb.data_src  is '数据源';
comment on column iasis.inspct_port_cfg_tb.port_nbr  is '接口编号';
comment on column iasis.inspct_port_cfg_tb.port_dirc  is '接口方向';
comment on column iasis.inspct_port_cfg_tb.vld_id  is '有效标识';
comment on column iasis.inspct_port_cfg_tb.entr_time  is '创建时间';
comment on column iasis.inspct_port_cfg_tb.alter_time  is '修改时间';

comment on TABLE iasis.iss_dept_send_debt_spvs_prjc_info_tb is '发行部发送同步监督项目信息表';
comment on column iasis.iss_dept_send_debt_spvs_prjc_info_tb.id  is '标识';
comment on column iasis.iss_dept_send_debt_spvs_prjc_info_tb.reg_rcod  is '注册记录';
comment on column iasis.iss_dept_send_debt_spvs_prjc_info_tb.select_spvs_id  is '抽选监督标识';
comment on column iasis.iss_dept_send_debt_spvs_prjc_info_tb.prjc_name  is '项目名称';
comment on column iasis.iss_dept_send_debt_spvs_prjc_info_tb.pub_board  is '上市板块';
comment on column iasis.iss_dept_send_debt_spvs_prjc_info_tb.prjc_vol  is '项目数量';
comment on column iasis.iss_dept_send_debt_spvs_prjc_info_tb.sendlt_time  is '发函时间';
comment on column iasis.iss_dept_send_debt_spvs_prjc_info_tb.version_nbr  is '版本号';
comment on column iasis.iss_dept_send_debt_spvs_prjc_info_tb.renew_time  is '更新时间';
comment on column iasis.iss_dept_send_debt_spvs_prjc_info_tb.corlt_id  is '关联标识';
comment on column iasis.iss_dept_send_debt_spvs_prjc_info_tb.import_time  is '导入时间';
comment on column iasis.iss_dept_send_debt_spvs_prjc_info_tb.biz_renew_time  is '业务更新时间';
comment on column iasis.iss_dept_send_debt_spvs_prjc_info_tb.if_new_data  is '是否新数据';
comment on column iasis.iss_dept_send_debt_spvs_prjc_info_tb.echg_file_name  is '交换文件名';
comment on column iasis.iss_dept_send_debt_spvs_prjc_info_tb.data_src  is '数据来源';
comment on column iasis.iss_dept_send_debt_spvs_prjc_info_tb.board  is '板块';

comment on TABLE iasis.iss_sys_send_field_chk_corlt_prjc_info_tb is '发行系统发送现场检查关联项目信息表';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.id  is '标识';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.prjc_info_tb_id  is '立项信息表标识';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.reg_rcod_id  is '注册记录标识';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.pub_board_cde  is '上市板块代码';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.prjc_name  is '项目名称';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.entrp_name  is '企业名称';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.if_exch_sbmtd_prjc_indc  is '是否交易所报送项目标志';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.version_nbr  is '版本号';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.renew_time  is '更新时间';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.import_time  is '导入时间';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.biz_renew_time  is '业务更新时间';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.if_new_data_indc  is '是否新数据标志';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.data_at_file_name  is '数据所在文件名';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.data_src  is '数据来源';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.board  is '板块';

comment on TABLE iasis.iss_sys_send_field_chk_corlt_prjc_info_tb is '发行系统发送现场检查关联项目信息表';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.id  is '标识';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.prjc_info_tb_id  is '立项信息表标识';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.reg_rcod_id  is '注册记录标识';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.pub_board_cde  is '上市板块代码';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.prjc_name  is '项目名称';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.entrp_name  is '企业名称';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.if_exch_sbmtd_prjc_indc  is '是否交易所报送项目标志';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.version_nbr  is '版本号';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.renew_time  is '更新时间';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.import_time  is '导入时间';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.biz_renew_time  is '业务更新时间';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.if_new_data_indc  is '是否新数据标志';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.data_at_file_name  is '数据所在文件名';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.data_src  is '数据来源';
comment on column iasis.iss_sys_send_field_chk_corlt_prjc_info_tb.board  is '板块';

comment on TABLE iasis.iss_sys_send_field_cthr_atch_tb is '发行系统发送现场检查结果附件表';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.id  is '标识';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.prjc_chk_info_tb_id  is '立项检查信息表标识';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.reg_rcod_id  is '注册记录标识';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.atch_cde  is '附件编码';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.atch_name  is '附件名称';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.file_suf_name  is '文件后缀名';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.file_clsf_cde  is '文件分类代码';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.atch_tot_vol  is '附件总数';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.version_nbr  is '版本号';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.renew_time  is '更新时间';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.import_time  is '导入时间';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.biz_renew_time  is '业务更新时间';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.if_new_data_indc  is '是否新数据标志';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.data_at_file_name  is '数据所在文件名';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.data_src  is '数据来源';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.board  is '板块';

comment on TABLE iasis.iss_sys_send_field_cthr_atch_tb is '发行系统发送现场检查结果附件表';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.id  is '标识';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.prjc_chk_info_tb_id  is '立项检查信息表标识';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.reg_rcod_id  is '注册记录标识';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.atch_cde  is '附件编码';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.atch_name  is '附件名称';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.file_suf_name  is '文件后缀名';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.file_clsf_cde  is '文件分类代码';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.atch_tot_vol  is '附件总数';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.version_nbr  is '版本号';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.renew_time  is '更新时间';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.import_time  is '导入时间';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.biz_renew_time  is '业务更新时间';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.if_new_data_indc  is '是否新数据标志';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.data_at_file_name  is '数据所在文件名';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.data_src  is '数据来源';
comment on column iasis.iss_sys_send_field_cthr_atch_tb.board  is '板块';

comment on TABLE iasis.my_prgm_dtld_fl_tb is '我的方案详情表';
comment on column iasis.my_prgm_dtld_fl_tb.id  is '标识';
comment on column iasis.my_prgm_dtld_fl_tb.my_prgm_tb_id  is '我的方案表标识';
comment on column iasis.my_prgm_dtld_fl_tb.wait_select_index_tb_eng_name  is '待选指标表英文名';
comment on column iasis.my_prgm_dtld_fl_tb.wait_select_index_tb_chn_name  is '待选指标表中文名';
comment on column iasis.my_prgm_dtld_fl_tb.wait_select_index_eng_name  is '待选指标英文名';
comment on column iasis.my_prgm_dtld_fl_tb.wait_select_index_chn_name  is '待选指标中文名';
comment on column iasis.my_prgm_dtld_fl_tb.wait_select_index_order  is '待选指标顺序';
comment on column iasis.my_prgm_dtld_fl_tb.wait_select_range_dict_class_name  is '待选范围字典大类名称';
comment on column iasis.my_prgm_dtld_fl_tb.wait_select_range_dict_code_value  is '待选范围字典码值';
comment on column iasis.my_prgm_dtld_fl_tb.wait_select_range_dict_name  is '待选范围字典名称';
comment on column iasis.my_prgm_dtld_fl_tb.wait_select_range_dict_order  is '待选范围字典顺序';

comment on TABLE iasis.my_prgm_tb is '我的方案表';
comment on column iasis.my_prgm_tb.id  is '标识';
comment on column iasis.my_prgm_tb.prgm_name  is '方案名称';
comment on column iasis.my_prgm_tb.entr_psn_id  is '创建人标识';
comment on column iasis.my_prgm_tb.entr_psn_name  is '创建人姓名';
comment on column iasis.my_prgm_tb.entr_time  is '创建时间';
comment on column iasis.my_prgm_tb.alter_time  is '修改时间';
comment on column iasis.my_prgm_tb.if_vld_indc  is '是否有效标志';

comment on TABLE iasis.port_sum_cntrst_info is '接口汇总对比信息';
comment on column iasis.port_sum_cntrst_info.id  is '标识';
comment on column iasis.port_sum_cntrst_info.data_src  is '数据源';
comment on column iasis.port_sum_cntrst_info.port_nbr  is '接口编号';
comment on column iasis.port_sum_cntrst_info.data_date  is '数据日期';
comment on column iasis.port_sum_cntrst_info.port_dirc  is '接口方向';
comment on column iasis.port_sum_cntrst_info.port_btch_nbr  is '接口批次号';
comment on column iasis.port_sum_cntrst_info.file_name  is '文件名';
comment on column iasis.port_sum_cntrst_info.check_cde  is '核对代码';
comment on column iasis.port_sum_cntrst_info.check_info  is '核对信息';
comment on column iasis.port_sum_cntrst_info.entr_time  is '创建时间';
comment on column iasis.port_sum_cntrst_info.alter_time  is '修改时间';

comment on TABLE iasis.pub_stmt_corlt_prjc_info_tb is '舆情关联项目信息表';
comment on column iasis.pub_stmt_corlt_prjc_info_tb.id  is '标识';
comment on column iasis.pub_stmt_corlt_prjc_info_tb.pub_stmt_info_id  is '舆情信息主键';
comment on column iasis.pub_stmt_corlt_prjc_info_tb.reg_rcodUUID  is '注册记录UUID';
comment on column iasis.pub_stmt_corlt_prjc_info_tb.entrp_abbr  is '企业简称';
comment on column iasis.pub_stmt_corlt_prjc_info_tb.stock_cde  is '股票代码';
comment on column iasis.pub_stmt_corlt_prjc_info_tb.entrp_name  is '企业名称';
comment on column iasis.pub_stmt_corlt_prjc_info_tb.board  is '板块';
comment on column iasis.pub_stmt_corlt_prjc_info_tb.version_nbr  is '版本号';
comment on column iasis.pub_stmt_corlt_prjc_info_tb.renew_time  is '更新时间';
comment on column iasis.pub_stmt_corlt_prjc_info_tb.ext_field_one  is '扩展字段一';
comment on column iasis.pub_stmt_corlt_prjc_info_tb.corlt_id  is '关联标识';
comment on column iasis.pub_stmt_corlt_prjc_info_tb.import_time  is '导入时间';
comment on column iasis.pub_stmt_corlt_prjc_info_tb.biz_renew_time  is '业务更新时间';
comment on column iasis.pub_stmt_corlt_prjc_info_tb.if_new_data  is '是否新数据';
comment on column iasis.pub_stmt_corlt_prjc_info_tb.echg_file_name  is '交换文件名';
comment on column iasis.pub_stmt_corlt_prjc_info_tb.data_src  is '数据源';

comment on TABLE iasis.pub_stmt_data_chg_qtt_stat_tb is '舆情数据变化量统计表';
comment on column iasis.pub_stmt_data_chg_qtt_stat_tb.id  is '标识';
comment on column iasis.pub_stmt_data_chg_qtt_stat_tb.vol  is '数量';
comment on column iasis.pub_stmt_data_chg_qtt_stat_tb.chg_type  is '变化类型';
comment on column iasis.pub_stmt_data_chg_qtt_stat_tb.data_src  is '数据来源';
comment on column iasis.pub_stmt_data_chg_qtt_stat_tb.stat_date  is '统计日期';

comment on TABLE iasis.pub_stmt_info_sum_tb is '舆情信息汇总表';
comment on column iasis.pub_stmt_info_sum_tb.id  is '标识';
comment on column iasis.pub_stmt_info_sum_tb.pub_stmt_info_id  is '舆情信息标识';
comment on column iasis.pub_stmt_info_sum_tb.pub_stmt_title  is '舆情标题';
comment on column iasis.pub_stmt_info_sum_tb.pub_stmt_dlr  is '舆情标签';
comment on column iasis.pub_stmt_info_sum_tb.media_src_name  is '媒体来源名称';
comment on column iasis.pub_stmt_info_sum_tb.tsm_qtt  is '转载量';
comment on column iasis.pub_stmt_info_sum_tb.emo_col  is '情感色彩';
comment on column iasis.pub_stmt_info_sum_tb.orig_doc_link  is '原文链接';
comment on column iasis.pub_stmt_info_sum_tb.issue_date  is '发布日期';
comment on column iasis.pub_stmt_info_sum_tb.pub_stmt_ftxt  is '舆情全文';
comment on column iasis.pub_stmt_info_sum_tb.reg_rcod_cde  is '注册记录编码';
comment on column iasis.pub_stmt_info_sum_tb.entrp_abbr  is '企业简称';
comment on column iasis.pub_stmt_info_sum_tb.stock_cde  is '股票代码';
comment on column iasis.pub_stmt_info_sum_tb.entrp_name  is '企业名称';
comment on column iasis.pub_stmt_info_sum_tb.board  is '板块';
comment on column iasis.pub_stmt_info_sum_tb.entr_time  is '创建时间';
comment on column iasis.pub_stmt_info_sum_tb.alter_time  is '修改时间';

comment on TABLE iasis.pub_stmt_info_tb is '舆情信息表';
comment on column iasis.pub_stmt_info_tb.id  is '标识';
comment on column iasis.pub_stmt_info_tb.pub_stmt_info_id  is '舆情信息主键';
comment on column iasis.pub_stmt_info_tb.pub_stmt_title  is '舆情标题';
comment on column iasis.pub_stmt_info_tb.pub_stmt_dlr  is '舆情标签';
comment on column iasis.pub_stmt_info_tb.media_src_name  is '媒体来源名称';
comment on column iasis.pub_stmt_info_tb.tsm_qtt  is '转载量';
comment on column iasis.pub_stmt_info_tb.emo_col  is '情感色彩';
comment on column iasis.pub_stmt_info_tb.orig_doc_link  is '原文链接';
comment on column iasis.pub_stmt_info_tb.issue_date  is '发布日期';
comment on column iasis.pub_stmt_info_tb.pub_stmt_ftxt  is '舆情全文';
comment on column iasis.pub_stmt_info_tb.version_nbr  is '版本号';
comment on column iasis.pub_stmt_info_tb.renew_time  is '更新时间';
comment on column iasis.pub_stmt_info_tb.ext_field_one  is '扩展字段一';
comment on column iasis.pub_stmt_info_tb.corlt_id  is '关联标识';
comment on column iasis.pub_stmt_info_tb.import_time  is '导入时间';
comment on column iasis.pub_stmt_info_tb.biz_renew_time  is '业务更新时间';
comment on column iasis.pub_stmt_info_tb.if_new_data  is '是否新数据';
comment on column iasis.pub_stmt_info_tb.echg_file_name  is '交换文件名';
comment on column iasis.pub_stmt_info_tb.data_src  is '数据源';

comment on TABLE iasis.sbmtd_csrc_file_lst is '报送证监会文件清单';
comment on column iasis.sbmtd_csrc_file_lst.id  is '标识';
comment on column iasis.sbmtd_csrc_file_lst.reg_rcod  is '注册记录';
comment on column iasis.sbmtd_csrc_file_lst.atch_type  is '附件类型';
comment on column iasis.sbmtd_csrc_file_lst.lst_type  is '清单类型';
comment on column iasis.sbmtd_csrc_file_lst.file_name  is '文件名';
comment on column iasis.sbmtd_csrc_file_lst.submit_csrc_reg_turn  is '提交证监会注册轮次';
comment on column iasis.sbmtd_csrc_file_lst.data_cde  is '材料编码';
comment on column iasis.sbmtd_csrc_file_lst.file_format  is '文件格式';
comment on column iasis.sbmtd_csrc_file_lst.submit_csrc_reg_time  is '提交证监会注册时间';
comment on column iasis.sbmtd_csrc_file_lst.atch_sort  is '附件排序';
comment on column iasis.sbmtd_csrc_file_lst.atch_idv_vol  is '附件个数';
comment on column iasis.sbmtd_csrc_file_lst.exch_file_listcode  is '交易所文件目录code';
comment on column iasis.sbmtd_csrc_file_lst.aprv_opni  is '审批意见';
comment on column iasis.sbmtd_csrc_file_lst.atch_if_for_impot_file  is '附件是否为重要文件';
comment on column iasis.sbmtd_csrc_file_lst.version_nbr  is '版本号';
comment on column iasis.sbmtd_csrc_file_lst.renew_time  is '更新时间';
comment on column iasis.sbmtd_csrc_file_lst.corlt_id  is '关联标识';
comment on column iasis.sbmtd_csrc_file_lst.import_time  is '导入时间';
comment on column iasis.sbmtd_csrc_file_lst.biz_renew_time  is '业务更新时间';
comment on column iasis.sbmtd_csrc_file_lst.if_new_data  is '是否新数据';
comment on column iasis.sbmtd_csrc_file_lst.echg_file_name  is '交换文件名';
comment on column iasis.sbmtd_csrc_file_lst.data_src  is '数据来源';
comment on column iasis.sbmtd_csrc_file_lst.board  is '板块';
comment on column iasis.sbmtd_csrc_file_lst.data_oprt_indc  is '数据操作标志';

comment on TABLE iasis.select_spvs_prjc_tb is '抽选监督项目表';
comment on column iasis.select_spvs_prjc_tb.id  is '标识';
comment on column iasis.select_spvs_prjc_tb.prin_board_acpt_vbl_ptb_id  is '主板受理可见主表标识';
comment on column iasis.select_spvs_prjc_tb.reg_rcod_id  is '注册记录标识';
comment on column iasis.select_spvs_prjc_tb.pub_board  is '上市板块';
comment on column iasis.select_spvs_prjc_tb.prjc_name  is '项目名称';
comment on column iasis.select_spvs_prjc_tb.work_flow_exmp_id  is '工作流实例标识';
comment on column iasis.select_spvs_prjc_tb.if_insd_aprv  is '是否内部审批';
comment on column iasis.select_spvs_prjc_tb.sts  is '状态';
comment on column iasis.select_spvs_prjc_tb.entr_time  is '创建时间';
comment on column iasis.select_spvs_prjc_tb.alter_time  is '修改时间';
comment on column iasis.select_spvs_prjc_tb.if_exh  is '是否展示';
comment on column iasis.select_spvs_prjc_tb.rmak  is '备注';
comment on column iasis.select_spvs_prjc_tb.random_select_spvs_prjc_rcod_id  is '随机抽选监督项目记录标识';

comment on TABLE iasis.tmplt_cfg is '模板配置表';
comment on column iasis.tmplt_cfg.tmplt_cfg_tb_id  is '模板配置表标识';
comment on column iasis.tmplt_cfg.cfg_blngs_class  is '配置所属类';
comment on column iasis.tmplt_cfg.cfg_blngs_class_describ  is '配置所属类描述';
comment on column iasis.tmplt_cfg.cfg_cde  is '配置编码';
comment on column iasis.tmplt_cfg.cfg_cde_value  is '配置编码值';
comment on column iasis.tmplt_cfg.cfg_cde_describ  is '配置编码描述';
comment on column iasis.tmplt_cfg.cfg_rule  is '配置规则';
comment on column iasis.tmplt_cfg.vld_sts_cde  is '删除标志';
comment on column iasis.tmplt_cfg.cfg_cde_value_clsf  is '配置编码值分类';
comment on column iasis.tmplt_cfg.entr_time  is '创建时间';
comment on column iasis.tmplt_cfg.renew_time  is '更新时间';

comment on TABLE iasis.wait_select_index_cfg_tb is '待选指标配置表';
comment on column iasis.wait_select_index_cfg_tb.id  is '标识';
comment on column iasis.wait_select_index_cfg_tb.wait_select_index_tb_eng_name  is '待选指标表英文名';
comment on column iasis.wait_select_index_cfg_tb.wait_select_index_tb_chn_name  is '待选指标表中文名';
comment on column iasis.wait_select_index_cfg_tb.wait_select_index_eng_name  is '待选指标英文名';
comment on column iasis.wait_select_index_cfg_tb.wait_select_index_chn_name  is '待选指标中文名';
comment on column iasis.wait_select_index_cfg_tb.clm_wide  is '列宽';
comment on column iasis.wait_select_index_cfg_tb.many_line_exh_indc  is '多行展示标志';
comment on column iasis.wait_select_index_cfg_tb.tb_order  is '表顺序';
comment on column iasis.wait_select_index_cfg_tb.field_order  is '字段顺序';
comment on column iasis.wait_select_index_cfg_tb.if_excd_clm_wide_indc  is '是否超过列宽标志';
comment on column iasis.wait_select_index_cfg_tb.rmak  is '备注';


