package com.tecknobit.refy.helpers.services.links;

import com.tecknobit.refy.helpers.services.repositories.links.LinksRepository;
import com.tecknobit.refycore.records.links.RefyLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.tecknobit.refycore.records.links.RefyLink.LINK_KEY;

@Service
public class LinksHelper {

    @Autowired
    private LinksRepository linksRepository;

    public List<RefyLink> getAllUserLinks(String userId) {
        return linksRepository.getAllUserLinks(userId);
    }

    public void createLink(String userId, String linkId, String title, String description, String referenceLink) {
        linksRepository.saveLink(LINK_KEY, linkId, title, description, referenceLink, userId);
    }

    public RefyLink getUserLinkIfOwner(String userId, String linkId) {
        return linksRepository.getUserLinkIfOwner(userId, linkId);
    }

    public void editLink(String userId, String linkId, String title, String description, String referenceLink) {
        linksRepository.updateLink(linkId, title, description, referenceLink, userId);
    }

    public void deleteLink(String linkId) {
        linksRepository.deleteLink(linkId);
    }

}
