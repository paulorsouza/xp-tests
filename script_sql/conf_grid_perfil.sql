create table pacificosul.conf_grid_perfil(
    id number(9),
    id_grid number(9),
    id_usuario_gerenciador number(9),
    nome varchar2(50) not null,
    publico number(1) default 0
)

COMMENT ON TABLE pacificosul.conf_grid_perfil IS 'Cadastro de perfil das grids do orion web';
COMMENT ON COLUMN pacificosul.conf_grid_perfil.id_usuario_gerenciador IS 'Codigo do usuario que criou o perfil';
COMMENT ON COLUMN pacificosul.conf_grid_perfil.nome IS 'Nome do perfil';
COMMENT ON COLUMN pacificosul.conf_grid_perfil.publico IS 'Perfil pode ser usado por todos usuarios: 0- não, 1- sim';

ALTER TABLE pacificosul.conf_grid_perfil ADD CONSTRAINT pk_conf_grid_perfil PRIMARY KEY (id);
ALTER TABLE pacificosul.conf_grid_perfil
    ADD CONSTRAINT REF_USUARIO_CONF_GRID_PERFIL FOREIGN KEY(id_usuario_gerenciador) REFERENCES PACIFICOSUL.PS_TB_USUARIO(COD_USUARIO);
ALTER TABLE pacificosul.conf_grid_perfil
    ADD CONSTRAINT REF_CONF_GRID_CONF_GRID_PERFIL FOREIGN KEY(ID_GRID) REFERENCES PACIFICOSUL.CONF_GRID(ID);
ALTER TABLE pacificosul.conf_grid_perfil ADD CONSTRAINT UNIQ_CONF_GRID_PERFIL UNIQUE(id_grid, id_usuario_gerenciador, nome);

CREATE SEQUENCE pacificosul.id_conf_grid_perfil START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER pacificosul.conf_tr_grid_perfil
BEFORE INSERT ON pacificosul.conf_grid_perfil FOR EACH ROW
DECLARE
    next_value number;
BEGIN
    if :new.id is null then
        select pacificosul.id_conf_grid_perfil.nextval
        into next_value from dual;
        :new.id := next_value;
    end if;
END conf_tr_grid_perfil;