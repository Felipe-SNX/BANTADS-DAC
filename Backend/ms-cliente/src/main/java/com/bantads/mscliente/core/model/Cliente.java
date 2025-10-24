package com.bantads.mscliente.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "cliente", schema = "ms_cliente")
public class Cliente {

}
