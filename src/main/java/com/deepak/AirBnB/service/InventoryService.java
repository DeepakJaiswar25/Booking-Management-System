package com.deepak.AirBnB.service;

import com.deepak.AirBnB.entity.Room;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteFutureInventories(Room room);

}
