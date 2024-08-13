package com.firstcomestore.domain.product.repository;

import com.firstcomestore.domain.product.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {

}
