package com.example.expressionsevaluator.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "expressions")
@Data
public class NamedExpression {

    @Id
    @Column
    private String name;

    @Column
    private String expression;

}
