package com.example.demo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Inventory;
import com.example.demo.service.InventoryService;
import com.example.demo.exception.InventoryNotFoundException;

@RestController
@RequestMapping("api/inventory")
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    private InventoryService service;

    @PostMapping
    public ResponseEntity<?> saveInventory(@RequestBody Inventory inventory) {
        logger.info("Received request to save new inventory record.");
        try {
            Inventory saved = service.saveInventory(inventory);
            logger.info("Inventory saved with ID: {}", saved.getInventoryid());
            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            logger.error("Error saving inventory: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error saving inventory: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateInventory(@RequestBody Inventory inventory) {
        logger.info("Received request to update inventory with ID: {}", inventory.getInventoryid());
        try {
            Inventory updated = service.updateInventory(inventory);
            logger.info("Inventory updated successfully.");
            return ResponseEntity.ok(updated);
        } catch (InventoryNotFoundException e) {
            logger.warn("Inventory not found for update with ID: {}", inventory.getInventoryid());
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating inventory: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error updating inventory: " + e.getMessage());
        }
    }

    @PatchMapping("/partial-update/{id}")
    public ResponseEntity<?> patchUpdateInventory(@PathVariable("id") Long inventoryid, @RequestBody Inventory updatedFields) {
        logger.info("Received request to patch update inventory with ID: {}", inventoryid);
        try {
            Inventory patched = service.patchUpdateInventory(inventoryid, updatedFields);
            logger.info("Inventory patched successfully.");
            return ResponseEntity.ok(patched);
        } catch (InventoryNotFoundException e) {
            logger.warn("Inventory not found for patch update with ID: {}", inventoryid);
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error patching inventory: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error updating inventory: " + e.getMessage());
        }
    }

    @GetMapping("/fetch/{id}")
    public ResponseEntity<?> getInventoryById(@PathVariable("id") Long inventoryid) {
        logger.info("Fetching inventory by ID: {}", inventoryid);
        try {
            Inventory found = service.getInventoryById(inventoryid);
            logger.info("Inventory found.");
            return ResponseEntity.ok(found);
        } catch (InventoryNotFoundException e) {
            logger.warn("Inventory not found with ID: {}", inventoryid);
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/fetch/by-book/{bookid}")
    public ResponseEntity<?> getInventoryByBookId(@PathVariable("bookid") Long bookid) {
        logger.info("Fetching inventory by Book ID: {}", bookid);
        try {
            return ResponseEntity.ok(service.getInventoryByBookid(bookid));
        } catch (Exception e) {
            logger.error("Error fetching inventory for book ID {}: {}", bookid, e.getMessage());
            return ResponseEntity.status(500).body("Error fetching inventory: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteInventoryById(@PathVariable("id") Long inventoryid) {
        logger.info("Deleting inventory with ID: {}", inventoryid);
        try {
            String message = service.deleteInventoryById(inventoryid);
            logger.info("Inventory deleted successfully.");
            return ResponseEntity.ok(message);
        } catch (InventoryNotFoundException e) {
            logger.warn("Inventory not found for deletion with ID: {}", inventoryid);
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Inventory>> getAllInventories() {
        logger.info("Retrieving all inventory records.");
        List<Inventory> list = service.getAllInventories();
        logger.info("Returned {} inventory records.", list.size());
        return ResponseEntity.ok(list);
    }
}
