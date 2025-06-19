package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Inventory;
import com.example.demo.repository.InventoryRepository;
import com.example.demo.exception.InventoryNotFoundException;

@Service
public class InventoryServiceImpl implements InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);

    @Autowired
    private InventoryRepository repository;

    @Override
    public Inventory saveInventory(Inventory inventory) {
        logger.info("Saving new inventory record for book: {}", inventory.getBook() != null ? inventory.getBook().getTitle() : "Unknown");
        Inventory saved = repository.save(inventory);
        logger.info("Inventory saved with ID: {}", saved.getInventoryid());
        return saved;
    }

    @Override
    public Inventory updateInventory(Inventory inventory) {
        logger.info("Updating inventory with ID: {}", inventory.getInventoryid());
        return repository.findById(inventory.getInventoryid())
                .map(existingInventory -> {
                    existingInventory.setBook(inventory.getBook());
                    existingInventory.setQuantity(inventory.getQuantity());
                    logger.debug("Updated book and quantity fields");
                    Inventory updated = repository.save(existingInventory);
                    logger.info("Inventory updated successfully for ID: {}", inventory.getInventoryid());
                    return updated;
                }).orElseThrow(() -> {
                    logger.error("Inventory not found with ID: {}", inventory.getInventoryid());
                    return new InventoryNotFoundException("Inventory not found with ID: " + inventory.getInventoryid());
                });
    }

    @Override
    public Inventory patchUpdateInventory(Long inventoryid, Inventory updatedFields) {
        logger.info("Attempting patch update for inventory ID: {}", inventoryid);
        return repository.findById(inventoryid).map(existingInventory -> {
            if (updatedFields.getBook() != null) {
                logger.debug("Updating book field during patch");
                existingInventory.setBook(updatedFields.getBook());
            }
            if (updatedFields.getQuantity() > 0) {
                logger.debug("Updating quantity to: {}", updatedFields.getQuantity());
                existingInventory.setQuantity(updatedFields.getQuantity());
            }
            Inventory patched = repository.save(existingInventory);
            logger.info("Patch update successful for inventory ID: {}", inventoryid);
            return patched;
        }).orElseThrow(() -> {
            logger.error("Inventory not found with ID: {}", inventoryid);
            return new InventoryNotFoundException("Inventory not found with ID: " + inventoryid);
        });
    }

    @Override
    public Inventory getInventoryById(Long inventoryid) {
        logger.info("Fetching inventory with ID: {}", inventoryid);
        return repository.findById(inventoryid)
                .orElseThrow(() -> {
                    logger.error("Inventory not found with ID: {}", inventoryid);
                    return new InventoryNotFoundException("Inventory not found with ID: " + inventoryid);
                });
    }

    @Override
    public Optional<Inventory> getInventoryByBookid(Long bookid) {
        logger.info("Fetching inventory for book ID: {}", bookid);
        return repository.findByBook_Bookid(bookid);
    }

    @Override
    public String deleteInventoryById(Long inventoryid) {
        logger.info("Attempting to delete inventory with ID: {}", inventoryid);
        if (!repository.existsById(inventoryid)) {
            logger.error("Inventory not found for deletion with ID: {}", inventoryid);
            throw new InventoryNotFoundException("Inventory not found with ID: " + inventoryid);
        }
        repository.deleteById(inventoryid);
        logger.info("Inventory with ID {} deleted successfully", inventoryid);
        return "Inventory deleted successfully!";
    }

    @Override
    public List<Inventory> getAllInventories() {
        logger.info("Retrieving all inventory records.");
        return repository.findAll();
    }
}
