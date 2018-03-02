DECLARE
    id_grid number(9);
    aux_id number(9);
    id_perfil number(9);

    function col_insert(p_id_grid NUMBER, p_key VARCHAR2, p_name VARCHAR2, p_type VARCHAR2, p_formatter number)
    return number
    as
        return_id number(9);
    begin
        insert into pacificosul.conf_grid_column
        (id_grid, key, name, type, formatter)
        values
        (p_id_grid, p_key, p_name, p_type, p_formatter)
        returning id into return_id;
        return return_id;
    end;

    procedure col_def_insert(p_id_grid_perfil number, p_id_grid_column number, p_position number, p_summary number)
    is
    begin
        insert into pacificosul.conf_grid_perfil_column
        (id_grid_perfil, id_grid_column, position, summary)
        values
        (p_id_grid_perfil, p_id_grid_column, p_position, p_summary);
    end;
BEGIN
    insert into pacificosul.conf_grid
    (nome)
    values
    ('tela200estagios')
    returning id into id_grid;

    insert into pacificosul.conf_grid_perfil
    (id_grid, id_usuario_gerenciador, nome, publico)
    values
    (id_grid, 3106, 'default', 1)
    returning id into id_perfil;

    aux_id := col_insert(id_grid, 'estagioComDescricao', 'Estágio', 'text', 0);
    col_def_insert(id_perfil, aux_id, 1, 0);

    aux_id := col_insert(id_grid, 'quantidadeOrdensAProduzir', 'OP', 'number', 1);
    col_def_insert(id_perfil, aux_id, 2, 0);

    aux_id := col_insert(id_grid, 'quantidadePecasAProduzir', 'Peças', 'number', 1);
    col_def_insert(id_perfil, aux_id, 3, 4);

    aux_id := col_insert(id_grid, 'descricaoAgrupador', 'Agrupador', 'text', 0);
    col_def_insert(id_perfil, aux_id, 4, 0);

    aux_id := col_insert(id_grid, 'responsavelEstagio', 'Responsável', 'number', 0);
    col_def_insert(id_perfil, aux_id, 5, 0);

    COMMIT;
END;