package es.daw.productoapirest.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "producto")
@Setter
@Getter
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)// GenerationType.IDENTITY hace que sea autoincremental
    private Integer id;

    @Column(nullable = false, length = 4, unique = true) //nullable es que no perite que est evacio
    private String codigo;

    @Column(nullable = false, length = 255) //nullable es que no perite que est evacio
    private String nombre;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    // Relación muchos a uno con Fabricante | un producto tiene un fabricante
    @ManyToOne(optional = false)
    @JoinColumn(name = "codigo_fabricante", nullable = false) //el join en producto es porque UN PRODUCTO pertenece a UN FABRICANTE (el producto depende el fabrivante, depende de él)
    private Fabricante fabricante;
}