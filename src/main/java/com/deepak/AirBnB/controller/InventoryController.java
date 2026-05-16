package com.deepak.AirBnB.controller;

import com.deepak.AirBnB.dto.InventoryDto;
import com.deepak.AirBnB.dto.UpdateInventoryRequestDto;
import com.deepak.AirBnB.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/inventory/rooms")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/{roomId}")
    public ResponseEntity<List<InventoryDto>> getAllInventory(@PathVariable Long roomId) {
        return ResponseEntity.ok().body(inventoryService.getAllInventory(roomId));
    }

    @PatchMapping("/{roomId}")
    public ResponseEntity<Void> updateInventory(@PathVariable Long roomId, @RequestBody UpdateInventoryRequestDto updateInventoryRequestDto) {
        inventoryService.updateInventory(roomId, updateInventoryRequestDto);
        return ResponseEntity.noContent().build();
    }
}
