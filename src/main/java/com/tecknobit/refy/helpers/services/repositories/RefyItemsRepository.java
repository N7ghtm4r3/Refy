package com.tecknobit.refy.helpers.services.repositories;

import com.tecknobit.refycore.records.RefyItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefyItemsRepository<T extends RefyItem> extends JpaRepository<T, String> {



}
