create table pacificosul.conf_grid_perfil_usuario(
    id_grid_perfil number(9),
    id_usuario number(9)
)

COMMENT ON TABLE pacificosul.conf_grid_perfil_usuario IS 'Relaciona perfil para o usuario';

ALTER TABLE pacificosul.conf_grid_perfil_usuario
    ADD CONSTRAINT REF_GRID_PER_GRID_PER_USER FOREIGN KEY(id_grid_perfil) REFERENCES PACIFICOSUL.conf_grid_perfil(ID);
ALTER TABLE pacificosul.conf_grid_perfil_usuario
    ADD CONSTRAINT REF_USER_GRID_PER_USER FOREIGN KEY(id_usuario) REFERENCES PACIFICOSUL.PS_TB_USUARIO(COD_USUARIO);

ALTER TABLE pacificosul.conf_grid_perfil_usuario ADD CONSTRAINT UNIQ_USER_CONF_GRID_PERFIL UNIQUE(id_grid_perfil, id_usuario);
