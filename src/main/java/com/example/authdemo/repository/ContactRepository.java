package com.example.authdemo.repository;

import com.example.authdemo.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    /**
     * Find all contacts ordered by creation date (newest first)
     */
    List<Contact> findAllByOrderByCreatedAtDesc();

    /**
     * Find contacts by status
     */
    List<Contact> findByStatusOrderByCreatedAtDesc(String status);

    /**
     * Count messages by status
     */
    long countByStatus(String status);

    /**
     * Find contacts by email
     */
    List<Contact> findByEmailOrderByCreatedAtDesc(String email);

    /**
     * Find contacts created between dates
     */
    List<Contact> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find contacts containing keyword in subject or message
     */
    @Query("SELECT c FROM Contact c WHERE c.subject LIKE %?1% OR c.message LIKE %?1% ORDER BY c.createdAt DESC")
    List<Contact> findByKeyword(String keyword);

    /**
     * Find unread messages (NEW status)
     */
    @Query("SELECT c FROM Contact c WHERE c.status = 'NEW' ORDER BY c.createdAt DESC")
    List<Contact> findUnreadMessages();
}