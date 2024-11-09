package com.edigest.journalapp.controller;

import com.edigest.journalapp.entity.JournalEntry;
import com.edigest.journalapp.service.JournalEntryService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {

    @Autowired
    private  JournalEntryService journalEntryService;

    @GetMapping()
    public ResponseEntity<List<JournalEntry>> getJournalEntries(){
        try{
            return new ResponseEntity<>(journalEntryService.getJournalEntries(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    @PostMapping()
    public ResponseEntity<JournalEntry> createJournalForUser(@RequestBody JournalEntry body){
        try{
            journalEntryService.createJournalForUser(body);
            return new ResponseEntity<>(body, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/id/{entryId}")
    public ResponseEntity<JournalEntry> JournalEntryById(@PathVariable ObjectId entryId){
        try{
            Optional<JournalEntry> entry =  journalEntryService.getJournalById(entryId);
            if(entry.isPresent()){
                return new ResponseEntity<>(entry.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("id/{entryId}")
    public ResponseEntity<String> DeleteJournal(@PathVariable ObjectId entryId ){
        try {
            journalEntryService.deleteJournalById(entryId);
            return new ResponseEntity<>(entryId.toString(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_GATEWAY);
        }
    }

    @PutMapping("id/{id}")
    public ResponseEntity<JournalEntry> updateJournalEntry(@RequestBody JournalEntry newEntry, @PathVariable ObjectId id){
        try {
            return new ResponseEntity<>(journalEntryService.updateJournalEntry(newEntry, id), HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
