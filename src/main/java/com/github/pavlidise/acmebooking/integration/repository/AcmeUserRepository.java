package com.github.pavlidise.acmebooking.integration.repository;

import com.github.pavlidise.acmebooking.model.entity.AcmeUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AcmeUserRepository extends JpaRepository<AcmeUserEntity, Long> {

    Optional<AcmeUserEntity> findByUserEmail(final String userEmail);
}
