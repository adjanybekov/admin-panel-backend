package com.example.test1.repo;

import com.example.test1.model.Parking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingRepo extends JpaRepository<Parking,String> {
}
