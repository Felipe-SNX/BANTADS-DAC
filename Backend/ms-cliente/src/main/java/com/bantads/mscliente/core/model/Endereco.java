package com.bantads.mscliente.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "endereco", schema = "ms_cliente")
public class Endereco {

}
