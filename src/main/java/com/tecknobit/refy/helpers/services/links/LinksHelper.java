package com.tecknobit.refy.helpers.services.links;

import com.tecknobit.refy.helpers.services.repositories.links.LinksRepository;
import com.tecknobit.refycore.records.links.RefyLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LinksHelper {

    @Autowired
    private LinksRepository linksRepository;

    public List<RefyLink> getUserLinks(String userId) {
        return linksRepository.getUserLinks(userId);
    }

}
