package com.example.expressionsevaluator.repositories;

import com.example.expressionsevaluator.model.NamedExpression;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpressionsRepository extends CrudRepository<NamedExpression, String> {}
