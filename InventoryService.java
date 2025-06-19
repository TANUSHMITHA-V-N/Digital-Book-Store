package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import com.example.demo.model.Inventory;

public interface InventoryService {
    Inventory saveInventory(Inventory inventory);
    Inventory updateInventory(Inventory inventory);
    Inventory patchUpdateInventory(Long inventoryid, Inventory updatedFields);
    Inventory getInventoryById(Long inventoryid);
    Optional<Inventory> getInventoryByBookid(Long bookid); 
    String deleteInventoryById(Long inventoryid);
    List<Inventory> getAllInventories();
}
