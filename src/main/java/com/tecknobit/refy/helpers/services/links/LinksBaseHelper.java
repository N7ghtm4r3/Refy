package com.tecknobit.refy.helpers.services.links;

import com.tecknobit.refy.helpers.services.RefyItemsHelper;
import com.tecknobit.refycore.records.links.RefyLink;
import org.springframework.stereotype.Service;

@Service
public abstract class LinksBaseHelper<T extends RefyLink> extends RefyItemsHelper<T> {

    public abstract void deleteLink(String linkId);

}
