package com.clou.admindemo.repository;

import com.clou.admindemo.POJO.po.BannerPO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends MongoRepository<BannerPO, String> {

    List<BannerPO> findAllByOrderBySortOrderAscCreateTimeDesc();

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'clickCount': 1 } }")
    void incrementClickCount(String bannerId);
}
