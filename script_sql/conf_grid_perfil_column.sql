create table pacificosul.conf_grid_perfil_column(
    id number(9),
    id_grid_perfil number(9),
    id_grid_column number(9),
    position number(3) not null,
    hidden number(1) default 0,
    sortable number(1) default 1,
    filterable number(1) default 1,
    resizable number(1) default 1,
    fixed number(1) default 0,
    summary number(3) default 0,
    width number(6)
)

COMMENT ON TABLE pacificosul.conf_grid_perfil_column IS 'Configuração de colunas das grids do orion web, nomes dos campos de configuração estão em ingles para ficar coerente com o frontend';
COMMENT ON COLUMN pacificosul.conf_grid_perfil_column.position IS 'Ordem que a coluna vai aparecer no grid';
COMMENT ON COLUMN pacificosul.conf_grid_perfil_column.hidden IS 'Coluna escondida no grid: (0) não, (1) sim';
COMMENT ON COLUMN pacificosul.conf_grid_perfil_column.sortable IS 'Permite ordernar por essa coluna: (0) não, (1) sim';
COMMENT ON COLUMN pacificosul.conf_grid_perfil_column.filterable IS 'Permite filtrar por essa coluna: (0) não, (1) sim';
COMMENT ON COLUMN pacificosul.conf_grid_perfil_column.resizable IS 'Permite redimensionar coluna: (0) não, (1) sim';
COMMENT ON COLUMN pacificosul.conf_grid_perfil_column.fixed IS 'Coluna fixa no grid: (0) não, (1) sim';
COMMENT ON COLUMN pacificosul.conf_grid_perfil_column.summary IS 'Tipo de totalizador da coluna (Component React): (0) Nenhum, (1) SummaryCount, (2) SummaryDistinctCount, (3) SummaryAverage, (4) SummarySum';
COMMENT ON COLUMN pacificosul.conf_grid_perfil_column.width IS 'Largura da coluna em pixels';

ALTER TABLE pacificosul.conf_grid_perfil_column ADD CONSTRAINT pk_conf_grid_perfil_column PRIMARY KEY (id);
ALTER TABLE pacificosul.conf_grid_perfil_column
    ADD CONSTRAINT REF_GRID_PERF_GRID_PERF_COL FOREIGN KEY(id_grid_perfil) REFERENCES PACIFICOSUL.conf_grid_perfil(ID);
ALTER TABLE pacificosul.conf_grid_perfil_column
    ADD CONSTRAINT REF_GRID_COL_GRID_PERF_COL FOREIGN KEY(id_grid_column) REFERENCES PACIFICOSUL.CONF_GRID_COLUMN(ID);
ALTER TABLE pacificosul.conf_grid_perfil_column ADD CONSTRAINT UNIQ_CONF_GRID_PERFIL_COL UNIQUE(id_grid_perfil, id_grid_column);

CREATE SEQUENCE pacificosul.id_conf_grid_perfil_column START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER pacificosul.conf_tr_grid_perfil_column
BEFORE INSERT ON pacificosul.conf_grid_perfil_column FOR EACH ROW
DECLARE
    next_value number;
BEGIN
    if :new.id is null then
        select pacificosul.id_conf_grid_perfil_column.nextval
        into next_value from dual;
        :new.id := next_value;
    end if;
END conf_tr_grid_perfil_column;