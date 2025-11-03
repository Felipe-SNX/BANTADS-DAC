package com.bantads.msauth.core.document;

import com.bantads.msauth.core.enums.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "usuario")
public class Usuario {

    @Id
    private long id;
    private String cpfUsuario;
    private String login;
    private String senha;
    private TipoUsuario tipoUsuario;
}
