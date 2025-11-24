-- tb_cmmn_code_detail 테이블 컬럼 코멘트 추가 SQL
-- 코멘트가 없는 컬럼들에 대한 COMMENT 추가

-- 사용여부
COMMENT ON COLUMN tb_cmmn_code_detail.use_yn IS '사용여부';

-- 등록자ID
COMMENT ON COLUMN tb_cmmn_code_detail.register_id IS '등록자ID';

-- 등록일시
COMMENT ON COLUMN tb_cmmn_code_detail.regist_dt IS '등록일시';

