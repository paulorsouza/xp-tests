create table pacificosul.conf_grid_column(
    id number(9),
    id_grid number(9),
    key varchar2(50) not null,
    name varchar2(50) not null,
    type varchar2(50) not null,
    formatter_index number(3) default 0,
)

COMMENT ON TABLE pacificosul.conf_grid_column IS 'Cadastro de colunas das grids do orion web, nomes dos campos de configuração estão em ingles para ficar coerente com o frontend';
COMMENT ON COLUMN pacificosul.conf_grid_column.key IS 'Chave da coluna para linkar o response do backend com a coluna no frontend';
COMMENT ON COLUMN pacificosul.conf_grid_column.name IS 'Descrição da coluna';
COMMENT ON COLUMN pacificosul.conf_grid_column.type IS 'Tipo de dados para o frontend: text, number, date';
COMMENT ON COLUMN pacificosul.conf_grid_column.formatter_index IS 'Tipo de formatação da coluna no frontend: 0 - Sem formatação, 1 - IntegerFormat';

ALTER TABLE pacificosul.conf_grid_column ADD CONSTRAINT pk_conf_grid_column PRIMARY KEY (id);
ALTER TABLE pacificosul.conf_grid_column
    ADD CONSTRAINT REF_CONF_GRID_CONF_GRID_COLUMN FOREIGN KEY(ID_GRID) REFERENCES PACIFICOSUL.CONF_GRID(ID);
ALTER TABLE pacificosul.conf_grid_column ADD CONSTRAINT UNIQ_CONF_GRID_COLUMN UNIQUE(id_grid, key);

CREATE SEQUENCE pacificosul.id_conf_grid_column START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER pacificosul.conf_tr_grid_column
BEFORE INSERT ON pacificosul.conf_grid_column FOR EACH ROW
DECLARE
    next_value number;
BEGIN
    if :new.id is null then
        select pacificosul.id_conf_grid_column.nextval
        into next_value from dual;
        :new.id := next_value;
    end if;
END conf_tr_grid_column;