package br.com.pacificosul.repository

import br.com.pacificosul.data.PsChatData.CustomFieldsData
import br.com.pacificosul.data.PsChatData.UserPayload
import br.com.pacificosul.data.PsChatData.PsUser
import br.com.pacificosul.rules.PsChatRule
import org.springframework.jdbc.core.JdbcTemplate

class PsChatRepository(private val jdbcTemplate: JdbcTemplate) {
    fun cargaInicial() {
        val auth = PsChatRule().auth()
        val sql = "select a.cod_usuario_vetorh, des_usuario, a.des_email, " +
                  "a.des_senha, descricao_setor, a.num_ramal, a.sit_usuario, a.tem_rocketchat, " +
                  "from pacificosul.ps_tb_usuario a " +
                  "left join vetorh.r034fun b on (a.cod_usuario_vetorh = b.numcad and a.num_empresa = b.numemp) " +
                  "left join systextil.basi_006 c on (b.numloc = c.setor_responsavel) " +
                  "left join vetorh.r024car d on (b.codcar = d.codcar) " +
                  "left join systextil.efic_050 f on (b.numcad = f.cracha_funcionario) " +
                  "left join pacificosul.ps_chat_usuario r on (r.cracha = a.cod_usuario_vetorh) " +
                  "where b.numemp = 17 " +
                  "  and ((a.sit_usuario = 'A' and a.tem_rocketchat = 1) or r.cracha is not null) "
        jdbcTemplate.query(sql) { rs, _ ->
            val cod = rs.getString("COD_USUARIO_VETORH")
            val des = rs.getString("DES_USUARIO").toLowerCase().split(" ").joinToString( separator = " ", transform = { s -> s.capitalize() } )
            val senha = rs.getString("DES_SENHA")
            val local = rs.getString("descricao_setor").toLowerCase().capitalize()
            val numRamal = rs.getInt("num_ramal")
            val email = rs.getString("des_email").orEmpty()
            val ativo = !cod.isNullOrBlank()
            val existsPsUser = !rs.getString("cracha").isNullOrBlank()

            if(!ativo && existsPsUser) {
                //desativar
            }

            if(!existsPsUser) {
                val customFields = CustomFieldsData(numRamal, email, local)
                val userData = UserPayload(des, "${cod}@${cod}.com", senha, cod, customFields)
                insertPsChatUser(userData)
                PsChatRule().createUser(auth, userData)
            }

            if (ativo && existsPsUser) {
                val customFields = CustomFieldsData(numRamal, email, local)
                val userData = UserPayload(des, "${cod}@${cod}.com", senha, cod, customFields)
                val psUser = PsUser(rs.getString("cracha"), rs.getString("nome"), rs.getString("senha"),
                        rs.getString("local"), rs.getInt("ramal"), rs.getInt("ativo"), rs.getString("email"))
                val hasChange = PsChatRule().hasChanges(ativo, userData, psUser)
                if(hasChange){
                    updatePsChatUser()
                }
            }
        }
    }

    private fun updatePsChatUser(): Any {
        /*Falta implementação*/
        return 0
    }

    fun insertPsChatUser(user: UserPayload) {
        val sql = "insert into pacificosul.ps_chat_usuario " +
                  "(cracha, nome, senha, local, ramal, ativo, email) " +
                  "values " +
                  "(:cracha, :nome, :senha, :local, :ramal, 1, :email) "
        val mapa = HashMap<String, Any>()
        mapa["cracha"] = user.username
        mapa["nome"] = user.name
        mapa["senha"] = user.password
        mapa["local"] = user.customFields.local
        mapa["ramal"] = user.customFields.ramal
        mapa["email"] = user.customFields.email.orEmpty()

        jdbcTemplate.update(sql, mapa)
    }
}