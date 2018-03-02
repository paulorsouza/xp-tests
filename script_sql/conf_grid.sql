create table pacificosul.conf_grid(
    id number(9) not null,
    nome varchar2(50)
)

COMMENT ON TABLE pacificosul.conf_grid IS 'Cadastro de grid';
COMMENT ON COLUMN pacificosul.conf_grid.nome IS 'Nome da grid';

ALTER TABLE pacificosul.conf_grid ADD CONSTRAINT pk_conf_grid PRIMARY KEY (id);
CREATE SEQUENCE pacificosul.id_conf_grid START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER pacificosul.conf_tr_grid
BEFORE INSERT ON pacificosul.conf_grid FOR EACH ROW
DECLARE
    next_value number;
BEGIN
    if :new.id is null then
        select pacificosul.id_conf_grid.nextval
        into next_value from dual;
        :new.id := next_value;
    end if;
END pacificosul.conf_tr_grid;