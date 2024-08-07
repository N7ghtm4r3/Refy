package com.tecknobit.refy.helpers.services;

import com.tecknobit.refy.helpers.services.repositories.LinksCollectionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class LinksCollectionsHelper {

    @Autowired
    private LinksCollectionsRepository collectionsRepository;

    public HashSet<String> getUserCollections(String userId) {
        return collectionsRepository.getUserCollections(userId);
    }

}
