package com.edigest.journalapp.service;

import com.edigest.journalapp.entity.JournalEntry;
import com.edigest.journalapp.entity.User;
import com.edigest.journalapp.repository.JournalEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class JournalEntryService {
    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(JournalEntryService.class);

    @Transactional
    public void createJournalForUser(JournalEntry journalEntry){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUserName(authentication.getName());

            if(user == null){
                throw new InstanceNotFoundException("User not found with given username");
            }

            journalEntry.setDate(LocalDateTime.now());
            final JournalEntry savedJournalEntry = journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(savedJournalEntry);
            userService.saveUser(user);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<JournalEntry> getJournalEntries(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUserName(authentication.getName());
            System.out.println(user);
            List<JournalEntry> journalEntries = user.getJournalEntries();

            if(journalEntries != null && !journalEntries.isEmpty()){
                return journalEntries;
            }
              throw new InstanceNotFoundException("Journal Entries not found");

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    public  Optional<JournalEntry> getJournalById(ObjectId id){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUserName(authentication.getName());

            if(user.getJournalEntries().stream().anyMatch(journalEntry -> journalEntry.getId().equals(id))){
                return journalEntryRepository.findById(id);
            }
            throw new InstanceNotFoundException("Journal not found for this user");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Transactional
    public void deleteJournalById(ObjectId id ) throws BadRequestException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUserName(authentication.getName());
            boolean isRemoved = user.getJournalEntries().removeIf(journalEntry -> journalEntry.getId().equals(id));
            if(isRemoved){
                userService.saveUser(user);
                journalEntryRepository.deleteById(id);
            }
        } catch (Exception e) {
            log.error("Exception", e);
            throw new BadRequestException(e.getMessage());
        }
    }

    public JournalEntry updateJournalEntry(JournalEntry newEntry, ObjectId id){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userService.findByUserName(authentication.getName());
            if(user.getJournalEntries().stream().anyMatch(journalEntry -> journalEntry.getId().equals(id))){
                JournalEntry entry = journalEntryRepository.findById(id).orElse(null);

                if(entry == null){
                    throw new InstanceNotFoundException("Journal entry not found");
                }

                entry.setTitle(newEntry.getTitle() != null && !newEntry.getTitle().isEmpty() ? newEntry.getTitle() : entry.getTitle());
                entry.setContent(newEntry.getContent() != null && !newEntry.getContent().isEmpty() ? newEntry.getContent() : entry.getContent());

                return journalEntryRepository.save(entry);
            }
            throw new Exception("Update not allowed");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
