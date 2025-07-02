package com.callcenter.emailmanagement.repository;

import com.callcenter.emailmanagement.domain.model.Email;
import com.callcenter.emailmanagement.domain.model.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    
    Optional<Email> findByMessageId(String messageId);
    
    List<Email> findByEmailCase(Case emailCase);
    
    List<Email> findByFromAddress(String fromAddress);
    
    List<Email> findByDirection(Email.EmailDirection direction);
}